/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.dialog;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ProjectSelectionDialog extends ListDialog implements ISelectionChangedListener {

	Button check;

	public ProjectSelectionDialog(Shell parent) {
		super(parent);
		setTitle("Projects");
		setMessage("Select Project");
		setLabelProvider(new WorkbenchLabelProvider());
		setInput(new Object());
		setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return ResourcesPlugin.getWorkspace().getRoot().getProjects();
			}
			public void dispose() {
			}
			public void inputChanged(Viewer viewer,
					Object oldInput, Object newInput) {
			}
		});
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getOkButton().setEnabled(false);
		getTableViewer().addSelectionChangedListener(this);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		getOkButton().setEnabled(!event.getSelection().isEmpty());
	}

}