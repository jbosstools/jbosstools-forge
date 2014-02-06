package org.jboss.tools.forge.ext.core.runtime;

import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.tools.forge.core.io.ForgeOutputListener;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class FurnaceRuntime implements ForgeRuntime {
	
	public static final FurnaceRuntime INSTANCE = new FurnaceRuntime();
	
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
		// TODO Auto-generated method stub

	}

	@Override
	public void stop(IProgressMonitor progressMonitor) {
		// TODO Auto-generated method stub

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

	@Override
	public void addPropertyChangeListener(
			PropertyChangeListener propertyChangeListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(
			PropertyChangeListener propertyChangeListener) {
		// TODO Auto-generated method stub

	}

}
