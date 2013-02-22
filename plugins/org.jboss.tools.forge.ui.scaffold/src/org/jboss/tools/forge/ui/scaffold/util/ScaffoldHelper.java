package org.jboss.tools.forge.ui.scaffold.util;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.forge.core.preferences.ForgeRuntimesPreferences;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class ScaffoldHelper {

	public static boolean isJPAProject(IProject project) {
		return true;
	}

	public static boolean isHibernateToolsPluginAvailable() {
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		String str = runtime.sendCommand("forge list-plugins");
		return str != null && str.contains("org.jboss.hibernate.forge.hibernate-tools-plugin");
	}
	
}
