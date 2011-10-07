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
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.console.ForgeTextViewer;

public class ForgeView extends ViewPart implements PropertyChangeListener {

	public static final String ID = "org.jboss.tools.forge.console";
	
	private static final String NOT_RUNNING_MESSAGE = "Forge is not running.";
	private static final String STARTING_MESSAGE = "Please wait while Forge is starting";
	
	public static ForgeView INSTANCE;
	
	private class ForgePage extends Page {
		
		private ForgeTextViewer viewer;
		private ForgeRuntime runtime;
		
		public ForgePage(ForgeRuntime runtime) {
			this.runtime = runtime;
		}
		
		@Override
		public void createControl(Composite parent) {
			viewer = new ForgeTextViewer(parent, runtime);
		}

		@Override
		public Control getControl() {
			return viewer == null ? null : viewer.getControl();
		}

		@Override
		public void setFocus() {
			viewer.getControl().setFocus();
		}
		
	}

	private PageBook pageBook = null;
	private Control notRunning;
	private Control running;
	private Page runningPage;
	private MessagePage notRunningPage;
	private String notRunningMessage;
	
	private ForgeRuntime runtime;
	
	public ForgeView() {
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
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				running.forceFocus();
			}			
		});
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
				if (!notRunningPage.getControl().isDisposed()) {
					notRunningPage.setMessage(notRunningMessage);
				}
				showPage(notRunning);
			}			
		});
	}
	
	private void showPage(final Control control) {
		if (getSite().getShell() != null) {
			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					pageBook.showPage(control);
				}			
			});
		}
	}
	
	private void createRunningPage() {
		Control oldForgeIsRunning = running;
		Page oldForgeIsRunningPage = runningPage;
		runningPage = new ForgePage(runtime);
		runningPage.createControl(pageBook);
		runningPage.init(new PageSite(getViewSite()));
		running = runningPage.getControl();
		if (oldForgeIsRunningPage != null) {
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
		return getSite().getShell().getDisplay();
	}
	
	public ForgeRuntime getRuntime() {
		return runtime;
	}
	
}
