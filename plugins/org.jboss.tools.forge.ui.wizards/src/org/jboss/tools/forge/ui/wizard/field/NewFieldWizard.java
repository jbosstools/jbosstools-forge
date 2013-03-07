package org.jboss.tools.forge.ui.wizard.field;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizard.WizardsPlugin;

public class NewFieldWizard extends AbstractForgeWizard {

	private NewFieldWizardPage newFieldWizardPage = new NewFieldWizardPage();

	public NewFieldWizard() {
		setWindowTitle("Create New Field");
	}

	@Override
	public void addPages() {
		addPage(newFieldWizardPage);
	}

	public void doExecute() {
		ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
		runtime.sendCommand("pick-up " + getTargetEntityLocation());
		runtime.sendCommand(
				"field " + getFieldType() + " --named " + getFieldName());
	}
	
	@Override
	public void doRefresh() {
		refreshResource(getTargetEntity().getResource());
	}
	
	private IJavaElement getTargetEntity() {
		IJavaElement result = null;
		try {
			String entityName = getEntityName();
			entityName = entityName.replace('.', '/') + ".java";
			IProject project = getProject(getProjectName());
			IJavaProject javaProject = JavaCore.create(project);
			result = javaProject.findElement(new Path(entityName));
		} catch (JavaModelException e) {
			WizardsPlugin.log(e);
		}
		return result;
	}
	
	private String getTargetEntityLocation() {
		return getTargetEntity().getResource().getLocation().toOSString();
	}
	
	private String getProjectName() {
		return (String)getWizardDescriptor().get(NewFieldWizardPage.PROJECT_NAME);		
	}
	
	private String getEntityName() {
		return (String)getWizardDescriptor().get(NewFieldWizardPage.ENTITY_NAME);		
	}
	
	private String getFieldName() {
		return (String)getWizardDescriptor().get(NewFieldWizardPage.FIELD_NAME);		
	}
	
	private String getFieldType() {
		return (String)getWizardDescriptor().get(NewFieldWizardPage.FIELD_TYPE);		
	}
	
}
