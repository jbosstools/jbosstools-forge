package org.jboss.tools.forge.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ForgeEmptyPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	public ForgeEmptyPreferencePage() {
		super();
		setDescription("Expand the tree to edit preferences for a specific feature.");
	}

	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		return new Composite(parent, SWT.NULL);
	}

	public void init(IWorkbench workbench) {
	}

}
