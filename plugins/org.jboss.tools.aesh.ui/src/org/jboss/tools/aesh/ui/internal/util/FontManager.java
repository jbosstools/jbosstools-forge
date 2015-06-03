/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.aesh.ui.internal.util;

import java.util.ArrayList;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class FontManager {
	
	public static final String AESH_CONSOLE_FONT = "org.jboss.tools.aesh.ui.font";

	public static FontManager INSTANCE = new FontManager();
	
	private static Font ITALIC;
	private static Font DEFAULT;
	private static Font BOLD;
	private static Font ITALIC_BOLD;
	
	private ArrayList<IPropertyChangeListener> listeners = new ArrayList<IPropertyChangeListener>();
	
	private FontManager() {
		initializeFonts();
		initializeListener();
	}
	
	private void initializeDefault() {
		DEFAULT = JFaceResources.getFont(AESH_CONSOLE_FONT);
	}
	
	private FontData createFontDataFromNormal() {
		FontData normalData = DEFAULT.getFontData()[0];
		FontData result = new FontData();
		result.setName(normalData.getName());
		result.height = normalData.height;
		return result;
	}
	
	private void initializeItalic() {
		FontData italicData = createFontDataFromNormal();
		italicData.setStyle(SWT.ITALIC);
		ITALIC = new Font(DEFAULT.getDevice(), italicData);
	}
	
	private void initializeBold() {
		FontData boldData = createFontDataFromNormal();
		boldData.setStyle(SWT.BOLD);
		BOLD = new Font(DEFAULT.getDevice(), boldData);
	}
	
	private void initializeItalicBold() {
		FontData italicBoldData = createFontDataFromNormal();
		italicBoldData.setStyle(SWT.BOLD | SWT.ITALIC);
		ITALIC_BOLD = new Font(DEFAULT.getDevice(), italicBoldData);
	}
	
	public Font getDefault() {
		return DEFAULT;
	}
	
	public Font getItalic() {
		return ITALIC;
	}
	
	public Font getBold() {
		return BOLD;
	}
	
	public Font getItalicBold() {
		return ITALIC_BOLD;
	}
	
	public void addListener(IPropertyChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IPropertyChangeListener listener) {
		listeners.remove(listener);
	}
	
	private void initializeListener() {
		JFaceResources.getFontRegistry().addListener(new IPropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (AESH_CONSOLE_FONT.equals(event.getProperty())) {
					initializeFonts();
					informListeners(event);
				}
			}
		});
	}
	
	private void informListeners(PropertyChangeEvent event) {
		for (IPropertyChangeListener listener : listeners) {
			listener.propertyChange(event);
		}
	}
	
	private void initializeFonts() {
		initializeDefault();
		initializeBold();
		initializeItalic();
		initializeItalicBold();
	}

}
