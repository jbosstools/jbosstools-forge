package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class AbstractCommandTest {
	
	private AbstractCommand command = new AbstractCommand() {		
		@Override public CommandType getType() { return null; }
	};
	
	@Test
	public void testHandle() {
		try {
			command.handle(null);
			Assert.fail("runtime exception should happen");
		} catch (RuntimeException e) {
			Assert.assertEquals(AbstractCommand.NOT_IMPLEMENTED, e.getMessage());
		}
	}

}
