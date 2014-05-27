package org.jboss.tools.forge.ui.internal.console;

import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.internal.viewer.ForgeTextViewer;

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
	
	protected abstract ForgeTextViewer createTextViewer(Composite parent);

	
}
