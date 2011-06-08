package org.jboss.tools.forge.view;

import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class ConsoleText extends StyledText {

	public ConsoleText(Composite parent, int style) {
		super(parent, style);
	}
	
	public void invokeAction(int action) {
		checkWidget();
		switch (action) {
			case ST.LINE_UP:
				doLineUp();
				break;
			case ST.LINE_DOWN:
				doLineDown();
				break;
//			case ST.DELETE_PREVIOUS:
//				doDeletePrevious();
//				break;
			default:
				super.invokeAction(action);
		}
	}
	
	private void doLineUp() {
//		System.out.println("Line up");
	}
	private void doLineDown() {
//		System.out.println("Line down");
	}
//	private void doDeletePrevious() {
//		System.out.println("Backspace");
//	}

}
