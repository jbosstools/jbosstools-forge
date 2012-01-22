package org.jboss.tools.forge.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class StopHandler extends AbstractHandler {

	public Object execute(ExecutionEvent executionEvent) {
		ForgeHelper.stopForge();
		return null;		
	}
	
}