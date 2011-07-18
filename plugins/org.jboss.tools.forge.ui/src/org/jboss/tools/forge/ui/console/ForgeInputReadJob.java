package org.jboss.tools.forge.ui.console;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jboss.tools.forge.core.ForgeCorePlugin;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class ForgeInputReadJob extends Job {

    private ForgeRuntime runtime;
    private InputStream input;

    public ForgeInputReadJob(ForgeRuntime runtime, InputStream input) {
        super("Forge Input Read Job");
        this.input = input;
        this.runtime = runtime;
    }

    protected IStatus run(IProgressMonitor monitor) {
        try {
            int read;
            while (input != null && (read = input.read()) != -1) {
            	runtime.sendInput(new String(new char[] { (char)read }));
            }
        } catch (IOException e) {
            ForgeCorePlugin.log(e);
        }
        return Status.OK_STATUS;
    }
    
    
    
}
