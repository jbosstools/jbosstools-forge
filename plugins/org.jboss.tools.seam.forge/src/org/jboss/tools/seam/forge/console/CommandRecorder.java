package org.jboss.tools.seam.forge.console;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.jboss.tools.seam.forge.importer.ProjectImporter;

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
		if ("pwd".equals(currentCommand)) {
			// do nothing
		} else if ("new-project".equals(currentCommand)) {
			int index = beforePrompt.lastIndexOf("***SUCCESS*** Created project [");
			if (index == -1) return;
			if (index + 31 > beforePrompt.length()) return;
			String str = beforePrompt.substring(index + 31);
			index = str.lastIndexOf("] in new working directory [");
			if (index == -1) return;
			if (index + 28 > str.length()) return;
			str = str.substring(index + 28);
			index = str.indexOf("]");
			if (index == -1) return;
			String projectPath = str.substring(0, index);
			index = projectPath.lastIndexOf('/');
			String projectDirName = projectPath.substring(index + 1);
			String projectBaseDirPath = projectPath.substring(0, index);
			new ProjectImporter(projectBaseDirPath, projectDirName).importProject();
		}
	}

}
