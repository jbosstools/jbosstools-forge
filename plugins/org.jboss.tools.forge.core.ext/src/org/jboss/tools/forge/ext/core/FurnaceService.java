/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ext.core;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.furnace.ContainerStatus;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.services.Imported;

/**
 * This is a singleton for the {@link Forge} class.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */

public enum FurnaceService {
	INSTANCE;

	private transient Furnace forge;
	private ConverterFactory converterFactory;

	private FurnaceService() {
	}

	public void setFurnace(Furnace forge) {
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

	public void waitUntilContainerIsStarted() throws InterruptedException {
		while (!getContainerStatus().isStarted()) {
			Thread.sleep(500);
		}
	}

	public ContainerStatus getContainerStatus() {
		return (forge == null) ? ContainerStatus.STOPPED : forge.getStatus();
	}

	public ConverterFactory getConverterFactory() {
		if (converterFactory == null) {
			converterFactory = lookup(ConverterFactory.class);
			while (converterFactory == null) {
				try {
					Thread.sleep(100);
					converterFactory = lookup(ConverterFactory.class);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		return converterFactory;
	}

	public <S> Imported<S> lookupImported(Class<S> service) {
		Imported<S> instance = null;
		if (forge != null) {
			instance = forge.getAddonRegistry().getServices(service);
		}
		return instance;
	}

	public <S> S lookup(Class<S> service) {
		Imported<S> instance = null;
		if (forge != null) {
			instance = forge.getAddonRegistry().getServices(service);
		}
		return (instance == null) ? null : instance.get();
	}

	public LockManager getLockManager() {
		return forge.getLockManager();
	}
}
