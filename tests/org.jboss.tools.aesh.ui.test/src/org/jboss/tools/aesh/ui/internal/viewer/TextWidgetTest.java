package org.jboss.tools.aesh.ui.internal.viewer;

import org.eclipse.swt.custom.ST;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.ui.internal.util.CharacterConstants;
import org.junit.Assert;
import org.junit.Test;

public class TextWidgetTest {
	
	private TextWidget testTextWidget = new TextWidget(new Shell(), 0);
	
	private Console testConsole = new Console() {		
		@Override public void stop() {}		
		@Override public void start() { }		
		@Override public void disconnect() {}	
		@Override public void connect(Document document) {}
		@Override public void sendInput(String input) {
			sentInput = input;
		}	
	};
	
	private String sentInput = null;
	
	@Test
	public void testConstructor() {
		Assert.assertNotNull(testTextWidget);
		Assert.assertNull(testTextWidget.console);
	}
	
	@Test 
	public void testSetConsole() {
		testTextWidget.setConsole(testConsole);
		Assert.assertEquals(testConsole, testTextWidget.console);
	}
	
	@Test
	public void testInvokeAction() {
		testTextWidget.console = testConsole;
		testTextWidget.invokeAction(ST.COPY);
		Assert.assertNull(sentInput);
		testTextWidget.invokeAction(ST.LINE_END);
		Assert.assertEquals(CharacterConstants.END_LINE, sentInput);
		testTextWidget.invokeAction(ST.LINE_START);
		Assert.assertEquals(CharacterConstants.START_LINE, sentInput);
		testTextWidget.invokeAction(ST.LINE_UP);
		Assert.assertEquals(CharacterConstants.PREV_HISTORY, sentInput);
		testTextWidget.invokeAction(ST.LINE_DOWN);
		Assert.assertEquals(CharacterConstants.NEXT_HISTORY, sentInput);
		testTextWidget.invokeAction(ST.COLUMN_PREVIOUS);
		Assert.assertEquals(CharacterConstants.PREV_CHAR, sentInput);
		testTextWidget.invokeAction(ST.COLUMN_NEXT);
		Assert.assertEquals(CharacterConstants.NEXT_CHAR, sentInput);
		testTextWidget.invokeAction(ST.DELETE_PREVIOUS);
		Assert.assertEquals(CharacterConstants.DELETE_PREV_CHAR, sentInput);
		testTextWidget.invokeAction(ST.DELETE_NEXT);
		Assert.assertEquals(CharacterConstants.DELETE_NEXT_CHAR, sentInput);
	}

}
