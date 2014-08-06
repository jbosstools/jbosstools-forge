/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import java.util.HashMap;
import java.util.Map;

public enum CommandType {
	
	CURSOR_UP('A'),
	CURSOR_DOWN('B'),
	CURSOR_FORWARD('C'),
	CURSOR_BACK('D'),
	CURSOR_NEXT_LINE('E'),
	CURSOR_PREVIOUS_LINE('F'),
	CURSOR_HORIZONTAL_ABSOLUTE('G'),
	CURSOR_POSITION('H'),
	ERASE_DATA('J'),
	ERASE_IN_LINE('K'),
	SCROLL_UP('S'),
	SCROLL_DOWN('T'),
	HORIZONTAL_AND_VERTICAL_POSITION('f'),
	SELECT_GRAPHIC_RENDITION('m'),
	DEVICE_STATUS_REPORT('n'),
	SAVE_CURSOR_POSITION('s'),
	RESTORE_CURSOR_POSITION('u'),
	HIDE_CURSOR('l'),
	SHOW_CURSOR('h');
	
	private CommandType(char c) {
		getCommandTypeMap().put(c, this);
	}
	
	private static Map<Character, CommandType> commandTypeMap;
	
	private static Map<Character, CommandType> getCommandTypeMap() {
		if (commandTypeMap == null) {
			commandTypeMap = new HashMap<Character, CommandType>();
		}
		return commandTypeMap;
	}
	
	public static CommandType fromCharacter(char c) {
		return getCommandTypeMap().get(c);
	}

}
