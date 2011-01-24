package org.jboss.tools.seam.forge.runtime;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.jobs.JobManager;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.seam.forge.Activator;
import org.osgi.framework.Bundle;

public class Manager implements IDebugEventSetListener {
	
	public static final Manager INSTANCE = new Manager();
	public static final String STATE_NOT_RUNNING = "org.jboss.tools.seam.forge.notRunning";
	public static final String STATE_RUNNING = "org.jboss.tools.seam.forge.running";
	public static final String STATE_STARTING = "org.jboss.tools.seam.forge.starting";
	public static final String STATE_STOPPING = "org.jboss.tools.seam.forge.stopping";
	
	private IProcess forgeProcess = null;
	private String runtimeState = STATE_NOT_RUNNING;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private Manager() {}
	
	public boolean isForgeRunning() {
		return forgeProcess != null && !forgeProcess.isTerminated();
	}
	
	public void startForge() {
		try {
			if (!isForgeRunning()) {
				setRuntimeState(STATE_STARTING);
				ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
				ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
				ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
				for (int i = 0; i < configurations.length; i++) {
					ILaunchConfiguration configuration = configurations[i];
					if (configuration.getName().equals("Seam Forge")) {
						configuration.delete();
						break;
					}
				}
				ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "Seam Forge");
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.jboss.seam.forge.shell.Bootstrap");
				List<String> classpath = new ArrayList<String>();
				Bundle bundle = Platform.getBundle("org.jboss.tools.seam.forge");
				File file = null;
				try {
					file = FileLocator.getBundleFile(bundle);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (file == null) return;
				File[] children = file.listFiles(new FilenameFilter() {					
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith("lib");
					}
				});
				if (children.length != 1) return;
				File forgeLibDir = children[0];
				
				File[] forgeLibFiles = forgeLibDir.listFiles(new FilenameFilter() {					
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith("jar");
					}
				});
				for (File libFile: forgeLibFiles) {
					IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(libFile.getAbsolutePath()));
					entry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
					classpath.add(entry.getMemento());
				}
				IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
				IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(
						systemLibsPath, 
						IRuntimeClasspathEntry.STANDARD_CLASSES);
				classpath.add(systemLibsEntry.getMemento());
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
//				workingCopy.setAttribute(
//						IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, 
//						"-Dforge.home=/Users/koen/Downloads/forge-1.0.0.Alpha1 "); // +
//						"-Dseam.forge.shell.colorEnabled=true" );
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IPath path = root.getLocation();
				File workingDir = path.toFile();
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, workingDir.getAbsolutePath());
				ILaunchConfiguration configuration = workingCopy.doSave();
				ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE, null, false, false);
				IProcess[] processes = launch.getProcesses();
				if (processes.length == 1) {
					forgeProcess = processes[0];
				}
				DebugPlugin.getDefault().addDebugEventListener(this);
				setRuntimeState(STATE_RUNNING);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void stopForge() {
		if (isForgeRunning()) {
			setRuntimeState(STATE_STOPPING);
			try {
				forgeProcess.terminate();
			} catch (DebugException e) {
				e.printStackTrace();
			}
			setRuntimeState(STATE_NOT_RUNNING);
			DebugPlugin.getDefault().removeDebugEventListener(this);
		}
	}
	
	public void setRuntimeState(String newRuntimeState) {
		String oldRuntimeState = this.runtimeState;
		this.runtimeState = newRuntimeState;
		propertyChangeSupport.firePropertyChange(
				new PropertyChangeEvent(
						this, 
						"runtimeState", 
						oldRuntimeState, 
						newRuntimeState));
	}
	
	public String getRuntimeState() {
		return runtimeState;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener("runtimeState", listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener("runtimeState", listener);
	}

	private IStatus createStatus(String message) {
		return new Status(
			IStatus.INFO,
			Activator.getDefault().getBundle().getSymbolicName(),
			IStatus.OK,
			message,
			null);
	}
	
	private void log(String message) {
		Activator.getDefault().getLog().log(createStatus(message));
	}
	
	protected void finalize() throws Throwable {
		if (forgeProcess != null) {
			if (!forgeProcess.isTerminated()) {
				forgeProcess.terminate();
			}
			forgeProcess = null;
		}
		super.finalize();
	}
	
	public IProcess getProcess() {
		return forgeProcess;
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            DebugEvent event = events[i];
            if (event.getSource().equals(getProcess())) {
                if (event.getKind() == DebugEvent.TERMINATE) {
                	if (forgeProcess.isTerminated()) {
                		DebugPlugin.getDefault().asyncExec(new Runnable() {
							public void run() {
		                		setRuntimeState(STATE_NOT_RUNNING);
							}
						});
                	}
                    DebugPlugin.getDefault().removeDebugEventListener(this);
                }
            }
        }
	}
	
}
