package org.jboss.tools.seam.forge.preferences;


import java.io.File;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.seam.forge.ForgePlugin;

public class ForgeRuntimesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
				
	private InstalledForgeRuntimesBlock fJREBlock;
		
	public ForgeRuntimesPreferencePage() {
		super("Installed Forge Runtimes");
	}

	public void init(IWorkbench workbench) {
	}

	private void initDefaultVM() {
		IVMInstall realDefault= JavaRuntime.getDefaultVMInstall();
		if (realDefault != null) {
			IVMInstall[] vms= fJREBlock.getJREs();
			for (int i = 0; i < vms.length; i++) {
				IVMInstall fakeVM= vms[i];
				if (fakeVM.equals(realDefault)) {
					verifyDefaultVM(fakeVM);
					break;
				}
			}
		}
	}
	
	protected Control createContents(Composite ancestor) {
		initializeDialogUnits(ancestor);		
		noDefaultAndApplyButton();		
		createLayout(ancestor);		
		createWrapLabel(ancestor);
		createVerticalSpacer(ancestor);		
		createInstalledForgeRuntimesBlock(ancestor);					
		initDefaultVM();
		applyDialogFont(ancestor);
		return ancestor;
	}

	private void createInstalledForgeRuntimesBlock(Composite ancestor) {
		fJREBlock = new InstalledForgeRuntimesBlock();
		fJREBlock.createControl(ancestor);
		Control control = fJREBlock.getControl();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		control.setLayoutData(data);		
		fJREBlock.restoreColumnSettings(
				ForgePlugin.getDefault().getDialogSettings(), 
				ForgePlugin.PLUGIN_ID + ".forge_runtimes_preference_page_context");
		
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

	public boolean performOk() {
		final boolean[] canceled = new boolean[] {false};
		BusyIndicator.showWhile(null, new Runnable() {
			public void run() {
				IVMInstall defaultVM = getCurrentDefaultVM();
				IVMInstall[] vms = fJREBlock.getJREs();
				ForgeRuntimesUpdater updater = new ForgeRuntimesUpdater();
				if (!updater.updateJRESettings(vms, defaultVM)) {
					canceled[0] = true;
				}
			}
		});		
		if(canceled[0]) {
			return false;
		}
		fJREBlock.saveColumnSettings(
				ForgePlugin.getDefault().getDialogSettings(), 
				ForgePlugin.PLUGIN_ID + ".forge_runtimes_preference_page_context");
		return super.performOk();
	}	
	
	private void verifyDefaultVM(IVMInstall vm) {
		if (vm != null) {
			LibraryLocation[] locations= JavaRuntime.getLibraryLocations(vm);
			boolean exist = true;
			for (int i = 0; i < locations.length; i++) {
				exist = exist && new File(locations[i].getSystemLibraryPath().toOSString()).exists();
			}
			if (exist) {
				fJREBlock.setCheckedJRE(vm);
			} else {
				fJREBlock.removeJREs(new IVMInstall[]{vm});
				IVMInstall def = JavaRuntime.getDefaultVMInstall();
				if (def == null) {
					fJREBlock.setCheckedJRE(null);
				} else {
					fJREBlock.setCheckedJRE(def);
				}
				return;
			}
		} else {
			fJREBlock.setCheckedJRE(null);
		}
	}
	
	private IVMInstall getCurrentDefaultVM() {
		return fJREBlock.getCheckedJRE();
	}
}
