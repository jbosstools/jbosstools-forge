package org.jboss.tools.forge.ui.wizard;

import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.jboss.tools.forge.ui.wizards.WizardsPlugin;

public abstract class AbstractForgeWizardPage extends WizardPage {

	protected AbstractForgeWizardPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public IForgeWizard getWizard() {
		IWizard result = super.getWizard();
		if (!(result instanceof IForgeWizard)) {
			RuntimeException e = new RuntimeException("Forge wizard pages need to be hosted by a Forge wizard");
			WizardsPlugin.log(e);
			throw e;
		}
		return (IForgeWizard)result;
	}
	
	protected Map<Object, Object> getWizardDescriptor() {
		return getWizard().getWizardDescriptor();
	}
	
}
