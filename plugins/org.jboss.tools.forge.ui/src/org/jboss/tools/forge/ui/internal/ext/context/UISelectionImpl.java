/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.tools.forge.ui.internal.ext.context;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.util.Assert;

public class UISelectionImpl<T> implements UISelection<T> {

	private final List<T> selection;
	private IResource resource;

	public UISelectionImpl(List<T> selection, IStructuredSelection ss) {
		Assert.notNull(selection, "Selection must not be null.");
		this.selection = Collections.unmodifiableList(selection);
		this.resource = extractSelection(ss);
	}

	@Override
	public T get() {
		return selection.isEmpty() ? null : selection.get(0);
	}

	@Override
	public Iterator<T> iterator() {
		return selection.iterator();
	}

	@Override
	public int size() {
		return selection.size();
	}

	@Override
	public boolean isEmpty() {
		return selection.isEmpty();
	}

	public IResource getResource() {
		return resource;
	}

	private IResource extractSelection(IStructuredSelection ss) {
		if (ss == null)
			return null;
		Object element = ss.getFirstElement();
		if (element instanceof IResource)
			return (IResource) element;
		if (!(element instanceof IAdaptable))
			return null;
		IAdaptable adaptable = (IAdaptable) element;
		Object adapter = adaptable.getAdapter(IResource.class);
		return (IResource) adapter;
	}

}
