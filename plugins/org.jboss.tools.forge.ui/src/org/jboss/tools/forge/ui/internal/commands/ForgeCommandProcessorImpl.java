/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.jboss.tools.forge.core.io.ForgeCommandProcessor;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.part.ForgeConsoleView;


public class ForgeCommandProcessorImpl implements ForgeCommandProcessor {
	
	static Map<String, ForgeCommandPostProcessor> POST_PROCESSORS = null;
	
	private static Map<String, ForgeCommandPostProcessor> getPostProcessors() {
		if (POST_PROCESSORS == null) {
			POST_PROCESSORS = new HashMap<>();
			POST_PROCESSORS.put("new-project", new NewProjectPostProcessor()); // OK
			POST_PROCESSORS.put("persistence", new PersistencePostProcessor()); // OK
			POST_PROCESSORS.put("pick-up", new PickUpPostProcessor()); // OK
			POST_PROCESSORS.put("open", new OpenPostProcessor());
			POST_PROCESSORS.put("field", new FieldPostProcessor());
			POST_PROCESSORS.put("cd", new CdPostProcessor());
			POST_PROCESSORS.put("rm", new RmPostProcessor());
			POST_PROCESSORS.put("entity", new EntityPostProcessor());
		}
		return POST_PROCESSORS;
	}
	
	private String getCommand(String commandString) {
		String result = null;
		int i = commandString.indexOf(' ', 5);
		if (i != -1) {
			result = commandString.substring(5, i);
		}
		return result;
	}
	
	public void postProcess(final String commandString) {
		if (!commandString.startsWith(" EC: ")) return;
		String command = getCommand(commandString);
		if (command == null) return;
		if (!("rm".equals(command))) {
			refreshWorkspace();
		}
		final ForgeCommandPostProcessor postProcessor = getPostProcessors().get(command);
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if (postProcessor != null) {
					postProcessor.postProcess(getCommandDetails(commandString));
				}
				refreshProjectExplorer();
				showForgeView();
			}			
		});
	}
	
	private Map<String, String> getCommandDetails(String commandString) {
		Map<String, String> result  = new HashMap<>();
		int ec = commandString.indexOf(" EC: ");
		int crn = commandString.indexOf(" CRN: ");
		int crt = commandString.indexOf(" CRT: ");
		int cpn = commandString.indexOf(" CPN: ");
		int par = commandString.indexOf(" PAR: ");
		result.put("ec", commandString.substring(ec + 5, crn));
		result.put("crn", commandString.substring(crn + 6, crt));
		result.put("crt", commandString.substring(crt + 6, cpn));
		result.put("cpn", commandString.substring(cpn + 6, par));
		result.put("par", commandString.substring(par + 6));		
		return result;
	}

	private void refreshWorkspace() {
		try {
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private void showForgeView() {		
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
			if (workbenchPage != null) {
				try {
					workbenchPage.showView((ForgeConsoleView.ID));
				} catch (PartInitException e) {
					ForgeUIPlugin.log(e);
				}
			}
		}
	}
	
	private void refreshProjectExplorer() {
		IWorkbenchPage workbenchPage = ForgeCommandPostProcessorHelper.getActiveWorkbenchPage();
		if (workbenchPage != null) {
			IViewPart projectExplorer = workbenchPage.findView("org.eclipse.ui.navigator.ProjectExplorer");
			if (projectExplorer != null && projectExplorer instanceof CommonNavigator) {
				((CommonNavigator)projectExplorer).getCommonViewer().refresh();
			} 
		}
	}
	
}
