package org.jboss.tools.forge.runtime.ext;

import java.io.IOException;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.jboss.forge.shell.Shell;

@Decorator
public abstract class ShellDecorator implements Shell {
	
	private static final String ESCAPE = new String(new char[] { 27, '[', '%' });

	@Inject @Delegate Shell shell;

	@Override
	public String readLine() throws IOException {
		String str = shell.readLine();
		int i1 = str.indexOf(ESCAPE);
		int i2 = str.indexOf(ESCAPE, i1 + 3);
		if (i1 != -1 && str.length() > i1 + 3) {
			i2 = str.indexOf(ESCAPE);
			if (i2 != -1) {
				handleHidden(str.substring(i1 + 3, i2));
			}
			return "";
		}
		return str;
	}
	
	private void handleHidden(String str) {
		shell.println(ESCAPE + "hidden detected: " + str + ESCAPE); 
	}

}
