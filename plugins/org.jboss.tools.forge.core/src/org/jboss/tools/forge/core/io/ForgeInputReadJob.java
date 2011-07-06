package org.jboss.tools.forge.core.io;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.jboss.tools.forge.core.ForgeCorePlugin;

public class ForgeInputReadJob extends Job {

    private IStreamsProxy streamsProxy;
    private InputStream input;

    public ForgeInputReadJob(IStreamsProxy streamsProxy, InputStream input) {
        super("Forge Input Read Job");
        this.input = input;
        this.streamsProxy = streamsProxy;
    }

    protected IStatus run(IProgressMonitor monitor) {
        try {
            int read;
            while (input != null && (read = input.read()) != -1) {
            	streamsProxy.write(new String(new char[] { (char)read }));
            }
        } catch (IOException e) {
            ForgeCorePlugin.log(e);
        }
        return Status.OK_STATUS;
    }
    
}
