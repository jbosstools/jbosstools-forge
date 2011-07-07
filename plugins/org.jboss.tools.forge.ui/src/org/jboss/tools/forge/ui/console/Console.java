package org.jboss.tools.forge.ui.console;

import java.io.IOException;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;
import org.jboss.tools.forge.core.io.ConsoleInputStream;
import org.jboss.tools.forge.core.io.ForgeInputReadJob;

public class Console extends TextConsole implements IDebugEventSetListener  {

    private ConsolePartitioner partitioner;
    private ConsoleInputStream inputStream;
    private IProcess process = null;
    private IStreamListener outputStreamListener;

    
    public Console(IProcess process) {
        super("Forge Console", null, null, true);
        this.process = process;
        initInputStream();
        initPartitioner();
        initCommandRecorder();
        initOutputStream();
        initInputReadJob();
    }
    
    private void initCommandRecorder() {
    	getDocument().addDocumentListener(new CommandRecorder());
    }
    
    private void initInputStream() {
    	inputStream = new ConsoleInputStream();
    }
    
    private void initOutputStream() {
    	outputStreamListener = new IStreamListener() {			
			@Override
			public void streamAppended(String text, IStreamMonitor monitor) {
				appendString(text);
			}
		};
		IStreamMonitor streamMonitor = getOutputStreamMonitor();
		synchronized(streamMonitor) {
			streamMonitor.addListener(outputStreamListener);
		}
    }
    
    private IStreamMonitor getOutputStreamMonitor() {
    	IStreamMonitor streamMonitor = null;
    	IStreamsProxy streamsProxy = process.getStreamsProxy();
    	if (streamsProxy != null) {
    		streamMonitor = streamsProxy.getOutputStreamMonitor();
    	}
    	return streamMonitor;
    }
    
    private void initInputReadJob() {
    	ForgeInputReadJob inputReadJob = new ForgeInputReadJob(process.getStreamsProxy(), inputStream);
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
    
    public ConsoleInputStream getInputStream() {
        return inputStream;
    }

    protected IConsoleDocumentPartitioner getPartitioner() {
        return partitioner;
    }

    public void dispose() {
        super.dispose();
        partitioner.disconnect();
        closeStreams();
        disposeStreams();
        DebugPlugin.getDefault().removeDebugEventListener(this);
    }

	private synchronized void closeStreams() {
		try {
			inputStream.close();
		} catch (IOException e) {}
	}

    private synchronized void disposeStreams() {
    	IStreamMonitor streamMonitor = getOutputStreamMonitor();
    	if (streamMonitor != null) {
    		synchronized(streamMonitor) {
    			if (outputStreamListener != null) {
    				streamMonitor.removeListener(outputStreamListener);
    			}
    		}
    	}
    	outputStreamListener = null;
        inputStream = null;
    }

    public void handleDebugEvents(DebugEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            DebugEvent event = events[i];
            if (event.getSource().equals(process)) {
                if (event.getKind() == DebugEvent.TERMINATE) {
                    closeStreams();
                    DebugPlugin.getDefault().removeDebugEventListener(this);
                }
            }
        }
    }

    protected void init() {
        super.init();
        if (process.isTerminated()) {
            closeStreams();
        } else {
            DebugPlugin.getDefault().addDebugEventListener(this);
        }
    }
    
    private int lastLineLength = 0;
    private int lastLinePosition = 0;
    private StringBuffer escapeSequence = new StringBuffer();
    private boolean escapeSequenceStarted = false;
    
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
							if (type == Character.LOWERCASE_LETTER || type == Character.UPPERCASE_LETTER) {
								if (c == 'G') {
									int columnNumber = Integer.valueOf(escapeSequence.toString());
									escapeSequence.setLength(0);
									lastLineLength = columnNumber - 1;
									escapeSequenceStarted = false;
								} else if (c == 'K') {
									int doclength = getDocument().getLength();
									int currentPosition = lastLinePosition + lastLineLength;									
									getDocument().replace(currentPosition, doclength - currentPosition, "");
									escapeSequenceStarted = false;
//								} else if (c == 'm') {
//									
								} 
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

}
