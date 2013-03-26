package org.jboss.tools.forge.ui.wizard.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class WizardsHelper {

	public static boolean isJPAProject(IProject project) {
		Object object = project.getAdapter(JpaProject.class);
		return object != null && object instanceof JpaProject;
	}

	public static boolean isHibernateToolsPluginAvailable() {
		final StringBuffer buff = new StringBuffer();
		Runnable command = new Runnable() {
			@Override
			public void run() {
				ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
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
	
	public static void installHibernateToolsPlugin() {
		
	}
	
}
