package org.jboss.tools.aesh.example;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.jboss.aesh.terminal.POSIXTerminal;
import org.jboss.aesh.terminal.TerminalSize;

public class ExampleTerminal extends POSIXTerminal {
	
	private ITextViewer textViewer;
	private TerminalSize terminalSize;
	
	public ExampleTerminal(ITextViewer textViewer) {
		this.textViewer = textViewer;
		initControlListener();
		updateTerminalSize();
	}
	
	private void initControlListener() {
		textViewer.getTextWidget().addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					updateTerminalSize();
				}
			});
		}
	
	private void updateTerminalSize() {
		StyledText textWidget = textViewer.getTextWidget();
		GC gc = new GC(textWidget);
		gc.setFont(textWidget.getFont());
		Point p = gc.stringExtent("w");
		gc.dispose();
		Point size = textWidget.getSize();
		terminalSize = 
				new TerminalSize(
						Math.max(1, size.y / p.y), 
						Math.max(1, size.x / p.x));
	}
	
    @Override
    public TerminalSize getSize() {
        return terminalSize;
    }


}
