/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.importer;

import java.util.HashSet;
import java.util.Set;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectListener;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.tools.forge.ui.ext.context.UIContextImpl;
import org.jboss.tools.forge.ui.ext.wizards.WizardListener;

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

	public void doImport() {
		for (Project project : projects) {
			DirectoryResource projectRoot = project.getRootDirectory();
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
	public void onFinish(UIContextImpl context) {
		doImport();
	}

	@Override
	public void dispose() {
		projects.clear();
	}

}
