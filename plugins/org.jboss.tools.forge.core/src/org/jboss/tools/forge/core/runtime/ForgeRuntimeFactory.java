/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.runtime;

import org.jboss.tools.forge.core.internal.runtime.ForgeExternalRuntime;

public class ForgeRuntimeFactory {
	
	public static final ForgeRuntimeFactory INSTANCE = new ForgeRuntimeFactory();
	
	private ForgeRuntimeFactory() {}
	
	public ForgeRuntime createForgeRuntime(String name, String location) {
		return new ForgeExternalRuntime(name, location);
	}

}
