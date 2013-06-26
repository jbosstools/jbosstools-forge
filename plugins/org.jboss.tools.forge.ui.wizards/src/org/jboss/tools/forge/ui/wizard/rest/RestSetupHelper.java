package org.jboss.tools.forge.ui.wizard.rest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class RestSetupHelper {
	
	private RestWizard wizard;
	private RestWizardPage wizardPage;
	private String installedFacets;
	private String currentDirectory;
	
	RestSetupHelper(RestWizard wizard) {
		this.wizard = wizard;
		this.wizardPage = (RestWizardPage)wizard.getPage(RestWizardPage.PAGE_NAME);
	}
	
	void checkRestSetup() {
		Job job = new Job("Setup Needed") {			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ForgeRuntime runtime = ForgeHelper.getDefaultRuntime();
				currentDirectory = runtime.sendCommand("pwd").trim();
				runtime.sendCommand("cd " + wizard.getProjectLocation());
				installedFacets = runtime.sendCommand("project list-facets");
				runtime.sendCommand("cd " + currentDirectory);
				return Status.OK_STATUS;
			}
		};
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						boolean setupNeeded = true;
						if (installedFacets.contains(getWebXmlString())) {
							wizardPage.setActivatorType(RestWizardPage.ACTIVATOR_TYPE_WEB_XML);
							setupNeeded = false;
						} else if (installedFacets.contains(getApplicationClassString())) {
							wizardPage.setActivatorType(RestWizardPage.ACTIVATOR_TYPE_APPLICATION_CLASS);
							setupNeeded = false;
						} else {
							wizardPage.setActivatorType(RestWizardPage.ACTIVATOR_TYPE_NONE);
						}
						wizardPage.setSetupNeeded(setupNeeded);
						wizardPage.setBusy(false);
					}				
				});
			}
		});
		job.schedule();
	}
	
	
	private String getWebXmlString() {
		return "+ forge.spec.jaxrs.webxml	[org.jboss.forge.spec.javaee.rest.RestWebXmlFacetImpl]";
	}
	
	private String getApplicationClassString() {
		return "+ forge.spec.jaxrs.applicationclass	[org.jboss.forge.spec.javaee.rest.RestApplicationFacetImpl]";
	}
	
}
