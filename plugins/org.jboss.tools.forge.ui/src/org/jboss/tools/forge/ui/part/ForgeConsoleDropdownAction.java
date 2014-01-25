package org.jboss.tools.forge.ui.part;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.jboss.tools.forge.ui.ForgeUIPlugin;
import org.jboss.tools.forge.ui.console.ForgeConsole;
import org.jboss.tools.forge.ui.console.ForgeConsoleManager;

public class ForgeConsoleDropdownAction extends Action implements IMenuCreator {
	
	private ForgeConsoleView forgeConsoleView = null;
	private Menu menu = null;
	
	public ForgeConsoleDropdownAction(ForgeConsoleView forgeConsoleView) {
		this.forgeConsoleView = forgeConsoleView;
		setImageDescriptor(createImageDescriptor());
		setMenuCreator(this);
	}

	@Override
	public void dispose() {
	}
	
	@Override
    public void runWithEvent(Event event) {
    	if (event.widget instanceof ToolItem) {
			ToolItem toolItem= (ToolItem) event.widget;
			Control control= toolItem.getParent();
    		Menu menu= getMenu(control);   		
    		Rectangle bounds= toolItem.getBounds();
    		Point topLeft= new Point(bounds.x, bounds.y + bounds.height);
    		menu.setLocation(control.toDisplay(topLeft));
    		menu.setVisible(true);
    	}
    }
    
	@Override
	public Menu getMenu(Control parent) {
		if (menu != null) {
			menu.dispose();
		}
		menu= new Menu(parent);
		for (ForgeConsole forgeConsole : ForgeConsoleManager.INSTANCE.getConsoles()) {
			addAction(menu, forgeConsole);
		}
		return menu;
	}

	@Override
	public Menu getMenu(Menu parent) {
		return null;
	}
	
	private void addAction(Menu menu, ForgeConsole forgeConsole) {
		Action action = new ForgeConsoleShowAction(forgeConsoleView, forgeConsole);
		action.setChecked(forgeConsole.equals(forgeConsoleView.getConsole()));
		ActionContributionItem item= new ActionContributionItem(action);
		item.fill(menu, -1);
	}
	
	private ImageDescriptor createImageDescriptor() {
		URL url = ForgeUIPlugin.getDefault().getBundle().getEntry("icons/forge.png");
		return ImageDescriptor.createFromURL(url);
	}

}
