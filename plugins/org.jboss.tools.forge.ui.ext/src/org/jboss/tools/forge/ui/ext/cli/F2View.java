package org.jboss.tools.forge.ui.ext.cli;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class F2View extends ViewPart {
	
	public static final String ID = "org.jboss.tools.forge.f2view";

	private F2TextViewer textViewer;	
	
	@Override
	public void createPartControl(Composite parent) {
		F2Starter.start();
		textViewer = new F2TextViewer(parent);
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
	
	static {
		if (!Boolean.getBoolean("forge.compatibility.IDE")) {
			System.setProperty("forge.compatibility.IDE", "true");
		}
	}

}
