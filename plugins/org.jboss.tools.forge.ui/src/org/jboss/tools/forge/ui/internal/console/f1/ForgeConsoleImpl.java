package org.jboss.tools.forge.ui.internal.console.f1;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;
import org.jboss.tools.forge.ui.internal.actions.GoToAction;
import org.jboss.tools.forge.ui.internal.actions.LinkAction;
import org.jboss.tools.forge.ui.internal.actions.StartAction;
import org.jboss.tools.forge.ui.internal.actions.StopAction;
import org.jboss.tools.forge.ui.internal.console.ForgeConsole;
import org.jboss.tools.forge.ui.internal.part.ForgeTextViewer;

public class ForgeConsoleImpl implements ForgeConsole, PropertyChangeListener {
	
	private ForgeTextViewer forgeTextViewer = null;
	private ForgeRuntime forgeRuntime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
	private String label = null;
	
	public ForgeConsoleImpl() {
		getRuntime().addPropertyChangeListener(this);
		label = "Forge " + getRuntime().getVersion() + " - " + getRuntime().getType().name().toLowerCase();
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
				new StartAction(getRuntime()),
				new StopAction(getRuntime()),
				new GoToAction(getRuntime()),
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
		if (ForgeRuntimeState.STOPPED.equals(evt.getOldValue()) 
				&& ForgeRuntimeState.STARTING.equals(evt.getNewValue())) {
			forgeTextViewer.startConsole();
		}
		if (ForgeRuntimeState.STOPPED.equals(evt.getNewValue())) {
			forgeTextViewer.stopConsole();
		}
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
}
