/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.core.furnace;

import java.util.List;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.furnace.ContainerStatus;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.repositories.AddonRepository;
import org.jboss.forge.furnace.services.Imported;

/**
 * This is a singleton for the {@link Forge} class.
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */

public enum FurnaceService {
	INSTANCE;

	private transient Furnace furnace;

	private FurnaceService() {
	}

	public void setFurnace(Furnace forge) {
		this.furnace = forge;
	}

	public void start(final ClassLoader loader) {
		furnace.startAsync(loader);
	}

	public AddonRegistry getAddonRegistry() {
		return furnace.getAddonRegistry();
	}
	
	public List<AddonRepository> getRepositories() {
		return furnace.getRepositories();
	}

	public void stop() {
		furnace.stop();
	}

	public void waitUntilContainerIsStarted() throws InterruptedException {
		while (!getContainerStatus().isStarted()) {
			Thread.sleep(500);
		}
	}

	public ContainerStatus getContainerStatus() {
		return (furnace == null) ? ContainerStatus.STOPPED : furnace
				.getStatus();
	}

	public ConverterFactory getConverterFactory() {
		ConverterFactory converterFactory = lookup(ConverterFactory.class);
		while (converterFactory == null) {
			try {
				Thread.sleep(100);
				converterFactory = lookup(ConverterFactory.class);
			} catch (InterruptedException e) {
				break;
			}
		}
		return converterFactory;
	}

	public <S> Imported<S> lookupImported(Class<S> service) {
		Imported<S> instance = null;
		if (furnace != null) {
			instance = furnace.getAddonRegistry().getServices(service);
		}
		return instance;
	}

	public <S> S lookup(Class<S> service) {
		Imported<S> instance = null;
		if (furnace != null) {
			instance = furnace.getAddonRegistry().getServices(service);
		}
		return (instance == null) ? null : instance.get();
	}

	public LockManager getLockManager() {
		return furnace.getLockManager();
	}
}
