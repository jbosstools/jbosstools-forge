package org.jboss.tools.forge.core.process;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.jboss.tools.forge.core.ForgeCorePlugin;

public abstract class ForgeAbstractRuntime implements ForgeRuntime {
	
	private static final String PROPERTY_STATE = "state";
	
	private IProcess process = null;
	private String state = STATE_NOT_RUNNING;
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public IProcess getProcess() {
		return process;
	}
	
	public String getState() {
		return state;
	}
	
	public void start(IProgressMonitor progressMonitor) {
		IStreamListener streamListener = null;
		if (progressMonitor == null) {
			progressMonitor = new NullProgressMonitor();
		}
		try {
			progressMonitor.beginTask("Starting Forge", IProgressMonitor.UNKNOWN);
			state = STATE_STARTING;
			propertyChangeSupport.firePropertyChange(PROPERTY_STATE, STATE_NOT_RUNNING, STATE_STARTING);
			streamListener = new StartupListener();
			process = ForgeLaunchHelper.launch(getName(), getLocation());
			process.getStreamsProxy().getOutputStreamMonitor().addListener(streamListener);
			progressMonitor.worked(1);
			while (STATE_STARTING.equals(state)) {
				if (progressMonitor.isCanceled()) {
					terminate();
					state = STATE_NOT_RUNNING;
					propertyChangeSupport.firePropertyChange(PROPERTY_STATE, STATE_STARTING, STATE_NOT_RUNNING);
				} else {
					progressMonitor.worked(1);
				}
			}
		} finally {
			process.getStreamsProxy().getOutputStreamMonitor().removeListener(streamListener);
			if (STATE_NOT_RUNNING.equals(state)) {
				process = null;
			}
			progressMonitor.done();
		}
	}
	
	public void stop(IProgressMonitor progressMonitor) {
		if (progressMonitor == null) {
			progressMonitor = new NullProgressMonitor();
		}
		try {
			progressMonitor.beginTask("Stopping Forge", 1);
			terminate();
			process = null;
			state = STATE_NOT_RUNNING;
			propertyChangeSupport.firePropertyChange(PROPERTY_STATE, STATE_RUNNING, STATE_NOT_RUNNING);
		} finally {
			progressMonitor.done();
		}
	}
	
	private void terminate() {
		try {
			process.terminate();
		} catch (DebugException e) {
			ForgeCorePlugin.log(e);
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.removePropertyChangeListener(propertyChangeListener);
	}
	
	private class StartupListener implements IStreamListener {
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			state = STATE_RUNNING;
			propertyChangeSupport.firePropertyChange(PROPERTY_STATE, STATE_STARTING, STATE_RUNNING);
		}		
	}

}
