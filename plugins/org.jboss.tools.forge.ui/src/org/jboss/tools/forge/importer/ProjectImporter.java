package org.jboss.tools.forge.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;


public class ProjectImporter {
	
	private String baseDirPath;
	private String projectName;
	
	public ProjectImporter(String baseDirPath, String projectName) {
		this.baseDirPath = baseDirPath;
		this.projectName = projectName;
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
