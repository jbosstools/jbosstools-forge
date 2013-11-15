package org.jboss.tools.aesh.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

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
		textViewer.cleanup();
		super.dispose();
	}

}
