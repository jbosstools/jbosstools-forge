/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class ShowCursorTest {

	@Test
	public void testGetType() {
		ShowCursor showCursor = new ShowCursor(null);
		Assert.assertEquals(CommandType.SHOW_CURSOR, showCursor.getType());
	}

}
