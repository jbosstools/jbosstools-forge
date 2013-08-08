package org.jboss.tools.aesh.core.ansi;

public class AnsiControlSequenceFactory {
	
	public static AnsiControlSequence create(AnsiControlSequenceType type, String controlSequence) {
		switch(type) {
//			case CURSOR_HORIZONTAL_ABSOLUTE: return new CursorHorizontalAbsoluteSequence(controlSequence);
			default: return  null;
		}
	}

//	private boolean isAnsiEnd(char c) {
//	return  c == 'G' || 
//			c == 'K' ||
//			c == 'm' ||
//			c == 'H' ||
//			c == 'J';
//}

}
