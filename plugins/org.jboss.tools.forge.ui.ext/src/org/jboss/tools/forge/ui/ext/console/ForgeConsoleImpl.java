package org.jboss.tools.forge.ui.ext.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ext.core.runtime.FurnaceRuntime;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.ext.actions.StartF2Action;
import org.jboss.tools.forge.ui.ext.actions.StopF2Action;
import org.jboss.tools.forge.ui.ext.cli.F2TextViewer;

public class ForgeConsoleImpl implements ForgeConsole, PropertyChangeListener {
	
	private String label = null;
	private F2TextViewer textViewer = null;
	
	public ForgeConsoleImpl() {
		getRuntime().addPropertyChangeListener(this);
		label = "Forge " + getRuntime().getVersion() + " - " + getRuntime().getType();		
	}

	@Override
	public Control createControl(Composite parent) {
		textViewer = new F2TextViewer(parent);
		return textViewer.getControl();
	}
	
	@Override
	public IAction[] createActions() {
		return new IAction[] {
				new StartF2Action(),
				new StopF2Action()
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
		if (ForgeRuntime.STATE_STARTING.equals(evt.getOldValue()) 
				&& ForgeRuntime.STATE_RUNNING.equals(evt.getNewValue())) {
			textViewer.startConsole();
		}
		if (ForgeRuntime.STATE_RUNNING.equals(evt.getOldValue())
				&& ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getNewValue())) {
			textViewer.stopConsole();
		}
	}
	
}
