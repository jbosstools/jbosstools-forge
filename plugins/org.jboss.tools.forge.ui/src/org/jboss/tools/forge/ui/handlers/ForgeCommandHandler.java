package org.jboss.tools.forge.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.forge.ui.dialog.UICommandListDialog;

public class ForgeCommandHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		UICommandListDialog dialog = new UICommandListDialog(window);
		return dialog.open();
		// String message = "";
		// Set<Addon> addons =
		// ForgeService.INSTANCE.getAddonRegistry().getRegisteredAddons();
		// for (Addon addon : addons) {
		// message += addon + "\n";
		// }
		// MessageDialog.openInformation(window.getShell(), "Uiview", message);
		// return null;
	}

}
