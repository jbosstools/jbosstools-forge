package org.jboss.tools.aesh.core.ansi;


public class DeviceStatusReport extends ControlSequence {

	public DeviceStatusReport(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.DEVICE_STATUS_REPORT;
	}

}
