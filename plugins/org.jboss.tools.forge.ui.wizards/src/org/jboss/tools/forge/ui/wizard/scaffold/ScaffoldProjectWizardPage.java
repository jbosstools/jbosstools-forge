package org.jboss.tools.forge.ui.wizard.scaffold;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class ScaffoldProjectWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "ScaffoldProjectWizardPage.projectName";
	final static String SCAFFOLD_TYPE = "ScaffoldProjectWizardPage.scaffoldType";
	
	final static String SCAFFOLD_TYPE_FACES = "faces";
	final static String SCAFFOLD_TYPE_ANGULARJS = "angularjs";
	
	private Combo projectNameCombo;
	private Combo scaffoldTypeCombo;
	
	protected ScaffoldProjectWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.scaffold.project", "Select Project", null);
	}	

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(2, false));
		createProjectEditor(control);
		createScaffoldTypeEditor(control);
		setControl(control);
	}
	
	private void createScaffoldTypeEditor(Composite parent) {
		Label scaffoldTypeLabel = new Label(parent, SWT.NONE);
		scaffoldTypeLabel.setText("Scaffold Type: ");
		scaffoldTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		scaffoldTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		scaffoldTypeCombo.add(SCAFFOLD_TYPE_FACES);
		scaffoldTypeCombo.add(SCAFFOLD_TYPE_ANGULARJS);
		String scaffoldType = (String)getWizardDescriptor().get(SCAFFOLD_TYPE);
		if (scaffoldType != null) {
			scaffoldTypeCombo.setText(scaffoldType);
			handleScaffoldTypeChange();
		}
		scaffoldTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String newScaffoldType = scaffoldTypeCombo.getText();
				String oldScaffoldType = (String)getWizardDescriptor().get(SCAFFOLD_TYPE);
				if ((oldScaffoldType == null && newScaffoldType != null) || !oldScaffoldType.equals(newScaffoldType)) {
					handleScaffoldTypeChange();
				}
			}
		});
	}
	
	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Project: ");
		projectNameCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		projectNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : allProjects) {
			if (WizardsHelper.isJPAProject(project)) {
				projectNameCombo.add(project.getName());
			}
		}
		String projectName = (String)getWizardDescriptor().get(PROJECT_NAME); 
		if (projectName != null) {
			projectNameCombo.setText(projectName);
			handleProjectChange();
		}
		projectNameCombo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String newProjectName = projectNameCombo.getText();
				String oldProjectName = (String)getWizardDescriptor().get(PROJECT_NAME);
				if ((oldProjectName == null && newProjectName != null) || !oldProjectName.equals(newProjectName)) {
					handleProjectChange();
				}
			}			
		});
	}
	
	private void handleScaffoldTypeChange() {
		getWizardDescriptor().put(SCAFFOLD_TYPE, scaffoldTypeCombo.getText());
		((ScaffoldWizard)getWizard()).handleScaffoldTypeChange();
	}
	
	private void handleProjectChange() {
		getWizardDescriptor().put(PROJECT_NAME, projectNameCombo.getText());
		((ScaffoldWizard)getWizard()).handleProjectChange();
	}
	
	public boolean isPageComplete() {
		String projectName = projectNameCombo.getText();
		boolean busy = ((ScaffoldWizard)getWizard()).isBusy();
		boolean nameComplete = projectName != null && !"".equals(projectName);
		return nameComplete && !busy;
	}
	
	void updatePageComplete() {
		setPageComplete(isPageComplete());
	}
	
}
