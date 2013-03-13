package org.jboss.tools.forge.ui.wizard.field;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;
import org.jboss.tools.forge.ui.wizard.AbstractForgeWizard;
import org.jboss.tools.forge.ui.wizard.WizardsPlugin;
import org.jboss.tools.forge.ui.wizard.util.WizardsHelper;

public class NewFieldWizard extends AbstractForgeWizard {

	private NewFieldWizardPage newFieldWizardPage = new NewFieldWizardPage();

	public NewFieldWizard() {
		setWindowTitle("Create New Field");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection sel) {
		super.init(workbench, sel);
		initializeProject(sel);
	}
	
	@SuppressWarnings("rawtypes")
	private void initializeProject(IStructuredSelection sel) {
		Iterator iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object instanceof IResource) {
				IProject project = ((IResource)object).getProject();
				if (WizardsHelper.isJPAProject(project)) {
					getWizardDescriptor().put(
							NewFieldWizardPage.PROJECT_NAME, 
							project.getName());
					return;
				}
			}
		}
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
	
	@Override
	public String getStatusMessage() {
		return "Creating new field '" + getFieldName() + "'.";
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
