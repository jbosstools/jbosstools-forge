/**
 * Copyright (c) Red Hat, Inc., contributors and others 2013 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.internal.ext.provider;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.jboss.forge.addon.ui.UIDesktop;
import org.jboss.forge.addon.ui.UIProvider;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;

/**
 * Eclipse implementation of {@link UIProvider}
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeUIProvider implements UIProvider, UIOutput {

	private MessageConsole forgeConsole;
	private PrintStream forgeConsoleOutputStream;
	private PrintStream forgeConsoleErrorStream;
	private Color red;

	private MessageConsole getForgeConsole() {
		if (forgeConsole == null) {
			forgeConsole = findForgeConsole();
		}
		return forgeConsole;
	}

	@Override
	public String getName() {
		return "Forge " + FurnaceRuntime.INSTANCE.getVersion() + " Console";
	}

	@Override
	public boolean isEmbedded() {
		return true;
	}

	private MessageConsole findForgeConsole() {
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager consoleManager = consolePlugin.getConsoleManager();
		IConsole[] allConsoles = consoleManager.getConsoles();
		for (int i = 0; i < allConsoles.length; i++) {
			if (getName().equals(allConsoles[i].getName())) {
				return (MessageConsole) allConsoles[i];
			}
		}
		MessageConsole myConsole = new MessageConsole(getName(), null);
		consoleManager.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	@Override
	public boolean isGUI() {
		return true;
	}

	@Override
	public UIOutput getOutput() {
		return this;
	}

	@Override
	public PrintStream out() {
		if (forgeConsoleOutputStream == null) {
			forgeConsoleOutputStream = new PrintStream(getForgeConsole().newMessageStream(), true);
		}
		return forgeConsoleOutputStream;
	}

	@Override
	public PrintStream err() {
		if (forgeConsoleErrorStream == null) {
			MessageConsoleStream messageConsoleStream = getForgeConsole().newMessageStream();
			Display.getDefault().syncExec(() -> messageConsoleStream.setColor(getRed()));
			forgeConsoleErrorStream = new PrintStream(messageConsoleStream, true);
		}
		return forgeConsoleErrorStream;
	}

	private Color getRed() {
		if (red == null) {
			Display.getDefault().syncExec(() -> red = Display.getDefault().getSystemColor(SWT.COLOR_RED));
		}
		return red;
	}

	@Override
	public void error(PrintStream writer, String message) {
		writer.print("[ERROR] ");
		writer.println(message);
	}

	@Override
	public void success(PrintStream writer, String message) {
		writer.print("[SUCCESS] ");
		writer.println(message);
	}

	@Override
	public void info(PrintStream writer, String message) {
		writer.print("[INFO] ");
		writer.println(message);
	}

	@Override
	public void warn(PrintStream writer, String message) {
		writer.print("[WARNING] ");
		writer.println(message);
	}

	@Override
	public UIDesktop getDesktop() {
		return new ForgeUIDesktop();
	}
}
