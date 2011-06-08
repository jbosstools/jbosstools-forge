package org.jboss.tools.forge.console;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.jboss.tools.forge.ForgePlugin;

class InputReadJob extends Job {

    private IStreamsProxy streamsProxy;
    private ConsoleInputStream input;

    InputReadJob(IStreamsProxy streamsProxy, ConsoleInputStream input) {
        super("Forge Console Input Job");
        this.input = input;
        this.streamsProxy = streamsProxy;
    }

    protected IStatus run(IProgressMonitor monitor) {
        try {
        	StringBuffer buffer = new StringBuffer();
            int read;
            while (input != null && (read = input.read()) != -1) {
            	buffer.append((char)read);
            	streamsProxy.write(buffer.toString());
            	buffer.setLength(0);
            }
        } catch (IOException e) {
            ForgePlugin.log(e);
        }
        return Status.OK_STATUS;
    }
    
}
