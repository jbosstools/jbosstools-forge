package org.jboss.tools.forge.ui.internal.console;

import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.internal.viewer.ForgeTextViewer;

public abstract class AbstractForgeConsole 
implements ForgeConsole, PropertyChangeListener {
	
	private ForgeRuntime runtime;
	private ForgeTextViewer textViewer;
	
	public AbstractForgeConsole(ForgeRuntime runtime) {
		this.runtime = runtime;
	}
	
	protected abstract ForgeTextViewer createTextViewer(Composite parent);
	
	protected ForgeTextViewer getTextViewer() {
		return textViewer;
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
	
	@Override
	public Control createControl(Composite parent) {
		if (textViewer == null) {
			textViewer = createTextViewer(parent);
		}
		return textViewer.getControl();
	}
	
}
