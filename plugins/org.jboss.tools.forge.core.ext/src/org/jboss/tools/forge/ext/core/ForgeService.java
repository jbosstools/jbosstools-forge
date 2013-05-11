/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ext.core;

import org.jboss.forge.container.ContainerStatus;
import org.jboss.forge.container.Forge;
import org.jboss.forge.container.addons.AddonRegistry;
import org.jboss.forge.container.services.ExportedInstance;
import org.jboss.forge.convert.ConverterFactory;

/**
 * This is a singleton for the {@link Forge} class.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */

public enum ForgeService {
	INSTANCE;

	private transient Forge forge;
	private ConverterFactory converterFactory;

	private ForgeService() {
	}

	public void setForge(Forge forge) {
		this.forge = forge;
	}

	public void start(final ClassLoader loader) {
		forge.startAsync(loader);
	}

	public AddonRegistry getAddonRegistry() {
		return forge.getAddonRegistry();
	}

	public void stop() {
		forge.stop();
	}

	public ContainerStatus getContainerStatus() {
		return (forge == null) ? ContainerStatus.STOPPED : forge.getStatus();
	}

	public ConverterFactory getConverterFactory() {
		if (converterFactory == null) {
			converterFactory = lookup(ConverterFactory.class);
		}
		return converterFactory;
	}

	public <S> S lookup(Class<S> service) {
		ExportedInstance<S> exportedInstance = null;
		if (forge != null) {
			exportedInstance = forge.getAddonRegistry().getExportedInstance(
					service);
		}
		return (exportedInstance == null) ? null : exportedInstance.get();
	}
}
