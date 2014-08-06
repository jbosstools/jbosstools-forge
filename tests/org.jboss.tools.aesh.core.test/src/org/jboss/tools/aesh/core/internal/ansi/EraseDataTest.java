/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Test;

public class EraseDataTest {
	
	private boolean resetPerformed = false;
	
	private Document testDocument = new TestDocument() {
		@Override public void reset() { resetPerformed = true; }
	};
	
	@Test
	public void testGetType() {
		EraseData eraseData = new EraseData("2");
		Assert.assertEquals(CommandType.ERASE_DATA, eraseData.getType());
	}
	
	@Test
	public void testHandle() {
		Assert.assertFalse(resetPerformed);
		EraseData eraseData = new EraseData(null);
		eraseData.handle(testDocument);
		Assert.assertFalse(resetPerformed);
		eraseData = new EraseData("2");
		eraseData.handle(testDocument);
		Assert.assertTrue(resetPerformed);
	}

}
