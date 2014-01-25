package org.jboss.tools.forge.ui.part;

import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.MessagePage;
import org.jboss.tools.forge.ui.console.ForgeConsole;

public class ForgeConsolePage extends MessagePage {
	
	private ForgeConsole forgeConsole = null;
	
	public ForgeConsolePage(ForgeConsole forgeConsole) {
		this.forgeConsole = forgeConsole;
	}
	
	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		setMessage(forgeConsole.getName());
	}

}
