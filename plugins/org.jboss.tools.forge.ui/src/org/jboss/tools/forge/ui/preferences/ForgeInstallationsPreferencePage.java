package org.jboss.tools.forge.ui.preferences;


import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeEmbeddedRuntime;
import org.jboss.tools.forge.core.process.ForgeExternalRuntime;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class ForgeInstallationsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
				
	private final int DEFAULT_COLUMN_WIDTH = 350/3 +1;

	private CheckboxTableViewer runtimesTableViewer	;
	private Button removeButton;
	private Button editButton;
	private ArrayList<ForgeRuntime> runtimes = null;
	private ForgeRuntime defaultRuntime = null;
	
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
		enableButtons();
		return parent;
	}
	
	private void createPageBody(Composite parent) {
		Composite pageBody = createPageBodyControl(parent);					
		createTitleLabel(pageBody);				
		createRuntimesArea(pageBody);			
		createButtonsArea(pageBody);						
	}

	private void createButtonsArea(Composite parent) {
		Composite buttons = createButtonsComposite(parent);
    	createAddButton(buttons);		
		createEditButton(buttons);		
		createRemoveButton(buttons);		
	}

	private void createRemoveButton(Composite parent) {
		removeButton = new Button(parent, SWT.PUSH);
		removeButton.setText("&Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				ISelection selection = runtimesTableViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)selection).getFirstElement();
					if (object != null && object instanceof ForgeRuntime) {
						runtimes.remove(object);
						refreshForgeInstallations();
					}
				}
			}
		});
	}

	private void createAddButton(Composite parent) {
		Button addButton = new Button(parent, SWT.PUSH);
		addButton.setText("&Add...");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				ForgeInstallationDialog dialog = new ForgeInstallationDialog(null);
				dialog.initialize("Add Forge Runtime", "", "");
				if (dialog.open() != Dialog.CANCEL) {
					runtimes.add(new ForgeExternalRuntime(dialog.getName(), dialog.getLocation()));
					refreshForgeInstallations();
				}
			}
		});
	}
	
	private void createEditButton(Composite parent) {
		editButton = new Button(parent, SWT.PUSH);
		editButton.setText("&Edit...");
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		editButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				ISelection selection = runtimesTableViewer.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)selection).getFirstElement();
					if (object != null && object instanceof ForgeExternalRuntime) {
						ForgeExternalRuntime installation = (ForgeExternalRuntime)object;
						ForgeInstallationDialog dialog = new ForgeInstallationDialog(null);
						dialog.initialize("Edit Forge Runtime", installation.getName(), installation.getLocation());
						if (dialog.open() != Dialog.CANCEL) {
							installation.setName(dialog.getName());
							installation.setLocation(dialog.getLocation());
							refreshForgeInstallations();
						}
					}
				}
			}
		});
	}

	private Composite createButtonsComposite(Composite parent) {
		Composite buttons = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
    	buttons.setLayout(layout);
    	GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
    	buttons.setLayoutData(gd);
		return buttons;
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
		runtimesTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}
		});
		runtimesTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(final CheckStateChangedEvent event) {
				Object object = event.getElement();
				if (object != null && object instanceof ForgeRuntime) {
					defaultRuntime = (ForgeRuntime)object;
					refreshForgeInstallations();
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
		l.setText("Add, remove or edit Forge runtimes. By default, the checked Forge runtime is used when launching Forge.");
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
		for (ForgeRuntime runtime : ForgeRuntimesPreferences.INSTANCE.getRuntimes()) {
			ForgeRuntime copy = null;
			if (runtime instanceof ForgeEmbeddedRuntime) {
				copy = runtime;
			} else if (runtime instanceof ForgeExternalRuntime) {
				copy = new ForgeExternalRuntime(runtime.getName(), runtime.getLocation());
			}
			if (runtime == ForgeRuntimesPreferences.INSTANCE.getDefault()) {
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

	private void enableButtons() {
		Object selectedObject = null;
		IStructuredSelection selection = (IStructuredSelection) runtimesTableViewer.getSelection();
		if (selection != null) {
			selectedObject = selection.getFirstElement();
		}
		if (selectedObject == null 
				|| (selectedObject instanceof ForgeEmbeddedRuntime)) {
			removeButton.setEnabled(false);
			editButton.setEnabled(false);
		} else {
			removeButton.setEnabled(selectedObject != runtimesTableViewer.getCheckedElements()[0]);
			editButton.setEnabled(true);
		}
	}	
	
	public boolean performOk() {
		final boolean[] canceled = new boolean[] {false};
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				ForgeRuntime[] runtimes = (ForgeRuntime[])runtimesTableViewer.getInput();
				ForgeRuntime defaultRuntime = (ForgeRuntime)runtimesTableViewer.getCheckedElements()[0];
				ForgeRuntimesPreferences.INSTANCE.setRuntimes(runtimes, defaultRuntime);
			}
		});		
		if(canceled[0]) {
			return false;
		}
		return super.performOk();
	}	
	
}
