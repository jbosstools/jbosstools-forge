package org.jboss.tools.forge.ui.internal.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.internal.viewer.ForgeTextViewer;

public interface ForgeConsole {
	
	String getLabel();
	ForgeTextViewer createTextViewer(Composite parent);
	Control createControl(Composite parent);
	IAction[] createActions();
	ForgeRuntime getRuntime();

}
