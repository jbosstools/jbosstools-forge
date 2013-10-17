package org.jboss.tools.forge.ui.ext.cli;

import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.console.settings.SettingsBuilder;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ShellFactory;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.tools.aesh.core.console.AeshConsole;
import org.jboss.tools.forge.ext.core.FurnaceService;

public class F2Console extends AeshConsole {

	protected void createConsole() {
		super.createConsole();
		// ShellFactory shellFactory = FurnaceService.INSTANCE
		// .lookup(ShellFactory.class);
		// Settings settings = new
		// SettingsBuilder().inputStream(getInputStream())
		// .outputStream(getStdOut()).outputStreamError(getStdErr())
		// .create();
		// Shell shell = shellFactory.createShell(
		// OperatingSystemUtils.getUserHomeDir(), settings);
	}

	public void start() {
		// Already started
		super.start();
	}
}
