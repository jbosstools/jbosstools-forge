package org.jboss.tools.forge.core.process;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IProcessFactory;
import org.eclipse.debug.core.model.IProcess;

public class ForgeProcessFactory implements IProcessFactory {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public IProcess newProcess(ILaunch launch, Process process, String label, Map attributes) {
		if (attributes == null) {
			attributes = new HashMap(1);
		}
		attributes.put(IProcess.ATTR_PROCESS_TYPE, IForgeLaunchConfiguration.ID_FORGE_PROCESS_TYPE);
		return new ForgeRuntimeProcess(launch, process, label, attributes);
	}
}
