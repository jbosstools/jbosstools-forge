package org.jboss.tools.forge.ui.wizard.scaffold;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ScaffoldWizardHelper {
	
	private ScaffoldWizard wizard;
	private String installedFacets;
	private String currentDirectory;
	
	ScaffoldWizardHelper(ScaffoldWizard wizard) {
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
	
	private String getScaffoldType() {
		return (String)wizard.getWizardDescriptor().get(ScaffoldProjectWizardPage.SCAFFOLD_TYPE);
	}
	
	private String getSetupString() {
		String scaffoldType = getScaffoldType();
		if (ScaffoldProjectWizardPage.SCAFFOLD_TYPE_FACES.equals(scaffoldType)) {
			return "+ faces	[org.jboss.forge.scaffold.faces.FacesScaffold]";
		} else if (ScaffoldProjectWizardPage.SCAFFOLD_TYPE_ANGULARJS.equals(scaffoldType)) {
			return "+ angularjs	[org.jboss.forge.scaffold.angularjs.AngularScaffold]";
		} else {
			return "INSTALLED"; // This string is always present;
		}
				
	}
	
}
