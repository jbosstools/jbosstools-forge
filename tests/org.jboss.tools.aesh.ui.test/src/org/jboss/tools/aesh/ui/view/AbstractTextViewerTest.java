package org.jboss.tools.aesh.ui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.ui.internal.document.StyleImpl;
import org.jboss.tools.aesh.ui.internal.viewer.TextWidget;
import org.junit.Assert;
import org.junit.Test;

public class AbstractTextViewerTest {
	
	private boolean consoleStopped = false;
	private boolean consoleStarted = false;
	private String sentInput = null;
	private boolean disconnected = false;
	private Document connectedDocument = null;
	
	private Console testConsole = new Console() {		
		@Override public void stop() { consoleStopped = true; }		
		@Override public void start() { consoleStarted = true; }	
		@Override public void sendInput(String input) { sentInput = input; }		
		@Override public void disconnect() { disconnected = true; }		
		@Override public void connect(Document document) { connectedDocument = document; }
	};
	
	private AbstractTextViewer testTextViewer = new AbstractTextViewer(new Shell()) {		
		@Override
		protected Console createConsole() {
			return testConsole;
		}
	};
	
	@Test
	public void testConstructor() {
		Assert.assertNotNull(testTextViewer.console);
		Assert.assertNotNull(testTextViewer.document);
		Assert.assertNotNull(testTextViewer.textWidget);
	}
	
	@Test 
	public void testCreateConsole() {
		Assert.assertEquals(testConsole, testTextViewer.createConsole());
	}
	
	@Test
	public void testCreateTextWidget() {
		TextWidget oldWidget = testTextViewer.textWidget;
		StyledText newWidget = testTextViewer.createTextWidget(new Shell(), SWT.NONE);
		Assert.assertEquals(newWidget, testTextViewer.textWidget);
		Assert.assertNotEquals(oldWidget, newWidget);
	}
	
	@Test
	public void testHandleVerifyEvent() {
		Event event = new Event();
		event.widget = new Button(new Shell(), SWT.NONE);
		VerifyEvent verifyEvent = new VerifyEvent(event);
		verifyEvent.text = "blahblah";
		verifyEvent.doit = true;
		testTextViewer.handleVerifyEvent(verifyEvent);
		Assert.assertEquals("blahblah", sentInput);
		Assert.assertFalse(verifyEvent.doit);
	}
	
	@Test
	public void testStartConsole() {
		testTextViewer.startConsole();
		Assert.assertTrue(consoleStarted);
		Assert.assertEquals(connectedDocument, testTextViewer.document);
		Assert.assertEquals(testTextViewer.document.getDelegate(), testTextViewer.getDocument());
	}
	
	@Test
	public void testStopConsole() {
		Style testStyle = new StyleImpl(new StyleRange());
		testTextViewer.document.getDelegate().set("blahblahblah");
		testTextViewer.document.moveCursorTo(7);
		testTextViewer.document.setCurrentStyle(testStyle);
		testTextViewer.stopConsole();
		Assert.assertTrue(consoleStopped);
		Assert.assertTrue(disconnected);
		Assert.assertEquals("", testTextViewer.document.getDelegate().get());
		Assert.assertEquals(0, testTextViewer.document.getCursorOffset());
		Assert.assertNotEquals(testStyle, testTextViewer.document.getCurrentStyle());
	}

}
