/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.context;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.jboss.forge.addon.ui.context.UIRegion;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.util.Assert;

public class UISelectionImpl<T> implements UISelection<T> {

	private final List<T> selection;
	private IResource resource;
	private ITextSelection textSelection;

	public UISelectionImpl(List<T> selection, IStructuredSelection ss, ITextSelection textSelection) {
		Assert.notNull(selection, "Selection must not be null.");
		this.selection = Collections.unmodifiableList(selection);
		this.resource = extractSelection(ss);
		this.textSelection = textSelection;
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

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Optional<UIRegion<T>> getRegion() {
		UIRegion<T> region = null;
		if (selection.size() > 0 && textSelection != null) {
			T resource = get();
			region = new UIRegionImpl(resource, textSelection);
		}
		return Optional.ofNullable(region);
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
