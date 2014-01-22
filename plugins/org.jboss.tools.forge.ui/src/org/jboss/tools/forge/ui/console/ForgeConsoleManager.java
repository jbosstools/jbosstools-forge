package org.jboss.tools.forge.ui.console;

import java.util.ArrayList;
import java.util.List;

public class ForgeConsoleManager {
	
	public static final ForgeConsoleManager INSTANCE = new ForgeConsoleManager();
	private List<ForgeConsole> consoles = new ArrayList<ForgeConsole>();
	
	private ForgeConsoleManager() {}
	
	public void addConsole(ForgeConsole console) {
		consoles.add(console);
	}
	
	public ForgeConsole[] getConsoles() {
		return consoles.toArray(new ForgeConsole[consoles.size()]);
	}

}
