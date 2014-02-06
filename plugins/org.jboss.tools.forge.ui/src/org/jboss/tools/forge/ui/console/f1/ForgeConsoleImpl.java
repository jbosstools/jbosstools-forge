package org.jboss.tools.forge.ui.console.f1;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.actions.f1.GoToAction;
import org.jboss.tools.forge.ui.actions.f1.LinkAction;
import org.jboss.tools.forge.ui.actions.f1.StartF1Action;
import org.jboss.tools.forge.ui.actions.f1.StopF1Action;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.document.ForgeDocument;
import org.jboss.tools.forge.ui.part.ForgeTextViewer;

public class ForgeConsoleImpl implements ForgeConsole, PropertyChangeListener {
	
	private ForgeTextViewer forgeTextViewer = null;
	private ForgeRuntime forgeRuntime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();

	public ForgeConsoleImpl() {
		getRuntime().addPropertyChangeListener(this);
	}
	
	@Override
	public Control createControl(Composite parent) {
		if (forgeTextViewer == null) {
			forgeTextViewer = new ForgeTextViewer(parent);
		}
		return forgeTextViewer.getControl();
	}
	
	@Override
	public IAction[] createActions() {
		return new IAction[] { 
				new StartF1Action(),
				new StopF1Action(),
				new GoToAction(),
				new LinkAction()
		};
	}
	
	@Override
	public ForgeRuntime getRuntime() {
		return forgeRuntime;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!ForgeRuntime.PROPERTY_STATE.equals(evt.getPropertyName())) return;
		if (ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getNewValue())) {
			resetForgeDocument();
		}
	}
	
	private void resetForgeDocument() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				ForgeDocument.INSTANCE.reset();
			}			
		});
	}
	
}
