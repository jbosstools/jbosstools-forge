package org.jboss.tools.forge.ui.internal.console;

import java.beans.PropertyChangeListener;

import org.jboss.tools.forge.core.runtime.ForgeRuntime;

public abstract class AbstractForgeConsole 
implements ForgeConsole, PropertyChangeListener {
	
	private ForgeRuntime runtime;
	
	public AbstractForgeConsole(ForgeRuntime runtime) {
		this.runtime = runtime;
	}
	
	public ForgeRuntime getRuntime() {
		return runtime;
	}

	public String getLabel() {
		return "Forge " + 
				getRuntime().getVersion() + 
				" - " + 
				getRuntime().getType().name().toLowerCase();
	}

}
