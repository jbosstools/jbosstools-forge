package org.jboss.tools.seam.forge.console;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.jboss.tools.seam.forge.Activator;

class InputReadJob extends Job {

    private IStreamsProxy streamsProxy;
    private IOConsoleInputStream input;

    InputReadJob(IStreamsProxy streamsProxy, IOConsoleInputStream input) {
        super("Forge Console Input Job");
        this.input = input;
        this.streamsProxy = streamsProxy;
    }

    protected IStatus run(IProgressMonitor monitor) {
        try {
            byte[] b = new byte[1024];
            int read = 0;
            while (input != null && read >= 0) {
                read = input.read(b);
                if (read > 0) {
                    String s = new String(b, 0, read);
                    streamsProxy.write(s);
                }
            }
        } catch (IOException e) {
            Activator.log(e);
        }
        return Status.OK_STATUS;
    }
    
}
