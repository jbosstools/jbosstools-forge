package org.jboss.tools.forge.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.MavenProjectInfo;
import org.eclipse.m2e.core.project.ProjectImportConfiguration;
import org.jboss.tools.forge.core.internal.ForgeCorePlugin;

public class ProjectTools {

	public static void updateProjectConfiguration(final IProject project) {		
	    Job job = new WorkspaceJob("Updating project configuration") {
	        public IStatus runInWorkspace(IProgressMonitor monitor) {
	          try {
	        	  MavenPlugin.getProjectConfigurationManager().updateProjectConfiguration(
	        			  project, 
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
	
	public static void importProject(final String baseDirPath, final String projectName) {		
	    Job job = new WorkspaceJob("Importing Forge project") {
	        public IStatus runInWorkspace(IProgressMonitor monitor) {
	          try {
	        	  MavenPlugin.getProjectConfigurationManager().importProjects(
	        			  getProjectToImport(baseDirPath, projectName), 
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
	
	private static Collection<MavenProjectInfo> getProjectToImport(String baseDirPath, String projectName) {
		ArrayList<MavenProjectInfo> result = new ArrayList<MavenProjectInfo>(1);
		result.add(createMavenProjectInfo(baseDirPath, projectName));
		return result;
	}
	
	private static MavenProjectInfo createMavenProjectInfo(String baseDirPath, String projectName) {
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
	

	
}
