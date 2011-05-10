package org.jboss.tools.seam.forge.importer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.MavenPlugin;

public class ProjectConfigurationUpdater {
	
	private IProject project;
	
	public ProjectConfigurationUpdater(IProject project) {
		this.project = project;
	}
	
	public void updateProject() {		
	    Job job = new WorkspaceJob("Importing Forge project") {
	        public IStatus runInWorkspace(IProgressMonitor monitor) {
	          try {
	        	  MavenPlugin.getDefault().getProjectConfigurationManager().updateProjectConfiguration(
	        			  project, 
	        			  monitor);
	          } catch(CoreException ex) {
	            return ex.getStatus();
	          }
	          return Status.OK_STATUS;
	        }
	      };
	      job.setRule(MavenPlugin.getDefault().getProjectConfigurationManager().getRule());
	      job.schedule();
	}
	
}
