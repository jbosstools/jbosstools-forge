package org.jboss.tools.seam.forge.importer;

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
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.project.MavenProjectInfo;
import org.maven.ide.eclipse.project.ProjectImportConfiguration;


public class ProjectImporter {
	
	private String baseDirPath;
	private String projectName;
	
	public ProjectImporter(String baseDirPath, String projectName) {
		this.baseDirPath = baseDirPath;
		this.projectName = projectName;
	}
	
	public void importProject() {		
		final MavenPlugin mavenPlugin = MavenPlugin.getDefault();
	    Job job = new WorkspaceJob("Importing Forge project") {
	        public IStatus runInWorkspace(IProgressMonitor monitor) {
	          try {
	        	  mavenPlugin.getProjectConfigurationManager().importProjects(
	        			  getProjectToImport(), 
	        			  new ProjectImportConfiguration(), 
	        			  monitor);
	          } catch(CoreException ex) {
	            return ex.getStatus();
	          }
	          return Status.OK_STATUS;
	        }
	      };
	      job.setRule(mavenPlugin.getProjectConfigurationManager().getRule());
	      job.schedule();
	}
	
	private MavenProjectInfo createMavenProjectInfo() {
		MavenProjectInfo result = null;
		try {
			File projectDir = new File(baseDirPath, projectName);
			File pomFile = new File(projectDir, IMavenConstants.POM_FILE_NAME);
			Model model = MavenPlugin.getDefault().getMavenModelManager().readMavenModel(pomFile);
			String pomName = projectName + "/" + IMavenConstants.POM_FILE_NAME;
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
