package org.jboss.tools.forge.core.io;

public interface ForgeOutputFilter extends ForgeOutputListener {
	
	void handleFilteredString(String str);

}
