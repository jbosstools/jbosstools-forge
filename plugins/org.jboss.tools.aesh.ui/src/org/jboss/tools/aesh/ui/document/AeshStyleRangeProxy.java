package org.jboss.tools.aesh.ui.document;

import org.eclipse.swt.custom.StyleRange;
import org.jboss.tools.aesh.core.document.StyleRangeProxy;

public class AeshStyleRangeProxy implements StyleRangeProxy {
	
	private StyleRange styleRange;
	
	public AeshStyleRangeProxy(StyleRange styleRange) {
		this.styleRange = styleRange;
	}
	
	public StyleRange getStyleRange() {
		return styleRange;
	}

}
