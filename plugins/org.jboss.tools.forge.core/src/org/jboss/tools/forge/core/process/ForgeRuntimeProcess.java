package org.jboss.tools.forge.core.process;

import java.util.Map;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.RuntimeProcess;

public class ForgeRuntimeProcess extends RuntimeProcess {

	public ForgeRuntimeProcess(ILaunch launch, Process process, String name, Map<Object, Object> attributes) {
		super(launch, process, name, attributes);
	}

	@Override
	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	public IStreamsProxy getForgeStreamsProxy() {
		return super.getStreamsProxy();
	}
	
}
