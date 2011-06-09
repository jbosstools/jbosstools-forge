package org.jboss.tools.forge.core.process;

import java.beans.PropertyChangeSupport;

import org.eclipse.debug.core.model.IProcess;

public class ForgeRuntime {
	
	public static final String STATE_NOT_RUNNING = "org.jboss.tools.forge.notRunning";
	public static final String STATE_RUNNING = "org.jboss.tools.forge.running";
	public static final String STATE_STARTING = "org.jboss.tools.forge.starting";
	public static final String STATE_STOPPING = "org.jboss.tools.forge.stopping";
	
	private IProcess forgeProcess = null;
	private String runtimeState = STATE_NOT_RUNNING;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private String name;
	private String location;
	
	public ForgeRuntime(String name, String location) {
		this.name = name;
		this.location = location;
	}
	
	public void start() {
		if (!isTerminated()) return;
		
	}
	
	public void stop() {
		if (isTerminated()) return;
	}
	
	public boolean isTerminated() {
		return forgeProcess == null || forgeProcess.isTerminated();
	}

}
