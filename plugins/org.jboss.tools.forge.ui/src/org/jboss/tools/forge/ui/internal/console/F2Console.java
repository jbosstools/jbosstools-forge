/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.console;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.actions.GoToAction;
import org.jboss.tools.forge.ui.internal.actions.LinkAction;
import org.jboss.tools.forge.ui.internal.actions.StartAction;
import org.jboss.tools.forge.ui.internal.actions.StopAction;
import org.jboss.tools.forge.ui.internal.viewer.F2TextViewer;
import org.jboss.tools.forge.ui.internal.viewer.ForgeTextViewer;

public class F2Console extends AbstractForgeConsole {

	private F2TextViewer textViewer;

	public F2Console(ForgeRuntime runtime) {
		super(runtime);
	}

	@Override
	public ForgeTextViewer createTextViewer(Composite parent) {
		return textViewer = new F2TextViewer(parent);
	}

	@Override
	public IAction[] createActions() {
		return new IAction[] {
				new StartAction(getRuntime()),
				new StopAction(getRuntime()),
				new GoToAction(getRuntime()),
				new LinkAction(getRuntime())
		};
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ForgeRuntimeState.RUNNING.equals(evt.getNewValue())) {
			getTextViewer().startConsole();
		}
		if (ForgeRuntimeState.STOPPED.equals(evt.getNewValue())) {
			getTextViewer().stopConsole();
		}
	}

	@Override
	public Resource<?> getCurrentResource() {
		Resource<?> currentResource = null;
		Console console = getConsole();
		if (console != null) {
			currentResource = (Resource<?>) console.getCurrentResource();
		}
		return currentResource;
	}

	@Override
	public void goToPath(String path) {
		Console console = getConsole();
		if (console != null)
			console.sendInput("cd " + path.replaceAll(" ", "\\ ") + " \n");
	}

	private Console getConsole() {
		return (textViewer != null) ? textViewer.getConsole() : null;
	}
}
