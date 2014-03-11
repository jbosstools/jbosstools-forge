package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class CommandTypeTest {
	
	@Test
	public void testFromCharacter() {
		Assert.assertEquals(CommandType.CURSOR_UP, CommandType.fromCharacter('A'));
		Assert.assertEquals(CommandType.CURSOR_DOWN, CommandType.fromCharacter('B'));
		Assert.assertEquals(CommandType.CURSOR_FORWARD, CommandType.fromCharacter('C'));
		Assert.assertEquals(CommandType.CURSOR_BACK, CommandType.fromCharacter('D'));
		Assert.assertEquals(CommandType.CURSOR_NEXT_LINE, CommandType.fromCharacter('E'));
		Assert.assertEquals(CommandType.CURSOR_PREVIOUS_LINE, CommandType.fromCharacter('F'));
		Assert.assertEquals(CommandType.CURSOR_HORIZONTAL_ABSOLUTE, CommandType.fromCharacter('G'));
		Assert.assertEquals(CommandType.CURSOR_POSITION, CommandType.fromCharacter('H'));
		Assert.assertEquals(CommandType.ERASE_DATA, CommandType.fromCharacter('J'));
		Assert.assertEquals(CommandType.ERASE_IN_LINE, CommandType.fromCharacter('K'));
		Assert.assertEquals(CommandType.SCROLL_UP, CommandType.fromCharacter('S'));
		Assert.assertEquals(CommandType.SCROLL_DOWN, CommandType.fromCharacter('T'));
		Assert.assertEquals(CommandType.HORIZONTAL_AND_VERTICAL_POSITION, CommandType.fromCharacter('f'));
		Assert.assertEquals(CommandType.SELECT_GRAPHIC_RENDITION, CommandType.fromCharacter('m'));
		Assert.assertEquals(CommandType.DEVICE_STATUS_REPORT, CommandType.fromCharacter('n'));
		Assert.assertEquals(CommandType.SAVE_CURSOR_POSITION, CommandType.fromCharacter('s'));
		Assert.assertEquals(CommandType.RESTORE_CURSOR_POSITION, CommandType.fromCharacter('u'));
		Assert.assertEquals(CommandType.HIDE_CURSOR, CommandType.fromCharacter('l'));
		Assert.assertEquals(CommandType.SHOW_CURSOR, CommandType.fromCharacter('h'));
	}

}
