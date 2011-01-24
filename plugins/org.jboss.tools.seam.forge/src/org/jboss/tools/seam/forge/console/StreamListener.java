package org.jboss.tools.seam.forge.console;

import java.io.IOException;

import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IFlushableStreamMonitor;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.jboss.tools.seam.forge.Activator;

class StreamListener implements IStreamListener {

    private IOConsoleOutputStream stream;
    private IStreamMonitor streamMonitor;
    private String streamId;
    private boolean flushed = false;
    private boolean listenerRemoved = false;
    private String encoding;

    StreamListener(String streamIdentifier, IStreamMonitor monitor, IOConsoleOutputStream stream, String encoding) {
        this.streamId = streamIdentifier;
        this.streamMonitor = monitor;
        this.stream = stream;
        this.encoding = encoding;
        streamMonitor.addListener(this);  
        streamAppended(null, monitor);
    }
    
    String getStreamId() {
    	return streamId;
    }
    
    IOConsoleOutputStream getStream() {
    	return stream;
    }

    public void streamAppended(String text, IStreamMonitor monitor) {
        if (flushed) {
            try {
                if (stream != null) {
                	if (encoding == null)
                		stream.write(text);
                	else 
                		stream.write(text.getBytes(encoding));
                }
            } catch (IOException e) {
                Activator.log(e);
            }
        } else {
            String contents = null;
            synchronized (streamMonitor) {
                flushed = true;
                contents = streamMonitor.getContents();
                if (streamMonitor instanceof IFlushableStreamMonitor) {
                    IFlushableStreamMonitor m = (IFlushableStreamMonitor) streamMonitor;
                    m.flushContents();
                    m.setBuffered(false);
                }
            }
            try {
                if (contents != null && contents.length() > 0) {
                    if (stream != null) {
                        stream.write(contents);
                    }
                }
            } catch (IOException e) {
                Activator.log(e);
            }
        }
    }

    void closeStream() {
        if (streamMonitor == null) {
            return;
        }
        synchronized (streamMonitor) {
            streamMonitor.removeListener(this);
            if (!flushed) {
                String contents = streamMonitor.getContents();
                streamAppended(contents, streamMonitor);
            }
            listenerRemoved = true;
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    void dispose() {
        if (!listenerRemoved) {
            closeStream();
        }
        stream = null;
        streamMonitor = null;
        streamId = null;
    }
}

