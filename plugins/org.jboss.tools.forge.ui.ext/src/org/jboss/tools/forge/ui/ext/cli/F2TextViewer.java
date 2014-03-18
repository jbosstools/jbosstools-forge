package org.jboss.tools.forge.ui.ext.cli;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.ui.view.AeshTextViewer;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ext.core.runtime.FurnaceRuntime;

public class F2TextViewer extends AeshTextViewer {
	
    public F2TextViewer(Composite parent) {
		super(parent);
		if (ForgeRuntimeState.RUNNING.equals(FurnaceRuntime.INSTANCE.getState())) {
			startConsole();
		}
	}

	protected Console createConsole() {
    	return new F2Console();
    }
	
}
    
