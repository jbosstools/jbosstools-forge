/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ForgeCommandPostProcessorHelper {

	private ForgeCommandPostProcessorHelper() {
	}

	public static Map<String, String> getCommandDetails(String commandString) {
		Map<String, String> result  = new HashMap<String, String>();
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

	public static IFile getFile(String fullyQualifiedName) {
		IPath filePath = new Path(fullyQualifiedName);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile file = root.getFileForLocation(filePath);
		return file;
	}
	
	public static IProject getProject(String projectFullyQualifiedName) {
		IPath projectPath = new Path(projectFullyQualifiedName);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IContainer container = root.getContainerForLocation(projectPath);
		return container.getProject();
	}
	
	public static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchPage result = null;
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			result = workbenchWindow.getActivePage();
		}
		return result;		
	}
	
}
