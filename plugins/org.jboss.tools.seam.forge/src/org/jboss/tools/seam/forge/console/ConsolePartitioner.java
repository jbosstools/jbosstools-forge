package org.jboss.tools.seam.forge.console;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;

public class ConsolePartitioner implements IConsoleDocumentPartitioner {

	@Override
	public void connect(IDocument document) {
		document.setDocumentPartitioner(this);
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean documentChanged(DocumentEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getLegalContentTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentType(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypedRegion[] computePartitioning(int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypedRegion getPartition(int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly(int offset) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StyleRange[] getStyleRanges(int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

}
