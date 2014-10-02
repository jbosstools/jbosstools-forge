package org.jboss.tools.forge.ui.internal.cli;

import java.io.IOException;

import org.jboss.forge.addon.shell.spi.Terminal;

public class MaxSizeTerminal implements Terminal {

	@Override
	public void close() throws IOException {
	}

	@Override
	public int getHeight() {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getWidth() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void initialize() {
	}

}
