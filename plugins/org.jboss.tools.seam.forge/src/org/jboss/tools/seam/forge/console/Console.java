package org.jboss.tools.seam.forge.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.WorkbenchEncoding;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;

public class Console extends TextConsole implements IDebugEventSetListener  {

	private static final RGB INPUT_STREAM_RGB = new RGB(25, 225, 25);
	private static final RGB ERROR_STREAM_RGB = new RGB(225, 25, 25);
	private static final RGB OUTPUT_STREAM_RGB = new RGB(0, 0, 0);
	private static final RGB CONSOLE_BACKGROUND_RGB = new RGB(255, 255, 255);
	
    private ConsolePartitioner partitioner;
    private ConsoleInputStream inputStream;
    private List<Object> openStreams;
    private String encoding = WorkbenchEncoding.getWorkbenchDefaultEncoding();
    private IProcess process = null;
    private List<StreamListener> streamListeners = new ArrayList<StreamListener>();

    
    public Console(IProcess process) {
        super("Forge Console", null, null, true);
        this.process = process;
        openStreams = new ArrayList<Object>();
        inputStream = new ConsoleInputStream(this);
        synchronized (openStreams) {
        	openStreams.add(inputStream);	
		}        
        partitioner = new ConsolePartitioner(inputStream, this);
        partitioner.connect(getDocument());
        inputStream.setColor(ColorManager.getInstance().getColor(INPUT_STREAM_RGB));
        connect();
    }

    public void connect() {
    	IStreamsProxy streamsProxy = process.getStreamsProxy();
        IStreamMonitor streamMonitor = streamsProxy.getErrorStreamMonitor();
        if (streamMonitor != null) {
            connect(streamMonitor, IDebugUIConstants.ID_STANDARD_ERROR_STREAM);
        }
        streamMonitor = streamsProxy.getOutputStreamMonitor();
        if (streamMonitor != null) {
            connect(streamMonitor, IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM);
        }
        InputReadJob inputReadJob = new InputReadJob(streamsProxy, inputStream);
        inputReadJob.setSystem(true);
        inputReadJob.schedule();
    }
    
    private void connect(IStreamMonitor streamMonitor, String streamIdentifier) {
        ConsoleOutputStream stream = newOutputStream();
        Color color = getColor(streamIdentifier);
        stream.setColor(color);
        synchronized (streamMonitor) {
            StreamListener listener = new StreamListener(streamIdentifier, streamMonitor, stream, getEncoding());
            streamListeners.add(listener);
        }
    }

	private Color getColor(String streamIdentifer) {
		if (IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM.equals(streamIdentifer)) {
			return ColorManager.getInstance().getColor(OUTPUT_STREAM_RGB);
		}
		if (IDebugUIConstants.ID_STANDARD_ERROR_STREAM.equals(streamIdentifer)) {
			return ColorManager.getInstance().getColor(ERROR_STREAM_RGB);
		}		
		if (IDebugUIConstants.ID_STANDARD_INPUT_STREAM.equals(streamIdentifer)) {
			return ColorManager.getInstance().getColor(INPUT_STREAM_RGB);
		}		
		return null;
	}

    public IPageBookViewPage createPage(IConsoleView view) {
        throw new UnsupportedOperationException();
    }
    
    public ConsoleOutputStream newOutputStream() {
        ConsoleOutputStream outputStream = new ConsoleOutputStream(this);
        outputStream.setEncoding(encoding);
        synchronized(openStreams) {
            openStreams.add(outputStream);
        }
        return outputStream;
    }
    
    public ConsoleInputStream getInputStream() {
        return inputStream;
    }

    protected IConsoleDocumentPartitioner getPartitioner() {
        return partitioner;
    }

    private void checkFinished() {
        if (openStreams.isEmpty()) {
            partitioner.streamsClosed();
        }
    }
    
    void streamClosed(ConsoleOutputStream stream) {
    	synchronized (openStreams) {
            openStreams.remove(stream);
            checkFinished();
		}
    }
    
    void streamClosed(ConsoleInputStream stream) {
    	synchronized (openStreams) {
            openStreams.remove(stream);
            checkFinished();
		}
    }
    
    public void dispose() {
        super.dispose();
        partitioner.disconnect();
        closeStreams();
        disposeStreams();
        DebugPlugin.getDefault().removeDebugEventListener(this);
    }

	private synchronized void closeStreams() {
		Object[] allStreams= openStreams.toArray();
        for (int i = 0; i < allStreams.length; i++) {
        	Object stream = allStreams[i];
        	if (stream instanceof ConsoleInputStream) {
        		ConsoleInputStream is = (ConsoleInputStream) stream;
        		try {
        			is.close();
        		} catch (IOException e) {
        		}
        	} else if (stream instanceof ConsoleOutputStream) {
        		ConsoleOutputStream os = (ConsoleOutputStream) stream;
        		try {
        			os.close();
        		} catch (IOException e) {
        		}					
        	}
        }
//        inputStream = null;
	}

    private synchronized void disposeStreams() {
        for (StreamListener listener : streamListeners) {
            listener.dispose();
        }
        inputStream = null;
    }

//    private synchronized void closeStreams() {
//        if (streamsClosed) {
//            return;
//        }
//        for (StreamListener listener : streamListeners) {
//            listener.closeStream();
//        }
//        try {
//            input.close();
//        } catch (IOException e) {
//        }
//        streamsClosed  = true;
//    }

    public String getEncoding() {
		return encoding;
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
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                setFont(JFaceResources.getFont(IDebugUIConstants.PREF_CONSOLE_FONT));
                setBackground(ColorManager.getInstance().getColor(CONSOLE_BACKGROUND_RGB));
            }
        });
    }

    public ConsoleOutputStream getStream(String streamIdentifier) {
        for (StreamListener listener : streamListeners) {
            if (listener.getStreamId().equals(streamIdentifier)) {
                return listener.getStream();
            }
        }
        return null;
    }

}
