package org.jboss.tools.forge.console;

import java.io.IOException;

import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.jboss.tools.forge.ForgePlugin;

class StreamListener implements IStreamListener {

    private ConsoleOutputStream stream;
    private StringBuffer buffer = new StringBuffer();
    private boolean creatingProject = false;

    StreamListener(ConsoleOutputStream stream) {
        this.stream = stream;
    }
    
    public void streamAppended(String text, IStreamMonitor streamMonitor) {
        try {
        	buffer.append(text);
        	if (buffer.indexOf("new-project") != -1) {
        		creatingProject = true;
        	}
            stream.write(text);
            if (creatingProject 
            		&& (text.indexOf('\n') != -1) 
            		&& (buffer.indexOf("Created project [") != -1) 
            		&& (buffer.indexOf("] in new working directory [") != -1)) {
            	postProcessCreatedProject(buffer.toString());
            	creatingProject = false;
            	buffer = new StringBuffer();
            }
        } catch (IOException e) {
            ForgePlugin.log(e);
        }
    }
    
    private void postProcessCreatedProject(String command) {
    	System.out.println("processing created project: \n" + command);
    }

}

