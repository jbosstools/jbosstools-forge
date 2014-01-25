package org.jboss.tools.forge.ui.console;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface ForgeConsole {
	
	String getName();
	Control createControl(Composite parent);

}
