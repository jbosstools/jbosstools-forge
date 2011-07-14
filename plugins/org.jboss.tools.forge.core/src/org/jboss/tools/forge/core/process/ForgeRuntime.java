package org.jboss.tools.forge.core.process;

import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IProcess;

public interface ForgeRuntime {
	
	String STATE_NOT_RUNNING = "org.jboss.tools.forge.runtime.notRunning";
	String STATE_RUNNING = "org.jboss.tools.forge.runtime.running";
	String STATE_STARTING = "org.jboss.tools.forge.runtime.starting";
	
	String PROPERTY_STATE = "org.jboss.tools.forge.runtime.state";

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
