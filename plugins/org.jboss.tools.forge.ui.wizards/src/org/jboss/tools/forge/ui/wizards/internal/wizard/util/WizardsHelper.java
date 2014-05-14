package org.jboss.tools.forge.ui.wizards.internal.wizard.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeType;
import org.jboss.tools.forge.ui.wizards.internal.WizardsPlugin;

public class WizardsHelper {

	public static boolean isJPAProject(IProject project) {
		boolean result = false;
		JpaProject.Reference reference = 
				(JpaProject.Reference)project.getAdapter(JpaProject.Reference.class);
		try {
			result = reference.getValue() != null;
		} catch (InterruptedException e) {
			WizardsPlugin.log(e);
		}
		return result;
	}

	public static boolean isHibernateToolsPluginAvailable() {
		final StringBuffer buff = new StringBuffer();
		Runnable command = new Runnable() {
			@Override
			public void run() {
				ForgeRuntime runtime = ForgeCorePreferences.INSTANCE.getDefaultRuntime();
				String str = runtime.sendCommand("forge list-plugins");
				synchronized(buff) {
					buff.append(str).append(" done");
				}
			}			
		};
		new Thread(command).start();
		while (buff.length() == 0) {
			try {
				Thread.currentThread().wait(100);
			} catch (InterruptedException e) {}
		}
		return buff.toString().contains("org.jboss.hibernate.forge.hibernate-tools-plugin");
	}
	
	// This method selects the embedded Forge 1 runtime with the highest version string
	public static ForgeRuntime getForgeRuntime() {
		ForgeRuntime result = null;
		ForgeRuntime[] runtimes = ForgeCorePreferences.INSTANCE.getRuntimes();
		for (ForgeRuntime runtime : runtimes) {
			if (ForgeRuntimeType.EXTERNAL.equals(runtime.getState())) continue;
			String version = runtime.getVersion();
			if (!version.startsWith("1.")) continue;
			if (result == null || isHigherVersion(version, result.getVersion())) {
				result = runtime;
			}
		}
		return result;
	}
	
	private static boolean isHigherVersion(String first, String second) {
		boolean result = false;
		String[] f = first.split("\\.-");
		String[] s = second.split("\\.-");
		// don't take identifier into consideration
		for (int i = 0; i < 3; i++) {
			if (Integer.valueOf(f[i]) > Integer.valueOf(s[i])) {
				result = true;
			}
		}
		return result;
	}
	
}
