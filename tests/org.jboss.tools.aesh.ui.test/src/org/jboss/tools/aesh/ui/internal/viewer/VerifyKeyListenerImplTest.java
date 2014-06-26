package org.jboss.tools.aesh.ui.internal.viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.aesh.core.console.Console;
import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.ui.internal.util.CharacterConstants;
import org.junit.Assert;
import org.junit.Test;

public class VerifyKeyListenerImplTest {
	

	private Console testConsole = new Console() {		
		@Override public void stop() {}		
		@Override public void start() { }		
		@Override public void disconnect() {}	
		@Override public void connect(Document document) {}
		@Override public void sendInput(String input) {
			sentInput = input;
		}	
		@Override public Object getCurrentResource() {return null;};
	};
	
	private String sentInput = null;
	
	private VerifyKeyListenerImpl testVerifyKeyListenerImpl = new VerifyKeyListenerImpl(testConsole);
	
	@Test
	public void testConstructor() {
		Assert.assertNotNull(testVerifyKeyListenerImpl);
		Assert.assertEquals(testConsole, testVerifyKeyListenerImpl.console);
	}
	
	@Test
	public void testVerifyKey() {
		Event event = new Event();
		event.widget = new Text(new Shell(), SWT.NONE);
		VerifyEvent verifyEvent = new VerifyEvent(event);
		verifyEvent.keyCode = 'x';
		Assert.assertNotEquals(SWT.CTRL, verifyEvent.stateMask & SWT.CTRL);
		testVerifyKeyListenerImpl.verifyKey(verifyEvent);
		Assert.assertNull(sentInput);
		verifyEvent.stateMask = verifyEvent.stateMask | SWT.CTRL;
		Assert.assertEquals(SWT.CTRL, verifyEvent.stateMask & SWT.CTRL);
		testVerifyKeyListenerImpl.verifyKey(verifyEvent);
		Assert.assertNull(sentInput);
		verifyEvent.keyCode = 'c';
		testVerifyKeyListenerImpl.verifyKey(verifyEvent);
		Assert.assertEquals(CharacterConstants.CTRL_C, sentInput);
		verifyEvent.keyCode = 'd';
		testVerifyKeyListenerImpl.verifyKey(verifyEvent);
		Assert.assertEquals(CharacterConstants.CTRL_D, sentInput);
	}

}
