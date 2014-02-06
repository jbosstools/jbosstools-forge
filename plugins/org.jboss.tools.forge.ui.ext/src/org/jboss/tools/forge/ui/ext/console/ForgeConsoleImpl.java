package org.jboss.tools.forge.ui.ext.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ext.core.runtime.FurnaceRuntime;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.ext.actions.StartF2Action;
import org.jboss.tools.forge.ui.ext.actions.StopF2Action;
import org.jboss.tools.forge.ui.ext.cli.F2TextViewer;

public class ForgeConsoleImpl implements ForgeConsole {
	
	private String label = null;
	
	public ForgeConsoleImpl() {
		label = "Forge " + getRuntime().getVersion() + " - " + getRuntime().getType();		
	}

	@Override
	public Control createControl(Composite parent) {
		return new F2TextViewer(parent).getControl();
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
	
	@Override
	public String getLabel() {
		return label;
	}
	
}
