package org.jboss.tools.forge.ui.ext.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.aesh.ui.view.AeshTextViewer;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ext.core.runtime.FurnaceRuntime;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.ext.actions.StartF2Action;
import org.jboss.tools.forge.ui.ext.actions.StopF2Action;

public class ForgeConsoleImpl implements ForgeConsole {

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
		return new IAction[] {
				new StartF2Action(),
				new StopF2Action()
		};
	}
	
	@Override
	public ForgeRuntime getRuntime() {
		return FurnaceRuntime.INSTANCE;
	}
	
}
