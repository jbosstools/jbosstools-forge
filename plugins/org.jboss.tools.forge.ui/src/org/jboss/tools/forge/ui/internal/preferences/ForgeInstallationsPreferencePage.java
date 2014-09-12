/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.preferences;


import java.util.ArrayList;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeFactory;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeType;

public class ForgeInstallationsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
				
	private final int DEFAULT_COLUMN_WIDTH = 350/3 +1;

	private CheckboxTableViewer runtimesTableViewer	;
	private ArrayList<ForgeRuntime> runtimes = null;
	private ForgeRuntime defaultRuntime = null;
	private boolean refreshNeeded = false;
	
	public ForgeInstallationsPreferencePage() {
		super("Installed Forge Runtimes");
	}

	public void init(IWorkbench workbench) {
	}

	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();		
		createLayout(parent);		
		createWrapLabel(parent);
		createVerticalSpacer(parent);	
		createPageBody(parent);
		initializeForgeInstallations();		
		return parent;
	}
	
	private void createPageBody(Composite parent) {
		Composite pageBody = createPageBodyControl(parent);					
		createTitleLabel(pageBody);				
		createRuntimesArea(pageBody);			
	}

	private void createTitleLabel(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Installed Forge Runtimes:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = false;
		label.setLayoutData(gd);
	}

	private void createRuntimesArea(Composite parent) {
		Table runtimesTable = createRuntimesTable(parent);	
		createNameColumn(runtimesTable);
		createLocationColumn(runtimesTable);		
		createRuntimesTableViewer(runtimesTable);
	}

	private void createRuntimesTableViewer(Table table) {
		runtimesTableViewer = new CheckboxTableViewer(table);			
		runtimesTableViewer.setLabelProvider(new ForgeInstallationLabelProvider());
		runtimesTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		runtimesTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(final CheckStateChangedEvent event) {
				if (runtimesTableViewer.getCheckedElements().length == 0) {
					runtimesTableViewer.setChecked(defaultRuntime, true);
				} else {
					Object object = event.getElement();
					if (object != null && object instanceof ForgeRuntime && !object.equals(defaultRuntime)) {
						defaultRuntime = (ForgeRuntime)object;
						refreshForgeInstallations();
						refreshNeeded = true;
					}
				}
			}
		});		
	}
		
	private void createLocationColumn(Table table) {
		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("Location"); 
		column.setWidth(DEFAULT_COLUMN_WIDTH);
	}

	private void createNameColumn(Table table) {
		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("Name"); 
		column.setWidth(DEFAULT_COLUMN_WIDTH);
	}

	private Table createRuntimesTable(Composite parent) {
		Table runtimesTable= new Table(parent, SWT.CHECK | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 250;
		gd.widthHint = 350;
		runtimesTable.setLayoutData(gd);
		runtimesTable.setHeaderVisible(true);
		runtimesTable.setLinesVisible(true);
		return runtimesTable;
	}
	
	private Composite createPageBodyControl(Composite ancestor) {
    	Composite result = new Composite(ancestor, SWT.NONE);
    	result.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	result.setLayoutData(gd);
		return result;
	}
	
	private void createLayout(Composite ancestor) {
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		ancestor.setLayout(layout);
	}

	private Label createWrapLabel(Composite parent) {
		Label l = new Label(parent, SWT.NONE | SWT.WRAP);
		l.setFont(parent.getFont());
		l.setText("Select the default Forge runtime.");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 250;
		l.setLayoutData(gd);
		return l;
	}
	
	private void createVerticalSpacer(Composite parent) {
		Label lbl = new Label(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = ((GridLayout)parent.getLayout()).numColumns;
		lbl.setLayoutData(gd);
	}
	
	private void initializeForgeInstallations() {
		runtimes = new ArrayList<ForgeRuntime>();
		for (ForgeRuntime runtime : ForgeCorePreferences.INSTANCE.getRuntimes()) {
			ForgeRuntime copy = null;
			if (ForgeRuntimeType.EMBEDDED.equals(runtime.getType())) {
				copy = runtime;
			} else if (ForgeRuntimeType.EXTERNAL.equals(runtime.getType())) {
				copy = ForgeRuntimeFactory.INSTANCE.createForgeRuntime(runtime.getName(), runtime.getLocation());
			}
			if (runtime == ForgeCorePreferences.INSTANCE.getDefaultRuntime()) {
				defaultRuntime = copy;
			}
			runtimes.add(copy);
		}
		refreshForgeInstallations();
	}

	private void refreshForgeInstallations() {
		runtimesTableViewer.setInput((ForgeRuntime[])runtimes.toArray(new ForgeRuntime[runtimes.size()]));
		runtimesTableViewer.setCheckedElements(new Object[] { defaultRuntime });
		runtimesTableViewer.refresh();
	}

	public boolean performOk() {
		if (refreshNeeded) {
			final boolean[] canceled = new boolean[] {false};
			BusyIndicator.showWhile(null, new Runnable() {
				public void run() {
					ForgeRuntime[] runtimes = (ForgeRuntime[])runtimesTableViewer.getInput();
					ForgeRuntime defaultRuntime = (ForgeRuntime)runtimesTableViewer.getCheckedElements()[0];
					ForgeCorePreferences.INSTANCE.setRuntimes(runtimes, defaultRuntime);
					refreshNeeded = false;
				}
			});	
			if(canceled[0]) {
				return false;
			}
		}
		return super.performOk();
	}	
	
}
