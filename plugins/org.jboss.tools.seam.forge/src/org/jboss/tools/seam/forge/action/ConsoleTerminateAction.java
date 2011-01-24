package org.jboss.tools.seam.forge.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.commands.ITerminateHandler;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.IDebugHelpContextIds;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.internal.ui.commands.actions.DebugCommandService;
import org.eclipse.debug.internal.ui.views.console.ConsoleMessages;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IUpdate;
import org.jboss.tools.seam.forge.view.Console;

/**
 * ConsoleTerminateAction
 */
public class ConsoleTerminateAction extends Action implements IUpdate {

	private Console fConsole;
	private IWorkbenchWindow fWindow;

	/**
	 * Creates a terminate action for the console 
	 */
	public ConsoleTerminateAction(IWorkbenchWindow window, Console console) {
		super(ConsoleMessages.ConsoleTerminateAction_0); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.CONSOLE_TERMINATE_ACTION);
		fConsole = console;
		fWindow = window;
		setToolTipText(ConsoleMessages.ConsoleTerminateAction_1); 
		setImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_LCL_TERMINATE));
		setDisabledImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_DLCL_TERMINATE));
		setHoverImageDescriptor(DebugPluginImages.getImageDescriptor(IInternalDebugUIConstants.IMG_LCL_TERMINATE));
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDebugHelpContextIds.CONSOLE_TERMINATE_ACTION);
		update();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		IProcess process = fConsole.getProcess(); 
		setEnabled(process.canTerminate());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		IProcess process = fConsole.getProcess();
		List targets = collectTargets(process);
		targets.add(process);
        DebugCommandService service = DebugCommandService.getService(fWindow);
        service.executeCommand(ITerminateHandler.class, targets.toArray(), null);
	}
	
	/**
	 * Collects targets associated with a process.
	 * 
	 * @param process
	 * @return associated targets
	 */
	private List collectTargets(IProcess process) {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunch[] launches = launchManager.getLaunches();
        List targets = new ArrayList();
        for (int i = 0; i < launches.length; i++) {
            ILaunch launch = launches[i];
            IProcess[] processes = launch.getProcesses();
            for (int j = 0; j < processes.length; j++) {
                IProcess process2 = processes[j];
                if (process2.equals(process)) {
                    IDebugTarget[] debugTargets = launch.getDebugTargets();
                    for (int k = 0; k < debugTargets.length; k++) {
                        targets.add(debugTargets[k]);
                    }
                    return targets; // all possible targets have been terminated for the launch.
                }
            }
        }
        return targets;
    }

    public void dispose() {
	    fConsole = null;
	}

}
