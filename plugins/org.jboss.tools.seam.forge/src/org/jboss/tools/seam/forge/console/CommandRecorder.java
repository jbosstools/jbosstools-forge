package org.jboss.tools.seam.forge.console;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

public class CommandRecorder implements IDocumentListener {
	
	private StringBuffer buffer = new StringBuffer();
	private String beforePrompt = null;
	private String currentPrompt = null;
	private String currentCommand = null;

	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	@Override
	public void documentChanged(DocumentEvent event) {
		if (event.getLength() == 1 && "".equals(event.getText())) {
			buffer.setLength(buffer.length() - 1);
		} else {
			buffer.append(event.getText());
			String newPrompt = getNewPrompt();
			if (newPrompt != null) {
				currentPrompt = newPrompt;
				beforePrompt = buffer.substring(0, buffer.length() - newPrompt.length());
				buffer.setLength(0);
				if (currentCommand != null) {
					postProcessCurrentCommand();
					currentCommand = null;
				}
			} else {
				if (currentPrompt != null) {
					String newCommand = getNewCommand();
					if (newCommand != null) {
						currentCommand = newCommand;
					}
				}
			}
		}
	}
	
	private String getNewPrompt() {
		int lastLineBreak = buffer.lastIndexOf("\n");
		if (lastLineBreak == -1) return null;
		String lastLine = buffer.substring(lastLineBreak + 1);
		if (lastLine.length() == 0) return null;
		if (lastLine.charAt(0) != '[') return null;
		int rightBracketIndex = lastLine.indexOf(']');
		if (rightBracketIndex == -1) return null;
		return lastLine.endsWith("$ ") ? lastLine : null;
	}
	
	private String getNewCommand() {
		String candidateCommand = buffer.toString();
		if ("pwd".equals(candidateCommand)) {
			return "pwd";
		} else if ("new-project".equals(candidateCommand)) {
			return "new-project";
		} else {
			return null;
		}
	}
	
	private void postProcessCurrentCommand() {
		System.out.println("post processing current command : " + currentCommand);
		System.out.println("beforePrompt :\n" + beforePrompt);
	}

}
