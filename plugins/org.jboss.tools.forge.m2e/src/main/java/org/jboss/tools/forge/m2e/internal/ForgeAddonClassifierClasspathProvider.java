/*************************************************************************************
 * Copyright (c) 2013 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Fred Bricon (Red Hat, Inc.) - initial API and implementation
 ************************************************************************************/
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
			IMavenProjectFacade mavenProjectFacade, IProgressMonitor monitor)
			throws CoreException {
		addMainFolder(runtimeClasspath, mavenProjectFacade, monitor);
	}

	@Override
	public void setTestClasspath(Set<IRuntimeClasspathEntry> runtimeClasspath,
			IMavenProjectFacade mavenProjectFacade, IProgressMonitor monitor)
			throws CoreException {
		setRuntimeClasspath(runtimeClasspath, mavenProjectFacade, monitor);
	}

	public boolean applies(IMavenProjectFacade mavenProjectFacade,
			String classifier) {
		return getClassifier().equals(classifier);
	}

}
