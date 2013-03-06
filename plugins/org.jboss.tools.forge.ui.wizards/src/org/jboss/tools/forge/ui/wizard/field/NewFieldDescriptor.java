package org.jboss.tools.forge.ui.wizard.field;


public class NewFieldDescriptor {
	
	public static final String[]  TYPES = {
		"string",
		"int",
		"long",
		"number",
		"boolean",
		"temporal",
		"oneToOne",
		"oneToMany",
		"ManyToOne",
		"ManyToMany",
		"custom"
	};
	
	public String name;
	public String project;
	public String entity;
	public String type;

}
