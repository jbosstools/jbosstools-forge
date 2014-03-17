package org.jboss.tools.forge.ui.ext.cli;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.ui.view.AeshTextViewer;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ext.core.runtime.FurnaceRuntime;

public class F2TextViewer extends AeshTextViewer {
	
    public F2TextViewer(Composite parent) {
		super(parent);
	}

	protected Console createConsole() {
    	return new F2Console();
    }
	
	protected void initialize() {
		super.initialize();
		if (ForgeRuntimeState.RUNNING.equals(FurnaceRuntime.INSTANCE.getState())) {
			startConsole();
		}
	}
    
    public void startConsole() {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				F2TextViewer.super.startConsole();
			}   		
    	});
    }
    
    public void stopConsole() {
    	Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
		    	F2TextViewer.super.stopConsole();
			}    		
    	});
    }
    
    
}
    
