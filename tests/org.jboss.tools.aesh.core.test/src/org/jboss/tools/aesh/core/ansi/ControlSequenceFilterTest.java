package org.jboss.tools.aesh.core.ansi;

import java.util.ArrayList;

import org.jboss.tools.aesh.core.internal.ansi.ControlSequenceFactory;
import org.jboss.tools.aesh.core.internal.ansi.DefaultControlSequenceFactory;
import org.jboss.tools.aesh.core.internal.io.AeshOutputStream;
import org.jboss.tools.aesh.core.io.StreamListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ControlSequenceFilterTest {
	
	private static final ControlSequence TEST_CONTROL_SEQUENCE = 
			new ControlSequence() {		
				@Override
				public void handle(AnsiDocument document) {}
			};
	private static final ControlSequenceFactory TEST_CONTROL_SEQUENCE_FACTORY = 
			new ControlSequenceFactory() {				
				@Override
				public ControlSequence create(String controlSequence) {					// TODO Auto-generated method stub
					return TEST_CONTROL_SEQUENCE;
				}
			};
	private static final byte[] sequence = new byte[] { 27, '[', '#' };
	
	private ArrayList<String> producedOutput;
	private ControlSequence availableControlSequence;	
	private StreamListener listener;	
	private ControlSequenceFilter filter;
	private AnsiControlSequenceHandler handler;
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
		handler = new AnsiControlSequenceHandler() {			
			@Override
			public void handle(ControlSequence controlSequence) {
				availableControlSequence = controlSequence;
			}
		};
		filter = new ControlSequenceFilter(listener, handler);
		outputStream.addStreamListener(filter);
	}
	
	@Test
	public void testOutputAvailable() throws Exception {
		outputStream.write("test".getBytes());
		Assert.assertNull(availableControlSequence);
		Assert.assertTrue(producedOutput.contains("test"));
	}
	
	@Test
	public void testControlSequenceFactory() {
		Assert.assertEquals(
				DefaultControlSequenceFactory.INSTANCE, 
				filter.getControlSequenceFactory());
	}
	
	@Test
	public void testControlSequenceAvailable() throws Exception {
		filter.setControlSequenceFactory(TEST_CONTROL_SEQUENCE_FACTORY);
		outputStream.write(sequence);
		Assert.assertNotNull(availableControlSequence);
		Assert.assertEquals(TEST_CONTROL_SEQUENCE, availableControlSequence);
		Assert.assertTrue(producedOutput.isEmpty());
	}
	
	
	
}
