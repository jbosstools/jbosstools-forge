package org.jboss.tools.forge.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.forge.launching.ForgeInstallation;

public class ForgeInstallationLabelProvider extends LabelProvider implements ITableLabelProvider {

	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ForgeInstallation) {
			ForgeInstallation forgeInstallation= (ForgeInstallation)element;
			switch(columnIndex) {
				case 0:
					return forgeInstallation.getName();
				case 1:
					return forgeInstallation.getLocation();
			}
		}
		return element.toString();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

}	
