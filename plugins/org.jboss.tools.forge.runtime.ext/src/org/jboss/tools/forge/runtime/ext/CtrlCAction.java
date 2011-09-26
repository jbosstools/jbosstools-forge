package org.jboss.tools.forge.runtime.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jboss.forge.shell.spi.TriggeredAction;

import sun.misc.Signal;

@SuppressWarnings("restriction")
public class CtrlCAction implements TriggeredAction {

	@Override
	public ActionListener getListener() {
		return new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				Signal.raise(new Signal("INT"));
			}
		};
	}
	
	@Override
	public char getTrigger() {
		return (char)3;
	}

}
