package org.jboss.tools.forge.ui.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class SourceProvider extends AbstractSourceProvider {
	
	private String runtimeState = ForgeRuntime.STATE_NOT_RUNNING;
	
	@Override
	public void dispose() {
	}

	@Override
	public Map<Object, Object> getCurrentState() {
		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put(ForgeRuntime.PROPERTY_STATE, runtimeState);
		return result;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { ForgeRuntime.PROPERTY_STATE };
	}
	
	public void setRuntimeState(String state) {
		runtimeState = state;
		fireSourceChanged(ISources.WORKBENCH, ForgeRuntime.PROPERTY_STATE, state);
	}
	
}
