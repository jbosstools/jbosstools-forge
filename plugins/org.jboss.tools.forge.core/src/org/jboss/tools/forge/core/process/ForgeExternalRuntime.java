package org.jboss.tools.forge.core.process;

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
	public String getType() {
		return "external";
	}

}
