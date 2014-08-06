/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
