/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.field;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jpt.common.core.resource.java.JavaResourceAbstractType;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.forge.ui.wizards.internal.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizards.internal.wizard.dialog.JPAProjectSelectionDialog;

public class NewFieldWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "NewFieldWizardPage.projectName";
	final static String ENTITY_NAME = "NewFieldWizardPage.entityName";
	final static String FIELD_NAME = "NewFieldWizardPage.fieldName";
	final static String FIELD_TYPE = "NewFieldWizardPage.fieldType";
	
	private static final String[]  FIELD_TYPES = {
		"string",
		"int",
		"long",
		"number",
		"boolean",
		"temporal",
		"oneToOne",
		"oneToMany",
		"manyToOne",
		"manyToMany",
		"custom"
	};
	
	private Combo entityCombo;

	protected NewFieldWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.field.new", "Create New Field", null);
	}

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createEntityEditor(control);
		createNameEditor(control);
		createTypeEditor(control);
		setControl(control);
	}
	
	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Project: ");
		final Text projectNameText = new Text(parent, SWT.BORDER);
		projectNameText.setText("");
		projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Button projectSearchButton = new Button(parent, SWT.NONE);
		projectSearchButton.setText("Search...");
		projectSearchButton.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				JPAProjectSelectionDialog dialog = new JPAProjectSelectionDialog(getShell());
				if (dialog.open() != SWT.CANCEL) {
					IProject project = (IProject)dialog.getResult()[0];
					projectNameText.setText(project.getName());
					getWizardDescriptor().put(PROJECT_NAME, project.getName());
					refreshEntityEditor(project);
				}
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}
	
	private void refreshEntityEditor(IProject project) {
		entityCombo.removeAll();
		JpaProject jpaProject = (JpaProject)project.getAdapter(JpaProject.class);
		Iterable<JavaResourceAbstractType> iterable = jpaProject.getAnnotatedJavaSourceTypes();
		Iterator<JavaResourceAbstractType> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			JavaResourceAbstractType jrat = iterator.next();
			String qualifiedName = jrat.getTypeBinding().getQualifiedName();
			entityCombo.add(qualifiedName);
		}
	}
	
	private void createEntityEditor(Composite parent) {
		Label entityLabel = new Label(parent, SWT.NONE);
		entityLabel.setText("Entity: ");
		entityCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		entityCombo.setLayoutData(gridData);
		entityCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizardDescriptor().put(ENTITY_NAME, entityCombo.getText());
			}
		});
	}
		
	private void createNameEditor(Composite parent) {
		Label nameLabel = new Label(parent, SWT.NONE);
		nameLabel.setText("Field name: ");
		final Text nameText = new Text(parent, SWT.BORDER);
		nameText.setText("");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		nameText.setLayoutData(gridData);
		nameText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				getWizardDescriptor().put(FIELD_NAME, nameText.getText());
			}
		});
	}
	
	private void createTypeEditor(Composite parent) {
		Label typeLabel = new Label(parent, SWT.NONE);
		typeLabel.setText("Field type: ");
		final Combo typeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		typeCombo.setLayoutData(gridData);
		for (String type : FIELD_TYPES) {
			typeCombo.add(type);
		}
		typeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizardDescriptor().put(FIELD_TYPE, typeCombo.getText());
			}
		});
	}
	
}
