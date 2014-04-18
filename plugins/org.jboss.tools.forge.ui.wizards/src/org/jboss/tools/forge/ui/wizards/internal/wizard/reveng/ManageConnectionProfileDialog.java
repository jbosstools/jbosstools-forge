package org.jboss.tools.forge.ui.wizards.internal.wizard.reveng;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;

public class ManageConnectionProfileDialog extends Dialog {

	protected ManageConnectionProfileDialog(Shell parentShell) {
		super(parentShell);
	}
	
	ConnectionProfileDescriptor[] getConnectionProfiles() {
		return new ConnectionProfileDescriptor[0];
	}

}
