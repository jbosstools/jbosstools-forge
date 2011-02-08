package org.jboss.tools.seam.forge.console;

import java.io.IOException;

import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.jboss.tools.seam.forge.Activator;

class StreamListener implements IStreamListener {

    private ConsoleOutputStream stream;

    StreamListener(ConsoleOutputStream stream) {
        this.stream = stream;
    }
    
    public void streamAppended(String text, IStreamMonitor streamMonitor) {
        try {
            stream.write(text);
        } catch (IOException e) {
            Activator.log(e);
        }
    }

}

