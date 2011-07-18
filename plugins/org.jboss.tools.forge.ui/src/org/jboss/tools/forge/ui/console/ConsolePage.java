package org.jboss.tools.forge.ui.console;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.part.Page;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class ConsolePage extends Page {
	
	private TextConsoleViewer viewer;
	private ForgeRuntime runtime;
	
	public ConsolePage(ForgeRuntime runtime) {
		this.runtime = runtime;
	}

	@Override
	public void createControl(Composite parent) {
		viewer = new ConsoleViewer(parent, runtime);
	}

	@Override
	public Control getControl() {
		return viewer == null ? null : viewer.getControl();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
}
