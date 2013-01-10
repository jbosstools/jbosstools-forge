package org.jboss.tools.forge.ui.handlers;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.forge.container.Addon;
import org.jboss.tools.forge.core.ForgeService;

public class ForgeCommandHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		String message = "";
		Set<Addon> addons = ForgeService.INSTANCE.getAddonRegistry().getRegisteredAddons();
		for (Addon addon : addons) {
			message += addon + "\n";
		}
		MessageDialog.openInformation(window.getShell(), "Uiview", message);
		return null;
	}
	
}
