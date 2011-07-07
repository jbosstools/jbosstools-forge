package org.jboss.tools.forge.ui.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.console.Console;
import org.jboss.tools.forge.ui.console.ConsolePage;

public class ConsoleView extends ViewPart implements PropertyChangeListener {
	
	public static ConsoleView INSTANCE;
	
	private PageBook pageBook = null;
	private Control notRunning;
	private Control starting;
	private Control running;
	private ConsolePage forgeIsRunningPage;
	
	private ForgeRuntime runtime;
	
	public ConsoleView() {
		if (INSTANCE == null) {
			INSTANCE = this;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		pageBook = new PageBook(parent, SWT.NONE);
		createNotRunningPage(parent);
		createStartingPage(parent);
		showPage(notRunning);
	}
	
	private void createNotRunningPage(Composite parent) {
		MessagePage page = new MessagePage();
		page.createControl(pageBook);
		page.init(new PageSite(getViewSite()));
		page.setMessage("Forge is not running.");
		notRunning = page.getControl();
	}
	
	private void createStartingPage(Composite parent) {
		MessagePage page = new MessagePage();
		page.createControl(pageBook);
		page.init(new PageSite(getViewSite()));
		page.setMessage("Please wait while Forge is starting");
		starting = page.getControl();
	}
	
	@Override
	public void setFocus() {
		if (runtime != null && ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) {
			forgeIsRunningPage.setFocus();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ForgeRuntime.STATE_STARTING.equals(evt.getNewValue())) {
			handleStateStarting();
		} else if (ForgeRuntime.STATE_RUNNING.equals(evt.getNewValue())) {
			handleStateRunning();
		} else if (ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getNewValue())) {
			handleStateNotRunning();
		}
	}
	
	private void handleStateStarting() {
		showPage(starting);
		createRunningPage();
	}
	
	private void handleStateRunning() {
		showPage(running);
	}
	
	private void handleStateNotRunning() {
		if (runtime != null) {
			runtime.removePropertyChangeListener(INSTANCE);
			runtime = null;
		}
		showPage(notRunning);
	}
	
	private void showPage(final Control control) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				pageBook.showPage(control);
			}			
		});
	}
	
	private void createRunningPage() {
		Control oldForgeIsRunning = running;
		ConsolePage oldForgeIsRunningPage = forgeIsRunningPage;
		forgeIsRunningPage = new ConsolePage(runtime.getProcess());
		forgeIsRunningPage.createControl(pageBook);
		forgeIsRunningPage.init(new PageSite(getViewSite()));
		running = forgeIsRunningPage.getControl();
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
		if (runtime != null) {
			runtime.stop(null);
			runtime = null;
		}
		super.dispose();
	}
	
	public void startForge() {
		if (runtime != null) return;
		runtime = ForgeRuntimesPreferences.INSTANCE.getDefault();
		runtime.addPropertyChangeListener(INSTANCE);
		final IProgressMonitor progressMonitor = getViewSite().getActionBars().getStatusLineManager().getProgressMonitor();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				runtime.start(progressMonitor);				
				if (progressMonitor.isCanceled()) {
					handleStateNotRunning();
				}
			}			
		});
	}
	
	public void stopForge() {
		if (runtime == null) return;
		final IProgressMonitor progressMonitor = getViewSite().getActionBars().getStatusLineManager().getProgressMonitor();
		runtime.stop(progressMonitor);
	}

}
