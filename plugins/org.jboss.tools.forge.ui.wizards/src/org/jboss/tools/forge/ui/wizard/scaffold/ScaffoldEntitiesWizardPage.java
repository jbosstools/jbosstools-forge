package org.jboss.tools.forge.ui.wizard.scaffold;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class ScaffoldEntitiesWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "ScaffoldEntitiesWizardPage.projectName";
	final static String ENTITY_NAMES = "ScaffoldEntitiesWizardPage.entityNames";
	
	private ArrayList<String> entityNames = new ArrayList<String>();
	private Table selectEntitiesTable;

	protected ScaffoldEntitiesWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.scaffold.entities", "Scaffold Entities", null);
	}
	
	

	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createEntitiesEditor(control);
		setControl(control);
	}
	
	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Project: ");
		final Combo projectNameCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
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
		}
		projectNameCombo.addSelectionListener(new SelectionAdapter() {		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizardDescriptor().put(PROJECT_NAME, projectNameCombo.getText());
				refreshEntitiesEditor();
			}
		});
		final Button newProjectButton = new Button(parent, SWT.NONE);
		newProjectButton.setText("New...");
	}
		
	private void createEntitiesEditor(Composite parent) {
		Label selectEntitiesLabel = new Label(parent, SWT.NONE);
		selectEntitiesLabel.setText("Entities:");
		GridData gridData = new GridData();
		gridData.verticalAlignment = SWT.TOP;
		selectEntitiesLabel.setLayoutData(gridData);
        selectEntitiesTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        selectEntitiesTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        selectEntitiesTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (e.item instanceof TableItem) {
                    TableItem source = (TableItem) e.item;
                    if (source.getChecked()) {
                        entityNames.add(source.getText());
                    } else {
                        entityNames.remove(source.getText());
                    }
                }
            }
        });
        getWizardDescriptor().put(ENTITY_NAMES, entityNames);
		refreshEntitiesEditor();
	}
	
	private void refreshEntitiesEditor() {
		String projectName = (String)getWizardDescriptor().get(PROJECT_NAME);
		if (projectName == null) return;
		selectEntitiesTable.removeAll();
		IProject project = getProject(projectName);
		JpaProject jpaProject = (JpaProject)project.getAdapter(JpaProject.class);
		Iterator<String> iterator = jpaProject.getAnnotatedJavaSourceClassNames().iterator();
		while (iterator.hasNext()) {
			TableItem tableItem = new TableItem(selectEntitiesTable, SWT.NONE);
			tableItem.setText(iterator.next());
		}		
	}
		
	private IProject getProject(String projectName) {
		return ResourcesPlugin
				.getWorkspace()
				.getRoot()
				.getProject(projectName);
	}

}
