package org.jboss.tools.forge.ui.wizards;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jboss.forge.ui.Result;
import org.jboss.forge.ui.UICommand;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.context.UIContextImpl;

public class GenericForgeWizard extends Wizard {
	
	private UICommand uiCommand;
	private UIContextImpl uiContext; 
	
	public GenericForgeWizard(UICommand uiCommand) {
		this.uiCommand = uiCommand;
		this.uiContext = new UIContextImpl();
	}

	@Override
	public void addPages() {
		addPage(new ForgeWizardPage(this, uiCommand, uiContext));
	}

	@Override
	public boolean performFinish() {
		try {
			Result result = uiCommand.execute(uiContext);
			String message = result.getMessage();
			if (message == null) {
				message = "Command " + uiCommand.getId().getName() + " is executed.";
			}
			writeToStatusBar(message);
			return true;
		} catch (Exception e) {
			ForgeUIPlugin.log(e);
			return false;
		}
	}
	
	private void writeToStatusBar(String message) {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchPage page = wb.getActiveWorkbenchWindow().getActivePage();
		IViewSite site = (IViewSite)page.getActivePart().getSite();
		IActionBars actionBars =  site.getActionBars();
		if( actionBars == null ) return ;
		IStatusLineManager statusLineManager = actionBars.getStatusLineManager();
		if( statusLineManager == null ) return ;
		statusLineManager.setMessage( message );		
	}

}
