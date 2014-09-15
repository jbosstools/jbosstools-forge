/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.io;

import java.io.IOException;
import java.io.OutputStream;

public class AeshOutputStream extends OutputStream {

	private final AeshOutputFilter filter;

	public AeshOutputStream(AeshOutputFilter filter) {
		this.filter = filter;
	}

	@Override
	public void write(int i) throws IOException {
		filter.filterOutput(String.valueOf((char) i));
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		filter.filterOutput(new String(b, off, len));
	}
}