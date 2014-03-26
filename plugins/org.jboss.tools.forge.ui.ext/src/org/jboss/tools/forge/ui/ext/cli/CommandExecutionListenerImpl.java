package org.jboss.tools.forge.ui.ext.cli;

import java.util.Iterator;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.result.Result;

public class CommandExecutionListenerImpl extends AbstractCommandExecutionListener {

	@Override
	public void postCommandExecuted(
			UICommand command, 
			UIExecutionContext uiExecutionContext,
			Result result) {
		UISelection<?> selection = uiExecutionContext.getUIContext().getInitialSelection();
		Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object instanceof Resource<?>) {
				refresh((Resource<?>)object);
			}
		}
		Object object = uiExecutionContext.getUIContext().getSelection();
		if (object != null) {
			if (object instanceof Resource<?>) {
				select((Resource<?>)object);
			}
		}
	}
	
	private void refresh(Resource<?> resource) {
		System.out.println("refreshing initial selection: " + resource.getFullyQualifiedName());
	}
	
	private void select(Resource<?> resource) {
		System.out.println("highlighting final selection: " + resource.getFullyQualifiedName());
	}

}
