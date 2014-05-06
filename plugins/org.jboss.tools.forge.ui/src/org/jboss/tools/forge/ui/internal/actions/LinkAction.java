package org.jboss.tools.forge.ui.internal.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.part.SelectionSynchronizer;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class LinkAction extends Action {
	
	private SelectionSynchronizer selectionSynchronizer = new SelectionSynchronizer();

	public LinkAction() {
		super("", SWT.TOGGLE);
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		selectionSynchronizer.setEnabled(isChecked());
	}
	
	@Override
	public boolean isEnabled() {
		return ForgeHelper.isForgeRunning();
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/link.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
