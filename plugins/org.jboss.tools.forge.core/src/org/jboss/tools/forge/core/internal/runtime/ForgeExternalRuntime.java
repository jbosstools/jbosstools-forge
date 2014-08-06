/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.internal.runtime;

import org.jboss.tools.forge.core.runtime.ForgeRuntimeType;

public class ForgeExternalRuntime extends ForgeAbstractRuntime {
	
	private String name, location;
	
	public ForgeExternalRuntime(String name, String location) {
		this.name = name;
		this.location = location;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public ForgeRuntimeType getType() {
		return ForgeRuntimeType.EXTERNAL;
	}

}
