package org.jboss.tools.aesh.core.ansi;


public class SelectGraphicRendition extends ControlSequence {

	public SelectGraphicRendition(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.SELECT_GRAPHIC_RENDITION;
	}

}
