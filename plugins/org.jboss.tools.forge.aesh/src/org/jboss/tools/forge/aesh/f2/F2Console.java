package org.jboss.tools.forge.aesh.f2;

import org.jboss.forge.addon.shell.ForgeShell;
import org.jboss.tools.forge.aesh.console.AeshConsole;
import org.jboss.tools.forge.aesh.io.AeshInputStream;
import org.jboss.tools.forge.aesh.io.AeshOutputStream;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class F2Console extends AeshConsole {

	protected void initialize() {
		ForgeShell shell = FurnaceService.INSTANCE.lookup(ForgeShell.class);
		inputStream = new AeshInputStream();
		shell.setInputStream(inputStream);
		stdOut = new AeshOutputStream();
		shell.setStdOut(stdOut);
		stdErr = new AeshOutputStream();
		shell.setStdErr(stdErr);
	}
	
	public void start() {
		try {
			ForgeShell shell = FurnaceService.INSTANCE.lookup(ForgeShell.class);
			shell.startShell();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
