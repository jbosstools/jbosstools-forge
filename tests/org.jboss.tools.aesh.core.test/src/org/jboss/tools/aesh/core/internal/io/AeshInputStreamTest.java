/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.io;

import org.junit.Assert;
import org.junit.Test;

public class AeshInputStreamTest {
	
	@Test
	public void testAppend() throws Exception {
		AeshInputStream ais = new AeshInputStream();
		byte[] bytes = new byte[4];
		ais.append("test");
		ais.read(bytes);
		Assert.assertEquals("test", new String(bytes));
		ais.close();
	}

}
