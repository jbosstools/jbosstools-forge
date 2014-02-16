package org.jboss.tools.forge.ui.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;

public interface ForgeConsole {
	
	String getLabel();
	Control createControl(Composite parent);
	IAction[] createActions();
	ForgeRuntime getRuntime();

}
