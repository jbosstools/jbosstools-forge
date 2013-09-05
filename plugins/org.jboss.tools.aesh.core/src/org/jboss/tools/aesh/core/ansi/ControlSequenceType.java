package org.jboss.tools.aesh.core.ansi;

import java.util.HashMap;
import java.util.Map;

public enum ControlSequenceType {
	
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
	
	private ControlSequenceType(char c) {
		getAnsiControlSequenceTypeMap().put(c, this);
	}
	
	private static Map<Character, ControlSequenceType> ansiControlSequenceTypeMap;
	
	private static Map<Character, ControlSequenceType> getAnsiControlSequenceTypeMap() {
		if (ansiControlSequenceTypeMap == null) {
			ansiControlSequenceTypeMap = new HashMap<Character, ControlSequenceType>();
		}
		return ansiControlSequenceTypeMap;
	}
	
	public static ControlSequenceType fromCharacter(char c) {
		return getAnsiControlSequenceTypeMap().get(c);
	}

}
