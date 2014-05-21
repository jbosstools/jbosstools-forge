package org.jboss.tools.forge.core.internal.helper;

import org.jboss.tools.forge.core.internal.ForgeCorePlugin;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.usage.event.UsageEvent;
import org.jboss.tools.usage.event.UsageEventType;
import org.jboss.tools.usage.event.UsageReporter;

public class ForgeHelper {
	
	public static void sendStartEvent(ForgeRuntime runtime) {
		UsageEventType startEventType = ForgeCorePlugin.getDefault().getForgeStartEventType();
		UsageEvent startEvent = startEventType.event(runtime.getVersion());
		UsageReporter.getInstance().trackEvent(startEvent);
	}

}
