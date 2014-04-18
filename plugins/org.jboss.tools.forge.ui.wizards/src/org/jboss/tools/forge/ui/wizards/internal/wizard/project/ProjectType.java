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