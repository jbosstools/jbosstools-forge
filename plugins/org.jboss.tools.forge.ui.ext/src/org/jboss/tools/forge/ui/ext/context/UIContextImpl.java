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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.proxy.Proxies;
import org.jboss.tools.forge.ext.core.FurnaceService;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class UIContextImpl extends AbstractUIContext {
	private UISelectionImpl<?> currentSelection;

	public UIContextImpl(UISelectionImpl<?> selection) {
		this.currentSelection = selection;
	}

	@Override
	public UISelectionImpl<?> getInitialSelection() {
		return currentSelection;
	}

	public static UIContextImpl createContext(IStructuredSelection selection) {
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
						File file = ((IResource) object).getLocation().toFile();
						result.add(Proxies.unwrap(converter.convert(file)));
					} else if (object instanceof IJavaElement) {
						File file;
						try {
							file = ((IJavaElement) object)
									.getCorrespondingResource().getLocation()
									.toFile();
							result.add(Proxies.unwrap(converter.convert(file)));
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						System.err.println("");
					}
				}
			}
		}
		UISelectionImpl<?> uiSelection = null;
		if (!result.isEmpty()) {
			uiSelection = new UISelectionImpl(result, selection);
		}
		return new UIContextImpl(uiSelection);
	}

	private static <T> Class<T> locateNativeClass(Class<T> type) {
		Class<T> result = type;
		AddonRegistry registry = FurnaceService.INSTANCE.getAddonRegistry();
		for (Addon addon : registry.getAddons()) {
			try {
				ClassLoader classLoader = addon.getClassLoader();
				result = (Class<T>) classLoader.loadClass(type.getName());
				break;
			} catch (ClassNotFoundException e) {
			}
		}
		return result;
	}

}
