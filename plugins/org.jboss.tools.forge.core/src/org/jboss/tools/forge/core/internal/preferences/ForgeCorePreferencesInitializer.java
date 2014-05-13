package org.jboss.tools.forge.core.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.internal.ForgeCorePlugin;
import org.jboss.tools.forge.core.internal.runtime.ForgeRuntimeManager;
import org.jboss.tools.forge.core.preferences.ForgeCorePreferences;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.osgi.framework.Version;

public class ForgeCorePreferencesInitializer extends AbstractPreferenceInitializer {

	public static final String INITIAL_RUNTIMES_PREFERENCE =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
			"<forgeRuntimes default=\"embedded\">" +
			"   <runtime name=\"embedded\" type=\"embedded\"/>" +
			"</forgeRuntimes>";

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID);
		preferences.put(ForgeCorePreferences.PREF_FORGE_RUNTIMES, INITIAL_RUNTIMES_PREFERENCE);
	}
	
	private String constructInitialRuntimesPreferences() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		buffer.append("<forgeRuntimes "                                               );
		buffer.append("      version=\"" + getForgeRuntimesVersion() + "\""           );
		buffer.append("      default=\"" + getDefaultRuntimeName() + "\">"            );
		buffer.append(getEmbeddedRuntimes()                                           );
		buffer.append("</forgeRuntimes>"                                              );
		return buffer.toString();
	}
	
	private String getForgeRuntimesVersion() {
		Version version = ForgeCorePlugin.getDefault().getBundle().getVersion();
		StringBuffer buffer = new StringBuffer();
		buffer.append(version.getMajor());
		buffer.append(".");
		buffer.append(version.getMinor());
		buffer.append(".");
		buffer.append(version.getMicro());
		return buffer.toString();
	}
	
	private String getDefaultRuntimeName() {
		return FurnaceRuntime.INSTANCE.getName();
	}
	
	private String getEmbeddedRuntimes() {
		StringBuffer buffer = new StringBuffer();
		for (ForgeRuntime runtime : ForgeRuntimeManager.getEmbeddedRuntimes()) {
			buffer.append("   <runtime "                        );
			buffer.append("         name=\"" + runtime.getName());
			buffer.append("         type=\"embedded\"/>"        );
		}
		return buffer.toString();
	}
	
}
