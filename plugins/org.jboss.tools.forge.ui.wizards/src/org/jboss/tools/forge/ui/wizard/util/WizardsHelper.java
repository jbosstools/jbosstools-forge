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
		ForgeRuntime runtime = ForgeRuntimesPreferences.INSTANCE.getDefaultRuntime();
		String str = runtime.sendCommand("forge list-plugins");
		return str != null && str.contains("org.jboss.hibernate.forge.hibernate-tools-plugin");
	}
	
}
