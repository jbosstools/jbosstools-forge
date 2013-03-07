package org.jboss.tools.forge.ui.wizard;

import java.util.Map;

import org.eclipse.ui.IWorkbenchWizard;

public interface IForgeWizard extends IWorkbenchWizard {

	void doExecute();
	void doRefresh();
	Map<Object, Object> getWizardDescriptor();
	String getStatusMessage();
}
