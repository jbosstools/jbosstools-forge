package org.jboss.tools.forge.core.process;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.forge.core.ForgeCorePlugin;

public class ForgeEmbeddedRuntime implements ForgeRuntime {
	
	public static final ForgeRuntime INSTANCE = new ForgeEmbeddedRuntime();
	
	private ForgeEmbeddedRuntime() {}

	@Override
	public final String getName() {
		return "embedded";
	}

	@Override
	public String getLocation() {
		String result = null;
		try {
			result = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime")).getAbsolutePath();
		} catch (IOException e) {
			ForgeCorePlugin.log(e);
		}
		return result;
	}

}
