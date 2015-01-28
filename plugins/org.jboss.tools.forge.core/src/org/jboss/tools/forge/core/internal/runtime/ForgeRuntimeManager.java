/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.internal.runtime;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;

public class ForgeRuntimeManager {
	
	public static List<ForgeRuntime> getEmbeddedRuntimes() {
		ArrayList<ForgeRuntime> result = new ArrayList<ForgeRuntime>();
		result.add(FurnaceRuntime.INSTANCE);
		return result;
	}

}
