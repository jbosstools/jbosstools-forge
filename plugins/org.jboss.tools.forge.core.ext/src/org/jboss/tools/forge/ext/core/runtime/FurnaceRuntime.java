package org.jboss.tools.forge.ext.core.runtime;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.tools.forge.core.io.ForgeOutputListener;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ext.core.ForgeCorePlugin;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class FurnaceRuntime implements ForgeRuntime {
	
	public static final FurnaceRuntime INSTANCE = new FurnaceRuntime();
	private String state = STATE_NOT_RUNNING;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private FurnaceRuntime() {}

	@Override
	public String getName() {
		return FurnaceService.INSTANCE.name();
	}

	@Override
	public String getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return "embedded";
	}

	@Override
	public String getState() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getVersion() {
		return "2.0.0.Final";
	}

	@Override
	public void start(IProgressMonitor progressMonitor) {
		if (progressMonitor == null) {
			progressMonitor = new NullProgressMonitor();
		}
		try {
			progressMonitor.beginTask("Starting Forge " + getVersion(), IProgressMonitor.UNKNOWN);
			setNewState(STATE_STARTING);
			ForgeCorePlugin.getDefault().startFurnace();
			progressMonitor.worked(1);
			while (FurnaceService.INSTANCE.getContainerStatus().isStarting()) {
				if (progressMonitor.isCanceled()) {
					ForgeCorePlugin.getDefault().stopFurnace();
					setNewState(STATE_NOT_RUNNING);
				} else {
					Thread.sleep(1000);
					progressMonitor.worked(1);
				}
			}
			FurnaceService.INSTANCE.waitUntilContainerIsStarted();
			getAllCandidatesAsMap();
			setNewState(STATE_RUNNING);
		} catch (InterruptedException e) {
			if (progressMonitor.isCanceled()) {
				ForgeCorePlugin.getDefault().stopFurnace();
				setNewState(STATE_NOT_RUNNING);
			}
		}
	}

	@Override
	public void stop(IProgressMonitor progressMonitor) {
		ForgeCorePlugin.getDefault().stopFurnace();
		setNewState(STATE_NOT_RUNNING);
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
	
	private void setNewState(String newState) {
		String oldState = state;
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

}
