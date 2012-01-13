package org.jboss.tools.forge.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.jboss.tools.forge.ui.part.ForgeView;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class LinkHandler extends AbstractHandler {

	public Object execute(ExecutionEvent executionEvent) {
		Object object = executionEvent.getTrigger();
		if (object != null && object instanceof Event) {
			Widget widget = ((Event)object).widget;
			if (widget != null) {
				if (widget instanceof ToolItem) {
					ForgeView forgeView = ForgeHelper.getForgeView();
					if (forgeView != null) {
						forgeView.setSynchronized(((ToolItem)widget).getSelection());
					}
				}
			}
		}
		return null;		
	}
	
}