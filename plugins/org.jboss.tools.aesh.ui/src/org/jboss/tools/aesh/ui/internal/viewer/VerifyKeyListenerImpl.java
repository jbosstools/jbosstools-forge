/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.ui.internal.viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.ui.internal.util.CharacterConstants;

public class VerifyKeyListenerImpl implements VerifyKeyListener {
	
	Console console = null;
	
	public VerifyKeyListenerImpl(Console console) {
		this.console = console;
	}

	@Override
	public void verifyKey(VerifyEvent event) {
		if ((event.stateMask & SWT.CTRL) == SWT.CTRL ) {
			if (event.keyCode == 'd') {
				console.sendInput(CharacterConstants.CTRL_D);
			} else if (event.keyCode == 'c') {
				console.sendInput(CharacterConstants.CTRL_C);
			}
		}
	}

}
