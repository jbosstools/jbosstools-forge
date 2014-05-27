package org.jboss.tools.forge.ui.internal.viewer;

import org.eclipse.swt.widgets.Control;

public interface ForgeTextViewer {
	
	Control getControl();
	void startConsole();
	void stopConsole();

}
