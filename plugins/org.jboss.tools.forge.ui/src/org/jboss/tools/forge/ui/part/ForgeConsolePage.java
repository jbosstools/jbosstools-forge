package org.jboss.tools.forge.ui.part;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;
import org.jboss.tools.forge.ui.console.ForgeConsole;

public class ForgeConsolePage extends Page {
	
	private ForgeConsole forgeConsole = null;
	private Control control = null;
	
	public ForgeConsolePage(ForgeConsole forgeConsole) {
		this.forgeConsole = forgeConsole;
	}

	@Override
	public void createControl(Composite parent) {
		control = forgeConsole.createControl(parent);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setFocus() {
		if (control != null) {
			control.setFocus();
		}
	}
	
}
