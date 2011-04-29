package org.jboss.tools.seam.forge.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.part.Page;
import org.jboss.tools.seam.forge.console.Console;
import org.jboss.tools.seam.forge.launching.ForgeRuntime;

public class ConsolePage extends Page {
	
	private TextConsoleViewer viewer;
	private Console console;

	@Override
	public void createControl(Composite parent) {
		console = new Console(ForgeRuntime.INSTANCE.getProcess());
		viewer = new ConsoleViewer(parent, console);
		console.initialize();
	}

	@Override
	public Control getControl() {
		return viewer == null ? null : viewer.getControl();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public Console getConsole() {
		return console;
	}
	
}
