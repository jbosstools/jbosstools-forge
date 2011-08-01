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

public class ForgeCommandListDialog extends PopupDialog {
	
	private SortedSet<String> commandSet = new TreeSet<String>();

	public ForgeCommandListDialog(IWorkbenchWindow window, String commands) {
		super(window.getShell(), 
				SWT.RESIZE, 
				true, 
				true, // persist size
				false, // but not location
				true, 
				true, 
				"Select the command you want Forge to execute",
				null);
		StringTokenizer tokenizer = new StringTokenizer(commands);
		if (tokenizer.hasMoreTokens()) {
			String first = tokenizer.nextToken();
			if ("command-list-answer:".equals(first)) {
				while (tokenizer.hasMoreTokens()) {
					commandSet.add(tokenizer.nextToken());
				}
			}
		}		
	}

	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite)super.createDialogArea(parent);
		result.setLayout(new FillLayout());
		List list = new List(result, SWT.SINGLE);
		for (String command : commandSet) {
			list.add(command);
		}
		return result;
	}
	
}
