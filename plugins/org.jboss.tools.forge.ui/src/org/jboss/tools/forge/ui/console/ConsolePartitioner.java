package org.jboss.tools.forge.ui.console;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;

public class ConsolePartitioner implements IConsoleDocumentPartitioner {

	@Override
	public void connect(IDocument document) {
//		System.out.println("connect");
		document.setDocumentPartitioner(this);
	}

	@Override
	public void disconnect() {
//		System.out.println("disconnect");
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
//		System.out.println("documentAboutToBeChanged");
	}

	@Override
	public boolean documentChanged(DocumentEvent event) {
//		System.out.println("documentChanged");
		return false;
	}

	@Override
	public String[] getLegalContentTypes() {
//		System.out.println("getLegalContentTypes");
		return null;
	}

	@Override
	public String getContentType(int offset) {
//		System.out.println("getContentType");
		return null;
	}

	@Override
	public ITypedRegion[] computePartitioning(int offset, int length) {
//		System.out.println("computePartitioning");
		return null;
	}

	@Override
	public ITypedRegion getPartition(int offset) {
//		System.out.println("getPartition");
		return null;
	}

	@Override
	public boolean isReadOnly(int offset) {
//		System.out.println("isReadOnly");
		return false;
	}

	@Override
	public StyleRange[] getStyleRanges(int offset, int length) {
//		System.out.println("getStyleRanges : [offset, " + offset + "] [length, " + length + "]");
		return null;
	}

}
