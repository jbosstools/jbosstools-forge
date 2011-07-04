package org.jboss.tools.forge.core.process;

public class ForgeExternalRuntime implements ForgeRuntime {
	
	private String name, location;
	
	public ForgeExternalRuntime(String name, String location) {
		this.name = name;
		this.location = location;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLocation() {
		return location;
	}
	
	@Override
	public String getType() {
		return "external";
	}

}
