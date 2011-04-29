package org.jboss.tools.seam.forge.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.seam.forge.console.Console;
import org.jboss.tools.seam.forge.launching.ForgeRuntime;

public class ConsoleView extends ViewPart implements PropertyChangeListener {
	
	private static ConsoleView INSTANCE;
	
	private PageBook pageBook = null;
	private Control notRunning;
	private Control forgeIsStopping;
	private Control waitWhileForgeIsStarting;
	private Control forgeIsRunning;
	private ConsolePage forgeIsRunningPage;
	
	public ConsoleView() {
		if (INSTANCE == null) {
			INSTANCE = this;
			ForgeRuntime.INSTANCE.addPropertyChangeListener(this);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		pageBook = new PageBook(parent, SWT.NONE);
		createForgeNotRunningPage(parent);
		createWaitWhileForgeIsStartingPage(parent);
		createForgeIsStoppingPage(parent);
		showAppropriatePage();
	}
	
	private void createForgeNotRunningPage(Composite parent) {
		MessagePage page = new MessagePage();
		page.createControl(pageBook);
		page.init(new PageSite(getViewSite()));
		page.setMessage("Forge is not running.");
		notRunning = page.getControl();
	}
	
	private void createWaitWhileForgeIsStartingPage(Composite parent) {
		MessagePage page = new MessagePage();
		page.createControl(pageBook);
		page.init(new PageSite(getViewSite()));
		page.setMessage("Please wait while Forge is starting");
		waitWhileForgeIsStarting = page.getControl();
	}
	
	private void createForgeIsStoppingPage(Composite parent) {
		MessagePage page = new MessagePage();
		page.createControl(pageBook);
		page.init(new PageSite(getViewSite()));
		page.setMessage("Please wait while Forge is stopping");
		forgeIsStopping = page.getControl();
	}
	
	@Override
	public void setFocus() {
		if (ForgeRuntime.STATE_RUNNING.equals(ForgeRuntime.INSTANCE.getRuntimeState())) {
			forgeIsRunningPage.setFocus();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				showAppropriatePage();
			}			
		});
	}
	
	private void showAppropriatePage() {
		String runtimeState = ForgeRuntime.INSTANCE.getRuntimeState();
		if (ForgeRuntime.STATE_NOT_RUNNING.equals(runtimeState)) {
			pageBook.showPage(notRunning);
		} else if (ForgeRuntime.STATE_RUNNING.equals(runtimeState)) {
			showForgeIsRunning();
		} else if (ForgeRuntime.STATE_STARTING.equals(runtimeState)) {
			pageBook.showPage(waitWhileForgeIsStarting);
		} else if (ForgeRuntime.STATE_STOPPING.equals(runtimeState)) {
			pageBook.showPage(forgeIsStopping);
		}
	}
	
	private void showForgeIsRunning() {
		Control oldForgeIsRunning = forgeIsRunning;
		ConsolePage oldForgeIsRunningPage = forgeIsRunningPage;
		forgeIsRunningPage = new ConsolePage();
		forgeIsRunningPage.createControl(pageBook);
		forgeIsRunningPage.init(new PageSite(getViewSite()));
		forgeIsRunning = forgeIsRunningPage.getControl();
		pageBook.showPage(forgeIsRunning);
		if (oldForgeIsRunningPage != null) {
			Console oldConsole = oldForgeIsRunningPage.getConsole();
			if (oldConsole != null) {
				DebugPlugin.getDefault().removeDebugEventListener(oldConsole);
				oldConsole.dispose();
			}
			oldForgeIsRunningPage.dispose();
		}
		if (oldForgeIsRunning != null) {			
			oldForgeIsRunning.dispose();
		}
	}
	
	public void dispose() {
		ForgeRuntime.INSTANCE.removePropertyChangeListener(this);
		super.dispose();
	}

}
