package org.jboss.tools.aesh.core.internal.ansi;



public class DeviceStatusReport extends AbstractCommand {

	public DeviceStatusReport(String arguments) {}

	@Override
	public CommandType getType() {
		return CommandType.DEVICE_STATUS_REPORT;
	}

}
