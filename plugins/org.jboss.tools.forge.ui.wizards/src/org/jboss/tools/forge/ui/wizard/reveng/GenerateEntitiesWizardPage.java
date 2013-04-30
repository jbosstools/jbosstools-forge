package org.jboss.tools.forge.ui.wizard.reveng;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.db.generic.ui.wizard.NewJDBCFilteredCPWizard;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizard.WizardsPlugin;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class GenerateEntitiesWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "GenerateEntitiesWizardPage.projectName";
	final static String ENTITY_PACKAGE = "GenerateEntitiesWizardPage.entityPackage";
	final static String CONNECTION_PROFILE = "GenerateEntitiesWizardPage.connectionProfile";
	
	private HashMap<String, ConnectionProfileDescriptor> connectionProfiles = 
			new HashMap<String, ConnectionProfileDescriptor>();
	private DataToolsConnectionProfileHelper connectionProfileHelper = 
			new DataToolsConnectionProfileHelper(this);
	
	private Combo projectNameCombo, connectionProfileCombo, hibernateDialectCombo;
	private Text entityPackageText, urlText, userNameText, userPasswordText, driverNameText, driverLocationText;
	private Button saveButton, revertButton, browsePackageButton;
	
	private boolean updatingConnectionProfileDetails = false;
	
	protected GenerateEntitiesWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.generate.entities", "Generate Entities", null);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createEntityPackageEditor(control);
		createConnectionProfileEditor(control);
		createConnectionProfileDetailsEditor(control);
		setControl(control);
	}
	
	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("JPA Project: ");
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
		}
		projectNameCombo.addSelectionListener(new SelectionAdapter() {		
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizardDescriptor().put(PROJECT_NAME, projectNameCombo.getText());
				updateEntityPackageText();
				browsePackageButton.setEnabled(isProjectSelected());
			}
		});
		final Button newProjectButton = new Button(parent, SWT.NONE);
		newProjectButton.setText("New...");
		newProjectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}
	
	private void updateEntityPackageText() {
		try {
			String projectName = projectNameCombo.getText();
			if (projectName == null || "".equals(projectName)) return;
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project == null) return;
			File pomFile = project.getFile("pom.xml").getLocation().toFile();
			Model model = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
			entityPackageText.setText(model.getGroupId() + ".model");
		} catch (CoreException e) {
			WizardsPlugin.log(e);
		}
	}
	
	private void createEntityPackageEditor(Composite parent) {
		Label entityPackageLabel = new Label(parent, SWT.NONE);
		entityPackageLabel.setText("Entity Package: ");
		entityPackageText = new Text(parent, SWT.BORDER);
		entityPackageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		entityPackageText.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizardDescriptor().put(ENTITY_PACKAGE, entityPackageText.getText());
			}
		});
		browsePackageButton = new Button(parent, SWT.NONE);
		browsePackageButton.setText("Browse...");
		browsePackageButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		browsePackageButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectEntityPackage();
			}
		});
		browsePackageButton.setEnabled(isProjectSelected());
	}
	
	private boolean isProjectSelected() {
		String projectName = projectNameCombo.getText();
		return projectName != null && !"".equals(projectName);
	}
	
	private List<IPackageFragment> getPackageFragments() {
		ArrayList<IPackageFragment> result = new ArrayList<IPackageFragment>();
		try {
			String projectName = projectNameCombo.getText();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			IJavaProject javaProject = JavaCore.create(project);
			for (IPackageFragmentRoot root : javaProject.getAllPackageFragmentRoots()) {
				if (root.getKind() != IPackageFragmentRoot.K_SOURCE) continue;
				for (IJavaElement javaElement : root.getChildren()) {
					addPackageFragments(javaElement, result);
				}
			}
		} catch (JavaModelException e) {
			WizardsPlugin.log(e);
		}
		return result;
	}
	
	private void addPackageFragments(IJavaElement javaElement, List<IPackageFragment> list) throws JavaModelException {
		if (javaElement instanceof IPackageFragment) {
			IPackageFragment packageFragment = (IPackageFragment)javaElement;
			if (!packageFragment.isDefaultPackage()) {
				list.add(packageFragment);
			}
			for (IJavaElement child : packageFragment.getChildren()) {
				addPackageFragments(child, list);
			}
		}
	}
	
	private void selectEntityPackage() {
		ElementListSelectionDialog dialog = 
				new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider());
		dialog.setTitle("Package Selection");
		dialog.setMessage("Select a package.");
		dialog.setElements(getPackageFragments().toArray());
		dialog.open();
		Object[] results = dialog.getResult();
		if (results.length > 0 && results[0] instanceof IPackageFragment) {
			IPackageFragment result = (IPackageFragment)results[0];
			entityPackageText.setText(result.getElementName());
		}
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
				getWizardDescriptor().put(CONNECTION_PROFILE, getSelectedConnectionProfile());
			}
		});
		Button connectionProfileButton = new Button(parent, SWT.NONE);
		connectionProfileButton.setText("New...");
		connectionProfileButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				NewJDBCFilteredCPWizard wizard = new NewJDBCFilteredCPWizard();
				WizardDialog wizardDialog = new WizardDialog(getShell(), wizard);
				wizardDialog.setHelpAvailable(false);
				wizardDialog.open();
				connectionProfileHelper.retrieveConnectionProfiles();
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
		urlText.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				if (updatingConnectionProfileDetails) return;
				getSelectedConnectionProfile().url = urlText.getText();
				enableButtons(true);
			}
		});
	}
	
	private void createUserNameEditor(Composite parent) {
		Label userNameLabel = new Label(parent, SWT.NONE);
		userNameLabel.setText("User Name: ");
		userNameText = new Text(parent, SWT.BORDER);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		userNameText.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				if (updatingConnectionProfileDetails) return;
				getSelectedConnectionProfile().user = userNameText.getText();
				enableButtons(true);
			}
		});
	}
	
	private void createPasswordEditor(Composite parent) {
		Label userPasswordLabel = new Label(parent, SWT.NONE);
		userPasswordLabel.setText("User Password: ");
		userPasswordText = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		userPasswordText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		userPasswordText.addModifyListener(modifyListener);
	}
	
	private void createHibernateDialectEditor(Composite parent) {
		Label hibernateDialectLabel = new Label(parent, SWT.NONE);
		hibernateDialectLabel.setText("Hibernate Dialect: ");
		hibernateDialectCombo = new Combo(parent, SWT.DROP_DOWN);
		hibernateDialectCombo.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		fillHibernateDialectCombo();
		hibernateDialectCombo.addModifyListener(modifyListener);
	}
	
	private void fillHibernateDialectCombo() {
		for (HibernateDialect hibernateDialect : HibernateDialect.values()) {
			hibernateDialectCombo.add(hibernateDialect.getClassName());
		}
	}
	
	private void createDriverNameEditor(Composite parent) {
		Label driverNameLabel = new Label(parent, SWT.NONE);
		driverNameLabel.setText("Driver Class: ");
		driverNameText = new Text(parent, SWT.BORDER);
		driverNameText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		driverNameText.addModifyListener(modifyListener);
	}
	
	private void createDriverLocationEditor(Composite parent) {
		Label driverLocationLabel = new Label(parent, SWT.NONE);
		driverLocationLabel.setText("Driver Location: ");
		driverLocationText = new Text(parent, SWT.BORDER);
		driverLocationText.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		driverLocationText.addModifyListener(modifyListener);
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
		saveButton = new Button(updateRestoreComposite, SWT.NONE);
		saveButton.setText("Save");
		saveButton.setEnabled(false);
		saveButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				connectionProfileHelper.saveConnectionProfile(getSelectedConnectionProfile());
				enableButtons(false);
			}
		});
		revertButton = new Button(updateRestoreComposite, SWT.NONE);
		revertButton.setText("Revert");
		revertButton.setEnabled(false);
		revertButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = connectionProfileCombo.getText();
				if (name != null && !"".equals(name)) {			
					connectionProfileHelper.revertConnectionProfile(getSelectedConnectionProfile());
					enableButtons(false);
				}
			}
		});
		updateRestoreComposite.setLayoutData(new GridData(SWT.END, SWT.DEFAULT, true, false, 3, SWT.DEFAULT));
	}
	
	void updateConnectionProfileDetails() {
		updatingConnectionProfileDetails = true;
		ConnectionProfileDescriptor selectedConnectionProfile = getSelectedConnectionProfile();
		String url = selectedConnectionProfile.url;
		url = url == null ? "" : url;
		urlText.setText(selectedConnectionProfile.url);
		String user = selectedConnectionProfile.user;
		user = user == null ? "" : user;
		userNameText.setText(user);
		String password = selectedConnectionProfile.password;
		password = password == null ? "" : password;
		String driverClassName = selectedConnectionProfile.driverClass;
		userPasswordText.setText(password);
		driverClassName = driverClassName == null ? "" : driverClassName;
		driverNameText.setText(driverClassName);
		String driverLocation = selectedConnectionProfile.driverLocation;
		driverLocation = driverLocation == null ? "" : driverLocation;
		driverLocationText.setText(driverLocation);
		updatingConnectionProfileDetails = false;
//		connectionProfileHelper.testConnectionProfile(selectedConnectionProfile);
	}
	
	private void enableButtons(boolean enabled) {
		saveButton.setEnabled(enabled);
		revertButton.setEnabled(enabled);
	}
	
	private ConnectionProfileDescriptor getSelectedConnectionProfile() {
		return connectionProfiles.get(connectionProfileCombo.getText());
	}
	
	private ModifyListener modifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			if (updatingConnectionProfileDetails) return;
			Widget widget = e.widget;
			if (widget == urlText) {
				getSelectedConnectionProfile().url = urlText.getText();
			} else if (widget == userNameText) {
				getSelectedConnectionProfile().user = userNameText.getText();
			} else if (widget == userPasswordText) {
				getSelectedConnectionProfile().password = userPasswordText.getText();
			} else if (widget == driverNameText) {
				getSelectedConnectionProfile().driverClass = driverNameText.getText();
			} else if (widget == driverLocationText) {
				getSelectedConnectionProfile().driverLocation = driverLocationText.getText();
			} else if (widget == hibernateDialectCombo) {
				getSelectedConnectionProfile().dialect = hibernateDialectCombo.getText();
			}
			enableButtons(true);
//			connectionProfileHelper.testConnectionProfile(getSelectedConnectionProfile());
		}
	};
	
}
