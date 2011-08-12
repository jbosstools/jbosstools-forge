package org.jboss.tools.forge.ui.dialog;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
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
	
	private SortedMap<String, SortedSet<String>> getCandidates() {
		SortedMap<String, SortedSet<String>> result = new TreeMap<String, SortedSet<String>>();
		String pluginCandidates = runtime.sendCommand("plugin-candidates-query");
		SortedSet<String> currentCommands = null;
		StringTokenizer tokenizer = new StringTokenizer(pluginCandidates);
		if (tokenizer.hasMoreTokens()) {
			String first = tokenizer.nextToken();
			if ("plugin-candidates-answer:".equals(first)) {
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (token.indexOf("p:") != -1) {
						currentCommands = new TreeSet<String>();
						result.put(token.substring(2), currentCommands);
					} else if (token.indexOf("c:") != -1) {
						currentCommands.add(token.substring(2));
					}
				}
			}
		}
		return result;
	}

	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite)super.createDialogArea(parent);
		result.setLayout(new FillLayout());
		Tree tree = new Tree(result, SWT.SINGLE | SWT.V_SCROLL);
//		List list = new List(result, SWT.SINGLE | SWT.V_SCROLL);
		SortedMap<String, SortedSet<String>> candidates = getCandidates();
		for (String plugin : candidates.keySet()) {
			TreeItem pluginItem = new TreeItem(tree, SWT.NONE);
			pluginItem.setText(plugin);
			SortedSet<String> commands = candidates.get(plugin);
			for (String command : commands) {
				TreeItem commandItem = new TreeItem(pluginItem, SWT.NONE);
				commandItem.setText(command);
			}
//			list.add(plugin);
		}
		return result;
	}
	
}
