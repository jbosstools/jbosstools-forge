package org.jboss.tools.forge.ui.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.console.ConsolePage;

public class ConsoleView extends ViewPart implements PropertyChangeListener {
	
	private static final String NOT_RUNNING_MESSAGE = "Forge is not running.";
	private static final String STARTING_MESSAGE = "Please wait while Forge is starting";
	
	public static ConsoleView INSTANCE;
	
	private PageBook pageBook = null;
	private Control notRunning;
	private Control running;
	private ConsolePage runningPage;
	private MessagePage notRunningPage;
	private String notRunningMessage;
	
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
		showPage(notRunning);
	}
	
	private void createNotRunningPage(Composite parent) {
		MessagePage page = new MessagePage();
		page.createControl(pageBook);
		page.init(new PageSite(getViewSite()));
		notRunningMessage = NOT_RUNNING_MESSAGE;
		page.setMessage(notRunningMessage);
		notRunning = page.getControl();
		notRunningPage = page;
	}
	
	@Override
	public void setFocus() {
		if (runtime != null && ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) {
			runningPage.setFocus();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ForgeRuntime.PROPERTY_STATE.equals(evt.getPropertyName())) {
			if (ForgeRuntime.STATE_STARTING.equals(evt.getNewValue())) {
				handleStateStarting();
			} else if (ForgeRuntime.STATE_RUNNING.equals(evt.getNewValue())) {
				handleStateRunning();
			} else if (ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getNewValue())) {
				handleStateNotRunning();
			}
		}
	}
	
	private void handleStateStarting() {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				notRunningMessage = STARTING_MESSAGE;
				notRunningPage.setMessage(notRunningMessage);
				createRunningPage();
			}			
		});
	}
	
	private void handleStateRunning() {
		showPage(running);
	}
	
	private void handleStateNotRunning() {
		if (runtime != null) {
			runtime.removePropertyChangeListener(INSTANCE);
			runtime = null;
		}
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				notRunningMessage = NOT_RUNNING_MESSAGE;
				notRunningPage.setMessage(notRunningMessage);
				showPage(notRunning);
			}			
		});
	}
	
	private void showPage(final Control control) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				pageBook.showPage(control);
			}			
		});
	}
	
	private void createRunningPage() {
		Control oldForgeIsRunning = running;
		ConsolePage oldForgeIsRunningPage = runningPage;
		runningPage = new ConsolePage(runtime);
		runningPage.createControl(pageBook);
		runningPage.init(new PageSite(getViewSite()));
		running = runningPage.getControl();
		if (oldForgeIsRunningPage != null) {
//			Console oldConsole = oldForgeIsRunningPage.getConsole();
//			if (oldConsole != null) {
//				DebugPlugin.getDefault().removeDebugEventListener(oldConsole);
//				oldConsole.dispose();
//			}
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
		Job job = new Job("Starting Forge") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				runtime.start(monitor);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		Thread waitThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!ForgeRuntime.STATE_RUNNING.equals(runtime.getState())) {
					try {
						Thread.sleep(1000);
						updateNonRunningPage();
					} catch (InterruptedException e) {
						ForgeUIPlugin.log(e);
					}
				}
			}			
		});
		waitThread.start();
	}
	
	private void updateNonRunningPage() {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				notRunningMessage += '.';
				notRunningPage.setMessage(notRunningMessage);
			}			
		});
	}
	
	public void stopForge() {
		if (runtime == null) return;
		final IProgressMonitor progressMonitor = getViewSite().getActionBars().getStatusLineManager().getProgressMonitor();
		runtime.stop(progressMonitor);
	}
	
	private Display getDisplay() {
		return getViewSite().getPage().getWorkbenchWindow().getShell().getDisplay();
	}

}
