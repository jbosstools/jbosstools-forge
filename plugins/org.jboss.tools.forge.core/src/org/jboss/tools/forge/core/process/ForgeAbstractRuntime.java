package org.jboss.tools.forge.core.process;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.jboss.tools.forge.core.io.ForgeOutputListener;

public abstract class ForgeAbstractRuntime implements ForgeRuntime {
	
	private IProcess process = null;
	private String state = STATE_NOT_RUNNING;	
	private final TerminateListener terminateListener = new TerminateListener();	
	private MasterOutputListener masterOutputListener = new MasterOutputListener();
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private List<ForgeOutputListener> outputListeners = new ArrayList<ForgeOutputListener>();
	
	
	public IProcess getProcess() {
		return process;
	}
	
	public String getState() {
		return state;
	}
	
	public void start(IProgressMonitor progressMonitor) {
		IStreamListener startupListener = null;
		if (progressMonitor == null) {
			progressMonitor = new NullProgressMonitor();
		}
		try {
			progressMonitor.beginTask("Starting Forge", IProgressMonitor.UNKNOWN);
			startupListener = new StartupListener();
			process = ForgeLaunchHelper.launch(getName(), getLocation());
			if (process != null) {
				setNewState(STATE_STARTING);
				DebugPlugin.getDefault().addDebugEventListener(terminateListener);
				IStreamsProxy streamsProxy = process.getStreamsProxy();
				if (streamsProxy != null) {
					IStreamMonitor streamMonitor = streamsProxy.getOutputStreamMonitor();
					if (streamMonitor != null) {
						streamMonitor.addListener(startupListener);
						streamMonitor.addListener(masterOutputListener);
					}
				}
			}
			progressMonitor.worked(1);
			while (STATE_STARTING.equals(state)) {
				if (progressMonitor.isCanceled()) {
					terminate();
				} else {
					Thread.sleep(1000);
					progressMonitor.worked(1);
				}
			}
		} catch (InterruptedException e) {
			if (progressMonitor.isCanceled()) {
				terminate();
			}
		} finally {
			if (process != null) {
				IStreamsProxy streamsProxy = process.getStreamsProxy();
				if (streamsProxy != null) {
					IStreamMonitor outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
					if (outputStreamMonitor != null) {
						outputStreamMonitor.removeListener(startupListener);
					}
				}
			}
			progressMonitor.done();
		}
	}
	
	public void sendInput(String str) {
		if (process != null && !process.isTerminated()) {
			IStreamsProxy streamProxy = process.getStreamsProxy();
			if (streamProxy != null) {
				try {
					streamProxy.write(str);
				} catch (IOException e) {
					ForgeCorePlugin.log(e);
				}
			}
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
			if (process != null) {
				IStreamsProxy streamsProxy = process.getStreamsProxy();
				if (streamsProxy != null) {
					IStreamMonitor streamMonitor = streamsProxy.getOutputStreamMonitor();
					if (streamMonitor != null) {
						streamMonitor.removeListener(masterOutputListener);
					}
				}
				process.terminate();
			}
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
	
	public void addOutputListener(ForgeOutputListener listener) {
		outputListeners.add(listener);
	}
	
	public void removeOutputListener(ForgeOutputListener listener) {
		outputListeners.remove(listener);
	}
	
	private class StartupListener implements IStreamListener {
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			process.getStreamsProxy().getOutputStreamMonitor().removeListener(this);
			setNewState(STATE_RUNNING);
		}		
	}
	
	private class MasterOutputListener implements IStreamListener {
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			for (ForgeOutputListener listener : outputListeners) {
				listener.outputAvailable(text);
			}
		}
	}
	
	private class TerminateListener implements IDebugEventSetListener {
		@Override
		public void handleDebugEvents(DebugEvent[] events) {
	        for (int i = 0; i < events.length; i++) {
	            DebugEvent event = events[i];
	            if (event.getSource().equals(process)) {
	                if (event.getKind() == DebugEvent.TERMINATE) {
	                	DebugPlugin.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
			                	setNewState(STATE_NOT_RUNNING);
			                	process = null;
			                	DebugPlugin.getDefault().removeDebugEventListener(terminateListener);
							}	                		
	                	});
	                }
	            }
	        }
		}
		
	}

}
