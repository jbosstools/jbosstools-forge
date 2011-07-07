package org.jboss.tools.forge.view;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.part.Page;
import org.jboss.tools.forge.console.Console;

public class ConsolePage extends Page {
	
	private TextConsoleViewer viewer;
	private Console console;
	private IProcess process;
	
	public ConsolePage(IProcess process) {
		this.process = process;
	}

	@Override
	public void createControl(Composite parent) {
		console = new Console(process);
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
