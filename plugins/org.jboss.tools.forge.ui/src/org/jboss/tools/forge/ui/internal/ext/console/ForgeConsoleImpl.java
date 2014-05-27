package org.jboss.tools.forge.ui.internal.ext.console;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.actions.StartAction;
import org.jboss.tools.forge.ui.internal.actions.StopAction;
import org.jboss.tools.forge.ui.internal.console.AbstractForgeConsole;
import org.jboss.tools.forge.ui.internal.viewer.F2TextViewer;

public class ForgeConsoleImpl extends AbstractForgeConsole {
	
	private F2TextViewer textViewer = null;
	
	public ForgeConsoleImpl(ForgeRuntime runtime) {
		super(runtime);
		getRuntime().addPropertyChangeListener(this);
	}

	@Override
	public Control createControl(Composite parent) {
		textViewer = new F2TextViewer(parent);
		return textViewer.getControl();
	}
	
	@Override
	public IAction[] createActions() {
		return new IAction[] {
				new StartAction(getRuntime()),
				new StopAction(getRuntime())
		};
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ForgeRuntimeState.STARTING.equals(evt.getOldValue()) 
				&& ForgeRuntimeState.RUNNING.equals(evt.getNewValue())) {
			textViewer.startConsole();
		}
		if (ForgeRuntimeState.RUNNING.equals(evt.getOldValue())
				&& ForgeRuntimeState.STOPPED.equals(evt.getNewValue())) {
			textViewer.stopConsole();
		}
	}
	
}
