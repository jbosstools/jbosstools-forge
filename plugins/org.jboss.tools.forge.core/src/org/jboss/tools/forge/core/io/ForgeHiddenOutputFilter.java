package org.jboss.tools.forge.core.io;

public class ForgeHiddenOutputFilter implements ForgeOutputListener {
	
	private static final String ESCAPE_SEQUENCE = new String(new char[] { 27, '[', '%' });
	
	private ForgeOutputListener target = null;
	private boolean hidden = false;
	private StringBuffer hiddenOutput = new StringBuffer();
	
	public ForgeHiddenOutputFilter(ForgeOutputListener target) {
		this.target = target;
	}

	@Override
	public void outputAvailable(String output) {
		System.out.println("ForgeHiddenOutputListener->outputAvailable: " + output);
		int i = output.indexOf(ESCAPE_SEQUENCE);
		if (i != -1) {
			if (hidden) {
				hiddenOutput.append(output.substring(0, i));
				handleHiddenOutput(hiddenOutput);
			} else {
				target.outputAvailable(output.substring(0, i));
				hidden = true;
			}
			if (output.length() > i + 3) {
				outputAvailable(output.substring(i + 3));
			}
		} else {
			if (hidden) {
				hiddenOutput.append(output);
			} else {
				target.outputAvailable(output);
			}
		}
	}
	
	private void handleHiddenOutput(StringBuffer output) {
		System.out.println(output.toString());
		output.setLength(0);
		hidden = false;
	}

}
