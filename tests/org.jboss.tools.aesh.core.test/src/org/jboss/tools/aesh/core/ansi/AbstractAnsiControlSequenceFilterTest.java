package org.jboss.tools.aesh.core.ansi;

import java.util.ArrayList;

import org.jboss.tools.aesh.core.internal.ansi.CursorBack;
import org.jboss.tools.aesh.core.internal.io.AeshOutputStream;
import org.jboss.tools.aesh.core.io.StreamListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AbstractAnsiControlSequenceFilterTest {
	
	private static final byte[] sequence = new byte[] { 27, '[', '5', 'D' };
	
	private ArrayList<String> producedOutput;
	private AnsiControlSequence availableControlSequence;	
	private StreamListener listener;	
	private AbstractAnsiControlSequenceFilter filter;
	private AeshOutputStream outputStream;
	
	@Before
	public void setup() {
		producedOutput = new ArrayList<String>();
		availableControlSequence = null;
		outputStream = new AeshOutputStream();
		listener = new StreamListener() {			
			@Override
			public void outputAvailable(String str) {
				producedOutput.add(str);
			}
		};
		filter = new AbstractAnsiControlSequenceFilter(listener) {
			@Override
			public void controlSequenceAvailable(AnsiControlSequence controlSequence) {
				availableControlSequence = controlSequence;
			}
		};
		outputStream.addStreamListener(filter);
	}
	
	@Test
	public void testOutputAvailable() throws Exception {
		outputStream.write("test".getBytes());
		Assert.assertNull(availableControlSequence);
		Assert.assertTrue(producedOutput.contains("test"));
	}
	
	@Test
	public void testControlSequenceAvailable() throws Exception {
		outputStream.write(sequence);
		Assert.assertNotNull(availableControlSequence);
		Assert.assertTrue(availableControlSequence instanceof CursorBack);
		Assert.assertTrue(producedOutput.isEmpty());
	}
	
}
