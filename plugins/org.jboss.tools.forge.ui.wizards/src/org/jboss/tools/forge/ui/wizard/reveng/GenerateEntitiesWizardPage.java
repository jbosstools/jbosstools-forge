package org.jboss.tools.forge.ui.wizard.reveng;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class GenerateEntitiesWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "GenerateEntitiesWizardPage.projectName";
	final static String CONNECTION_PROFILE = "GenerateEntitiesWizardPage.connectionProfile";
	
	private HashMap<String, ConnectionProfileDescriptor> connectionProfiles = 
			new HashMap<String, ConnectionProfileDescriptor>();
	private DataToolsConnectionProfileHelper connectionProfileHelper = 
			new DataToolsConnectionProfileHelper(this);
	
	private Combo connectionProfileCombo, hibernateDialectCombo;
	private Text urlText, userNameText, userPasswordText, driverNameText, driverLocationText;
	
	
	protected GenerateEntitiesWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.generate.entities", "Generate Entities", null);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createConnectionProfileEditor(control);
		createConnectionProfileDetailsEditor(control);
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
			}
		});
		final Button newProjectButton = new Button(parent, SWT.NONE);
		newProjectButton.setText("New...");
		newProjectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}
		
	private void createConnectionProfileEditor(Composite parent) {
		Label connectionProfileLabel = new Label(parent, SWT.NONE);
		connectionProfileLabel.setText("Connection Profile Name: ");
		connectionProfileCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		connectionProfileCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		connectionProfileCombo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateConnectionProfileDetails();
			}
		});
		Button connectionProfileButton = new Button(parent, SWT.NONE);
		connectionProfileButton.setText("Manage...");
		connectionProfileButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				ManageConnectionProfileDialog dialog = new ManageConnectionProfileDialog(getShell());
				dialog.open();
				refreshConnectionProfiles(dialog.getConnectionProfiles());
			}			
		});
		connectionProfileButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		connectionProfileHelper.retrieveConnectionProfiles();
	}
	
	void refreshConnectionProfiles(ConnectionProfileDescriptor[] connectionProfiles) {
		this.connectionProfiles.clear();
		connectionProfileCombo.removeAll();
		for (ConnectionProfileDescriptor connectionProfile : connectionProfiles) {
			connectionProfileCombo.add(connectionProfile.name);
			this.connectionProfiles.put(connectionProfile.name, connectionProfile);
		}
	}
	
	private void createConnectionProfileDetailsEditor(Composite parent) {
		Label connectionProfileDetailsLabel = new Label(parent, SWT.NONE);
		connectionProfileDetailsLabel.setText("Connection Profile Details: ");
		connectionProfileDetailsLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, SWT.DEFAULT));
		Combo dummyCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		dummyCombo.setVisible(false);
		Group group = new Group(parent, SWT.DEFAULT);
		group.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false, 3, SWT.DEFAULT));
		group.setLayout(new GridLayout(2, false));
		createUrlEditor(group);
		createUserNameEditor(group);
		createPasswordEditor(group);
		createHibernateDialectEditor(group);
		createDriverNameEditor(group);
		createDriverLocationEditor(group);
		createUpdateRestoreComposite(group);
	}
	
	private void createUrlEditor(Composite parent) {
		Label urlLabel = new Label(parent, SWT.NONE);
		urlLabel.setText("URL: ");
		urlText = new Text(parent, SWT.BORDER);
		urlText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
	}
	
	private void createUserNameEditor(Composite parent) {
		Label userNameLabel = new Label(parent, SWT.NONE);
		userNameLabel.setText("User Name: ");
		userNameText = new Text(parent, SWT.BORDER);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
	}
	
	private void createPasswordEditor(Composite parent) {
		Label userPasswordLabel = new Label(parent, SWT.NONE);
		userPasswordLabel.setText("User Password: ");
		userPasswordText = new Text(parent, SWT.BORDER);
		userPasswordText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
	}
	
	private void createHibernateDialectEditor(Composite parent) {
		Label hibernateDialectLabel = new Label(parent, SWT.NONE);
		hibernateDialectLabel.setText("Hibernate Dialect: ");
		hibernateDialectCombo = new Combo(parent, SWT.DROP_DOWN);
		hibernateDialectCombo.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
	}
	
	private void createDriverNameEditor(Composite parent) {
		Label driverNameLabel = new Label(parent, SWT.NONE);
		driverNameLabel.setText("Driver Name: ");
		driverNameText = new Text(parent, SWT.BORDER);
		driverNameText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
	}
	
	private void createDriverLocationEditor(Composite parent) {
		Label driverLocationLabel = new Label(parent, SWT.NONE);
		driverLocationLabel.setText("Driver Location: ");
		driverLocationText = new Text(parent, SWT.BORDER);
		driverLocationText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
	}
	
	private void createUpdateRestoreComposite(Composite parent) {
		Composite updateRestoreComposite = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout();
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.spacing = 0;
		updateRestoreComposite.setLayout(layout);
		Button updateButton = new Button(updateRestoreComposite, SWT.NONE);
		updateButton.setText("Update");
		Button restoreButton = new Button(updateRestoreComposite, SWT.NONE);
		restoreButton.setText("Restore");
		updateRestoreComposite.setLayoutData(new GridData(SWT.END, SWT.DEFAULT, true, false, 3, SWT.DEFAULT));
	}
	
	private void updateConnectionProfileDetails() {
		ConnectionProfileDescriptor selectedConnectionProfile = 
				connectionProfiles.get(connectionProfileCombo.getText());
		String url = selectedConnectionProfile.url;
		url = url == null ? "" : url;
		urlText.setText(selectedConnectionProfile.url);
	}
	
}
