package org.jboss.tools.forge.ui.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.junit.Before;
import org.junit.Test;

public class ForgeInputReadJobTest {
	
	private int[] in;
	private Map<String, List<Object>> invocations = new HashMap<String, List<Object>>();
	
	@Before
	public void setUp() {
		in = null;
		invocations.clear();
	}

	@Test
	public void testNullInput() {
		ForgeInputReadJob forgeInputReadJob = new ForgeInputReadJob(newForgeTestRuntime(), null);
		forgeInputReadJob.run(null);
		assertEquals(0, invocations.size());
	}
	
	@Test
	public void testNormalInput() {
		in = new int[] { 'b', 'l', 'a', 'h', -1 }; 
		ForgeInputReadJob forgeInputReadJob = new ForgeInputReadJob(newForgeTestRuntime(), new TestInputStream());
		forgeInputReadJob.run(null);
		assertEquals(1, invocations.size());
		List<Object> arguments = invocations.get("sendInput");
		assertNotNull(arguments);
		assertEquals(1, arguments.size());
		assertEquals("blah", arguments.get(0));
	}
	
	private class TestInputStream extends InputStream {
		int index = 0;
		public int read() throws IOException {
			int result = in.length > index ? in[index] : -1;
			index++;
			return result;
		}		
	}
	
	private ForgeRuntime newForgeTestRuntime() {
		ForgeRuntime result = null;
		try {
			result = (ForgeRuntime)Proxy.newProxyInstance(
					ForgeInputReadJobTest.this.getClass().getClassLoader(), 
					new Class[] { ForgeRuntime.class }, 
					new ForgeTestRuntime());
		} catch (Throwable t) {}
		return result;
	}
	
	private class ForgeTestRuntime implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			List<Object> arguments = new ArrayList<Object>();
			for (Object arg : args) {
				arguments.add(arg);
			}
			invocations.put(method.getName(), arguments);
			return null;
		}		
	}

}
