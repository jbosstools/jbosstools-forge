package org.jboss.tools.aesh.core.internal.ansi;



public class DeviceStatusReport extends AbstractCommand {

	public DeviceStatusReport(String arguments) {}

	@Override
	public ControlSequenceType getType() {
		return ControlSequenceType.DEVICE_STATUS_REPORT;
	}

}
