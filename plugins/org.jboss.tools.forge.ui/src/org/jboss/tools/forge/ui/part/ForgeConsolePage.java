package org.jboss.tools.forge.ui.part;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPage;
import org.jboss.tools.forge.ui.console.ForgeConsole;

public class ForgeConsolePage implements IPage {
	
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
	
	@Override
	public void dispose() {
        Control control = getControl();
        if (control != null && !control.isDisposed()) {
			control.dispose();
		}
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
		System.out.println(actionBars);
	}
}
