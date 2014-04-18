package org.jboss.tools.forge.ui.wizards.internal.wizard.reveng;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.jboss.tools.forge.ui.wizards.internal.WizardsPlugin;
import org.jboss.tools.forge.ui.wizards.internal.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizards.internal.wizard.project.NewProjectWizard;
import org.jboss.tools.forge.ui.wizards.internal.wizard.util.WizardsHelper;

public class GenerateEntitiesWizardPage extends AbstractForgeWizardPage {

	final static String PROJECT_NAME = "GenerateEntitiesWizardPage.projectName";
	final static String ENTITY_PACKAGE = "GenerateEntitiesWizardPage.entityPackage";
	final static String CONNECTION_PROFILE = "GenerateEntitiesWizardPage.connectionProfile";

	private HashMap<String, ConnectionProfileDescriptor> connectionProfiles = new HashMap<String, ConnectionProfileDescriptor>();
	private DataToolsConnectionProfileHelper connectionProfileHelper = new DataToolsConnectionProfileHelper(
			this);

	private Combo projectNameCombo, connectionProfileCombo,
			hibernateDialectCombo;
	private Text entityPackageText, urlText, userNameText, userPasswordText,
			driverNameText, driverLocationText;
	private Button saveButton, revertButton, browsePackageButton, 
			browseDriverClassButton, browseDriverLocationButton;

	private boolean updatingConnectionProfileDetails = false;

	protected GenerateEntitiesWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.generate.entities",
				"Generate Entities", null);
	}

	@Override
	public void createControl(Composite parent) {
		getShell().setSize(getShell().computeSize(500, getShellHeight(), true));
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		createProjectEditor(control);
		createEntityPackageEditor(control);
		createConnectionProfileEditor(control);
		createConnectionProfileDetailsEditor(control);
		setControl(control);
	}
	
	private int getShellHeight() {
		String os = System.getProperty("os.name").toLowerCase();
		return os.indexOf("linux") >= 0 ? 660 : 570;
	}

	private void createProjectEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("JPA Project: ");
		projectNameCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		projectNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		String projectName = (String) getWizardDescriptor().get(PROJECT_NAME);
		updateProjectComboBox(projectName);
		projectNameCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizardDescriptor().put(PROJECT_NAME,
						projectNameCombo.getText());
				updateEntityPackageText();
				browsePackageButton.setEnabled(isProjectSelected());
			}
		});
		final Button newProjectButton = new Button(parent, SWT.NONE);
		newProjectButton.setText("New...");
		newProjectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		newProjectButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				openNewProjectWizard();
			}
		});
	}
	
	private void openNewProjectWizard() {
		PropertyChangeListener listener = new PropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateProjectComboBox((String)evt.getNewValue());
			}
		};
		WizardDialog dialog = new WizardDialog(
				getShell(), 
				new NewProjectWizard(listener));
		dialog.open();
	}
	
	private void updateProjectComboBox(String selectedProjectName) {
		String currentSelection = projectNameCombo.getText();
		projectNameCombo.removeAll();
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		for (IProject project : allProjects) {
			if (WizardsHelper.isJPAProject(project)) {
				String name = project.getName();
				projectNameCombo.add(name);
				if (name.equals(selectedProjectName)) {
					projectNameCombo.setText(name);
					getWizardDescriptor().put(PROJECT_NAME, name);
					updateEntityPackageText();
					return;
				}
			}
		}
		projectNameCombo.setText(currentSelection);
	}

	private void updateEntityPackageText() {
		if (entityPackageText == null) return;
		try {
			String projectName = projectNameCombo.getText();
			if (projectName == null || "".equals(projectName))
				return;
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			if (project == null)
				return;
			File pomFile = project.getFile("pom.xml").getLocation().toFile();
			Model model = MavenPlugin.getMavenModelManager().readMavenModel(
					pomFile);
			String entityPackage = model.getGroupId() + ".model";
			entityPackageText.setText(entityPackage);
			getWizardDescriptor().put(ENTITY_PACKAGE, entityPackage);
		} catch (CoreException e) {
			WizardsPlugin.log(e);
		}
	}

	private void createEntityPackageEditor(Composite parent) {
		Label entityPackageLabel = new Label(parent, SWT.NONE);
		entityPackageLabel.setText("Entity Package: ");
		entityPackageText = new Text(parent, SWT.BORDER);
		entityPackageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
		entityPackageText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				getWizardDescriptor().put(ENTITY_PACKAGE,
						entityPackageText.getText());
			}
		});
		updateEntityPackageText();
		browsePackageButton = new Button(parent, SWT.NONE);
		browsePackageButton.setText("Browse...");
		browsePackageButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
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
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			IJavaProject javaProject = JavaCore.create(project);
			for (IPackageFragmentRoot root : javaProject
					.getAllPackageFragmentRoots()) {
				if (root.getKind() != IPackageFragmentRoot.K_SOURCE)
					continue;
				for (IJavaElement javaElement : root.getChildren()) {
					addPackageFragments(javaElement, result);
				}
			}
		} catch (JavaModelException e) {
			WizardsPlugin.log(e);
		}
		return result;
	}

	private void addPackageFragments(IJavaElement javaElement,
			List<IPackageFragment> list) throws JavaModelException {
		if (javaElement instanceof IPackageFragment) {
			IPackageFragment packageFragment = (IPackageFragment) javaElement;
			if (!packageFragment.isDefaultPackage()) {
				list.add(packageFragment);
			}
			for (IJavaElement child : packageFragment.getChildren()) {
				addPackageFragments(child, list);
			}
		}
	}

	private void selectEntityPackage() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), new JavaElementLabelProvider());
		dialog.setTitle("Package Selection");
		dialog.setMessage("Select a package.");
		dialog.setElements(getPackageFragments().toArray());
		dialog.open();
		Object[] results = dialog.getResult();
		if (results != null && results.length > 0
				&& results[0] instanceof IPackageFragment) {
			IPackageFragment result = (IPackageFragment) results[0];
			entityPackageText.setText(result.getElementName());
		}
	}

	private void createConnectionProfileEditor(Composite parent) {
		Label connectionProfileLabel = new Label(parent, SWT.NONE);
		connectionProfileLabel.setText("Connection Profile Name: ");
		connectionProfileCombo = new Combo(parent, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		connectionProfileCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
		connectionProfileCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateConnectionProfileDetails();
				getWizardDescriptor().put(CONNECTION_PROFILE,
						getSelectedConnectionProfile());
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
		connectionProfileButton.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false));
		connectionProfileHelper.retrieveConnectionProfiles();
		connectionProfileCombo.setText("");
		getWizardDescriptor().put(CONNECTION_PROFILE,
				getSelectedConnectionProfile());
	}
	
	@SuppressWarnings("unchecked")
	void refreshConnectionProfiles(
			ConnectionProfileDescriptor[] newConnectionProfiles) {
		Set<String> oldConnectionProfileNames = ((HashMap<String, ConnectionProfileDescriptor>)connectionProfiles.clone()).keySet();
		String newConnectionProfileName = null;
		this.connectionProfiles.clear();
		connectionProfileCombo.removeAll();
		for (ConnectionProfileDescriptor connectionProfile : newConnectionProfiles) {
			connectionProfileCombo.add(connectionProfile.name);
			this.connectionProfiles.put(connectionProfile.name,
					connectionProfile);
			if (!oldConnectionProfileNames.contains(connectionProfile.name)) {
				newConnectionProfileName = connectionProfile.name;
			}
			if (newConnectionProfileName != null && isConnectionDetailsControlCreated()) {
				connectionProfileCombo.setText(newConnectionProfileName);
				updateConnectionProfileDetails();
				getWizardDescriptor().put(CONNECTION_PROFILE,
						getSelectedConnectionProfile());			}
		}
	}
	
	private boolean isConnectionDetailsControlCreated() {
		return urlText != null;
	}

	private void createConnectionProfileDetailsEditor(Composite parent) {
		Label connectionProfileDetailsLabel = new Label(parent, SWT.NONE);
		connectionProfileDetailsLabel.setText("Connection Profile Details: ");
		connectionProfileDetailsLabel.setLayoutData(new GridData(SWT.FILL,
				SWT.BOTTOM, true, false, 2, SWT.DEFAULT));
		Combo dummyCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		dummyCombo.setVisible(false);
		Group group = new Group(parent, SWT.DEFAULT);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3,
				SWT.DEFAULT));
		GridLayout groupLayout = new GridLayout(3, false);
		groupLayout.verticalSpacing = -2;
		group.setLayout(groupLayout);
		createUrlEditor(group);
		createUserNameEditor(group);
		createPasswordEditor(group);
		createHibernateDialectEditor(group);
		createDriverLocationEditor(group);
		createDriverNameEditor(group);
		createUpdateRestoreComposite(group);
		updateConnectionProfileDetails();
	}
	
	private void createUrlEditor(Composite parent) {
		Label urlLabel = new Label(parent, SWT.NONE);
		urlLabel.setText("URL: ");
		urlText = new Text(parent, SWT.BORDER);
		urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		urlText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (updatingConnectionProfileDetails)
					return;
				getSelectedConnectionProfile().url = urlText.getText();
				enableButtons(true);
			}
		});
		Button dummyButton = new Button(parent, SWT.NONE);
		dummyButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		dummyButton.setVisible(false);
	}

	private void createUserNameEditor(Composite parent) {
		Label userNameLabel = new Label(parent, SWT.NONE);
		userNameLabel.setText("User Name: ");
		userNameText = new Text(parent, SWT.BORDER);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		userNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (updatingConnectionProfileDetails)
					return;
				getSelectedConnectionProfile().user = userNameText.getText();
				enableButtons(true);
			}
		});
		Button dummyButton = new Button(parent, SWT.NONE);
		dummyButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		dummyButton.setVisible(false);
	}

	private void createPasswordEditor(Composite parent) {
		Label userPasswordLabel = new Label(parent, SWT.NONE);
		userPasswordLabel.setText("User Password: ");
		userPasswordText = new Text(parent, SWT.BORDER | SWT.PASSWORD);
		userPasswordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		userPasswordText.addModifyListener(modifyListener);
		Button dummyButton = new Button(parent, SWT.NONE);
		dummyButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		dummyButton.setVisible(false);
	}

	private void createHibernateDialectEditor(Composite parent) {
		Label hibernateDialectLabel = new Label(parent, SWT.NONE);
		hibernateDialectLabel.setText("Hibernate Dialect: ");
		hibernateDialectCombo = new Combo(parent, SWT.DROP_DOWN);
		hibernateDialectCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fillHibernateDialectCombo();
		hibernateDialectCombo.addModifyListener(modifyListener);
		hibernateDialectCombo.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				getSelectedConnectionProfile().dialect = hibernateDialectCombo.getText();
				enableButtons(true);
			}
		});
		Button dummyButton = new Button(parent, SWT.NONE);
		dummyButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		dummyButton.setVisible(false);
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
		driverNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		driverNameText.addModifyListener(modifyListener);
		browseDriverClassButton = new Button(parent, SWT.NONE);
		browseDriverClassButton.setText("Browse...");
		browseDriverClassButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		browseDriverClassButton.setEnabled(false);
		browseDriverClassButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseForDriverClass();
			}
		});
	}
	
	private void browseForDriverClass() {
		LabelProvider labelProvider = new LabelProvider() {
			public Image getImage(Object element) {
				WizardsPlugin plugin = WizardsPlugin.getDefault();
				ImageRegistry registry = plugin.getImageRegistry();
				Image image = registry.get(WizardsPlugin.CLASS_ICON);
				return image;
			}
			public String getText(Object element) {
				return ((Class<?>)element).getName();
			}
		};
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), 
				labelProvider);
		dialog.setTitle("Driver Selection");
		dialog.setMessage("Select a driver.");
		dialog.setElements(getDriverClasses());
		if (dialog.open() == Window.OK) {
			Class<?> selectedDriver = (Class<?>)dialog.getFirstResult();
			driverNameText.setText(selectedDriver.getName());
		}
	}

	private Class<?>[] getDriverClasses() {
		ArrayList<Class<?>> result = new ArrayList<Class<?>>();
		try {
			File file = new File(driverLocationText.getText());
			URL[] urls = new URL[] { file.toURI().toURL() };
			URLClassLoader classLoader = URLClassLoader.newInstance(urls);
			Class<?> driverClass = classLoader.loadClass(Driver.class.getName());
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> iter = jarFile.entries();
			while (iter.hasMoreElements()) {
				JarEntry entry = iter.nextElement();
				if (entry.getName().endsWith(".class")) { 
					String name = entry.getName();
					name = name.substring(0, name.length() - 6);
					name = name.replace('/', '.');
					try {
						Class<?> clazz = classLoader.loadClass(name);
						if (driverClass.isAssignableFrom(clazz)) {
							result.add(clazz);
						}
					} catch (ClassNotFoundException cnfe) {
						//ignore
					} catch (NoClassDefFoundError err) {
						//ignore
					}
				}
			}
			jarFile.close();
		} catch (Exception e) {
			// ignore and return an empty list
		}
		return result.toArray(new Class<?>[result.size()]);
	}

	private void createDriverLocationEditor(Composite parent) {
		Label driverLocationLabel = new Label(parent, SWT.NONE);
		driverLocationLabel.setText("Driver Location: ");
		driverLocationText = new Text(parent, SWT.BORDER);
		driverLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
		driverLocationText.addModifyListener(modifyListener);
		browseDriverLocationButton = new Button(parent, SWT.NONE);
		browseDriverLocationButton.setText("Browse...");
		browseDriverLocationButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		browseDriverLocationButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseForDriverLocation();
			}
		});
	}
	
	private void browseForDriverLocation() {
		FileDialog dialog = new FileDialog(getShell());
		dialog.setFilterExtensions(new String[] { "*.jar" });
		dialog.open();
		String fileName = dialog.getFileName();
		String path = dialog.getFilterPath();
		if (fileName != null && path != null) {
			driverLocationText.setText(path + File.separator + fileName);
		}
	}

	private void createUpdateRestoreComposite(Composite parent) {
		Composite updateRestoreComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		layout.marginHeight = 0;
		layout.marginTop = 5;
		updateRestoreComposite.setLayout(layout);
		saveButton = new Button(updateRestoreComposite, SWT.NONE);
		saveButton.setText("Save");
		saveButton.setEnabled(false);
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConnectionProfileDescriptor connectionProfile = getSelectedConnectionProfile();
				if (connectionProfile.name == null || "".equals(connectionProfile.name)) {
					InputDialog dialog = new InputDialog(
							getShell(), 
							"Connection Profile", 
							"Please enter a name for the connection profile.", 
							"connection profile", null);
					if (dialog.open() != Dialog.CANCEL) {
						connectionProfile.name = dialog.getValue();
						connectionProfiles.put(connectionProfile.name, connectionProfile);
						connectionProfiles.put("", new ConnectionProfileDescriptor());
						
					} else {
						return;
					}
				}
				connectionProfileHelper
						.saveConnectionProfile(connectionProfile);
				refreshConnectionProfiles(connectionProfiles.values().toArray(new ConnectionProfileDescriptor[connectionProfiles.size()]));
				connectionProfileCombo.setText(connectionProfile.name);
				enableButtons(false);
			}
		});
		saveButton.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		revertButton = new Button(updateRestoreComposite, SWT.NONE);
		revertButton.setText("Revert");
		revertButton.setEnabled(false);
		revertButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = connectionProfileCombo.getText();
				if (name != null && !"".equals(name)) {
					connectionProfileHelper
							.revertConnectionProfile(getSelectedConnectionProfile());
					enableButtons(false);
				}
			}
		});
		revertButton.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		updateRestoreComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				true, false, 3, SWT.DEFAULT));
	}

	void updateConnectionProfileDetails() {
		updatingConnectionProfileDetails = true;
		ConnectionProfileDescriptor selectedConnectionProfile = getSelectedConnectionProfile();
		String url = selectedConnectionProfile.url;
		url = url == null ? "" : url;
		urlText.setText(url);
		String user = selectedConnectionProfile.user;
		user = user == null ? "" : user;
		userNameText.setText(user);
		String password = selectedConnectionProfile.password;
		password = password == null ? "" : password;
		String driverClassName = selectedConnectionProfile.driverClass;
		userPasswordText.setText(password);
		driverClassName = driverClassName == null ? "" : driverClassName;
		driverNameText.setText(driverClassName);
		String hibernateDialect = selectedConnectionProfile.dialect;
		hibernateDialect = hibernateDialect == null ? "" : hibernateDialect;
		hibernateDialectCombo.setText(hibernateDialect);
		String driverLocation = selectedConnectionProfile.driverLocation;
		driverLocation = driverLocation == null ? "" : driverLocation;
		driverLocationText.setText(driverLocation);
		updatingConnectionProfileDetails = false;
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
			if (updatingConnectionProfileDetails)
				return;
			Widget widget = e.widget;
			if (widget == urlText) {
				getSelectedConnectionProfile().url = urlText.getText();
			} else if (widget == userNameText) {
				getSelectedConnectionProfile().user = userNameText.getText();
			} else if (widget == userPasswordText) {
				getSelectedConnectionProfile().password = userPasswordText
						.getText();
			} else if (widget == driverNameText) {
				getSelectedConnectionProfile().driverClass = driverNameText
						.getText();
			} else if (widget == driverLocationText) {
				getSelectedConnectionProfile().driverLocation = driverLocationText.getText();
				updateBrowseDriverClassButton();
			} else if (widget == hibernateDialectCombo) {
				getSelectedConnectionProfile().dialect = hibernateDialectCombo
						.getText();
			}
			enableButtons(true);
		}
	};
	
	private void updateBrowseDriverClassButton() {
		String driverLocation = driverLocationText.getText();
		if (driverLocation != null) {
			File file = new File(driverLocation);
			if (file.exists()) {
				browseDriverClassButton.setEnabled(true);
				return;
			}
		}
		browseDriverClassButton.setEnabled(false);
	}

}
