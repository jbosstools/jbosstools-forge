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
	private String installedFacets;
	private String currentDirectory;
	
	RestSetupHelper(RestWizard wizard) {
		this.wizard = wizard;
	}
	
	void checkIfSetupNeeded() {
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
						wizard.setSetupNeeded(!installedFacets.contains(getSetupString()));
						wizard.setBusy(false);
					}				
				});
			}
		});
		job.schedule();
	}
	
	private String getSetupString() {
		return "+ forge.spec.jaxrs.webxml	[org.jboss.forge.spec.javaee.rest.RestWebXmlFacetImpl]";
	}
	
}
