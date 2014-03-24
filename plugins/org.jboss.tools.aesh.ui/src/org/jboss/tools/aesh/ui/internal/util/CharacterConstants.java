package org.jboss.tools.aesh.ui.internal.util;

public class CharacterConstants {

	public static final String START_LINE = new Character((char)1).toString();
	public static final String PREV_CHAR = new Character((char)2).toString();
	public static final String CTRL_C = new Character((char)3).toString();
	public static final String CTRL_D = new Character((char)4).toString();
	public static final String END_LINE = new Character((char)5).toString();
	public static final String NEXT_CHAR = new Character((char)6).toString();
	public static final String DELETE_PREV_CHAR = new Character((char)8).toString();
	public static final String PREV_HISTORY = new Character((char)16).toString();
	public static final String NEXT_HISTORY = new Character((char)14).toString();
	public static final String DELETE_NEXT_CHAR = new String(new char[] {(char)27,(char)91,(char)51,(char)126});

}
