/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.internal.runtime;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
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
import org.jboss.tools.forge.core.internal.ForgeCorePlugin;
import org.jboss.tools.forge.core.internal.helper.ForgeHelper;
import org.jboss.tools.forge.core.internal.process.ForgeLaunchHelper;
import org.jboss.tools.forge.core.internal.process.ForgeRuntimeProcess;
import org.jboss.tools.forge.core.io.ForgeHiddenOutputFilter;
import org.jboss.tools.forge.core.io.ForgeOutputListener;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;

public abstract class ForgeAbstractRuntime implements ForgeRuntime {
	
	private IProcess process = null;
	private ForgeRuntimeState state = ForgeRuntimeState.STOPPED;
	private String version = null;
	private final TerminateListener terminateListener = new TerminateListener();	
	private MasterStreamListener masterStreamListener = new MasterStreamListener();
	private CommandResultListener commandResultListener = new CommandResultListener();
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private List<ForgeOutputListener> outputListeners = new ArrayList<ForgeOutputListener>();
	
	
	public IProcess getProcess() {
		return process;
	}
	
	public ForgeRuntimeState getState() {
		return state;
	}
	
	public String getVersion() {
		if (version == null) {
			version = initializeVersion();
		}
		return version;
	}
	
	private String initializeVersion() {
		String result = "unknown version";
		String location = getLocation();
		if (location == null) return result;
		location += "/modules/org/jboss/forge/shell/api/main";
		File file = new File(location);
		if (!file.exists()) return result;
		String[] candidates = file.list();
		for (String candidate : candidates) {
			if (candidate.startsWith("forge-shell-api-")) {
				int end = candidate.indexOf(".jar");
				if (end != -1) {
					result = candidate.substring("forge-shell-api-".length(), end);
				}
			}
		}
		return result;
	}
	
	public void start(IProgressMonitor progressMonitor) {
		ForgeHelper.sendStartEvent(this);
		errorMessage = null;
		IStreamListener startupListener = null;
		IStreamListener errorListener = null;
		if (progressMonitor == null) {
			progressMonitor = new NullProgressMonitor();
		}
		try {
			progressMonitor.beginTask("Starting Forge " + getVersion(), IProgressMonitor.UNKNOWN);
			startupListener = new StartupListener();
			process = ForgeLaunchHelper.launch(getName(), getLocation());
			if (process != null) {
				setNewState(ForgeRuntimeState.STARTING);
				DebugPlugin.getDefault().addDebugEventListener(terminateListener);
				IStreamsProxy streamsProxy = getStreamsProxy();
				if (streamsProxy != null) {
					IStreamMonitor outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
					if (outputStreamMonitor != null) {
						outputStreamMonitor.addListener(startupListener);
						outputStreamMonitor.addListener(masterStreamListener);
						outputStreamMonitor.addListener(commandResultListener);
					}
					IStreamMonitor errorStreamMonitor = streamsProxy.getErrorStreamMonitor();
					if (errorStreamMonitor != null) {
						errorStreamMonitor.addListener(masterStreamListener);
						errorListener = new ErrorListener(errorStreamMonitor);
					}
				}
			}
			progressMonitor.worked(1);
			while (ForgeRuntimeState.STARTING.equals(state)) {
				if (process.isTerminated()) {
					setNewState(ForgeRuntimeState.STOPPED);
					progressMonitor.done();
					return;
				}
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
				if (!process.isTerminated()) {
					ForgeCorePlugin.addForgeProcess(process);
				}
				IStreamsProxy streamsProxy = getStreamsProxy();
				if (streamsProxy != null) {
					IStreamMonitor outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
					if (outputStreamMonitor != null) {
						outputStreamMonitor.removeListener(startupListener);
					}
					IStreamMonitor errorStreamMonitor = streamsProxy.getErrorStreamMonitor();
					if (errorStreamMonitor != null && errorListener != null) {
						errorStreamMonitor.removeListener(errorListener);
					}
				}
			}
			progressMonitor.done();
		}
	}
	
	private boolean commandResultAvailable = false;
	private String commandResult = null;
	private Object mutex = new Object();
	private String errorMessage = null;
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String sendCommand(String str) {
//		System.out.println("sendCommand(" + str + ")");
		String result = null;
		if (process != null && !process.isTerminated()) {
			IStreamsProxy streamsProxy = getStreamsProxy();
			if (streamsProxy != null) {
				IStreamMonitor errorStreamMonitor = streamsProxy.getErrorStreamMonitor();
				errorStreamMonitor.removeListener(masterStreamListener);
				IStreamMonitor streamMonitor = streamsProxy.getOutputStreamMonitor();
				if (streamMonitor != null) {
					synchronized(mutex) {
						try {
							streamsProxy.write(Character.toString((char)31) + str + '\n');
						} catch (IOException e) {
							ForgeCorePlugin.log(e);
						}
						while (!commandResultAvailable) {
							try {
								mutex.wait();
							} catch (InterruptedException e) {}
						}
					}
					result = commandResult;
					commandResult = null;
					commandResultAvailable = false;
				}
				errorStreamMonitor.addListener(masterStreamListener);
			}
		}
//		System.out.println("ForgeAbstractRuntime.sendCommand result: " + result);
		return result;
	}
	
	public void sendInput(String str) {
		if (process != null && !process.isTerminated()) {
			IStreamsProxy streamProxy = getStreamsProxy();
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
				IStreamsProxy streamsProxy = getStreamsProxy();
				if (streamsProxy != null) {
					IStreamMonitor outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
					if (outputStreamMonitor != null) {
						outputStreamMonitor.removeListener(masterStreamListener);
					}
					IStreamMonitor errorStreamMonitor = streamsProxy.getErrorStreamMonitor();
					if (errorStreamMonitor != null) {
						errorStreamMonitor.removeListener(masterStreamListener);
					}
				}
				process.terminate();
				ForgeCorePlugin.removeForgeProcess(process);
			}
		} catch (DebugException e) {
			ForgeCorePlugin.log(e);
		}
	}
	
	private void setNewState(ForgeRuntimeState newState) {
		ForgeRuntimeState oldState = state;
		state = newState;
		propertyChangeSupport.firePropertyChange(PROPERTY_STATE, oldState, state);
	}
	
	private IStreamsProxy getStreamsProxy() {
		if (process instanceof ForgeRuntimeProcess) {
			return ((ForgeRuntimeProcess) process).getForgeStreamsProxy();
		}
		return process.getStreamsProxy();
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
			getStreamsProxy().getOutputStreamMonitor().removeListener(this);
			setNewState(ForgeRuntimeState.RUNNING);
		}		
	}
	
	private class ErrorListener implements IStreamListener {
		private IStreamMonitor fMonitor;
		public ErrorListener(IStreamMonitor monitor) {
			fMonitor = monitor;
			monitor.addListener(this);
			// make sure that output is processed if forge process dies quickly
			streamAppended(null, fMonitor);
		}
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			if (text == null) return;
			errorMessage = monitor.getContents();
			ForgeCorePlugin.logErrorMessage(errorMessage);
		}
	}
	
	private class MasterStreamListener implements IStreamListener {
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			for (ForgeOutputListener listener : outputListeners) {
				listener.outputAvailable(text);
			}
		}
	}
	
	private class CommandResultListener extends ForgeHiddenOutputFilter implements IStreamListener {
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {	
//			System.out.println("CommandResultListener.streamAppended(" + text + ")");
			outputAvailable(text);
		}
		@Override
		public void handleFilteredString(String str) {
//			System.out.println("CommandResultListener.handleFilteredString(" + str + ")");
			if (str.startsWith("RESULT: ")) {
				commandResult = str.substring(8);
				commandResultAvailable = true;
				synchronized (mutex) {
					mutex.notifyAll();
				}
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
			                	setNewState(ForgeRuntimeState.STOPPED);
			                	ForgeCorePlugin.removeForgeProcess(process);
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
