package org.jboss.tools.forge.aesh.view;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.aesh.view.AeshOutputStream.StreamListener;

public class AeshTextViewer extends TextViewer {
	
	private StreamListener stdOutListener = new StreamListener() {
		@Override
		public void charAppended(char c) {
			handleCharAppended(c);
		}
	};

    public AeshTextViewer(Composite parent) {
    	super(parent, SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    	initialize();
    }
    
    private void initialize() {
    	AeshOutputStream.STD_OUT.addStreamListener(stdOutListener);
    	getTextWidget().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
		    	AeshInputStream.INSTANCE.append("" + e.character);
			}
		});
    	AeshHelper.initAesh();
    }
    
    public void cleanup() {
    	AeshOutputStream.STD_OUT.removeStreamListener(stdOutListener);
    }
    
    private void handleCharAppended(final char c) {
    	Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
		    	getTextWidget().append("" + c);
			} 		
    	});
    }
    
}
    
