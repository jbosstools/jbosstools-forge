package org.jboss.tools.forge.ui.ext.cli;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.tools.forge.ui.ext.importer.ProjectImporter;

public class CommandExecutionListenerImpl 
extends AbstractCommandExecutionListener 
implements ProjectListener {
	
	private List<Project> projects = new ArrayList<Project>();

	@Override
	public void postCommandExecuted(
			UICommand command, 
			UIExecutionContext uiExecutionContext,
			Result result) {
		if (!projects.isEmpty()) {
			importProjects();
			projects.clear();
		}
		UISelection<?> selection = uiExecutionContext.getUIContext().getInitialSelection();
		Iterator<?> iterator = selection.iterator();
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (object instanceof Resource<?>) {
				refresh((Resource<?>)object);
			}
		}
		Object object = uiExecutionContext.getUIContext().getSelection();
		if (object != null) {
			if (object instanceof Resource<?>) {
				select((Resource<?>)object);
			}
		}
	}
	
	private void refresh(Resource<?> resource) {
		System.out.println("refreshing initial selection: " + resource.getFullyQualifiedName());
	}
	
	private void select(Resource<?> resource) {
		System.out.println("highlighting final selection: " + resource.getFullyQualifiedName());
	}
	
	private void importProjects() {
		for (Project project : projects) {
			Resource<?> projectRoot = project.getRoot();
			String baseDirPath = projectRoot.getParent()
					.getFullyQualifiedName();
			String moduleLocation = projectRoot.getName();
			String projectName = project.getFacet(MetadataFacet.class)
					.getProjectName();
			ProjectImporter projectImporter = new ProjectImporter(baseDirPath,
					moduleLocation, projectName);
			projectImporter.importProject();
		}
	}

	@Override
	public void projectCreated(Project project) {
		projects.add(project);
	}

}
