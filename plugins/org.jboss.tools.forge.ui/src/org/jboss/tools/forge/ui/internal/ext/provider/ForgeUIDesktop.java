/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.provider;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.ide.IDE;
import org.jboss.forge.addon.ui.DefaultUIDesktop;
import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;

/**
 * Implementation of the {@link UIDesktop} interface
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeUIDesktop extends DefaultUIDesktop {

	@Override
	public void open(File file) throws IOException {
		// Open should do the same as edit in Eclipse
		edit(file);
	}

	@Override
	public void edit(File file) throws IOException {
		final IFileStore fileStore = EFS.getLocalFileSystem().getStore(
				file.toURI());
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null) {
			window = workbench.getWorkbenchWindows()[0];
		}
		final IWorkbenchPage page = window.getActivePage();
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IDE.openEditorOnFileStore(page, fileStore);
				} catch (PartInitException e) {
					Status status = new Status(IStatus.ERROR,
							ForgeUIPlugin.PLUGIN_ID, "Edit File action failed");
					ForgeUIPlugin.log(e);
					MessageDialog.openError(null, "Edit File",
							status.getMessage());
				}
			}
		});
	}

	@Override
	public void browse(URI uri) throws IOException {
		IWorkbenchBrowserSupport support = PlatformUI.getWorkbench()
				.getBrowserSupport();
		try {
			IWebBrowser browser = support
					.createBrowser(ForgeUIPlugin.PLUGIN_ID);
			browser.openURL(uri.toURL());
		} catch (PartInitException e) {
			Status status = new Status(IStatus.ERROR, ForgeUIPlugin.PLUGIN_ID,
					"Browser initialization failed");
			ForgeUIPlugin.log(e);
			MessageDialog.openError(null, "Browse URL", status.getMessage());
		}
	}
}
