package org.jboss.tools.forge.ui.document;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;

public class ForgeDocumentPartitioner implements IDocumentPartitioner {

	@Override
	public void connect(IDocument document) {
		// TODO Auto-generated method stub

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

}
