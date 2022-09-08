/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Contributors:
 *     Fred Bricon (Red Hat, Inc.) - initial API and implementation
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.m2e.internal;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.jdt.AbstractClassifierClasspathProvider;

/**
 * Adds support for workspace projects referenced with the
 * <code>forge-addon</code> classifier in Eclipse Launch Configurations.
 *
 * @author Fred Bricon
 *
 */
public class ForgeAddonClassifierClasspathProvider extends
		AbstractClassifierClasspathProvider {

	public String getClassifier() {
		return "forge-addon";
	}

	@Override
	public void setRuntimeClasspath(
			Set<IRuntimeClasspathEntry> runtimeClasspath,
			IMavenProjectFacade mavenProjectFacade,
			IProgressMonitor monitor, int classpathProperty)
			throws CoreException {
		addMainFolder(runtimeClasspath, mavenProjectFacade, monitor, classpathProperty);
	}

	@Override
	public void setTestClasspath(Set<IRuntimeClasspathEntry> runtimeClasspath,
			IMavenProjectFacade mavenProjectFacade,
			IProgressMonitor monitor, int classpathProperty) throws CoreException {
		setRuntimeClasspath(runtimeClasspath, mavenProjectFacade, monitor, classpathProperty);
	}

	public boolean applies(IMavenProjectFacade mavenProjectFacade,
			String classifier) {
		return getClassifier().equals(classifier);
	}

}
