/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.forge.reddeer.ui.wizard;

import java.util.List;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.api.Table;
import org.eclipse.reddeer.swt.api.TableItem;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;

/**
 * Reddeer implementation of forge Entities from tables wizard second page
 * @author jrichter
 *
 */
public class EntitiesFromTablesWizardSecondPage extends WizardPage {
	
	public EntitiesFromTablesWizardSecondPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Selects tables with given names
	 * @param tableNames names of the tables to select 
	 */
	public void selectTables(String... tableNames) {
		Table table = new DefaultTable();
		table.select(tableNames);
	}
	
	/**
	 * Select all tables
	 */
	public void selectAll() {
		new PushButton("Select All").click();
	}
	
	/**
	 * Deselect all tables
	 */
	public void selectNone() {
		new PushButton("Select None").click();
	}
	
	/**
	 * Get all listed tables
	 * @return List of TableItems representing the tables
	 */
	public List<TableItem> getAllTables() {
		return new DefaultTable().getItems();
	}
}
