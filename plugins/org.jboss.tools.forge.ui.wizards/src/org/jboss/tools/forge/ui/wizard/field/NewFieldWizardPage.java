package org.jboss.tools.forge.ui.wizard.field;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
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
import org.jboss.tools.forge.ui.wizard.dialog.JPAProjectSelectionDialog;

public class NewFieldWizardPage extends WizardPage {
	
	private NewFieldDescriptor newFieldDescriptor = new NewFieldDescriptor();
	
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
					newFieldDescriptor.project = project.getName();
					updateEntityCombo(project);
				}
			}			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}
	
	private void updateEntityCombo(IProject project) {
		entityCombo.removeAll();
		JpaProject jpaProject = (JpaProject)project.getAdapter(JpaProject.class);
		Iterator<String> iterator = jpaProject.getAnnotatedJavaSourceClassNames().iterator();
		while (iterator.hasNext()) {
			entityCombo.add(iterator.next());
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
				newFieldDescriptor.entity = entityCombo.getText();
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
				newFieldDescriptor.name = nameText.getText();
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
		for (String type : NewFieldDescriptor.TYPES) {
			typeCombo.add(type);
		}
		typeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				newFieldDescriptor.type = typeCombo.getText();
			}
		});
	}
	
	public NewFieldDescriptor getNewFieldDescriptor() {
		return newFieldDescriptor;
	}
	
}
