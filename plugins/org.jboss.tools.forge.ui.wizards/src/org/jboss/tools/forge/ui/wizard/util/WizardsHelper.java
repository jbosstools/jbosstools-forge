package org.jboss.tools.forge.ui.wizard.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.ui.wizards.WizardsPlugin;

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
	
}
