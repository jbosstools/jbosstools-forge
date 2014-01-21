package org.jboss.tools.forge.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.jboss.tools.forge.ui.part.F1View;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class GoToHandler extends AbstractHandler {

	public Object execute(ExecutionEvent executionEvent) {
		F1View forgeView = ForgeHelper.getForgeView();
		if (forgeView != null) {
			forgeView.goToSelection(forgeView.getSelection());
		}
		return null;		
	}
	
}