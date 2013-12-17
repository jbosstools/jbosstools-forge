package org.jboss.tools.aesh.core.ansi;


public class ControlSequenceFactory {
	
	public static ControlSequence create(String controlSequence) {
		int last = controlSequence.length() - 1;
		char c = controlSequence.charAt(last);
		ControlSequenceType type = ControlSequenceType.fromCharacter(c);
		if (type == null) {
			return null;
		} else {
			String arguments = controlSequence.substring(2, last);
			return create(type, arguments);
		}
	}
	
	private static ControlSequence create(
			ControlSequenceType type, 
			String controlSequenceString) {
		switch (type) {
		case CURSOR_UP: return new CursorUp(controlSequenceString);
		case CURSOR_DOWN: return new CursorDown(controlSequenceString);
		case CURSOR_FORWARD: return new CursorForward(controlSequenceString);
		case CURSOR_BACK: return new CursorBack(controlSequenceString);
		case CURSOR_NEXT_LINE: return new CursorNextLine(controlSequenceString);
		case CURSOR_PREVIOUS_LINE: return new CursorPreviousLine(controlSequenceString);
		case CURSOR_HORIZONTAL_ABSOLUTE: return new CursorHorizontalAbsolute(controlSequenceString);
		case CURSOR_POSITION: return new CursorPosition(controlSequenceString);
		case ERASE_DATA: return new EraseData(controlSequenceString);
		case ERASE_IN_LINE: return new EraseInLine(controlSequenceString);
		case SCROLL_UP: return new ScrollUp(controlSequenceString);
		case SCROLL_DOWN: return new ScrollDown(controlSequenceString);
		case HORIZONTAL_AND_VERTICAL_POSITION: return new HorizontalAndVerticalPosition(controlSequenceString);
		case SELECT_GRAPHIC_RENDITION: return new SelectGraphicRendition(controlSequenceString);
		case DEVICE_STATUS_REPORT: return new DeviceStatusReport(controlSequenceString);
		case SAVE_CURSOR_POSITION: return new SaveCursorPosition(controlSequenceString);
		case RESTORE_CURSOR_POSITION: return new RestoreCursorPosition(controlSequenceString);
		case HIDE_CURSOR: return new HideCursor(controlSequenceString);
		case SHOW_CURSOR: return new ShowCursor(controlSequenceString);
		default: throw new RuntimeException("Unknown Ansi Control Sequence");
		}
		
	}

}
