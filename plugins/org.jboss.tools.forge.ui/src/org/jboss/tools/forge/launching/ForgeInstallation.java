package org.jboss.tools.forge.launching;

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
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

}
