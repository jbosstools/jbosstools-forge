/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.ui.internal.util;

import org.jboss.aesh.terminal.Key;

public class CharacterConstants {

	public static final String START_LINE = toString(Key.HOME);
	public static final String PREV_CHAR = toString(Key.LEFT);
	public static final String CTRL_C = toString(Key.CTRL_C);
	public static final String CTRL_D = toString(Key.CTRL_D);
	public static final String END_LINE = toString(Key.END);
	public static final String NEXT_CHAR = toString(Key.RIGHT);
	public static final String DELETE_PREV_CHAR = toString(Key.BACKSPACE);
	public static final String PREV_HISTORY = toString(Key.UP);
	public static final String NEXT_HISTORY = toString(Key.DOWN);
	public static final String DELETE_NEXT_CHAR = toString(Key.DELETE);
	
	private static String toString(Key key) {
		int[] keyValues = key.getKeyValues();
		StringBuffer buffer = new StringBuffer(keyValues.length);
		for (int i : keyValues) {
			buffer.append((char)i);
		}
		return buffer.toString();
	}

}
