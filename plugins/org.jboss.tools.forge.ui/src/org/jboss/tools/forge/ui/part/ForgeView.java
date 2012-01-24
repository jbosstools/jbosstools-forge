package org.jboss.tools.forge.ui.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.commands.SourceProvider;
import org.jboss.tools.forge.ui.console.ForgeTextViewer;
import org.jboss.tools.forge.ui.document.ForgeDocument;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ForgeView extends ViewPart implements PropertyChangeListener, IShowInTarget {

	public static final String ID = "org.jboss.tools.forge.ui.view";
	
	private static final String NOT_RUNNING_MESSAGE = "Forge is not running.";
	private static final String STARTING_MESSAGE = "Please wait while Forge is starting";
	
	private class ForgePage extends Page {		
		private ForgeTextViewer viewer;
		@Override
		public void createControl(Composite parent) {
			viewer = new ForgeTextViewer(parent);
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
	private ForgeRuntime currentRuntime;
	
	private ISelection selection;
	private SelectionSynchronizer synchronizer;
	private ISelectionListener selectionListener = new ISelectionListener() {		
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection newSelection) {
			selection = newSelection;
		}
	};
	
	private IPreferenceChangeListener preferenceChangeListener = new IPreferenceChangeListener() {		
		@Override
		public void preferenceChange(PreferenceChangeEvent event) {
			if (ForgeRuntimesPreferences.PREF_FORGE_RUNTIMES.equals(event.getKey())) {
				ForgeRuntime newRuntime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
				if (!newRuntime.getName().equals(currentRuntime.getName())) {
					ForgeRuntime oldRuntime = currentRuntime;
					oldRuntime.stop(null);
					oldRuntime.removePropertyChangeListener(ForgeView.this);
					currentRuntime = newRuntime;
					currentRuntime.addPropertyChangeListener(ForgeView.this);
					ForgeDocument.INSTANCE.connect(currentRuntime);
					ForgeHelper.startForge();
				}
			}
		}
	};
	
	@Override
	public void createPartControl(Composite parent) {
		synchronizer = new SelectionSynchronizer(this);
		pageBook = new PageBook(parent, SWT.NONE);
		createRunningPage();
		createNotRunningPage();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		currentRuntime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		if (currentRuntime != null) {
			updatePages(currentRuntime.getState());
			currentRuntime.addPropertyChangeListener(this);
		}
		ForgeRuntimesPreferences.INSTANCE.addPreferenceChangeListener(preferenceChangeListener);
	}
	
	public ISelection getSelection() {
		return selection;
	}
	
	public void setSynchronized(boolean synced) {
		synchronizer.setEnabled(synced);
	}
	
	private void createNotRunningPage() {
		MessagePage page = new MessagePage();
		page.createControl(pageBook);
		page.init(new PageSite(getViewSite()));
		notRunningMessage = NOT_RUNNING_MESSAGE;
		page.setMessage(notRunningMessage);
		notRunning = page.getControl();
		notRunningPage = page;
	}
	
	private void createRunningPage() {
		runningPage = new ForgePage();
		runningPage.createControl(pageBook);
		runningPage.init(new PageSite(getViewSite()));
		running = runningPage.getControl();
	}
	
	@Override
	public void setFocus() {
		runningPage.setFocus();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ForgeRuntime.PROPERTY_STATE.equals(evt.getPropertyName())) {
			updatePages(evt.getNewValue());
		}
	}
	
	public void dispose() {
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		if (runtime != null) {
			runtime.removePropertyChangeListener(this);
		}
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
		runningPage.dispose();
		notRunningPage.dispose();
		pageBook.dispose();
		super.dispose();
	}
	
	private void updatePages(Object state) {
		if (ForgeRuntime.STATE_STARTING.equals(state)) {
			handleStateStarting();
		} else if (ForgeRuntime.STATE_RUNNING.equals(state)) {
			handleStateRunning();
		} else if (ForgeRuntime.STATE_NOT_RUNNING.equals(state)) {
			handleStateNotRunning();
		}
	}
	
	private void handleStateStarting() {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				notRunningMessage = STARTING_MESSAGE;
				notRunningPage.setMessage(notRunningMessage);
				Thread waitThread = new Thread(new Runnable() {
					@Override
					public void run() {
						while (!ForgeHelper.isForgeRunning()) {
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
		});
	}
	
	private void handleStateRunning() {
		showPage(running);
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				running.forceFocus();
				updateCommands(ForgeRuntime.STATE_RUNNING);
			}			
		});
	}
	
	private void handleStateNotRunning() {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				notRunningMessage = NOT_RUNNING_MESSAGE;
				if (!notRunningPage.getControl().isDisposed()) {
					notRunningPage.setMessage(notRunningMessage);
				}
				showPage(notRunning);
				updateCommands(ForgeRuntime.STATE_NOT_RUNNING);
			}			
		});
	}
	
	private void updateCommands(String state) {
		ISourceProviderService service = 
				(ISourceProviderService)getViewSite().getService(ISourceProviderService.class);
		SourceProvider sourceProvider = 
				(SourceProvider) service.getSourceProvider(ForgeRuntime.PROPERTY_STATE); 
		sourceProvider.setRuntimeState(state); 
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
	
	private void updateNonRunningPage() {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				notRunningMessage += '.';
				notRunningPage.setMessage(notRunningMessage);
			}			
		});
	}
	
	private Display getDisplay() {
		return getSite().getShell().getDisplay();
	}
	
	public boolean show(ShowInContext context) {
        if (context == null) {
		    return false;
        }
        if (ForgeHelper.isForgeRunning()) {
        	return goToSelection(context.getSelection());
        } else {
        	return startForgeIsOK(context);
        }
	}
	
	private boolean startForgeIsOK(final ShowInContext context) {
		boolean start = MessageDialog.open(
				MessageDialog.QUESTION, 
				null, 
				"Forge Not Running", 
				"Forge is not running. Do you want to start the Forge runtime?", 
				SWT.NONE);
		if (start) {
			ForgeHelper.startForge();
			Thread waitThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!ForgeHelper.isForgeRunning()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							ForgeUIPlugin.log(e);
						}
					}
					goToSelection(context.getSelection());
				}			
			});
			waitThread.start();
		}
		return start;
	}

	public boolean goToSelection(ISelection sel) {
		if (sel instanceof IStructuredSelection) {
		    IStructuredSelection ss = (IStructuredSelection)sel;
		    Object first = ss.getFirstElement();
		    if (first instanceof IResource) {
		    	goToPath(((IResource)first).getLocation().toOSString());
		    } else if (first instanceof IJavaElement) {
		    	try {
		    		IResource resource = ((IJavaElement)first).getCorrespondingResource();
		    		if (resource == null) return false;
					goToPath(resource.getLocation().toOSString());
				} catch (JavaModelException e) {
					ForgeUIPlugin.log(e);
					return false;
				}
		    } else if (first instanceof IRemoteFile) {
		    	goToPath(((IRemoteFile)first).getAbsolutePath());
		    }
		    return true;
		} else {
			return false;
		}
	}
	
	private void goToPath(String str) {
		if (str.indexOf(' ') != -1) {
			str = '\"' + str + '\"';
		}
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		runtime.sendInput("pick-up " + str + "\n");
	}
	
}