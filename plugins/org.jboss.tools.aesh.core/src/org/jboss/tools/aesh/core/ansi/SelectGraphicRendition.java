package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


public class SelectGraphicRendition extends ControlSequence {

	public SelectGraphicRendition(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.SELECT_GRAPHIC_RENDITION;
	}
	
	@Override
	public void handle(DocumentProxy document) {
    	System.out.println(getControlSequenceString());
	}

}
