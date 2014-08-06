/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;

public class ForgeInstallationLabelProvider extends LabelProvider implements ITableLabelProvider {

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ForgeRuntime) {
			ForgeRuntime forgeRuntime= (ForgeRuntime)element;
			switch(columnIndex) {
				case 0:
					return forgeRuntime.getName();
				case 1:
					return forgeRuntime.getLocation();
			}
		}
		return element.toString();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

}	
