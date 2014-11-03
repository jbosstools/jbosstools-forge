/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.importer;

import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.tools.forge.ui.internal.ForgeUIPlugin;
import org.jboss.tools.forge.ui.internal.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.internal.ext.wizards.WizardListener;

/**
 * A project listener that imports created projects into the Eclipse Workspace
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public enum ImportEclipseProjectListener implements ProjectListener,
		WizardListener {

	INSTANCE;

	private Set<Project> projects = new HashSet<Project>();

	@Override
	public void projectCreated(Project project) {
		projects.add(project);
	}

	public boolean projectsAvailableForImport() {
		return !projects.isEmpty();
	}

	public void doImport() {
		try {
			for (Project project : projects) {
				Resource<?> projectRoot = project.getRoot();
				String baseDirPath = projectRoot.getParent()
						.getFullyQualifiedName();
				String moduleLocation = projectRoot.getName();
				String projectName = project.getFacet(MetadataFacet.class)
						.getProjectName();
				ProjectImporter projectImporter = new ProjectImporter(
						baseDirPath, moduleLocation, projectName);
				projectImporter.importProject();
			}
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
		} finally {
			projects.clear();
		}
	}

	@Override
	public void onFinish(UIContextImpl context) {
		doImport();
	}

	@Override
	public void dispose() {
		projects.clear();
	}

}
