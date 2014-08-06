/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.project;

public enum ProjectType {
	
	NONE(""),
	POM("pom"),
	JAR("jar"),
	WAR("war"),
	BUNDLE("bundle"),
	EAR("ear");
	
	private String name;
	
	private ProjectType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static ProjectType getType(String name) {
		ProjectType result = null;
		if (name != null) {
			for (ProjectType type : ProjectType.values()) {
				if (name.equals(type.getName())) {
					result = type;
					break;
				}
			}
		}
		return result;
	}
	
}