package org.jboss.tools.seam.forge.launching;

public class ForgeInstallation {
	
	private String name;
	private String location;
	
	public ForgeInstallation(String name, String location) {
		this.name = name;
		this.location = location;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLocation() {
		return location;
	}

}
