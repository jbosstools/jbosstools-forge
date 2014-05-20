package org.jboss.tools.forge.core.furnace;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.tools.forge.core.internal.ForgeCorePlugin;
import org.jboss.tools.forge.core.io.ForgeOutputListener;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeType;

public class FurnaceRuntime implements ForgeRuntime {
	
	public static final FurnaceRuntime INSTANCE = new FurnaceRuntime();
	private ForgeRuntimeState state = ForgeRuntimeState.STOPPED;
	private String location = null;
	private String version = null;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private FurnaceRuntime() {}

	@Override
	public String getName() {
		return getVersion() + " - " + getType().toString().toLowerCase();
	}

	@Override
	public String getLocation() {
		if (location == null) {
			initLocation();
		}
		return location;
	}

	@Override
	public ForgeRuntimeType getType() {
		return ForgeRuntimeType.EMBEDDED;
	}

	@Override
	public ForgeRuntimeState getState() {
		if (FurnaceService.INSTANCE.getContainerStatus().isStarted()) {
			return ForgeRuntimeState.RUNNING;
		} else if (FurnaceService.INSTANCE.getContainerStatus().isStarting()) {
			return ForgeRuntimeState.STARTING;
		} else if (FurnaceService.INSTANCE.getContainerStatus().isStopped()) {
			return ForgeRuntimeState.STOPPED;
		}
		return null;
	}
	
	@Override
	public String getVersion() {
		if (version == null) {
			version = initializeVersion();
		}
		return version;
	}

	@Override
	public void start(IProgressMonitor progressMonitor) {
		if (progressMonitor == null) {
			progressMonitor = new NullProgressMonitor();
		}
		try {
			String taskName = "Please wait while Forge " + getVersion() + " is started.";
			progressMonitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
			setNewState(ForgeRuntimeState.STARTING);
			FurnaceProvider.INSTANCE.startFurnace();
			progressMonitor.worked(1);
			while (FurnaceService.INSTANCE.getContainerStatus().isStarting()) {
				if (progressMonitor.isCanceled()) {
					FurnaceService.INSTANCE.stop();
					setNewState(ForgeRuntimeState.STOPPED);
				} else {
					Thread.sleep(1000);
					progressMonitor.worked(1);
				}
			}
			FurnaceService.INSTANCE.waitUntilContainerIsStarted();
			getAllCandidatesAsMap();
			setNewState(ForgeRuntimeState.RUNNING);
		} catch (InterruptedException e) {
			if (progressMonitor.isCanceled()) {
				FurnaceService.INSTANCE.stop();
				setNewState(ForgeRuntimeState.STOPPED);
			}
		}
	}

	@Override
	public void stop(IProgressMonitor progressMonitor) {
		setNewState(ForgeRuntimeState.STOPPED);
		FurnaceService.INSTANCE.stop();
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendInput(String str) {
		// TODO Auto-generated method stub

	}

	@Override
	public String sendCommand(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addOutputListener(ForgeOutputListener outputListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeOutputListener(ForgeOutputListener outputListener) {
		// TODO Auto-generated method stub

	}

	public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
	}
	
	private void setNewState(ForgeRuntimeState newState) {
		ForgeRuntimeState oldState = state;
		state = newState;
		propertyChangeSupport.firePropertyChange(PROPERTY_STATE, oldState, state);
	}
	
	private static Map<String, UICommand> getAllCandidatesAsMap() {
		Map<String, UICommand> result = new TreeMap<>();
		Imported<UICommand> instances = FurnaceService.INSTANCE
				.lookupImported(UICommand.class);
		for (@SuppressWarnings("unused")
		UICommand uiCommand : instances) {
		}
		return result;
	}

	private String initializeVersion() {
		String result = "unknown version";
		String location = getLocation();
		if (location == null) return result;
		location += "/lib";
		File file = new File(location);
		if (!file.exists()) return result;
		String[] candidates = file.list();
		for (String candidate : candidates) {
			if (candidate.startsWith("shell-api-")) {
				int end = candidate.indexOf(".jar");
				if (end != -1) {
					result = candidate.substring("shell-api-".length(), end);
				}
			}
		}
		return result;
	}
	
	private void initLocation() {
		try {
			location = FileLocator.getBundleFile(
					Platform.getBundle("org.jboss.tools.forge2.runtime"))
					.getCanonicalPath();
		} catch (IOException e) {
			ForgeCorePlugin.log(e);
		}
	}

	
}
