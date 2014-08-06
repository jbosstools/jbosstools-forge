/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.runtime.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jboss.forge.shell.spi.TriggeredAction;

public class CtrlCAction implements TriggeredAction {
	
	@Override
	public ActionListener getListener() {
		return new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent event) {
				try {
					Class<?> signalClass = getClass().getClassLoader().loadClass("sun.misc.Signal");
					Method raiseMethod = signalClass.getMethod("raise", signalClass);
					Object newSignal = signalClass.getConstructor(String.class).newInstance("INT");
					raiseMethod.invoke(null, newSignal);
				} catch (ClassNotFoundException e) {
					// ignored, trapping is not supported, switch to sun vm if you want this
				} catch (SecurityException e) {
					// should never happen;
				} catch (NoSuchMethodException e) {
					// should never happen;
				} catch (IllegalArgumentException e) {
					// should never happen;
				} catch (InstantiationException e) {
					// should never happen;
				} catch (IllegalAccessException e) {
					// should never happen;
				} catch (InvocationTargetException e) {
					// should never happen;
				}
			}
		};
	}
	
	@Override
	public char getTrigger() {
		return (char)3;
	}

}
