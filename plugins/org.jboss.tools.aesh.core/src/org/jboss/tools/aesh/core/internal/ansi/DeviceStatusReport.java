package org.jboss.tools.aesh.core.internal.ansi;



public class DeviceStatusReport extends AbstractControlSequence {

	public DeviceStatusReport(String arguments) {}

	@Override
	public AnsiControlSequenceType getType() {
		return AnsiControlSequenceType.DEVICE_STATUS_REPORT;
	}

}
