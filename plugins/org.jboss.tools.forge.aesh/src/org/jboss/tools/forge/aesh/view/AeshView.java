package org.jboss.tools.forge.aesh.view;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jboss.aesh.console.Console;

public class AeshView extends ViewPart {
	
	public static final String ID = "org.jboss.tools.forge.aesh.view";

	private AeshTextViewer textViewer;	
	
	@Override
	public void createPartControl(Composite parent) {
		textViewer = new AeshTextViewer(parent);
	}

	@Override
	public void setFocus() {
		textViewer.getControl().setFocus();
	}
	
	@Override
	public void dispose() {
		try {
			Console.getInstance().stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		textViewer.cleanup();
		super.dispose();
	}

}
