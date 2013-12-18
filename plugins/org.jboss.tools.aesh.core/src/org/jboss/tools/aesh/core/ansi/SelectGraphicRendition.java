package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;
import org.jboss.tools.aesh.core.document.StyleRangeProxy;


public class SelectGraphicRendition extends ControlSequence {
	
	private String arguments;

	public SelectGraphicRendition(String arguments) {
		this.arguments = arguments;
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SELECT_GRAPHIC_RENDITION;
	}
	
	@Override
	public void handle(DocumentProxy document) {
		StyleRangeProxy styleRange = document.newStyleRangeFromCurrent();
		// do stuff with styleRange based on arguments
		System.out.println(arguments);
    	document.setCurrentStyleRange(styleRange);
	}

}
