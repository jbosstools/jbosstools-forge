package org.jboss.tools.forge.core.internal.runtime;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;

public class ForgeRuntimeManager {
	
	public static List<ForgeRuntime> getEmbeddedRuntimes() {
		ArrayList<ForgeRuntime> result = new ArrayList<ForgeRuntime>();
		result.add(FurnaceRuntime.INSTANCE);
		result.add(ForgeEmbeddedRuntime.INSTANCE);
		return result;
	}

}
