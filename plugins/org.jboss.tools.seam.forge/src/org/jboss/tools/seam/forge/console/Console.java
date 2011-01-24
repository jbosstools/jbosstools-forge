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
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class Console extends IOConsole implements IDebugEventSetListener {
	
	private static final RGB INPUT_STREAM_RGB = new RGB(25, 225, 25);
	private static final RGB ERROR_STREAM_RGB = new RGB(225, 25, 25);
	private static final RGB OUTPUT_STREAM_RGB = new RGB(0, 0, 0);
	private static final RGB CONSOLE_BACKGROUND_RGB = new RGB(255, 255, 255);
	
    private IProcess process = null;
    private IOConsoleInputStream input;
    private boolean streamsClosed = false;
    private List<StreamListener> streamListeners = new ArrayList<StreamListener>();

    public Console(IProcess process) {
        super("Forge Console", null, null, null, true);
        this.process = process;
        input = getInputStream();
        input.setColor(ColorManager.getInstance().getColor(INPUT_STREAM_RGB));
        connect();
    }

    public IOConsoleOutputStream getStream(String streamIdentifier) {
        for (StreamListener listener : streamListeners) {
            if (listener.getStreamId().equals(streamIdentifier)) {
                return listener.getStream();
            }
        }
        return null;
    }

    public void dispose() {
        super.dispose();
        closeStreams();
        disposeStreams();
        DebugPlugin.getDefault().removeDebugEventListener(this);
    }

    private synchronized void closeStreams() {
        if (streamsClosed) {
            return;
        }
        for (StreamListener listener : streamListeners) {
            listener.closeStream();
        }
        try {
            input.close();
        } catch (IOException e) {
        }
        streamsClosed  = true;
    }

    private synchronized void disposeStreams() {
        for (StreamListener listener : streamListeners) {
            listener.dispose();
        }
        input = null;
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
        InputReadJob inputReadJob = new InputReadJob(streamsProxy, input);
        inputReadJob.setSystem(true);
        inputReadJob.schedule();
    }
    
    private void connect(IStreamMonitor streamMonitor, String streamIdentifier) {
        IOConsoleOutputStream stream = newOutputStream();
        Color color = getColor(streamIdentifier);
        stream.setColor(color);
        synchronized (streamMonitor) {
            StreamListener listener = new StreamListener(streamIdentifier, streamMonitor, stream, getEncoding());
            streamListeners.add(listener);
        }
    }

	public Color getColor(String streamIdentifer) {
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

}
