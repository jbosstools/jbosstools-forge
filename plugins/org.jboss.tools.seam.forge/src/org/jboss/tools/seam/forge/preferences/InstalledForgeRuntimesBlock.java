/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.seam.forge.preferences;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.jdt.launching.AbstractVMInstallType;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMStandin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class InstalledForgeRuntimesBlock implements ISelectionProvider {
	
	private final int DEFAULT_COLUMN_WIDTH = 350/3 +1;

	
	private Composite control;
	private List fVMs = new ArrayList(); 
	private CheckboxTableViewer runtimesTableViewer;
	private Button addButton;
	private Button removeButton;
	private Button editButton;
	private Button searchButton;	
	private int fSortColumn = 0;
	private ListenerList fSelectionListeners = new ListenerList();
	private ISelection fPrevSelection = new StructuredSelection();
    private Table runtimesTable;
	private static String fgLastUsedID;	

	class JREsContentProvider implements IStructuredContentProvider {		
		public Object[] getElements(Object input) {
			return fVMs.toArray();
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
	}
	
	class VMLabelProvider extends LabelProvider implements ITableLabelProvider {

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IVMInstall) {
				IVMInstall vm= (IVMInstall)element;
				switch(columnIndex) {
					case 0:
						return vm.getName();
					case 1:
						return vm.getInstallLocation().getAbsolutePath();
				}
			}
			return element.toString();
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

	}	
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionListeners.add(listener);
	}

	public ISelection getSelection() {
		return new StructuredSelection(runtimesTableViewer.getCheckedElements());
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			if (!selection.equals(fPrevSelection)) {
				fPrevSelection = selection;
				Object jre = ((IStructuredSelection)selection).getFirstElement();
				if (jre == null) {
					runtimesTableViewer.setCheckedElements(new Object[0]);
				} else {
					runtimesTableViewer.setCheckedElements(new Object[]{jre});
					runtimesTableViewer.reveal(jre);
				}
				fireSelectionChanged();
			}
		}
	}
	
	public void createControl(Composite ancestor) {
		control = createMainControl(ancestor);					
		createTitleLabel();				
		createRuntimesArea();			
		createButtonsArea();				
		fillWithWorkspaceForgeRuntimes();		
		sortByName();		
		enableButtons();
	}
	
	private void createTitleLabel() {
		Label label = new Label(control, SWT.NONE);
		label.setText("Installed Forge Runtimes:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.grabExcessHorizontalSpace = false;
		label.setLayoutData(gd);
	}

	private void createButtonsArea() {
		Composite buttons = createButtonsComposite();
    	createAddButton(buttons);		
		createEditButton(buttons);		
		createRemoveButton(buttons);		
		createSearchButton(buttons);
	}

	private void createSearchButton(Composite buttons) {
		searchButton = new Button(buttons, SWT.PUSH);
		searchButton.setText("&Search...");
		searchButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		searchButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				search();
			}
		});
	}

	private void createRemoveButton(Composite buttons) {
		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText("&Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				removeVMs();
			}
		});
	}

	private void createEditButton(Composite buttons) {
		editButton = new Button(buttons, SWT.PUSH);
		editButton.setText("&Edit...");
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		editButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				editVM();
			}
		});
	}

	private void createAddButton(Composite buttons) {
		addButton = new Button(buttons, SWT.PUSH);
		addButton.setText("&Add...");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				addVM();
			}
		});
	}
	
	private Composite createButtonsComposite() {
		Composite buttons = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
    	buttons.setLayout(layout);
    	GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 1;
    	buttons.setLayoutData(gd);
		return buttons;
	}

	private Composite createMainControl(Composite ancestor) {
    	Composite result = new Composite(ancestor, SWT.NONE);
    	result.setLayout(new GridLayout(2, false));
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	result.setLayoutData(gd);
		return result;
	}

	private void createRuntimesArea() {
		createRuntimesTable();	
		createNameColumn();
		createLocationColumn();		
		createRuntimesTableViewer();
	}

	private void createRuntimesTableViewer() {
		runtimesTableViewer = new CheckboxTableViewer(runtimesTable);			
		runtimesTableViewer.setLabelProvider(new VMLabelProvider());
		runtimesTableViewer.setContentProvider(new JREsContentProvider());
		runtimesTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}
		});
		
		runtimesTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					setCheckedJRE((IVMInstall)event.getElement());
				} else {
					setCheckedJRE(null);
				}
			}
		});
		
		runtimesTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				if (!runtimesTableViewer.getSelection().isEmpty()) {
					editVM();
				}
			}
		});
	}

	private void createLocationColumn() {
		TableColumn column = new TableColumn(runtimesTable, SWT.NULL);
		column.setText("Location"); 
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByLocation();
			}
		});
		column.setWidth(DEFAULT_COLUMN_WIDTH);
	}

	private void createNameColumn() {
		TableColumn column = new TableColumn(runtimesTable, SWT.NULL);
		column.setText("Name"); 
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sortByName();
			}
		});
		column.setWidth(DEFAULT_COLUMN_WIDTH);
	}

	private void createRuntimesTable() {
		runtimesTable= new Table(control, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 250;
		gd.widthHint = 350;
		runtimesTable.setLayoutData(gd);
		runtimesTable.setFont(control.getFont());
		runtimesTable.setHeaderVisible(true);
		runtimesTable.setLinesVisible(true);
		runtimesTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					if (removeButton.isEnabled()){
						removeVMs();
					}
				}
			}
		});
	}
	
	public String generateName(String name){
            if (!isDuplicateName(name)) {
                return name;
            }
            
            if (name.matches(".*\\(\\d*\\)")) { //$NON-NLS-1$
                int start = name.lastIndexOf('(');
                int end = name.lastIndexOf(')');
                String stringInt = name.substring(start+1, end);
                int numericValue = Integer.parseInt(stringInt);
                String newName = name.substring(0, start+1) + (numericValue+1) + ")"; //$NON-NLS-1$
                return generateName(newName);
            } else {
                return generateName(name + " (1)"); //$NON-NLS-1$
            }
        }
	
	private void fireSelectionChanged() {
		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		Object[] listeners = fSelectionListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener)listeners[i];
			listener.selectionChanged(event);
		}	
	}

	private void sortByType() {
		runtimesTableViewer.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if ((e1 instanceof IVMInstall) && (e2 instanceof IVMInstall)) {
					IVMInstall left= (IVMInstall)e1;
					IVMInstall right= (IVMInstall)e2;
					String leftType= left.getVMInstallType().getName();
					String rightType= right.getVMInstallType().getName();
					int res= leftType.compareToIgnoreCase(rightType);
					if (res != 0) {
						return res;
					}
					return left.getName().compareToIgnoreCase(right.getName());
				}
				return super.compare(viewer, e1, e2);
			}
			
			public boolean isSorterProperty(Object element, String property) {
				return true;
			}
		});	
		fSortColumn = 3;			
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
		fSortColumn = 1;		
	}
	
	/**
	 * Sorts by VM location.
	 */
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
		fSortColumn = 2;		
	}
		
	/**
	 * Enables the buttons based on selected items counts in the viewer
	 */
	private void enableButtons() {
		IStructuredSelection selection = (IStructuredSelection) runtimesTableViewer.getSelection();
		int selectionCount= selection.size();
		editButton.setEnabled(selectionCount == 1);
		if (selectionCount > 0 && selectionCount < runtimesTableViewer.getTable().getItemCount()) {
			Iterator iterator = selection.iterator();
			while (iterator.hasNext()) {
				IVMInstall install = (IVMInstall)iterator.next();
				if (isContributed(install)) {
					removeButton.setEnabled(false);
					return;
				}
			}
			removeButton.setEnabled(true);
		} else {
			removeButton.setEnabled(false);
		}
	}	
	
	/**
	 * Returns if the specified VM install has been contributed
	 * @param install
	 * @return true if the specified VM is contributed, false otherwise
	 */
	private boolean isContributed(IVMInstall install) {
		return JavaRuntime.isContributedVMInstall(install.getId());
	}
	
	/**
	 * Returns this block's control
	 * 
	 * @return control
	 */
	public Control getControl() {
		return control;
	}
	
	/**
	 * Sets the JREs to be displayed in this block
	 * 
	 * @param vms JREs to be displayed
	 */
	protected void setJREs(IVMInstall[] vms) {
		fVMs.clear();
		for (int i = 0; i < vms.length; i++) {
			fVMs.add(vms[i]);
		}
		runtimesTableViewer.setInput(fVMs);
		runtimesTableViewer.refresh();
	}
	
	/**
	 * Returns the JREs currently being displayed in this block
	 * 
	 * @return JREs currently being displayed in this block
	 */
	public IVMInstall[] getJREs() {
		return (IVMInstall[])fVMs.toArray(new IVMInstall[fVMs.size()]);
	}
	
	/**
	 * Bring up a wizard that lets the user create a new VM definition.
	 */
	private void addVM() {
//		AddVMInstallWizard wizard = new AddVMInstallWizard((IVMInstall[]) fVMs.toArray(new IVMInstall[fVMs.size()]));
//		WizardDialog dialog = new WizardDialog(getShell(), wizard);
//		if (dialog.open() == Window.OK) {
//			VMStandin result = wizard.getResult();
//			if (result != null) {
//				fVMs.add(result);
//				fVMList.refresh();
//				fVMList.setSelection(new StructuredSelection(result));
//			}
//		}
	}
	
	/**
	 * @see IAddVMDialogRequestor#vmAdded(IVMInstall)
	 */
	public void vmAdded(IVMInstall vm) {
		fVMs.add(vm);
		runtimesTableViewer.refresh();
	}
	
	/**
	 * @see IAddVMDialogRequestor#isDuplicateName(String)
	 */
	public boolean isDuplicateName(String name) {
		for (int i= 0; i < fVMs.size(); i++) {
			IVMInstall vm = (IVMInstall)fVMs.get(i);
			if (vm.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}	
	
	/**
	 * Performs the edit VM action when the Edit... button is pressed
	 */
	private void editVM() {
		IStructuredSelection selection= (IStructuredSelection)runtimesTableViewer.getSelection();
		VMStandin vm= (VMStandin)selection.getFirstElement();
		if (vm == null) {
			return;
		}
//		if (isContributed(vm)) {
//			VMDetailsDialog dialog= new VMDetailsDialog(getShell(), vm);
//			dialog.open();
//		} else {
//			EditVMInstallWizard wizard = new EditVMInstallWizard(vm, (IVMInstall[]) fVMs.toArray(new IVMInstall[fVMs.size()]));
//			WizardDialog dialog = new WizardDialog(getShell(), wizard);
//			if (dialog.open() == Window.OK) {
//				VMStandin result = wizard.getResult();
//				if (result != null) {
//					// replace with the edited VM
//					int index = fVMs.indexOf(vm);
//					fVMs.remove(index);
//					fVMs.add(index, result);
//					fVMList.refresh();
//					fVMList.setSelection(new StructuredSelection(result));
//				}
//			}
//		}
		
		
	}
	
	/**
	 * Performs the remove VM(s) action when the Remove... button is pressed
	 */
	private void removeVMs() {
		IStructuredSelection selection= (IStructuredSelection)runtimesTableViewer.getSelection();
		IVMInstall[] vms = new IVMInstall[selection.size()];
		Iterator iter = selection.iterator();
		int i = 0;
		while (iter.hasNext()) {
			vms[i] = (IVMInstall)iter.next();
			i++;
		}
		removeJREs(vms);
	}	
	
	/**
	 * Removes the given VMs from the table.
	 * 
	 * @param vms
	 */
	public void removeJREs(IVMInstall[] vms) {
		IStructuredSelection prev = (IStructuredSelection) getSelection();
		for (int i = 0; i < vms.length; i++) {
			fVMs.remove(vms[i]);
		}
		runtimesTableViewer.refresh();
		IStructuredSelection curr = (IStructuredSelection) getSelection();
		if (!curr.equals(prev)) {
			IVMInstall[] installs = getJREs();
			if (curr.size() == 0 && installs.length == 1) {
				// pick a default VM automatically
				setSelection(new StructuredSelection(installs[0]));
			} else {
				fireSelectionChanged();
			}
		}
	}
	
	/**
	 * Search for installed VMs in the file system
	 */
	protected void search() {
//		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
//			doMacSearch();
//			return;
//		}
		// choose a root directory for the search 
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Select a directory to search in:"); 
		dialog.setText("Directory Selection"); 
		String path = dialog.open();
		if (path == null) {
			return;
		}
		
		// ignore installed locations
		final Set exstingLocations = new HashSet();
		Iterator iter = fVMs.iterator();
		while (iter.hasNext()) {
			exstingLocations.add(((IVMInstall)iter.next()).getInstallLocation());
		}
		
		// search
		final File rootDir = new File(path);
		final List locations = new ArrayList();
		final List types = new ArrayList();

		IRunnableWithProgress r = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				monitor.beginTask("Searching...", IProgressMonitor.UNKNOWN); 
				search(rootDir, locations, types, exstingLocations, monitor);
				monitor.done();
			}
		};
		
		try {
            ProgressMonitorDialog progress = new ProgressMonitorDialog(getShell()) {
                /*
                 * Overridden createCancelButton to replace Cancel label with Stop label
                 * More accurately reflects action taken when button pressed.
                 * Bug [162902]
                 */
                protected void createCancelButton(Composite parent) {
                    cancel = createButton(parent, IDialogConstants.CANCEL_ID,
                            IDialogConstants.STOP_LABEL, true);
                    if (arrowCursor == null) {
            			arrowCursor = new Cursor(cancel.getDisplay(), SWT.CURSOR_ARROW);
            		}
                    cancel.setCursor(arrowCursor);
                    setOperationCancelButtonEnabled(enableCancelButton);
                }
            };
            progress.run(true, true, r);
		} catch (InvocationTargetException e) {
//			JDIDebugUIPlugin.log(e);
		} catch (InterruptedException e) {
			// canceled
			return;
		}
		
		if (locations.isEmpty()) {
			String messagePath = path.replaceAll("&", "&&"); // @see bug 29855  //$NON-NLS-1$//$NON-NLS-2$
//			MessageDialog.openInformation(getShell(), JREMessages.InstalledJREsBlock_12, MessageFormat.format(JREMessages.InstalledJREsBlock_13, new String[]{messagePath})); // 
		} else {
			iter = locations.iterator();
			Iterator iter2 = types.iterator();
			while (iter.hasNext()) {
				File location = (File)iter.next();
				IVMInstallType type = (IVMInstallType)iter2.next();
				AbstractVMInstall vm = new VMStandin(type, createUniqueId(type));
				String name = location.getName();
				String nameCopy = new String(name);
				int i = 1;
				while (isDuplicateName(nameCopy)) {
					nameCopy = name + '(' + i++ + ')'; 
				}
				vm.setName(nameCopy);
				vm.setInstallLocation(location);
				if (type instanceof AbstractVMInstallType) {
					//set default java doc location
					AbstractVMInstallType abs = (AbstractVMInstallType)type;
					vm.setJavadocLocation(abs.getDefaultJavadocLocation(location));
					vm.setVMArgs(abs.getDefaultVMArguments(location));
				}
				vmAdded(vm);
			}
		}
		
	}
	
	/**
	 * Calls out to {@link MacVMSearch} to find all installed JREs in the standard
	 * Mac OS location
	 */
//	private void doMacSearch() {
//		final List added = new ArrayList();
//		IRunnableWithProgress r = new IRunnableWithProgress() {
//			public void run(IProgressMonitor monitor) {
//				Set exists = new HashSet();
//				Iterator iterator = fVMs.iterator();
//				while (iterator.hasNext()) {
//					IVMInstall vm = (IVMInstall) iterator.next();
//					exists.add(vm.getId());
//				}
//				VMStandin[] standins = new MacVMSearch().search(monitor);
//				for (int i = 0; i < standins.length; i++) {
//					if (!exists.contains(standins[i].getId())) {
//						added.add(standins[i]);
//					}
//				}
//				monitor.done();
//			}
//		};
//		
//		try {
//            ProgressMonitorDialog progress = new ProgressMonitorDialog(getShell());
//            progress.run(true, true, r);
//		} catch (InvocationTargetException e) {
//			JDIDebugUIPlugin.log(e);
//		} catch (InterruptedException e) {
//			// canceled
//			return;
//		}
//		
//		Iterator iterator = added.iterator();
//		while (iterator.hasNext()) {
//			IVMInstall vm = (IVMInstall) iterator.next();
//			vmAdded(vm);
//		}
//
//	}

	protected Shell getShell() {
		return getControl().getShell();
	}

	/**
	 * Find a unique VM id.  Check existing 'real' VMs, as well as the last id used for
	 * a VMStandin.
	 */
	private String createUniqueId(IVMInstallType vmType) {
		String id= null;
		do {
			id= String.valueOf(System.currentTimeMillis());
		} while (vmType.findVMInstall(id) != null || id.equals(fgLastUsedID));
		fgLastUsedID = id;
		return id;
	}	
	
	/**
	 * Searches the specified directory recursively for installed VMs, adding each
	 * detected VM to the <code>found</code> list. Any directories specified in
	 * the <code>ignore</code> are not traversed.
	 * 
	 * @param directory
	 * @param found
	 * @param types
	 * @param ignore
	 */
	protected void search(File directory, List found, List types, Set ignore, IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return;
		}

		String[] names = directory.list();
		if (names == null) {
			return;
		}
		List subDirs = new ArrayList();
		for (int i = 0; i < names.length; i++) {
			if (monitor.isCanceled()) {
				return;
			}
			File file = new File(directory, names[i]);
//			try {
//				monitor.subTask(MessageFormat.format(JREMessages.InstalledJREsBlock_14, new String[]{Integer.toString(found.size()),
//						file.getCanonicalPath().replaceAll("&", "&&")}));   // @see bug 29855 //$NON-NLS-1$ //$NON-NLS-2$
//			} catch (IOException e) {
//			}		
			IVMInstallType[] vmTypes = JavaRuntime.getVMInstallTypes();	
			if (file.isDirectory()) {
				if (!ignore.contains(file)) {
					boolean validLocation = false;
					
					// Take the first VM install type that claims the location as a
					// valid VM install.  VM install types should be smart enough to not
					// claim another type's VM, but just in case...
					for (int j = 0; j < vmTypes.length; j++) {
						if (monitor.isCanceled()) {
							return;
						}
						IVMInstallType type = vmTypes[j];
						IStatus status = type.validateInstallLocation(file);
						if (status.isOK()) {
							found.add(file);
							types.add(type);
							validLocation = true;
							break;
						}
					}
					if (!validLocation) {
						subDirs.add(file);
					}
				}
			}
		}
		while (!subDirs.isEmpty()) {
			File subDir = (File)subDirs.remove(0);
			search(subDir, found, types, ignore, monitor);
			if (monitor.isCanceled()) {
				return;
			}
		}
		
	}	
	
	/**
	 * Sets the checked JRE, possible <code>null</code>
	 * 
	 * @param vm JRE or <code>null</code>
	 */
	public void setCheckedJRE(IVMInstall vm) {
		if (vm == null) {
			setSelection(new StructuredSelection());
		} else {
			setSelection(new StructuredSelection(vm));
		}
	}
	
	/**
	 * Returns the checked JRE or <code>null</code> if none.
	 * 
	 * @return the checked JRE or <code>null</code> if none
	 */
	public IVMInstall getCheckedJRE() {
		Object[] objects = runtimesTableViewer.getCheckedElements();
		if (objects.length == 0) {
			return null;
		}
		return (IVMInstall)objects[0];
	}
	
	/**
	 * Persist table settings into the give dialog store, prefixed
	 * with the given key.
	 * 
	 * @param settings dialog store
	 * @param qualifier key qualifier
	 */
	public void saveColumnSettings(IDialogSettings settings, String qualifier) {
        int columnCount = runtimesTable.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			settings.put(qualifier + ".columnWidth" + i, runtimesTable.getColumn(i).getWidth());	 //$NON-NLS-1$
		}
		settings.put(qualifier + ".sortColumn", fSortColumn); //$NON-NLS-1$
	}
	
	/**
	 * Restore table settings from the given dialog store using the
	 * given key.
	 * 
	 * @param settings dialog settings store
	 * @param qualifier key to restore settings from
	 */
	public void restoreColumnSettings(IDialogSettings settings, String qualifier) {
		runtimesTableViewer.getTable().layout(true);
        restoreColumnWidths(settings, qualifier);
		try {
			fSortColumn = settings.getInt(qualifier + ".sortColumn"); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			fSortColumn = 1;
		}
		switch (fSortColumn) {
			case 1:
				sortByName();
				break;
			case 2:
				sortByLocation();
				break;
			case 3:
				sortByType();
				break;
		}
	}
	
	private void restoreColumnWidths(IDialogSettings settings, String qualifier) {
        int columnCount = runtimesTable.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            int width = -1;
            try {
                width = settings.getInt(qualifier + ".columnWidth" + i); 
            } catch (NumberFormatException e) {}
            
            if ((width <= 0) || (i == runtimesTable.getColumnCount() - 1)) {
                runtimesTable.getColumn(i).pack();
            } else {
                runtimesTable.getColumn(i).setWidth(width);
            }
        }
	}
	
	protected void fillWithWorkspaceForgeRuntimes() {
		List standins = new ArrayList();
		IVMInstallType[] types = JavaRuntime.getVMInstallTypes();
		for (int i = 0; i < types.length; i++) {
			IVMInstallType type = types[i];
			IVMInstall[] installs = type.getVMInstalls();
			for (int j = 0; j < installs.length; j++) {
				IVMInstall install = installs[j];
				standins.add(new VMStandin(install));
			}
		}
		setJREs((IVMInstall[])standins.toArray(new IVMInstall[standins.size()]));	
	}
		
}
