package org.jboss.tools.forge.ui.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class ScaffoldWizard extends Wizard implements IWorkbenchWizard {
	
	public ScaffoldWizard() {
		setWindowTitle("Scaffold JPA Entities");
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(ScaffoldWizard.class, "ScaffoldEntitiesWizBan.png"));	
	}

	@Override
	public void addPages() {
		addPage(new ScaffoldWizardPage());
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		// TODO Auto-generated method stub
		
	}

}
