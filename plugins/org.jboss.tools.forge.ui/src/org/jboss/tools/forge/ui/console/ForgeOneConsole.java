package org.jboss.tools.forge.ui.console;

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
	
}
