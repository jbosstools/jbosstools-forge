package org.jboss.tools.seam.forge.view;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.part.Page;
import org.jboss.tools.seam.forge.runtime.Manager;

public class ConsolePage extends Page {
	
	private TextConsoleViewer viewer;
	private Console console;

	@Override
	public void createControl(Composite parent) {
		console = new Console(Manager.INSTANCE.getProcess(), new ConsoleColorProvider());
		viewer = new TextConsoleViewer(parent, console);
		console.initialize();
	}

	@Override
	public Control getControl() {
		return viewer == null ? null : viewer.getControl();
	}

	@Override
	public void setFocus() {
	}
	
	public Console getConsole() {
		return console;
	}
	
}
