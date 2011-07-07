package org.jboss.tools.forge.core.process;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.jboss.tools.forge.core.ForgeCorePlugin;

public abstract class ForgeAbstractRuntime implements ForgeRuntime {
	
	private static final String PROPERTY_STATE = "state";
	
	private IProcess process = null;
	private String state = STATE_NOT_RUNNING;
	private final TerminateListener terminateListener = new TerminateListener();
	
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
			streamListener = new StartupListener();
			process = ForgeLaunchHelper.launch(getName(), getLocation());
			setNewState(STATE_STARTING);
			DebugPlugin.getDefault().addDebugEventListener(terminateListener);
			process.getStreamsProxy().getOutputStreamMonitor().addListener(streamListener);
			progressMonitor.worked(1);
			while (STATE_STARTING.equals(state)) {
				if (progressMonitor.isCanceled()) {
					terminate();
				} else {
					progressMonitor.worked(1);
				}
			}
		} finally {
			if (process != null) {
				IStreamsProxy streamsProxy = process.getStreamsProxy();
				if (streamsProxy != null) {
					IStreamMonitor outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
					if (outputStreamMonitor != null) {
						outputStreamMonitor.removeListener(streamListener);
					}
				}
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
	
	private void setNewState(String newState) {
		String oldState = state;
		state = newState;
		propertyChangeSupport.firePropertyChange(PROPERTY_STATE, oldState, state);
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
			setNewState(STATE_RUNNING);
		}		
	}
	
	private class TerminateListener implements IDebugEventSetListener {
		@Override
		public void handleDebugEvents(DebugEvent[] events) {
	        for (int i = 0; i < events.length; i++) {
	            DebugEvent event = events[i];
	            if (event.getSource().equals(process)) {
	                if (event.getKind() == DebugEvent.TERMINATE) {
	                	setNewState(STATE_NOT_RUNNING);
	                	process = null;
	                	DebugPlugin.getDefault().removeDebugEventListener(terminateListener);
	                }
	            }
	        }
		}
		
	}

}
