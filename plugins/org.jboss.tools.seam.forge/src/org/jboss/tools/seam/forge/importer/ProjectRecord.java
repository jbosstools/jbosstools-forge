package org.jboss.tools.seam.forge.importer;

import java.io.File;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class ProjectRecord {

    File projectFile;
    
    String projectName;
  
    IProjectDescription description;
  
    /**
     * Create a record for a project based on the info in the file.
     * 
     * @param file
     */
    ProjectRecord(File file) {
      projectFile = file;
      setProjectName();
    }
  
    /**
     * Set the name of the project based on the projectFile.
     */
    private void setProjectName() {
      IProjectDescription newDescription = null;
      try {
        IPath path = new Path(projectFile.getPath());
        // if the file is in the default location, use the directory
        // name as the project name
        newDescription = ResourcesPlugin.getWorkspace().loadProjectDescription(path);
        
        if(isDefaultLocation(path)) {
          // projectName = path.segment(path.segmentCount() - 2);
          // newDescription = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
        }
      } catch(CoreException e) {
        // no good couldn't get the name
      }
  
      if(newDescription == null) {
        this.description = null;
        projectName = ""; //$NON-NLS-1$
      } else {
        this.description = newDescription;
        projectName = this.description.getName();
      }
    }
  
    /**
     * Returns whether the given project description file path is in the default location for a project
     * 
     * @param path The path to examine
     * @return Whether the given path is the default location for a project
     */
    private boolean isDefaultLocation(IPath path) {
      // The project description file must at least be within the project, which is within the workspace location
      return path.segmentCount() > 1 && path.removeLastSegments(2).toFile().equals(Platform.getLocation().toFile());
    }
  
    /**
     * Get the name of the project
     * 
     * @return String
     */
    public String getProjectName() {
      return projectName;
    }
}
