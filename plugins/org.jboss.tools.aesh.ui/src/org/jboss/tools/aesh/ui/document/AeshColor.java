package org.jboss.tools.aesh.ui.document;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public enum AeshColor {
	
	DEFAULT_TEXT(
			org.jboss.aesh.terminal.Color.DEFAULT,
			Display.getDefault().getSystemColor(SWT.COLOR_INFO_FOREGROUND)),
	BLACK_TEXT(
			org.jboss.aesh.terminal.Color.BLACK,
			Display.getDefault().getSystemColor(SWT.COLOR_BLACK)),
	RED_TEXT(
			org.jboss.aesh.terminal.Color.RED,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED)),
	GREEN_TEXT(
			org.jboss.aesh.terminal.Color.GREEN,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN)),
	YELLOW_TEXT(
			org.jboss.aesh.terminal.Color.YELLOW,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW)),
	BLUE_TEXT(
			org.jboss.aesh.terminal.Color.BLUE,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE)),
	MAGENTA_TEXT(
			org.jboss.aesh.terminal.Color.MAGENTA,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_MAGENTA)),
	CYAN_TEXT(
			org.jboss.aesh.terminal.Color.CYAN,
			Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN)),
	WHITE_TEXT(
			org.jboss.aesh.terminal.Color.WHITE,
			Display.getDefault().getSystemColor(SWT.COLOR_WHITE)),
	DEFAULT_BG(
			org.jboss.aesh.terminal.Color.DEFAULT,
			Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND)),
	BLACK_BG(
			org.jboss.aesh.terminal.Color.BLACK,
			Display.getDefault().getSystemColor(SWT.COLOR_BLACK)),
	RED_BG(
			org.jboss.aesh.terminal.Color.RED,
			Display.getDefault().getSystemColor(SWT.COLOR_RED)),
	GREEN_BG(
			org.jboss.aesh.terminal.Color.GREEN,
			Display.getDefault().getSystemColor(SWT.COLOR_GREEN)),
	YELLOW_BG(
			org.jboss.aesh.terminal.Color.YELLOW,
			Display.getDefault().getSystemColor(SWT.COLOR_YELLOW)),
	BLUE_BG(
			org.jboss.aesh.terminal.Color.BLUE,
			Display.getDefault().getSystemColor(SWT.COLOR_BLUE)),
	MAGENTA_BG(
			org.jboss.aesh.terminal.Color.MAGENTA,
			Display.getDefault().getSystemColor(SWT.COLOR_MAGENTA)),
	CYAN_BG(
			org.jboss.aesh.terminal.Color.CYAN,
			Display.getDefault().getSystemColor(SWT.COLOR_CYAN)),
	WHITE_BG(
			org.jboss.aesh.terminal.Color.WHITE,
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
