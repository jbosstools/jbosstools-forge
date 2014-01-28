package org.jboss.tools.forge.ui.console.f1;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.ui.actions.f1.GoToAction;
import org.jboss.tools.forge.ui.actions.f1.LinkAction;
import org.jboss.tools.forge.ui.actions.f1.StartF1Action;
import org.jboss.tools.forge.ui.actions.f1.StopF1Action;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.part.ForgeTextViewer;

public class ForgeConsoleImpl implements ForgeConsole {

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
		return new IAction[] { 
				new StartF1Action(),
				new StopF1Action(),
				new GoToAction(),
				new LinkAction()
		};
	}
	
}
