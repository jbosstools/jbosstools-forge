/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.quickaccess;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @since 3.3
 * 
 */
public abstract class QuickAccessProvider {

	private List<QuickAccessElement> sortedElements;

	/**
	 * Returns the unique ID of this provider.
	 * 
	 * @return the unique ID
	 */
	public abstract String getId();

	/**
	 * Returns the name of this provider to be displayed to the user.
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Returns the image descriptor for this provider.
	 * 
	 * @return the image descriptor, or null if not defined
	 */
	public abstract ImageDescriptor getImageDescriptor();

	/**
	 * Returns the elements provided by this provider.
	 * 
	 * @return this provider's elements
	 */
	public abstract List<QuickAccessElement> getElements();

	public List<QuickAccessElement> getElementsSorted() {
		if (sortedElements == null) {
			sortedElements = getElements();
			Collections.sort(sortedElements, new Comparator<QuickAccessElement>() {
				@Override
				public int compare(QuickAccessElement e1, QuickAccessElement e2) {
					return e1.getLabel().compareToIgnoreCase(e2.getLabel());
				}
			});
		}
		return sortedElements;
	}

	/**
	 * Returns the element for the given ID if available, or null if no matching
	 * element is available.
	 * 
	 * @param id
	 *            the ID of an element
	 * @return the element with the given ID, or null if not found.
	 */
	public abstract QuickAccessElement getElementForId(String id);

	public boolean isAlwaysPresent() {
		return false;
	}

	public void reset() {
		sortedElements = null;
		doReset();
	}

	protected abstract void doReset();
}
