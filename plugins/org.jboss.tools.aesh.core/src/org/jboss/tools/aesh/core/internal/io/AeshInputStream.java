/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.io;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class AeshInputStream extends PipedInputStream {
	
	public static final AeshInputStream INSTANCE = new AeshInputStream();

    private PipedOutputStream outputStream = new PipedOutputStream();
    
    public AeshInputStream() {
    	super();
    	try {
			connect(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public synchronized void append(String str) {
        try {
			outputStream.write(str.getBytes());
	        outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
