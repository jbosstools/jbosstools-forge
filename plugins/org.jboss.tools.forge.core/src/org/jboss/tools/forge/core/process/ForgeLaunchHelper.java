package org.jboss.tools.forge.core.process;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.jboss.tools.forge.core.ForgeCorePlugin;

public class ForgeLaunchHelper {
	
	private static final FilenameFilter JAR_FILTER = new FilenameFilter() {		
		@Override
		public boolean accept(File dir, String name) {
				return name.endsWith("jar");
		}
	};
	
	private static final FilenameFilter LIB_FILTER = new FilenameFilter() {		
		@Override
		public boolean accept(File dir, String name) {
				return name.endsWith("lib");
		}
	};
	
	private static final ILaunchManager LAUNCH_MANAGER = DebugPlugin.getDefault().getLaunchManager();
	
	private static final ILaunchConfigurationType JAVA_LAUNCH_CONFIGURATION_TYPE = 
			LAUNCH_MANAGER.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
	
	private static final File WORKING_DIR = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
	
	private static void removeLaunchConfiguration(String name) {
		try {
			ILaunchConfiguration[] configurations = LAUNCH_MANAGER.getLaunchConfigurations(JAVA_LAUNCH_CONFIGURATION_TYPE);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals(name)) {
					configuration.delete();
					break;
				}
			}
		} catch (CoreException e) {
			ForgeCorePlugin.log(new RuntimeException("CoreException while cleaning up launch configuration", e));
		}
	}
	
	private static List<String> createClassPath(String location) {
		List<String> result = new ArrayList<String>();
		result = addUserClasses(result, location);
		if (result != null) {
			result = addExtClasses(result);
			result = addSystemLibs(result);
		}
		return result;
	}
	
	private static List<String> addExtClasses(List<String> classPath) {
		try {
			File file = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime.ext"));
			if (file != null) {
				if (file.isDirectory()) {
					File[] files = file.listFiles(new FileFilter() {
						public boolean accept(File pathname) {
							return pathname.getAbsolutePath().endsWith("bin");
						}
					});
					if (files.length > 0) {
						classPath.add(createUserClassEntryMemento(files[0]));
					}
				} else {
					classPath.add(createUserClassEntryMemento(file));
				}
			}
		} catch (IOException e) {
			ForgeCorePlugin.log(new RuntimeException("Problem while adding ext lib entry", e));
		}
		return classPath;
	}
	
	private static List<String> addSystemLibs(List<String> classPath) {
		try {
			IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
			IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(
					systemLibsPath, 
					IRuntimeClasspathEntry.STANDARD_CLASSES);
			classPath.add(systemLibsEntry.getMemento());
			return classPath;
		} catch (CoreException e) {
			ForgeCorePlugin.log(new RuntimeException("Problem while creating System libs entry", e));
			return null;
		}
	}
	
	private static List<String> addUserClasses(List<String> classPath, String location) {
		File file = new File(location);
		if (!file.exists()) {
			ForgeCorePlugin.log(new RuntimeException(location + " does not point to a correct Forge runtime."));
			return null;
		} else {
			File[] children = file.listFiles(LIB_FILTER);
			if (children.length != 1) {
				ForgeCorePlugin.log(new RuntimeException(location + " does not point to a correct Forge runtime."));
				return null;
			} else {
				for (File jarFile : children[0].listFiles(JAR_FILTER)) {
					String memento = createUserClassEntryMemento(jarFile);
					if (memento != null) {
						classPath.add(memento);
					}
				}
				return classPath;
			}
		}
	}
	
	private static String createUserClassEntryMemento(File file) {
		String result = null;
		try {
			IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(file.getAbsolutePath()));
			entry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			result = entry.getMemento();			
		} catch (CoreException e) {
			ForgeCorePlugin.log(new RuntimeException("Problem while creating classpath entry memento", e));
		}
		return result;
	}
	
	private static ILaunch launch(String name, List<String> classPath) {
		ILaunch launch = null;
		ILaunchConfigurationWorkingCopy workingCopy = createWorkingCopy(name, classPath);
		if (workingCopy != null) {
			try {
				launch = workingCopy.doSave().launch(ILaunchManager.RUN_MODE, null, false, false);
			} catch (CoreException e) {
				ForgeCorePlugin.log(new RuntimeException("Problem while launching working copy.", e));
			}
		}
		return launch;
	}
	
	private static ILaunchConfigurationWorkingCopy createWorkingCopy(String name, List<String> classPath) {
		ILaunchConfigurationWorkingCopy result = null;
		try {
			result = JAVA_LAUNCH_CONFIGURATION_TYPE.newInstance(null, name);
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.jboss.forge.shell.Bootstrap");
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classPath);
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, WORKING_DIR.getAbsolutePath());
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-Dforge.compatibility.IDE=true");
		} catch (CoreException e) {
			ForgeCorePlugin.log(new RuntimeException("Problem while creating launch configuration working copy.", e));
		}
		return result;
	}
	
	public static IProcess launch(String name, String location) {
		IProcess result = null;
		String launchConfigurationName = name + System.currentTimeMillis();
		List<String> classPath = createClassPath(location);
		if (classPath != null) {
			ILaunch launch = launch(launchConfigurationName, classPath);
			removeLaunchConfiguration(launchConfigurationName);
			if (launch != null) {
				IProcess[] processes = launch.getProcesses();
				if (processes.length == 1) {
					result = processes[0];
				}
			}
		}
		return result;
	}
	
}
