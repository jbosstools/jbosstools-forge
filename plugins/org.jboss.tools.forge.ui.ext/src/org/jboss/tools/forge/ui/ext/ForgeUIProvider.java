/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext;

import java.io.PrintStream;

import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.output.UIOutput;

/**
 * Eclipse implementation of {@link UIProvider}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeUIProvider implements UIProvider, UIOutput {

	@Override
	public boolean isGUI() {
		return true;
	}

	@Override
	public UIOutput getOutput() {
		return this;
	}

	@Override
	public PrintStream out() {
		// TODO: Change this
		return System.out;
	}

	@Override
	public PrintStream err() {
		// TODO: Change this
		return System.err;
	}
}
