package org.jboss.tools.forge.ui.internal.viewer;

import org.eclipse.swt.widgets.Control;
import org.jboss.tools.aesh.core.console.Console;

public interface ForgeTextViewer {
	
	Control getControl();
	void startConsole();
	void stopConsole();
	Console getConsole();

}
