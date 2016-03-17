/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.console;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;

public enum ForgeConsoleManager {
	INSTANCE;

	private List<ForgeConsole> consoles = new ArrayList<>();

	private ForgeConsoleManager() {
		createConsoles();
	}

	private void createConsoles() {
		consoles = new ArrayList<>();
		for (ForgeRuntime runtime : ForgeCorePreferences.INSTANCE.getRuntimes()) {
			ForgeConsole console = new org.jboss.tools.forge.ui.internal.console.F2Console(
					runtime);
			int index = calculateIndex(console);
			consoles.add(index, console);
		}
	}

	public ForgeConsole[] getConsoles() {
		return consoles.toArray(new ForgeConsole[consoles.size()]);
	}

	public ForgeConsole getDefaultConsole() {
		return getConsole(ForgeCorePreferences.INSTANCE.getDefaultRuntime());
	}

	public ForgeConsole getConsole(ForgeRuntime runtime) {
		for (ForgeConsole console : getConsoles()) {
			if (console.getRuntime() == runtime) {
				return console;
			}
		}
		return null;
	}

	private int calculateIndex(ForgeConsole forgeConsole) {
		ForgeRuntime runtime = forgeConsole.getRuntime();
		String newVersion = runtime.getVersion();
		for (int i = 0; i < consoles.size(); i++) {
			String version = consoles.get(i).getRuntime().getVersion();
			if (isNewer(newVersion, version)) {
				return i;
			}
		}
		return consoles.size();
	}

	private boolean isNewer(String first, String second) {
		String[] firstValues = first.split("\\.|-");
		String[] secondValues = second.split("\\.|-");
		for (int i = 0; i < 2; i++) {
			if (Integer.parseInt(firstValues[i]) < Integer
					.parseInt(secondValues[i])) {
				return false;
			}
		}
		return "Final".equals(firstValues[3]);
	}

}
