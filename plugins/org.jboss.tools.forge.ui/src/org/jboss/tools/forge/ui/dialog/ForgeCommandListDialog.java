package org.jboss.tools.forge.ui.dialog;

import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class ForgeCommandListDialog extends PopupDialog {
	
	private ForgeRuntime runtime = null;
	
	private Tree tree;
	private Text filterText;
	
	private TreeItem selectedItem;

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
		tree = new Tree(result, SWT.SINGLE | SWT.V_SCROLL);
		SortedMap<String, SortedSet<String>> candidates = getCandidates();
		for (String plugin : candidates.keySet()) {
			TreeItem pluginItem = new TreeItem(tree, SWT.NONE);
			pluginItem.setText(plugin);
			SortedSet<String> commands = candidates.get(plugin);
			for (String command : commands) {
				TreeItem commandItem = new TreeItem(pluginItem, SWT.NONE);
				commandItem.setText(command);
			}
			pluginItem.addListener(SWT.MouseDoubleClick, new Listener() {
				
				@Override
				public void handleEvent(Event event) {
					System.out.println("plugin item doubleclicked :" + event.widget);
				}
			});
		}
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item instanceof TreeItem) {
					selectedItem = (TreeItem)e.item;
				}
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				insertCommand();
			}
		});		
		return result;
	}
	
	private void insertCommand() {
		String str = "";
		if (selectedItem != null && selectedItem.getText() != null) {
			str = selectedItem.getText();
		}
		TreeItem parentItem = selectedItem.getParentItem();
		if (parentItem != null) {
			String parentString = parentItem.getText();
			if (parentString != null && !parentString.equals(str)) {
				str = parentString + " " + str;
			}
		}
		runtime.sendInput(str);
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

//		filterText.addKeyListener(getKeyAdapter());
//		filterText.addKeyListener(new KeyListener() {
//			public void keyPressed(KeyEvent e) {
//				switch (e.keyCode) {
//				case SWT.CR:
//				case SWT.KEYPAD_CR:
//					handleSelection();
//					break;
//				case SWT.ARROW_DOWN:
//					int index = table.getSelectionIndex();
//					if (index != -1 && table.getItemCount() > index + 1) {
//						table.setSelection(index + 1);
//					}
//					table.setFocus();
//					break;
//				case SWT.ARROW_UP:
//					index = table.getSelectionIndex();
//					if (index != -1 && index >= 1) {
//						table.setSelection(index - 1);
//						table.setFocus();
//					}
//					break;
//				case SWT.ESC:
//					close();
//					break;
//				}
//			}
//
//			public void keyReleased(KeyEvent e) {
//				// do nothing
//			}
//		});
//		filterText.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				String text = ((Text) e.widget).getText().toLowerCase();
//				refresh(text);
//			}
//		});

		return filterText;
	}

}
