package org.jboss.tools.forge.ui.dialog;

import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class ForgeCommandListDialog extends PopupDialog {
	
	private ForgeRuntime runtime = null;

	public ForgeCommandListDialog(IWorkbenchWindow window, ForgeRuntime runtime) {
		super(window.getShell(), 
				SWT.RESIZE, 
				true, 
				true, // persist size
				false, // but not location
				true, 
				true, 
				"Select the command you want Forge to execute",
				null);
		this.runtime = runtime;
	}
	
	private SortedSet<String> getPluginCandidates() {
		SortedSet<String> result = new TreeSet<String>();
		String pluginCandidates = runtime.sendCommand("plugin-candidates-query");
		StringTokenizer tokenizer = new StringTokenizer(pluginCandidates);
		if (tokenizer.hasMoreTokens()) {
			String first = tokenizer.nextToken();
			if ("plugin-candidates-answer:".equals(first)) {
				while (tokenizer.hasMoreTokens()) {
					result.add(tokenizer.nextToken());
				}
			}
		}		
		return result;
	}

	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite)super.createDialogArea(parent);
		result.setLayout(new FillLayout());
		List list = new List(result, SWT.SINGLE);
		SortedSet<String> pluginCandidates = getPluginCandidates();
		for (String plugin : pluginCandidates) {
			list.add(plugin);
		}
		return result;
	}
	
}
