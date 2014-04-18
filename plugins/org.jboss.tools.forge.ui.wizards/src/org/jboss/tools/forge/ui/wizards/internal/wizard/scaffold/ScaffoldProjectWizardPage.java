package org.jboss.tools.forge.ui.wizards.internal.wizard.scaffold;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jpt.common.core.resource.java.JavaResourceAbstractType;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jboss.tools.forge.ui.wizards.internal.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizards.internal.wizard.util.WizardsHelper;

public class ScaffoldProjectWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "ScaffoldProjectWizardPage.projectName";
	final static String SCAFFOLD_TYPE = "ScaffoldProjectWizardPage.scaffoldType";
	final static String ENTITY_NAMES = "ScaffoldProjectWizardPage.entityNames";
	final static String OVERWRITE_EXISTING = "ScaffoldProjectWizardPage.overwriteExisting";
	final static String FORCE_SETUP = "ScaffoldProjectWizardPage.forceSetup";
	
	final static String SCAFFOLD_TYPE_FACES = "faces";
	final static String SCAFFOLD_TYPE_ANGULARJS = "angularjs";
	
	private Combo projectNameCombo;
	private Combo scaffoldTypeCombo;
	private Button overwriteButton;
	private Button forceSetupButton;
	private Table selectEntitiesTable;
	private Label selectEntitiesLabel;

	private ArrayList<String> entityNames = new ArrayList<String>();
	
	protected ScaffoldProjectWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.scaffold.project", "Select Project", null);
	}	

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createScaffoldTypeEditor(control);
		createOverwriteButton(control);
		createForceSetupButton(control);
		createEntitiesEditor(control);
		setControl(control);
	}
	
	private void createForceSetupButton(Composite parent) {
		forceSetupButton = new Button(parent, SWT.CHECK);
		forceSetupButton.setSelection(true);
		getWizardDescriptor().put(FORCE_SETUP, true);
		forceSetupButton.setText("Force Scaffold Setup");
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        forceSetupButton.setLayoutData(gridData);
        forceSetupButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getWizardDescriptor().put(FORCE_SETUP, overwriteButton.getSelection());
			}
		});
	}
	
	private void createOverwriteButton(Composite parent) {
		overwriteButton = new Button(parent, SWT.CHECK);
		overwriteButton.setSelection(true);
		getWizardDescriptor().put(OVERWRITE_EXISTING, true);
		overwriteButton.setText("Overwrite Existing Files");
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        overwriteButton.setLayoutData(gridData);
        overwriteButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getWizardDescriptor().put(OVERWRITE_EXISTING, overwriteButton.getSelection());
			}
		});
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
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
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
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void createEntitiesEditor(Composite parent) {
		selectEntitiesLabel = new Label(parent, SWT.NONE);
		selectEntitiesLabel.setText(getEntitiesLabelText());
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = 3;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
		selectEntitiesLabel.setLayoutData(gridData);
        selectEntitiesTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;
        selectEntitiesTable.setLayoutData(gridData);
        selectEntitiesTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.item instanceof TableItem) {
                    TableItem source = (TableItem) e.item;
                    if (source.getChecked()) {
                        entityNames.add((String)source.getData());
                    } else {
                        entityNames.remove((String)source.getData());
                    }
                }
            }
        });
        Button selectAllButton = new Button(parent, SWT.PUSH);
        selectAllButton.setText("Select All");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.TOP;
        selectAllButton.setLayoutData(gridData);
        selectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	entityNames.clear();
            	for (TableItem tableItem : selectEntitiesTable.getItems()) {
            		tableItem.setChecked(true);
            		entityNames.add((String)tableItem.getData());
            	}
            }
		});
        Button selectNoneButton = new Button(parent, SWT.PUSH);
        selectNoneButton.setText("Select None");
        gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.TOP;
        selectNoneButton.setLayoutData(gridData);
        selectNoneButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	entityNames.clear();
            	for (TableItem tableItem : selectEntitiesTable.getItems()) {
            		tableItem.setChecked(false);
            	}
            }
		});
        getWizardDescriptor().put(ENTITY_NAMES, entityNames);
		refreshEntitiesTable();
	}
	
	private void refreshEntitiesTable() {
		if (selectEntitiesTable == null) return;
		String projectName = (String)getWizardDescriptor().get(ScaffoldProjectWizardPage.PROJECT_NAME);
		if (projectName == null) return;
		selectEntitiesTable.removeAll();
		IProject project = getProject(projectName);
		JpaProject jpaProject = (JpaProject)project.getAdapter(JpaProject.class);
		Iterable<JavaResourceAbstractType> iterable = jpaProject.getAnnotatedJavaSourceTypes();
		Iterator<JavaResourceAbstractType> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			TableItem tableItem = new TableItem(selectEntitiesTable, SWT.NONE);
			JavaResourceAbstractType jrat = iterator.next();
			String qualifiedName = jrat.getTypeBinding().getQualifiedName();
			tableItem.setData(jrat.getFile().getLocation().toOSString());
			tableItem.setText(qualifiedName);
		}		
	}
	
	private String getEntitiesLabelText() {
		String labelText = "Select entities";
		String projectName = (String)getWizardDescriptor().get(PROJECT_NAME);
		if (projectName != null) {
			labelText += " for project '" + projectName +"'";
		}
		labelText += " :";
		return labelText;
	}
	
	private void refreshEntitiesLabel() {
		if (selectEntitiesLabel == null) return;
		selectEntitiesLabel.setText(getEntitiesLabelText());		
	}
	
	private void handleProjectChange() {
		getWizardDescriptor().put(PROJECT_NAME, projectNameCombo.getText());
		if (getWizardDescriptor().get(SCAFFOLD_TYPE) != null) {
			((ScaffoldWizard)getWizard()).checkIfSetupNeeded();
		}
		refreshEntitiesTable();
		refreshEntitiesLabel();
	}
		
	private void handleScaffoldTypeChange() {
		getWizardDescriptor().put(SCAFFOLD_TYPE, scaffoldTypeCombo.getText());
		if (getWizardDescriptor().get(PROJECT_NAME) != null) {
			((ScaffoldWizard)getWizard()).checkIfSetupNeeded();
		}
		refreshEntitiesTable();
		refreshEntitiesLabel();
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
