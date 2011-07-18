package org.jboss.tools.forge.ui.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;
import org.jboss.tools.forge.core.io.ForgeHiddenOutputFilter;
import org.jboss.tools.forge.core.io.ForgeInputStream;
import org.jboss.tools.forge.core.io.ForgeOutputListener;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

public class Console extends TextConsole {

    private ConsolePartitioner partitioner;
    private ForgeInputStream inputStream;
    private RuntimeStopListener stopListener;
    private ForgeOutputListener outputListener;
    private ForgeRuntime runtime;

    
    public Console(ForgeRuntime runtime) {
        super("Forge Console", null, null, true);
        this.runtime = runtime;
        initialize();
    }
    
    protected void init() {
    	super.init();
        initInputStream();
        initPartitioner();
        initCommandRecorder();
        initOutputListener();
        initStopListener();
        initInputReadJob();
    }
    
    private void initCommandRecorder() {
    	getDocument().addDocumentListener(new CommandRecorder());
    }
    
    private void initInputStream() {
    	inputStream = new ForgeInputStream();
    }
    
    private void initStopListener() {
    	stopListener = new RuntimeStopListener();
    	runtime.addPropertyChangeListener(stopListener);
    }
    
    private void initOutputListener() {
    	ForgeOutputListener target = new ForgeOutputListener() {			
			@Override
			public void outputAvailable(String output) {
				System.out.println("ForgeOutputListener->outputAvailable : " + output);
				appendString(output);
			}
		};
		outputListener = new ForgeHiddenOutputFilter(target);
		runtime.addOutputListener(outputListener);
    }
    
    private void initInputReadJob() {
    	ForgeInputReadJob inputReadJob = new ForgeInputReadJob(runtime, inputStream);
    	inputReadJob.setSystem(true);
    	inputReadJob.schedule();
    }
    
    private void initPartitioner() {
    	partitioner = new ConsolePartitioner();
    	partitioner.connect(getDocument());
    }

    public IPageBookViewPage createPage(IConsoleView view) {
        throw new UnsupportedOperationException();
    }
    
    protected IConsoleDocumentPartitioner getPartitioner() {
        return partitioner;
    }

    public void dispose() {
    	if (!ForgeRuntime.STATE_NOT_RUNNING.equals(runtime.getState())) {
    		runtime.stop(null);
    	}
        super.dispose();
    }
    
    private void handleRuntimeStopped() {
    	try {
	    	runtime.removePropertyChangeListener(stopListener);
	    	stopListener = null;
	    	runtime.removeOutputListener(outputListener);
	    	outputListener = null;
	    	partitioner.disconnect();
	    	inputStream.close();
	    	inputStream = null;
    	} catch (IOException e) {
    		ForgeUIPlugin.log(e);
    	}
    }


    private int lastLineLength = 0;
    private int lastLinePosition = 0;
    private StringBuffer escapeSequence = new StringBuffer();
    private boolean escapeSequenceStarted = false;
    private boolean metaDataSequenceStarted = false; 
    
    public void appendString(final String str) {
    	Display.getDefault().asyncExec(new Runnable() {				
			@Override
			public void run() {
				try {
					for (int i = 0; i < str.length(); i++) {
						char c = str.charAt(i);
						if (c == '\r') continue; //ignore
						if (c == '[' && escapeSequenceStarted) continue; 
						if (c == 27) {
							escapeSequenceStarted = true;
							continue;
						}
						if (escapeSequenceStarted) {
							int type = Character.getType(c);
//							if (!metaDataSequenceStarted && (type == Character.LOWERCASE_LETTER || type == Character.UPPERCASE_LETTER)) {
							if (type == Character.LOWERCASE_LETTER || type == Character.UPPERCASE_LETTER) {
								if (c == 'G') {
									int columnNumber = Integer.valueOf(escapeSequence.toString());
									lastLineLength = columnNumber - 1;
									escapeSequence.setLength(0);
									escapeSequenceStarted = false;
								} else if (c == 'K') {
									int doclength = getDocument().getLength();
									int currentPosition = lastLinePosition + lastLineLength;									
									getDocument().replace(currentPosition, doclength - currentPosition, "");
									escapeSequence.setLength(0);
									escapeSequenceStarted = false;
//								} else if (c == 'm') {
//									
								}
//							} else if (c == '%') {
//								if (metaDataSequenceStarted) {
//									metaDataSequenceStarted = false;
//									escapeSequenceStarted = false;
//									handleMetaData(escapeSequence.toString());
//									escapeSequence.setLength(0);
//								} else {
//									metaDataSequenceStarted = true;
//								}
							} else {
								escapeSequence.append(c);
							}
							continue;
						}
						if (str.charAt(i) == '\b') {
							getDocument().replace(getDocument().getLength() - 1, 1, "");
							lastLineLength--;
						} else {
							getDocument().replace(getDocument().getLength(), 0, str.substring(i, i + 1));
							lastLineLength++;
						}
						if (c == '\n') {
							lastLineLength = 0;
							lastLinePosition = getDocument().getLength();
						}
					}
				} catch (BadLocationException e) {}
			}
		});
    }
    
    private void handleMetaData(String metaData) {
    	System.out.println("meta data detected: " + metaData);
    }
    
    private class RuntimeStopListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ForgeRuntime.PROPERTY_STATE.equals(evt.getPropertyName()) && 
					ForgeRuntime.STATE_NOT_RUNNING.equals(evt.getNewValue())) {
				handleRuntimeStopped();
			}
		}   	
    }
    
}
