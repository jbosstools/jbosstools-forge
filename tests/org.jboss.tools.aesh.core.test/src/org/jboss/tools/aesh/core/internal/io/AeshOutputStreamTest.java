/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.io;

import org.junit.Assert;
import org.junit.Test;

public class AeshOutputStreamTest {
	
	private String filteredOutput = null;
	
	private AeshOutputFilter testFilter = new AeshOutputFilter() {		
		@Override
		public void filterOutput(String output) {
			filteredOutput = output;
		}
	};
	
	private AeshOutputStream aeshOutputStream = null;
	
	@Test
	public void testWrite() throws Exception {
		Assert.assertNull(filteredOutput);
		aeshOutputStream = new AeshOutputStream(testFilter);
		aeshOutputStream.write(65);
		Assert.assertEquals("A", filteredOutput);
		aeshOutputStream.write(new byte[] { 't', 'e', 's', 't' });
		Assert.assertEquals("test", filteredOutput);
	}

}
