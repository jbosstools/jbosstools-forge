/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
