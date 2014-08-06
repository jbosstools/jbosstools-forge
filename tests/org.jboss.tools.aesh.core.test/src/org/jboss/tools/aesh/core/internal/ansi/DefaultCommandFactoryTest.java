/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class DefaultCommandFactoryTest {

	private static final String ESCAPE_SEQUENCE = new String(new byte[] { 27, '[' });
	
	private static final String CURSOR_UP_SEQUECE = ESCAPE_SEQUENCE + 'A';
	private static final String CURSOR_DOWN_SEQUENCE = ESCAPE_SEQUENCE + 'B';
	private static final String CURSOR_FORWARD_SEQUENCE = ESCAPE_SEQUENCE + "0" + 'C';
	private static final String CURSOR_BACK_SEQUENCE = ESCAPE_SEQUENCE + "0" + 'D';
	private static final String CURSOR_NEXT_LINE_SEQUENCE = ESCAPE_SEQUENCE + 'E';
	private static final String CURSOR_PREVIOUS_LINE_SEQUENCE = ESCAPE_SEQUENCE + 'F';
	private static final String CURSOR_HORIZONTAL_ABSOLUTE_SEQUENCE = ESCAPE_SEQUENCE + "0" + 'G';
	private static final String CURSOR_POSITION_SEQUENCE = ESCAPE_SEQUENCE + 'H';
	private static final String ERASE_DATA_SEQUENCE = ESCAPE_SEQUENCE + 'J';
	private static final String ERASE_IN_LINE_SEQUENCE = ESCAPE_SEQUENCE + 'K';
	private static final String SCROLL_UP_SEQUENCE = ESCAPE_SEQUENCE + 'S';
	private static final String SCROLL_DOWN_SEQUENCE = ESCAPE_SEQUENCE + 'T';
	private static final String HORIZONTAL_AND_VERTICAL_POSITION_SEQUENCE = ESCAPE_SEQUENCE + 'f';
	private static final String SELECT_GRAPHIC_RENDITION_SEQUENCE = ESCAPE_SEQUENCE + 'm';
	private static final String DEVICE_STATUS_REPORT_SEQUENCE = ESCAPE_SEQUENCE + 'n';
	private static final String SAVE_CURSOR_POSITION_SEQUENCE = ESCAPE_SEQUENCE + 's';
	private static final String RESTORE_CURSOR_POSITION_SEQUENCE = ESCAPE_SEQUENCE + 'u';
	private static final String HIDE_CURSOR_SEQUENCE = ESCAPE_SEQUENCE + 'l';
	private static final String SHOW_CURSOR_SEQUENCE = ESCAPE_SEQUENCE + 'h';
	
	private static final String BAD_SEQUENCE = ESCAPE_SEQUENCE + 'X';
	
	private DefaultCommandFactory factory = DefaultCommandFactory.INSTANCE;
	
	@Test
	public void testCreate() {
		Assert.assertTrue("cursor up", factory.create(CURSOR_UP_SEQUECE) instanceof CursorUp);
		Assert.assertTrue("cursor down", factory.create(CURSOR_DOWN_SEQUENCE) instanceof CursorDown);
		Assert.assertTrue("cursor forward", factory.create(CURSOR_FORWARD_SEQUENCE) instanceof CursorForward);
		Assert.assertTrue("cursor back", factory.create(CURSOR_BACK_SEQUENCE) instanceof CursorBack);
		Assert.assertTrue("cursor next line", factory.create(CURSOR_NEXT_LINE_SEQUENCE) instanceof CursorNextLine);
		Assert.assertTrue("cursor previous line", factory.create(CURSOR_PREVIOUS_LINE_SEQUENCE) instanceof CursorPreviousLine);
		Assert.assertTrue("cursor horizontal absolute", factory.create(CURSOR_HORIZONTAL_ABSOLUTE_SEQUENCE) instanceof CursorHorizontalAbsolute);
		Assert.assertTrue("cursor position", factory.create(CURSOR_POSITION_SEQUENCE) instanceof CursorPosition);
		Assert.assertTrue("erase data", factory.create(ERASE_DATA_SEQUENCE) instanceof EraseData);
		Assert.assertTrue("erase in line", factory.create(ERASE_IN_LINE_SEQUENCE) instanceof EraseInLine);
		Assert.assertTrue("scroll up", factory.create(SCROLL_UP_SEQUENCE) instanceof ScrollUp);
		Assert.assertTrue("scroll down", factory.create(SCROLL_DOWN_SEQUENCE) instanceof ScrollDown);
		Assert.assertTrue("horizontal and vertical position", factory.create(HORIZONTAL_AND_VERTICAL_POSITION_SEQUENCE) instanceof HorizontalAndVerticalPosition);
		Assert.assertTrue("select graphic rendition", factory.create(SELECT_GRAPHIC_RENDITION_SEQUENCE) instanceof SelectGraphicRendition);
		Assert.assertTrue("device status report", factory.create(DEVICE_STATUS_REPORT_SEQUENCE) instanceof DeviceStatusReport);
		Assert.assertTrue("save cursor position", factory.create(SAVE_CURSOR_POSITION_SEQUENCE) instanceof SaveCursorPosition);
		Assert.assertTrue("restore cursor position", factory.create(RESTORE_CURSOR_POSITION_SEQUENCE) instanceof RestoreCursorPosition);
		Assert.assertTrue("hide cursor", factory.create(HIDE_CURSOR_SEQUENCE) instanceof HideCursor);
		Assert.assertTrue("show cursor", factory.create(SHOW_CURSOR_SEQUENCE) instanceof ShowCursor);
		Assert.assertNull("bad sequence", factory.create(BAD_SEQUENCE));
	}

}
