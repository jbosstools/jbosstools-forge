package org.jboss.tools.forge.console;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

class ColorManager {	
	
	private static ColorManager INSTANCE;
	
	private ColorManager() {}
	
	public static ColorManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE= new ColorManager();
		}
		return INSTANCE;
	}
	
	protected Map<RGB, Color> fColorTable= new HashMap<RGB, Color>(10);
	
	public Color getColor(RGB rgb) {
		Color color= (Color) fColorTable.get(rgb);
		if (color == null) {
			color= new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
	
	public void dispose() {
		Iterator<Color> e= fColorTable.values().iterator();
		while (e.hasNext())
			((Color) e.next()).dispose();
	}
}


