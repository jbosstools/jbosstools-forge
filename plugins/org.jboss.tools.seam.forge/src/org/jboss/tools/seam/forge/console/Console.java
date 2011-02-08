package org.jboss.tools.seam.forge.console;

import java.io.IOException;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;

public class Console extends TextConsole implements IDebugEventSetListener  {

    private ConsolePartitioner partitioner;
    private ConsoleInputStream inputStream;
    private ConsoleOutputStream outputStream;
    private IProcess process = null;
    private StreamListener outputStreamListener;

    
    public Console(IProcess process) {
        super("Forge Console", null, null, true);
        this.process = process;
        initInputStream();
        initPartitioner();
        initOutputStream();
        initInputReadJob();
    }
    
    private void initInputStream() {
    	inputStream = new ConsoleInputStream();
    }
    
    private void initOutputStream() {
    	outputStream = new ConsoleOutputStream(partitioner);
    	IStreamMonitor streamMonitor = getOutputStreamMonitor();
    	if (streamMonitor != null) {
    		synchronized(streamMonitor) {
    			outputStreamListener = new StreamListener(outputStream);
    			streamMonitor.addListener(outputStreamListener);
    		}
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
    	InputReadJob inputReadJob = new InputReadJob(process.getStreamsProxy(), inputStream);
    	inputReadJob.setSystem(true);
    	inputReadJob.schedule();
    }
    
    private void initPartitioner() {
    	partitioner = new ConsolePartitioner(inputStream, this);
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
			outputStream.close();
			partitioner.streamsClosed();
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
        outputStream = null;
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

}
