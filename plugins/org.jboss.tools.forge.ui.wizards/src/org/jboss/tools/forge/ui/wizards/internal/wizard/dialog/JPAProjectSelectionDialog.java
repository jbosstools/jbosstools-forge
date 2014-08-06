/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.dialog;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.jboss.tools.forge.ui.wizards.internal.wizard.util.WizardsHelper;

public class JPAProjectSelectionDialog extends ListDialog implements ISelectionChangedListener {

	Button check;

	public JPAProjectSelectionDialog(Shell parent) {
		super(parent);
		setTitle("JPA Projects");
		setMessage("Select JPA Project");
		setLabelProvider(new WorkbenchLabelProvider());
		setInput(new Object());
		setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				ArrayList<IProject> jpaProjects = new ArrayList<IProject>();
				for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
						if(project.isAccessible() 
						   && (check != null && (check.getSelection())
						       || (WizardsHelper.isJPAProject(project)))) { 
							jpaProjects.add(project);
						}
				}
				return jpaProjects.toArray();
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

    protected Control createDialogArea(Composite container) {
    	Composite parent = (Composite) super.createDialogArea(container);
		check = new Button(parent, SWT.CHECK);
		check.setText("Show all projects");
		check.setSelection(false);
		check.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				TableViewer v = getTableViewer();
				v.refresh();
			}    			
		});
    	return parent;
    }
}