package org.jboss.tools.forge.core.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;


public class ProjectImporter {
	
	private String baseDirPath;
	private String projectName;
	private IJobChangeListener listener;
	
	public ProjectImporter(String baseDirPath, String projectName, IJobChangeListener listener) {
		this.baseDirPath = baseDirPath;
		this.projectName = projectName;
		this.listener = listener;
	}
	
	public ProjectImporter(String baseDirPath, String projectName) {
		this(baseDirPath, projectName, null);
	}
	
	public void importProject() {		
	    Job job = new WorkspaceJob("Importing Forge project") {
	        public IStatus runInWorkspace(IProgressMonitor monitor) {
	          try {
	        	  MavenPlugin.getProjectConfigurationManager().importProjects(
	        			  getProjectToImport(), 
	        			  new ProjectImportConfiguration(), 
	        			  monitor);
	          } catch(CoreException ex) {
	            return ex.getStatus();
	          }
	          return Status.OK_STATUS;
	        }
	      };
	      job.setRule(MavenPlugin.getProjectConfigurationManager().getRule());
	      if (listener != null) {
	    	  job.addJobChangeListener(listener);
	      }
	      job.schedule();
	}
	
	private MavenProjectInfo createMavenProjectInfo() {
		MavenProjectInfo result = null;
		try {
			File projectDir = new File(baseDirPath, projectName);
			File pomFile = new File(projectDir, "pom.xml");
			Model model = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
			String pomName = projectName + "/" + "pom.xml";
			result = new MavenProjectInfo(pomName, pomFile, model, null);
		} catch (CoreException e) {
			
		}
		return result;
	}
	
	private Collection<MavenProjectInfo> getProjectToImport() {
		ArrayList<MavenProjectInfo> result = new ArrayList<MavenProjectInfo>(1);
		result.add(createMavenProjectInfo());
		return result;
	}
	
}
