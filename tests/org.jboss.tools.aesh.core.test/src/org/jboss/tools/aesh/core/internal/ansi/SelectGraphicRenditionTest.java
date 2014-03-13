package org.jboss.tools.aesh.core.internal.ansi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import org.jboss.tools.aesh.core.document.Document;
import org.jboss.tools.aesh.core.document.Style;
import org.jboss.tools.aesh.core.test.util.TestDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SelectGraphicRenditionTest {
	
	private HashMap<String, Object[]> invokedMethods = null;
	private boolean styleSet = false;
	
	private InvocationHandler styleHandler = new InvocationHandler() {		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			return invokedMethods.put(method.getName(), args);
		}
	};
	
	private Style testStyle = null;
	
	private Document testDocument = new TestDocument() {
		@Override public Style newStyleFromCurrent() { return testStyle; }
		@Override public void setCurrentStyle(Style style) { styleSet = true; }
	};
	
	@Before
	public void setup() {
		invokedMethods = new HashMap<String, Object[]>();
		testStyle = (Style)Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(), 
				new Class[] { Style.class },  
				styleHandler);
	}
	
	@Test
	public void testGetType() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("");
		Assert.assertEquals(CommandType.SELECT_GRAPHIC_RENDITION, selectGraphicRendition.getType());
	}
	
	@Test
	public void testHandleNoChanges() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
	}
	
	@Test
	public void testHandleResetToNormal() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("0");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("resetToNormal"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBoldOn() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("1");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBoldOn"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetFaintOn() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("2");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setFaintOn"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetItalicOn() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("3");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setItalicOn"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetUnderlineSingle() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("4");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setUnderlineSingle"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetImageNegative() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("7");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setImageNegative"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetCrossedOut() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("9");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setCrossedOut"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBoldOrFaintOff() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("22");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBoldOrFaintOff"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetItalicOff() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("23");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setItalicOff"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleUnderlineNone() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("24");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setUnderlineNone"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetImagePositive() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("27");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setImagePositive"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetNotCrossedOut() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("29");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setNotCrossedOut"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundBlack() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("30");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundBlack"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundRed() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("31");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundRed"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundGreen() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("32");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundGreen"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundYellow() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("33");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundYellow"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundBlue() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("34");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundBlue"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundMagenta() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("35");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundMagenta"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundCyan() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("36");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundCyan"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundWhite() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("37");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundWhite"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundXTerm() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("38");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("38;X;255");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("38;0;255");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("38;5");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("38;5;X");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("38;5;255");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundXTerm"));
		Object[] args = invokedMethods.get("setForegroundXTerm");
		Assert.assertEquals(1, args.length);
		Assert.assertEquals(255, args[0]);
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetForegroundDefault() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("39");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setForegroundDefault"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundBlack() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("40");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundBlack"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundRed() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("41");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundRed"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundGreen() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("42");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundGreen"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundYellow() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("43");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundYellow"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundBlue() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("44");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundBlue"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundMagenta() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("45");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundMagenta"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundCyan() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("46");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundCyan"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundWhite() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("47");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundWhite"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundXTerm() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("48");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("48;X;255");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("48;0;255");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("48;5");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("48;5;X");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
		selectGraphicRendition = new SelectGraphicRendition("48;5;255");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundXTerm"));
		Object[] args = invokedMethods.get("setBackgroundXTerm");
		Assert.assertEquals(1, args.length);
		Assert.assertEquals(255, args[0]);
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleSetBackgroundDefault() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("49");
		selectGraphicRendition.handle(testDocument);
		Assert.assertEquals(1, invokedMethods.size());
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundDefault"));
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testHandleUnknown() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("666");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.isEmpty());
		Assert.assertFalse(styleSet);
	}
	
	@Test 
	public void testHandleAll() {
		SelectGraphicRendition selectGraphicRendition = 
				new SelectGraphicRendition(
						"0;1;2;3;4;7;9;22;23;24;27;29;30;31;" +
						"32;33;34;35;36;37;38;5;255;39;40;" +
						"41;42;43;44;45;46;47;48;5;255;49");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.containsKey("resetToNormal"));
		Assert.assertTrue(invokedMethods.containsKey("setBoldOn"));
		Assert.assertTrue(invokedMethods.containsKey("setFaintOn"));
		Assert.assertTrue(invokedMethods.containsKey("setItalicOn"));
		Assert.assertTrue(invokedMethods.containsKey("setUnderlineSingle"));
		Assert.assertTrue(invokedMethods.containsKey("setImageNegative"));
		Assert.assertTrue(invokedMethods.containsKey("setCrossedOut"));
		Assert.assertTrue(invokedMethods.containsKey("setBoldOrFaintOff"));
		Assert.assertTrue(invokedMethods.containsKey("setItalicOff"));
		Assert.assertTrue(invokedMethods.containsKey("setUnderlineNone"));
		Assert.assertTrue(invokedMethods.containsKey("setImagePositive"));
		Assert.assertTrue(invokedMethods.containsKey("setNotCrossedOut"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundBlack"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundRed"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundGreen"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundYellow"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundBlue"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundMagenta"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundCyan"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundWhite"));
		Assert.assertTrue(invokedMethods.containsKey("setForegroundXTerm"));
		Object[] setForegroundXTermArgs = invokedMethods.get("setForegroundXTerm");
		Assert.assertEquals(1, setForegroundXTermArgs.length);
		Assert.assertEquals(255, setForegroundXTermArgs[0]);
		Assert.assertTrue(invokedMethods.containsKey("setForegroundDefault"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundBlack"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundRed"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundGreen"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundYellow"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundBlue"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundMagenta"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundCyan"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundWhite"));
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundXTerm"));
		Object[] setBackgroundXTermArgs = invokedMethods.get("setBackgroundXTerm");
		Assert.assertEquals(1, setBackgroundXTermArgs.length);
		Assert.assertEquals(255, setBackgroundXTermArgs[0]);
		Assert.assertTrue(invokedMethods.containsKey("setBackgroundDefault"));
		Assert.assertEquals(32, invokedMethods.size());
		Assert.assertTrue(styleSet);
	}
	
	@Test
	public void testBadSequence() {
		SelectGraphicRendition selectGraphicRendition = new SelectGraphicRendition("0;1;-2;3;4");
		selectGraphicRendition.handle(testDocument);
		Assert.assertTrue(invokedMethods.containsKey("resetToNormal"));
		Assert.assertTrue(invokedMethods.containsKey("setBoldOn"));
		Assert.assertEquals(2, invokedMethods.size());
		Assert.assertFalse(styleSet);
	}
	
}
