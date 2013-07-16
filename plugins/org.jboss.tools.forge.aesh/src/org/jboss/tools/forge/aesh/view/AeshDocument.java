package org.jboss.tools.forge.aesh.view;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.aesh.view.AeshOutputStream.StreamListener;

public class AeshDocument extends Document {
	
	private StreamListener stdOutListener, stdErrListener;
	private AnsiCommandFilter ansiCommandFilter;
	private int cursorOffset = 0;
	private AeshConsole console;
	
	public AeshDocument() {
		stdOutListener = new StreamListener() {			
			@Override
			public void charAppended(char c) {
				handleCharAppended(c);
			}
		};
		ansiCommandFilter = new AnsiCommandFilter(stdOutListener) {			
			@Override
			public void ansiCommandAvailable(String command) {
				handleAnsiCommand(command);
			}
		};
		stdErrListener = new StreamListener() {		
			@Override
			public void charAppended(char c) {
				handleCharAppended(c);
			}
		};
	}
	
	private void handleAnsiCommand(String command) {
		System.out.println("handleAnsiCommand(" + command + ")");
	}
	
	private void handleCharAppended(final char c) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				try {
					if (c == '\r') return;
					replace(cursorOffset, getLength() - cursorOffset, new String(new char[] { c }));
					cursorOffset++;
				} catch (BadLocationException e) {
		        	e.printStackTrace();							
				}
			}			
		});
	}

	public void connect(AeshConsole aeshConsole) {
		if (console == null) {
			console = aeshConsole;
			console.addStdOutListener(ansiCommandFilter);
			console.addStdErrListener(stdErrListener);
		}
	}
	
	public void disconnect() {
		if (console != null) {
			console.removeStdOutListener(ansiCommandFilter);
			console.removeStdErrListener(stdErrListener);
			console = null;
		}
	}

}
