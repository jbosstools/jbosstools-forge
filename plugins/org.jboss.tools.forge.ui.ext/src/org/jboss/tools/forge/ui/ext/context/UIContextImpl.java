/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.ext.context;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonFilter;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.lock.LockManager;
import org.jboss.forge.furnace.lock.LockMode;
import org.jboss.forge.furnace.proxy.Proxies;
import org.jboss.tools.forge.ext.core.FurnaceService;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class UIContextImpl extends AbstractUIContext {
	private UISelectionImpl<?> currentSelection;

	public UIContextImpl(UISelectionImpl<?> selection) {
		this.currentSelection = selection;
	}

	public UIContextImpl(IStructuredSelection selection) {
		List<Object> selectedElements = selection == null ? Collections.EMPTY_LIST
				: selection.toList();
		List<Object> result = new LinkedList<Object>();
		ConverterFactory converterFactory = FurnaceService.INSTANCE
				.lookup(ConverterFactory.class);
		if (converterFactory != null) {
			Converter<File, Resource> converter = converterFactory
					.getConverter(File.class, locateNativeClass(Resource.class));

			if (selectedElements.isEmpty()) {
				// Get the Workspace directory path
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				File workspaceDirectory = workspace.getRoot().getLocation()
						.toFile();
				Object convertedObj = converter.convert(workspaceDirectory);
				result.add(Proxies.unwrap(convertedObj));
			} else {
				for (Object object : selectedElements) {
					if (object instanceof IResource) {
						IPath location = ((IResource) object).getLocation();
						if (location != null) {
							File file = location.toFile();
							result.add(Proxies.unwrap(converter.convert(file)));
						}
					} else if (object instanceof IJavaElement) {
						try {
							IJavaElement javaElem = (IJavaElement) object;
							IResource correspondingResource = javaElem
									.getCorrespondingResource();
							if (correspondingResource != null) {
								IPath location = correspondingResource
										.getLocation();
								if (location != null) {
									File file = location.toFile();
									result.add(Proxies.unwrap(converter
											.convert(file)));
								}
							}
						} catch (JavaModelException e) {
							ForgeUIPlugin.log(e);
						}
					}
				}
			}
		}
		this.currentSelection = new UISelectionImpl(result, selection);
	}

	@Override
	public UISelectionImpl<?> getInitialSelection() {
		return currentSelection;
	}

	private static <T> Class<T> locateNativeClass(final Class<T> type) {
		LockManager manager = FurnaceService.INSTANCE.getLockManager();
		Class<T> result = manager.performLocked(LockMode.READ,
				new Callable<Class<T>>() {

					@Override
					public Class<T> call() throws Exception {
						AddonRegistry registry = FurnaceService.INSTANCE
								.getAddonRegistry();
						Class<T> result = type;
						for (Addon addon : registry
								.getAddons(new AddonFilter() {
									@Override
									public boolean accept(Addon addon) {
										return addon.getStatus().isStarted();
									}
								})) {
							try {
								ClassLoader classLoader = addon
										.getClassLoader();
								result = (Class<T>) classLoader.loadClass(type
										.getName());
								break;
							} catch (ClassNotFoundException e) {
							}
						}
						return result;
					}
				});
		return result;
	}

}
