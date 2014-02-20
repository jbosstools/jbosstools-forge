package org.jboss.tools.forge.core.runtime;

import org.jboss.tools.forge.core.internal.runtime.ForgeExternalRuntime;

public class ForgeRuntimeFactory {
	
	public static final ForgeRuntimeFactory INSTANCE = new ForgeRuntimeFactory();
	
	private ForgeRuntimeFactory() {}
	
	public ForgeRuntime createForgeRuntime(String name, String location) {
		return new ForgeExternalRuntime(name, location);
	}

}
