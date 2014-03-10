package org.jboss.tools.aesh.core.internal.io;

import java.util.ArrayList;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.internal.ansi.Command;
import org.jboss.tools.aesh.core.internal.ansi.CommandFactory;
import org.junit.Assert;
import org.junit.Test;

public class CommandFilterTest {
	
	private Command handledCommand = null;
	private ArrayList<String> handledOutput = new ArrayList<String>();
	
	private String testSequence = new String(new byte[] { 27, '[', 'x' });
	private String before = "before";
	private String after = "after";
	
	private Command testCommand = new Command() {
		@Override
		public void handle(Document document) {
		}		
	};
	
	private CommandFactory testFactory = new CommandFactory() {		
		@Override
		public Command create(String controlSequence) {
			return testCommand;
		}
	};
	
	private AeshOutputHandler testHandler = new AeshOutputHandler() {
		@Override
		public void handleOutput(String output) {
			handledOutput.add(output);
		}
		@Override
		public void handleCommand(Command command) {
			handledCommand = command;
		}		
	};
	
	private CommandFilter commandFilter = null;
	
	@Test
	public void testFilterOutput() {
		Assert.assertNull(handledCommand);
		Assert.assertTrue(handledOutput.isEmpty());
		commandFilter = new CommandFilter(testHandler);
		commandFilter.setCommandFactory(testFactory);
		commandFilter.filterOutput(before + testSequence + after);
		Assert.assertEquals("handled command", testCommand, handledCommand);
		Assert.assertEquals("handled output", 2, handledOutput.size());
		Assert.assertEquals("before", before, handledOutput.get(0));
		Assert.assertEquals("after", after, handledOutput.get(1));
	}

}
