package org.jboss.tools.forge.ui.wizard.project;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizardPage;
import org.jboss.tools.forge.ui.wizard.persistence.ContainerType;
import org.jboss.tools.forge.ui.wizard.persistence.ProviderType;

public class NewProjectWizardPage extends AbstractForgeWizardPage {
	
	final static String PROJECT_NAME = "NewProjectWizardPage.projectName";
	final static String PROJECT_LOCATION = "NewProjectWizardPage.projectLocation";
	final static String PROJECT_TYPE = "NewProjectWizardPage.projectType";
	final static String TOP_LEVEL_PACKAGE = "NewProjectWizardPage.topLevelPackage";	
	final static String PROVIDER_NAME = "NewProjectWizardPage.providerName";
	final static String CONTAINER_NAME = "NewProjectWizardPage.containerName";
	final static String SETUP_PERSISTENCE = "NewProjectWizardPage.setupPersistence";
	final static String CREATE_MAIN = "NewProjectWizardPage.createMain";
	final static String FINAL_NAME = "NewProjectWizardPage.finalName";
	
	private final static String DEFAULT_PROVIDER = "HIBERNATE";
	private final static String DEFAULT_CONTAINER = "JBOSS_AS7"; 
	private final static String INITIAL_LOCATION = 
			ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();

	private Button setupPersistenceButton;
	private Text projectNameText, topLevelPackageText, projectLocationText;
	private Combo projectTypeCombo, providerCombo, containerCombo;
	private boolean projectNameAndPackageNameAreSynchronized = true;
	
	protected NewProjectWizardPage() {
		super("org.jboss.tools.forge.ui.wizard.newproject", "Create New Project", null);
		setDescription("This is the description: blablablah");
		setMessage("Enter the project name.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));
		createNameEditor(container);
		createLocationEditor(container);
		createTopLevelPackageEditor(container);
		createProjectTypeEditor(container);
		createSeparator(container);
		createMainClassEditor(container);
		createFinalNameEditor(container);
		createSeparator(container);
		createSetupPersistenceEditor(container);
		createProviderEditor(container);
		createContainerEditor(container);
		setControl(container);
	}
	
	private void createProviderEditor(Composite parent) {
		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText("Provider: ");
		providerCombo = new Combo(parent, SWT.DROP_DOWN);
		providerCombo.setText("");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		providerCombo.setLayoutData(gridData);
		providerCombo.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				getWizardDescriptor().put(PROVIDER_NAME, providerCombo.getText());
			}
		});
		for (ProviderType type : ProviderType.values()) {
			providerCombo.add(type.name());
		}
		providerCombo.setText(DEFAULT_PROVIDER);
		providerCombo.setEnabled(setupPersistenceButton.getSelection());
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void createContainerEditor(Composite parent) {
		Label containerLabel = new Label(parent, SWT.NONE);
		containerLabel.setText("Container: ");
		containerCombo = new Combo(parent, SWT.DROP_DOWN);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		containerCombo.setLayoutData(gridData);
		containerCombo.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				getWizardDescriptor().put(CONTAINER_NAME, containerCombo.getText());
			}
		});
		for (ContainerType type : ContainerType.values()) {
			containerCombo.add(type.name());
		}
		containerCombo.setText(DEFAULT_CONTAINER);
		containerCombo.setEnabled(setupPersistenceButton.getSelection());
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		separator.setLayoutData(gridData);
	}
	
	private void createNameEditor(Composite parent) {
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText("Project Name: ");
		projectNameText = new Text(parent, SWT.BORDER);
		projectNameText.setText("");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 250;
		projectNameText.setLayoutData(gridData);
		projectNameText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				updateProjectName();
			}
		});
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void createLocationEditor(Composite parent) {
		Label projectLocationLabel = new Label(parent, SWT.NONE);
		projectLocationLabel.setText("Project Location: ");
		projectLocationText = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 250;
		projectLocationText.setLayoutData(gridData);
		projectLocationText.setText(INITIAL_LOCATION);
		getWizardDescriptor().put(PROJECT_LOCATION, INITIAL_LOCATION);
		projectLocationText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateProjectLocation();
			}
		});
		Button projectLocationButton = new Button(parent, SWT.NONE);
		projectLocationButton.setText("Browse...");
		projectLocationButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		projectLocationButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setMessage("Select the destination folder where the project will be created.");
				dialog.setText("Folder Selection");
				dialog.setFilterPath((String)getWizardDescriptor().get(PROJECT_LOCATION));
				String result = dialog.open();
				if (result != null) {
					projectLocationText.setText(result);
					updateProjectLocation();
				}
			}
		});		
	}
	
	private void updateProjectLocation() {
		String projectLocation = projectLocationText.getText();
		getWizardDescriptor().put(PROJECT_LOCATION, projectLocation);
		setPageComplete(checkPageComplete());
	}
	
	private void updateProjectName() {
		String projectName = projectNameText.getText();
		getWizardDescriptor().put(PROJECT_NAME, projectName);
		if (projectNameAndPackageNameAreSynchronized) {
			topLevelPackageText.setText("com.example." + projectName);
		}
		if (!checkProjectName()) {
			setErrorMessage("The project name cannot be empty.");
		} else {
			setErrorMessage(null);
		}
		setPageComplete(checkPageComplete());
	}
	
	private void createTopLevelPackageEditor(Composite parent) {
		Label topLevelPackageLabel = new Label(parent, SWT.NONE);
		topLevelPackageLabel.setText("Top Level Package: ");
		topLevelPackageText = new Text(parent, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 250;
		topLevelPackageText.setLayoutData(gridData);
		topLevelPackageText.setText(getInitialTopLevelPackage());
		topLevelPackageText.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				String topLevelPackage = topLevelPackageText.getText();
				getWizardDescriptor().put(TOP_LEVEL_PACKAGE, topLevelPackage);
				setPageComplete(checkPageComplete());
			}
		});
		topLevelPackageText.addKeyListener(new KeyAdapter() {			
			@Override
			public void keyPressed(KeyEvent e) {
				projectNameAndPackageNameAreSynchronized = false;
			}
		});
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private String getInitialTopLevelPackage() {
		String result = (String)getWizardDescriptor().get(TOP_LEVEL_PACKAGE);
		if (result == null) {
			result = "com.example";
			getWizardDescriptor().put(TOP_LEVEL_PACKAGE, result);
		}
		return result;
	}
	
	private void createProjectTypeEditor(Composite parent) {
		Label projectTypeLabel = new Label(parent, SWT.NONE);
		projectTypeLabel.setText("Project Type: ");
		projectTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.verticalIndent = 15;
		projectTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fillProjectTypeCombo();
		projectTypeCombo.addSelectionListener(new SelectionAdapter() {		
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProjectType type = ProjectType.getType(projectTypeCombo.getText());
				getWizardDescriptor().put(PROJECT_TYPE, type);
				enableSetupPersistence(
						!ProjectType.POM.equals(type) && !ProjectType.EAR.equals(type));
			}
		});
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void enableSetupPersistence(boolean enabled) {
		getWizardDescriptor().put(
				SETUP_PERSISTENCE, 
				enabled ? setupPersistenceButton.getSelection() : false);
		setupPersistenceButton.setEnabled(enabled);
		providerCombo.setEnabled(setupPersistenceButton.getSelection() && setupPersistenceButton.isEnabled());
		containerCombo.setEnabled(setupPersistenceButton.getSelection() && setupPersistenceButton.isEnabled());
	}
	
	private void fillProjectTypeCombo() {
		for (ProjectType type : ProjectType.values()) {
			projectTypeCombo.add(type.getName());
		}
		projectTypeCombo.setText("");
		getWizardDescriptor().put(PROJECT_TYPE, ProjectType.NONE);
	}
	
	private void createMainClassEditor(Composite parent) {
		final Button mainClassButton = new Button(parent, SWT.CHECK);
		mainClassButton.setText("Create a Main class for the new project");
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		mainClassButton.setLayoutData(gridData);
		mainClassButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizardDescriptor().put(CREATE_MAIN, mainClassButton.getSelection());
			}
		});
		getWizardDescriptor().put(CREATE_MAIN, false);
	}
	
	private void createFinalNameEditor(Composite parent) {
		final Button finalNameButton = new Button(parent, SWT.CHECK);
		finalNameButton.setSelection(false);
		finalNameButton.setText("Final Name: ");
		final Text finalNameText = new Text(parent, SWT.BORDER);
		finalNameText.setText("");
		finalNameText.setEnabled(false);
		finalNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		finalNameButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = finalNameButton.getSelection();
				finalNameText.setEnabled(selected);
				if (selected) {
					getWizardDescriptor().put(FINAL_NAME, finalNameText.getText());
				} else {
					getWizardDescriptor().put(FINAL_NAME, null);
				}
			}
		});
		finalNameText.addModifyListener(new ModifyListener() {		
			@Override
			public void modifyText(ModifyEvent e) {
				getWizardDescriptor().put(FINAL_NAME, finalNameText.getText());
			}
		});
		Label filler = new Label(parent, SWT.NONE);
		filler.setText("");
	}
	
	private void createSetupPersistenceEditor(Composite parent) {
		setupPersistenceButton = new Button(parent, SWT.CHECK);
		setupPersistenceButton.setText("Set up persistence for the new project");
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		setupPersistenceButton.setLayoutData(gridData);
		setupPersistenceButton.setSelection(true);
		getWizardDescriptor().put(SETUP_PERSISTENCE, true);
		setupPersistenceButton.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = setupPersistenceButton.getSelection();
				providerCombo.setEnabled(selection);
				containerCombo.setEnabled(selection);
				getWizardDescriptor().put(SETUP_PERSISTENCE, selection);
			}
		});
	}
	
	public boolean isPageComplete() {
		return checkProjectName() && checkProjectLocation() && checkTopLevelPackage();
	}
	
	private boolean checkPageComplete() {
		if (!checkProjectName()) {
			setErrorMessage("The project name cannot be empty.");
			return false;
		} 
		if (!checkProjectLocation()) {
			setErrorMessage("The project location cannot be empty.");
			return false;
		}
		if (!checkTopLevelPackage()) {
			setErrorMessage("The top level package cannot be empty.");
			return false;
		}
		setErrorMessage(null);
		setMessage("Push the Finish button to create the project.");
		return true;
	}
	
	private boolean checkProjectName() {
		String projectName = (String)getWizardDescriptor().get(PROJECT_NAME);
		if (projectName == null || "".equals(projectName)) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean checkProjectLocation() {
		String projectLocation = (String)getWizardDescriptor().get(PROJECT_LOCATION);
		if (projectLocation == null || "".equals(projectLocation)) {
			return false;
		} else {
			return true;
		}
	}
	
	private boolean checkTopLevelPackage() {
		String topLevelPackage = (String)getWizardDescriptor().get(TOP_LEVEL_PACKAGE);
		if (topLevelPackage == null || "".equals(topLevelPackage)) {
			return false;
		} else {
			return true;
		}
	}
	
}
