package org.jboss.tools.forge.ui.internal.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.part.SelectionSynchronizer;

public class LinkAction extends Action {
	
	private SelectionSynchronizer selectionSynchronizer = new SelectionSynchronizer();
	private ForgeRuntime runtime;
	
	public LinkAction(ForgeRuntime runtime) {
		super("", SWT.TOGGLE);
		this.runtime = runtime;
		setImageDescriptor(createImageDescriptor());
	}

	@Override
	public void run() {
		selectionSynchronizer.setEnabled(isChecked());
	}
	
	@Override
	public boolean isEnabled() {
		return ForgeRuntimeState.RUNNING.equals(runtime.getState());
	}

	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/link.gif");
		return ImageDescriptor.createFromURL(url);
	}

}
