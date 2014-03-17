package org.jboss.tools.aesh.example;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.aesh.ui.view.AeshTextViewer;

public class ExampleView extends ViewPart {
	
	public static final String ID = "org.jboss.tools.forge.aesh.example";

	private AeshTextViewer textViewer;	
	
	@Override
	public void createPartControl(Composite parent) {
		textViewer = new ExampleTextViewer(parent);
		textViewer.startConsole();
	}

	@Override
	public void setFocus() {
		textViewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		textViewer.stopConsole();
		super.dispose();
	}

}
