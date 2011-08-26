package org.jboss.tools.forge.ui.dialog;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class ForgeCommandListDialog extends PopupDialog {
	
	private ForgeRuntime runtime = null;
	
	private SortedMap<String, SortedSet<String>> allCandidates = null;
	
	private Tree tree;
	private Text filterText;
	
//	private TreeItem selectedItem;
	
	private String selectedPlugin = "";
	private String selectedCommand = "";

	public ForgeCommandListDialog(IWorkbenchWindow window, ForgeRuntime runtime) {
		super(window.getShell(), 
				SWT.RESIZE, 
				true, 
				true, // persist size
				false, // but not location
				true, 
				true, 
				"Select the command you want Forge to execute",
				"Start typing to filter the list");
		this.runtime = runtime;
		this.allCandidates = getAllCandidates();
	}
	
	private SortedMap<String, SortedSet<String>> getAllCandidates() {
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
	
	private void refreshTree() {
		String filter = filterText.getText();
		if ("".equals(filter)) {
			createTree(allCandidates, false);
		} else {
			SortedMap<String, SortedSet<String>> candidates = new TreeMap<String, SortedSet<String>>();
			for (Entry<String, SortedSet<String>> entry : allCandidates.entrySet()) {
				SortedSet<String> set = new TreeSet<String>();
				for (String candidate : entry.getValue()) {
					if (candidate.indexOf(filter) != -1) {
						set.add(candidate);
					}
				}
				if ((entry.getKey().indexOf(filter) != -1 || !set.isEmpty())) {
					candidates.put(entry.getKey(), set);
				}
			}
			createTree(candidates, true);
		}
	}
	
	private void createTree(SortedMap<String, SortedSet<String>> candidates, boolean expand) {
		tree.removeAll();
		for (String plugin : candidates.keySet()) {
			TreeItem pluginItem = new TreeItem(tree, SWT.NONE);
			pluginItem.setText(plugin);
			SortedSet<String> commands = candidates.get(plugin);
			for (String command : commands) {
				TreeItem commandItem = new TreeItem(pluginItem, SWT.NONE);
				commandItem.setText(command);
			}
			pluginItem.setExpanded(expand);
		}
	}

	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite)super.createDialogArea(parent);
		result.setLayout(new FillLayout());
		tree = new Tree(result, SWT.SINGLE | SWT.V_SCROLL);
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item instanceof TreeItem) {
					TreeItem selectedItem = (TreeItem)e.item;
					if (selectedItem.getParentItem() != null) {
						selectedPlugin = selectedItem.getParentItem().getText();
						selectedCommand = selectedItem.getText();
						if (selectedPlugin.equals(selectedCommand)) {
							selectedCommand = "";
						}
					} else {
						selectedPlugin = selectedItem.getText();
						selectedCommand = "";
					}
				}
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				insertCommand();
			}
		});		
		refreshTree();
		return result;
	}
	
	private void insertCommand() {
		String str = selectedPlugin + " " + selectedCommand;
		runtime.sendInput(str.trim());
		close();
	}
	
	protected Point getDefaultSize() {
		GC gc = new GC(tree);
		FontMetrics fontMetrics = gc.getFontMetrics();
		gc.dispose();
		int x = Dialog.convertHorizontalDLUsToPixels(fontMetrics, 300);
//		if (x < 350)
//			x = 350;
		int y = Dialog.convertVerticalDLUsToPixels(fontMetrics, 270);
//		if (y < 420)
//			y = 420;
		return new Point(x, y);
	}

	protected Point getDefaultLocation(Point initialSize) {
		Point size = new Point(400, 400);
		Rectangle parentBounds = getParentShell().getBounds();
		int x = parentBounds.x + parentBounds.width / 2 - size.x / 2;
		int y = parentBounds.y + parentBounds.height / 2 - size.y / 2;
		return new Point(x, y);
	}

	protected Control createTitleControl(Composite parent) {
		filterText = new Text(parent, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.applyTo(filterText);		
		filterText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				refreshTree();
			}
		});
		return filterText;
	}

	protected Control getFocusControl() {
		return filterText;
	}

}
