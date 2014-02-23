package org.jboss.tools.forge.ui.part;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeState;

public class SourceProvider extends AbstractSourceProvider {
	
	private ForgeRuntimeState runtimeState = ForgeRuntimeState.STOPPED;
	
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
	
	public void setRuntimeState(ForgeRuntimeState state) {
		runtimeState = state;
		fireSourceChanged(ISources.WORKBENCH, ForgeRuntime.PROPERTY_STATE, state);
	}
	
}
