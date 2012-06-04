package org.jboss.tools.forge.core.process;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.jboss.tools.forge.core.ForgeCorePlugin;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;


public class ForgeLaunchHelper {
	
	private static final ILaunchManager LAUNCH_MANAGER = DebugPlugin.getDefault().getLaunchManager();
	
	private static final ILaunchConfigurationType JAVA_LAUNCH_CONFIGURATION_TYPE = 
			LAUNCH_MANAGER.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
	
	private static final File WORKING_DIR = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
	
	private static void removeLaunchConfiguration(String name) {
		try {
			ILaunchConfiguration[] configurations = LAUNCH_MANAGER.getLaunchConfigurations(JAVA_LAUNCH_CONFIGURATION_TYPE);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration != null && configuration.exists()) {
					String configName = configuration.getName();
					if (configName.startsWith(name)) {
						configuration.delete();
						break;
					}
				}
			}
		} catch (CoreException e) {
			ForgeCorePlugin.log(new RuntimeException("CoreException while cleaning up launch configuration", e));
		}
	}
		
	public static IProcess launch(String name, String location) {
		IProcess result = null;
		String launchConfigurationName = name + System.currentTimeMillis();
		ILaunch launch = doLaunch(launchConfigurationName, location);
		if (launch != null) {
			IProcess[] processes = launch.getProcesses();
			if (processes.length == 1) {
				result = processes[0];
			}
		}
		return result;
	}
	
	private static ILaunch doLaunch(String launchConfigurationName, String location) {
		ILaunch launch = null;
		ILaunchConfigurationWorkingCopy workingCopy = createWorkingCopy(launchConfigurationName, location);
		if (workingCopy != null) {
			try {
				LAUNCH_MANAGER.addLaunchListener(new ForgeLaunchListener(launchConfigurationName));
				launch = workingCopy.doSave().launch(getLaunchMode(), null, false, true);
			} catch (CoreException e) {
				ForgeCorePlugin.log(new RuntimeException("Problem while launching working copy.", e));
			}
		}
		return launch;
	}
	
	private static String getLaunchMode() {
		return ForgeRuntimesPreferences.INSTANCE.getStartInDebug() ? 
				ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE;
	}
	
	private static ILaunchConfigurationWorkingCopy createWorkingCopy(String name, String location) {
		ILaunchConfigurationWorkingCopy result = null;
		try {
			String launchConfigurationName = name + System.currentTimeMillis();
			result = JAVA_LAUNCH_CONFIGURATION_TYPE.newInstance(null, launchConfigurationName);
			result.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, IForgeLaunchConfiguration.ID_FORGE_PROCESS_FACTORY);
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.jboss.modules.Main");
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, WORKING_DIR.getAbsolutePath());
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, createVmArguments(location));
			result.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, createProgramArguments(location));
		} catch (CoreException e) {
			ForgeCorePlugin.log(new RuntimeException("Problem while creating launch configuration working copy.", e));
		}
		return result;
	}
	
	private static String createProgramArguments(String location) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createJBossModulesPathArgument(location)).append(' ');
		buffer.append("org.jboss.forge");
		return buffer.toString();
	}
	
	private static String createJBossModulesPathArgument(String location) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getMainModulesLocation(location)).append(File.pathSeparator);
		buffer.append(getUserModulesLocation()).append(File.pathSeparator);
		buffer.append(getExtraModulesLocation());
		return "-modulepath " + encloseWithDoubleQuotesIfNeeded(buffer.toString());
	}
	
	private static String encloseWithDoubleQuotesIfNeeded(String str) {
		if (str.contains(" ")) { 
			return "\"" + str + "\"";
		} else {
			return str;
		}
	}
	
	private static String getMainModulesLocation(String location) {
		return  location + "/modules";
	}
	
	private static String getUserModulesLocation() {
		return System.getProperty("user.home") + "/.forge/plugins";
	}
	
	private static String getExtraModulesLocation() {
		String result = "";
		try {
			result = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime.ext")).getAbsolutePath() + "/modules";
		} catch (IOException e) {
			ForgeCorePlugin.log(new RuntimeException("Problem while obtaining location of extra runtime classes.", e));
		}
		return result;
	}
	
	private static String createVmArguments(String location) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("-Dforge.home=").append(encloseWithDoubleQuotesIfNeeded(location)).append(' ');
		buffer.append("-Dforge.shell.colorEnabled=true").append(' ');
		buffer.append("-Dforge.compatibility.IDE=true").append(' ');
		buffer.append(getClassPathArgument(location)).append(' ');
		buffer.append("-Dforge.workspace=").append(getWorkspaceLocation());
		return buffer.toString();
	}
	
	private static String getWorkspaceLocation() {
		return encloseWithDoubleQuotesIfNeeded(
				ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
	}
	
	private static String getClassPathArgument(String location) {
		return "-cp " + encloseWithDoubleQuotesIfNeeded(location + File.separator + "jboss-modules.jar");
	}
	
	static class ForgeLaunchListener implements ILaunchListener {
		String launchConfigurationName;

		public ForgeLaunchListener(String launchConfigruationName) {
			this.launchConfigurationName = launchConfigruationName;
		}

		@Override
		public void launchAdded(ILaunch launch) {
		}

		@Override
		public void launchChanged(ILaunch launch) {
		}

		@Override
		public void launchRemoved(ILaunch launch) {
			if (launch.getLaunchConfiguration().getName().startsWith(launchConfigurationName)) {
				ForgeLaunchHelper.removeLaunchConfiguration(launchConfigurationName);
				LAUNCH_MANAGER.removeLaunchListener(this);
			}
		}
	}
}
