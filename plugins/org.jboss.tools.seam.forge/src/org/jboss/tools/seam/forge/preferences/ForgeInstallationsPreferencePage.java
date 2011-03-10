package org.jboss.tools.seam.forge.preferences;


import java.util.Iterator;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.jboss.tools.seam.forge.launching.ForgeRuntime;

public class ForgeInstallationsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
				
	private final int DEFAULT_COLUMN_WIDTH = 350/3 +1;

//	private InstalledForgeRuntimesBlock fJREBlock;
	
//	private Table runtimesTable;
	private CheckboxTableViewer runtimesTableViewer	;
	private Button removeButton;
	private Button editButton;
	private int sortColumn = 0;
	
	public ForgeInstallationsPreferencePage() {
		super("Installed Forge Runtimes");
	}

	public void init(IWorkbench workbench) {
	}

//	private void initDefaultVM() {
//		IVMInstall realDefault= JavaRuntime.getDefaultVMInstall();
//		if (realDefault != null) {
////			IVMInstall[] vms= fJREBlock.getJREs();
//			IVMInstall[] vms= (IVMInstall[])runtimesTableViewer.getInput();
//			for (int i = 0; i < vms.length; i++) {
//				IVMInstall fakeVM= vms[i];
//				if (fakeVM.equals(realDefault)) {
//					verifyDefaultVM(fakeVM);
//					break;
//				}
//			}
//		}
//	}
	
	protected Control createContents(Composite ancestor) {
//		initializeDialogUnits(ancestor);		
		noDefaultAndApplyButton();		
		createLayout(ancestor);		
		createWrapLabel(ancestor);
		createVerticalSpacer(ancestor);	
		createPageBody(ancestor);
//		createInstalledForgeRuntimesBlock(ancestor);					
		initForgeInstallations();		
		sortByName();		
		enableButtons();
//		applyDialogFont(ancestor);
		return ancestor;
	}
	
	private void createPageBody(Composite ancestor) {
		Composite pageBody = createPageBodyControl(ancestor);					
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

	private void createRemoveButton(Composite buttons) {
		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText("&Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				ForgeInstallationsPreferenceHelper.removeVMs();
			}
		});
	}

	private void createAddButton(Composite buttons) {
		Button addButton = new Button(buttons, SWT.PUSH);
		addButton.setText("&Add...");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				ForgeInstallationsPreferenceHelper.addVM();
			}
		});
	}
	
	private void createEditButton(Composite buttons) {
		editButton = new Button(buttons, SWT.PUSH);
		editButton.setText("&Edit...");
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		editButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				ForgeInstallationsPreferenceHelper.editVM();
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
//		runtimesTableViewer.addCheckStateListener(new ICheckStateListener() {
//			public void checkStateChanged(CheckStateChangedEvent event) {
//				if (event.getChecked()) {
//					setCheckedJRE((IVMInstall)event.getElement());
//				} else {
//					setCheckedJRE(null);
//				}
//			}
//		});		
		runtimesTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				if (!runtimesTableViewer.getSelection().isEmpty()) {
					ForgeInstallationsPreferenceHelper.editVM();
				}
			}
		});
	}
		
	private void createLocationColumn(Table table) {
		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("Location"); 
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByLocation();
			}
		});
		column.setWidth(DEFAULT_COLUMN_WIDTH);
	}

	private void createNameColumn(Table table) {
		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("Name"); 
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByName();
			}
		});
		column.setWidth(DEFAULT_COLUMN_WIDTH);
	}

	private Table createRuntimesTable(Composite parent) {
		Table runtimesTable= new Table(parent, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 250;
		gd.widthHint = 350;
		runtimesTable.setLayoutData(gd);
		runtimesTable.setHeaderVisible(true);
		runtimesTable.setLinesVisible(true);
		runtimesTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					if (removeButton.isEnabled()){
						ForgeInstallationsPreferenceHelper.removeVMs();
					}
				}
			}
		});
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

	private void initForgeInstallations() {
		runtimesTableViewer.setInput(ForgeRuntime.getInstallations());
		runtimesTableViewer.setCheckedElements(new Object[] { ForgeRuntime.getDefaultInstallation() });
		runtimesTableViewer.refresh();
	}

	private void sortByName() {
		runtimesTableViewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IVMInstall) && (e2 instanceof IVMInstall)) {
					IVMInstall left= (IVMInstall)e1;
					IVMInstall right= (IVMInstall)e2;
					return left.getName().compareToIgnoreCase(right.getName());
				}
				return super.compare(viewer, e1, e2);
			}
			
			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});		
		sortColumn = 1;		
	}

	private void sortByLocation() {
		runtimesTableViewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IVMInstall) && (e2 instanceof IVMInstall)) {
					IVMInstall left= (IVMInstall)e1;
					IVMInstall right= (IVMInstall)e2;
					return left.getInstallLocation().getAbsolutePath().compareToIgnoreCase(right.getInstallLocation().getAbsolutePath());
				}
				return super.compare(viewer, e1, e2);
			}
			
			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});		
		sortColumn = 2;		
	}

	private void enableButtons() {
		IStructuredSelection selection = (IStructuredSelection) runtimesTableViewer.getSelection();
		int selectionCount= selection.size();
		editButton.setEnabled(selectionCount == 1);
		if (selectionCount > 0 && selectionCount < runtimesTableViewer.getTable().getItemCount()) {
			Iterator iterator = selection.iterator();
			while (iterator.hasNext()) {
				IVMInstall install = (IVMInstall)iterator.next();
//				if (isContributed(install)) {
//					removeButton.setEnabled(false);
//					return;
//				}
			}
			removeButton.setEnabled(true);
		} else {
			removeButton.setEnabled(false);
		}
	}	
	
	public boolean performOk() {
		final boolean[] canceled = new boolean[] {false};
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
//				IVMInstall defaultVM = getCurrentDefaultVM();
////				IVMInstall[] vms = fJREBlock.getJREs();
//				IVMInstall[] vms = (IVMInstall[])runtimesTableViewer.getInput();
//				ForgeRuntimesUpdater updater = new ForgeRuntimesUpdater();
//				if (!updater.updateJRESettings(vms, defaultVM)) {
//					canceled[0] = true;
//				}
			}
		});		
		if(canceled[0]) {
			return false;
		}
//		fJREBlock.saveColumnSettings(
//				ForgePlugin.getDefault().getDialogSettings(), 
//				ForgePlugin.PLUGIN_ID + ".forge_runtimes_preference_page_context");
		return super.performOk();
	}	
	
//	private void verifyDefaultVM(IVMInstall vm) {
//		if (vm != null) {
//			LibraryLocation[] locations= JavaRuntime.getLibraryLocations(vm);
//			boolean exist = true;
//			for (int i = 0; i < locations.length; i++) {
//				exist = exist && new File(locations[i].getSystemLibraryPath().toOSString()).exists();
//			}
//			if (exist) {
//				fJREBlock.setCheckedJRE(vm);
//			} else {
//				fJREBlock.removeJREs(new IVMInstall[]{vm});
//				IVMInstall def = JavaRuntime.getDefaultVMInstall();
//				if (def == null) {
//					fJREBlock.setCheckedJRE(null);
//				} else {
//					fJREBlock.setCheckedJRE(def);
//				}
//				return;
//			}
//		} else {
//			fJREBlock.setCheckedJRE(null);
//		}
//	}
	
//	private IVMInstall getCurrentDefaultVM() {
//		return fJREBlock.getCheckedJRE();
//	}
}
