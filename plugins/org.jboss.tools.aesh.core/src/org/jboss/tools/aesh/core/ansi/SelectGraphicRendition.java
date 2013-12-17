package org.jboss.tools.aesh.core.ansi;

import org.jboss.tools.aesh.core.document.DocumentProxy;


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
    	System.out.println(arguments);
	}

}
