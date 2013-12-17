package org.jboss.tools.aesh.ui.document;

import org.eclipse.jface.text.BadLocationException;
import org.jboss.tools.aesh.core.document.DocumentProxy;
import org.jboss.tools.aesh.ui.AeshUIPlugin;

public class AeshDocumentProxy implements DocumentProxy {
	
	private AeshDocument document;
	
	public AeshDocumentProxy(AeshDocument document) {
		this.document = document;
	}

	@Override
	public int getCursorOffset() {
		return document.getCursorOffset();
	}
	
	@Override
	public int getLineOfOffset(int offset) {
		int result = -1;
		try {
			result = document.getLineOfOffset(offset);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}
	
	@Override
	public int getLineOffset(int line) {
		int result = -1;
		try {
			result = document.getLineOffset(line);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}
	
	@Override 
	public int getLineLength(int line) {
		int result = -1;
		try {
			result = document.getLineLength(line);
		} catch (BadLocationException e) {
			AeshUIPlugin.log(e);
		}
		return result;
	}

	@Override
	public void moveCursorTo(int offset) {
		document.moveCursorTo(offset);
	}
	
	@Override
	public void reset() {
		document.reset();
	}

	@Override
	public int getLength() {
		return document.getLength();
	}

	@Override
	public void replace(int pos, int length, String text) {
    	try {
        	document.replace(pos, length, text);
        } catch (BadLocationException e) {
        	AeshUIPlugin.log(e);
        }
	}

	@Override
	public void restoreCursor() {
		document.restoreCursor();
	}

	@Override
	public void saveCursor() {
		document.saveCursor();
	}
	

}
