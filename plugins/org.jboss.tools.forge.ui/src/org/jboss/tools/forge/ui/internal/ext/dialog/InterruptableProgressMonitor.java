package org.jboss.tools.forge.ui.internal.ext.dialog;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author <a href="lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class InterruptableProgressMonitor implements IProgressMonitor {

	private IProgressMonitor wrapped;
	private Thread thread;
	private boolean previouslyCancelled;

	public InterruptableProgressMonitor(IProgressMonitor wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void beginTask(String name, int totalWork) {
		wrapped.beginTask(name, totalWork);
	}

	@Override
	public void done() {
		wrapped.done();
	}

	@Override
	public void internalWorked(double work) {
		wrapped.internalWorked(work);
	}

	@Override
	public boolean isCanceled() {
		return wrapped.isCanceled();
	}

	public boolean isPreviouslyCancelled() {
		return previouslyCancelled;
	}

	@Override
	public void setCanceled(boolean value) {
		if (value == true) {
			if (previouslyCancelled && thread != null) {
				thread.interrupt();
			}
			previouslyCancelled = true;
		}
		wrapped.setCanceled(value);
	}

	@Override
	public void setTaskName(String name) {
		wrapped.setTaskName(name);
	}

	@Override
	public void subTask(String name) {
		wrapped.subTask(name);
	}

	@Override
	public void worked(int work) {
		wrapped.worked(work);
	}

	public void setRunnableThread(Thread thread) {
		this.thread = thread;
	}

}
