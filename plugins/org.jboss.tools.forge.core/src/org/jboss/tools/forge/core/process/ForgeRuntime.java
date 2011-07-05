package org.jboss.tools.forge.core.process;

import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IProcess;

public interface ForgeRuntime {
	
	String STATE_NOT_RUNNING = "org.jboss.tools.forge.notRunning";
	String STATE_RUNNING = "org.jboss.tools.forge.running";
	String STATE_STARTING = "org.jboss.tools.forge.starting";

	String getName();
	String getLocation();
	String getType();
	String getState();
	IProcess getProcess();
	void start(IProgressMonitor progressMonitor);
	void stop(IProgressMonitor progressMonitor);
	void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);
	void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

}
