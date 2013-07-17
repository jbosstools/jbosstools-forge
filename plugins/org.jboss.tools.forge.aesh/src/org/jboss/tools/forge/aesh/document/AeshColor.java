package org.jboss.tools.forge.aesh.document;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public enum AeshColor {
	
	DEFAULT_TEXT(
			org.jboss.aesh.terminal.Color.DEFAULT_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND)),
	BLACK_TEXT(
			org.jboss.aesh.terminal.Color.BLACK_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_BLACK)),
	RED_TEXT(
			org.jboss.aesh.terminal.Color.RED_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED)),
	GREEN_TEXT(
			org.jboss.aesh.terminal.Color.GREEN_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN)),
	YELLOW_TEXT(
			org.jboss.aesh.terminal.Color.YELLOW_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW)),
	BLUE_TEXT(
			org.jboss.aesh.terminal.Color.BLUE_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE)),
	MAGENTA_TEXT(
			org.jboss.aesh.terminal.Color.MAGENTA_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA)),
	CYAN_TEXT(
			org.jboss.aesh.terminal.Color.CYAN_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN)),
	WHITE_TEXT(
			org.jboss.aesh.terminal.Color.WHITE_TEXT,
			Display.getDefault().getSystemColor(SWT.COLOR_WHITE)),
	DEFAULT_BG(
			org.jboss.aesh.terminal.Color.DEFAULT_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND)),
	BLACK_BG(
			org.jboss.aesh.terminal.Color.BLACK_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_BLACK)),
	RED_BG(
			org.jboss.aesh.terminal.Color.RED_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_RED)),
	GREEN_BG(
			org.jboss.aesh.terminal.Color.GREEN_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_GREEN)),
	YELLOW_BG(
			org.jboss.aesh.terminal.Color.YELLOW_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_YELLOW)),
	BLUE_BG(
			org.jboss.aesh.terminal.Color.BLUE_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_BLUE)),
	MAGENTA_BG(
			org.jboss.aesh.terminal.Color.MAGENTA_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA)),
	CYAN_BG(
			org.jboss.aesh.terminal.Color.CYAN_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_CYAN)),
	WHITE_BG(
			org.jboss.aesh.terminal.Color.WHITE_BG,
			Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	
	private int code;
	private Color color;
	
	private AeshColor(
			org.jboss.aesh.terminal.Color terminalColor, 
			Color swtColor) {
		code = terminalColor.getValue();
		color = swtColor;
		getColorMap().put(Integer.valueOf(code), this);
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getCode() {
		return code;
	}
	
	private static Map<Integer, AeshColor> colorMap;
	
	private static Map<Integer, AeshColor> getColorMap() {
		if (colorMap == null) {
			colorMap = new HashMap<Integer, AeshColor>();
		}
		return colorMap;
	}
	
	public static AeshColor fromCode(int code) {
		return getColorMap().get(code);
	}

}
