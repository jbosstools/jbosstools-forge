package org.jboss.tools.forge.ui.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.ui.part.ForgeTextViewer;

public class ForgeOneConsole implements ForgeConsole {

	@Override
	public String getName() {
		return "Forge One";
	}

	@Override
	public Control createControl(Composite parent) {
		return new ForgeTextViewer(parent).getControl();
	}
	
	@Override
	public IAction[] createActions() {
		return new IAction[0];
	}
	
}
