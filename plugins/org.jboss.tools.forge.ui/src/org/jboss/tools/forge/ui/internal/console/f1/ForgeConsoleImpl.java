package org.jboss.tools.forge.ui.internal.console.f1;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.actions.GoToAction;
import org.jboss.tools.forge.ui.internal.actions.LinkAction;
import org.jboss.tools.forge.ui.internal.actions.StartAction;
import org.jboss.tools.forge.ui.internal.actions.StopAction;
import org.jboss.tools.forge.ui.internal.console.AbstractForgeConsole;
import org.jboss.tools.forge.ui.internal.viewer.F1TextViewer;
import org.jboss.tools.forge.ui.internal.viewer.ForgeTextViewer;

public class ForgeConsoleImpl extends AbstractForgeConsole {
	
 	public ForgeConsoleImpl(ForgeRuntime runtime) {
		super(runtime);
		getRuntime().addPropertyChangeListener(this);
	}
	
	@Override
	public ForgeTextViewer createTextViewer(Composite parent) {
		return new F1TextViewer(parent, getRuntime());
	}
	
	@Override
	public Control createControl(Composite parent) {
		if (textViewer == null) {
			textViewer = createTextViewer(parent);
		}
		return textViewer.getControl();
	}
	
	@Override
	public IAction[] createActions() {
		return new IAction[] { 
				new StartAction(getRuntime()),
				new StopAction(getRuntime()),
				new GoToAction(getRuntime()),
				new LinkAction(getRuntime())
		};
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (!ForgeRuntime.PROPERTY_STATE.equals(evt.getPropertyName())) return;
		if (ForgeRuntimeState.STOPPED.equals(evt.getOldValue()) 
				&& ForgeRuntimeState.STARTING.equals(evt.getNewValue())) {
			textViewer.startConsole();
		}
		if (ForgeRuntimeState.STOPPED.equals(evt.getNewValue())) {
			textViewer.stopConsole();
		}
	}
	
}
