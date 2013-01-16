package org.jboss.tools.forge.ui.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ScaffoldWizard extends Wizard implements IWorkbenchWizard {
	
	private ScaffoldWizardPage scaffoldWizardPage = new ScaffoldWizardPage();
	private StartForgePage startForgePage = new StartForgePage();
	
	public ScaffoldWizard() {
		setWindowTitle("Scaffold JPA Entities");
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(ScaffoldWizard.class, "ScaffoldEntitiesWizBan.png"));
	}

	@Override
	public void addPages() {
		addPage(startForgePage);
		addPage(scaffoldWizardPage);
	}
	
	@Override
	public IWizardPage getStartingPage() {
		if (ForgeHelper.isForgeRunning() && ForgeHelper.isHibernateToolsPluginAvailable()) {
			return scaffoldWizardPage;
		}
		return startForgePage;
	}
	
	@Override
	public boolean performFinish() {
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection sel) {
	}

}
