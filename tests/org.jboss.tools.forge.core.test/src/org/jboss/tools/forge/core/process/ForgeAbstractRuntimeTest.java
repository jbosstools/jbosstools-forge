package org.jboss.tools.forge.core.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ForgeAbstractRuntimeTest {
	
	private ForgeAbstractRuntime runtime;
	private Set<String> calledMethods;
	private List<PropertyChangeEvent> propertyChangeEvents;
	private PropertyChangeListener propertyChangeListener;
	private int amountWorked;
	private int totalWork;
	private String taskName;
	
	@Before
	public void setUp() {
		propertyChangeEvents = new ArrayList<PropertyChangeEvent>();
		calledMethods = new HashSet<String>();
		runtime = new ForgeTestRuntime();
		propertyChangeListener = new TestPropertyChangeListener();
		runtime.addPropertyChangeListener(propertyChangeListener);
		amountWorked = 0;
		totalWork = 0;
		taskName = null;
	}
	
	@After
	public void tearDown() throws Exception {
		if (runtime.getProcess() != null) {
			runtime.getProcess().terminate();
		}
		runtime.removePropertyChangeListener(propertyChangeListener);
		propertyChangeListener = null;
		runtime = null;
		calledMethods = null;
		propertyChangeEvents = null;
	}

	@Test
	public void testStartComplete() {
		runtime.start(null);
		assertEquals(ForgeRuntime.STATE_RUNNING, runtime.getState());
		assertNotNull(runtime.getProcess());
		assertEquals(2, propertyChangeEvents.size());
		for (PropertyChangeEvent evt : propertyChangeEvents) {
			if (ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getOldValue())) {
				assertEquals(ForgeRuntime.STATE_STARTING, evt.getNewValue());
			} else if (ForgeRuntime.STATE_STARTING.equals(evt.getOldValue())) {
				assertEquals(ForgeRuntime.STATE_RUNNING, evt.getNewValue());
			}
		}
	}
	
	@Test
	public void testStartWithProgressMonitor() {
		runtime.start(new TestProgressMonitor());
		assertEquals(ForgeRuntime.STATE_NOT_RUNNING, runtime.getState());
		assertNull(runtime.getProcess());
		assertEquals("Starting Forge", taskName);
		assertEquals(IProgressMonitor.UNKNOWN, totalWork);
		assertEquals(4, calledMethods.size());
		assertTrue(calledMethods.contains("beginTask"));
		assertTrue(calledMethods.contains("isCanceled"));
		assertTrue(calledMethods.contains("worked"));
		assertTrue(calledMethods.contains("done"));
		assertEquals(1, amountWorked);
		assertEquals(2, propertyChangeEvents.size());
		for (PropertyChangeEvent evt : propertyChangeEvents) {
			if (ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getOldValue())) {
				assertEquals(ForgeRuntime.STATE_STARTING, evt.getNewValue());
			} else if (ForgeRuntime.STATE_STARTING.equals(evt.getOldValue())) {
				assertEquals(ForgeRuntime.STATE_NOT_RUNNING, evt.getNewValue());
			}
		}
	}
	
	@Test
	public void testStopComplete() {
		runtime.start(null);
		assertEquals(ForgeRuntime.STATE_RUNNING, runtime.getState());
		propertyChangeEvents.clear();
		runtime.stop(null);
		assertEquals(ForgeRuntime.STATE_NOT_RUNNING, runtime.getState());
		assertNull(runtime.getProcess());
		assertEquals(1, propertyChangeEvents.size());
		for (PropertyChangeEvent evt : propertyChangeEvents) {
			assertEquals(ForgeRuntime.STATE_RUNNING, evt.getOldValue());
			assertEquals(ForgeRuntime.STATE_NOT_RUNNING, evt.getNewValue());
		}
	}
	
	@Test
	public void testStopWithProgressMonitor() {
		runtime.start(null);
		assertEquals(ForgeRuntime.STATE_RUNNING, runtime.getState());
		propertyChangeEvents.clear();
		runtime.stop(new TestProgressMonitor());
		assertEquals(ForgeRuntime.STATE_NOT_RUNNING, runtime.getState());
		assertNull(runtime.getProcess());
		assertEquals("Stopping Forge", taskName);
		assertEquals(1, totalWork);
		assertEquals(2, calledMethods.size());
		assertTrue(calledMethods.contains("beginTask"));
		assertTrue(calledMethods.contains("done"));
		assertEquals(0, amountWorked);
		assertEquals(1, propertyChangeEvents.size());
		for (PropertyChangeEvent evt : propertyChangeEvents) {
			assertEquals(ForgeRuntime.STATE_RUNNING, evt.getOldValue());
			assertEquals(ForgeRuntime.STATE_NOT_RUNNING, evt.getNewValue());
		}
	}
	
	private class TestPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			propertyChangeEvents.add(evt);
			if (ForgeRuntime.STATE_STARTING.equals(evt.getNewValue())) {
				assertNotNull(runtime.getProcess());
			}
		}		
	}
	
	private class TestProgressMonitor extends NullProgressMonitor {
		public void beginTask(String name, int total) {
			taskName = name;
			totalWork = total;
			calledMethods.add("beginTask");
		}
		public void done() {
			calledMethods.add("done");
		}
		public boolean isCanceled() {
			calledMethods.add("isCanceled");
			return super.isCanceled();
		}
		public void worked(int work) {
			amountWorked++;
			calledMethods.add("worked");
			setCanceled(true);			
			super.worked(work);
		}
	}
	
	private class ForgeTestRuntime extends ForgeAbstractRuntime {
		public String getName() {
			return "test";
		}
		public String getLocation() {
			String result = null;
			try {
				result = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime")).getAbsolutePath();
			} catch (IOException e) {}
			return result;
		}
		public String getType() {
			return "test";
		}
	}
	
}
