package org.jboss.tools.aesh.core.ansi;


public class DeviceStatusReport extends ControlSequence {

	public DeviceStatusReport(String controlSequenceString) {
		super(controlSequenceString);
	}

	@Override
	protected ControlSequenceType getType() {
		return ControlSequenceType.DEVICE_STATUS_REPORT;
	}

}
