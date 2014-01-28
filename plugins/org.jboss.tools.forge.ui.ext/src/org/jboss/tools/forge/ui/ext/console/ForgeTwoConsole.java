package org.jboss.tools.forge.ui.ext.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.aesh.ui.view.AeshTextViewer;
import org.jboss.tools.forge.ui.console.ForgeConsole;

public class ForgeTwoConsole implements ForgeConsole {

	@Override
	public String getName() {
		return "Forge Two";
	}

	@Override
	public Control createControl(Composite parent) {
		return new AeshTextViewer(parent).getControl();
	}
	
	@Override
	public IAction[] createActions() {
		return new IAction[0];
	}
	
}
