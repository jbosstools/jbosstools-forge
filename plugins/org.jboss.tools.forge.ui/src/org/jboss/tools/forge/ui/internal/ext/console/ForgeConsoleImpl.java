package org.jboss.tools.forge.ui.internal.ext.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.console.ForgeConsole;
import org.jboss.tools.forge.ui.internal.ext.actions.StartAction;
import org.jboss.tools.forge.ui.internal.ext.actions.StopAction;
import org.jboss.tools.forge.ui.internal.ext.cli.F2TextViewer;

public class ForgeConsoleImpl implements ForgeConsole, PropertyChangeListener {
	
	private String label = null;
	private F2TextViewer textViewer = null;
	
	public ForgeConsoleImpl() {
		getRuntime().addPropertyChangeListener(this);
		label = "Forge " + getRuntime().getVersion() + " - " + getRuntime().getType().name().toLowerCase();		
	}

	@Override
	public Control createControl(Composite parent) {
		textViewer = new F2TextViewer(parent);
		return textViewer.getControl();
	}
	
	@Override
	public IAction[] createActions() {
		return new IAction[] {
				new StartAction(),
				new StopAction()
		};
	}
	
	@Override
	public ForgeRuntime getRuntime() {
		return FurnaceRuntime.INSTANCE;
	}
	
	@Override
	public String getLabel() {
		return label;
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
