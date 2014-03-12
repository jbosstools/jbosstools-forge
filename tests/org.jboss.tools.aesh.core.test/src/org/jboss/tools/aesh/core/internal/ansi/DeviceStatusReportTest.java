package org.jboss.tools.aesh.core.internal.ansi;

import org.junit.Assert;
import org.junit.Test;

public class DeviceStatusReportTest {
	
	@Test
	public void testGetType() {
		DeviceStatusReport deviceStatusReport = new DeviceStatusReport(null);
		Assert.assertEquals(CommandType.DEVICE_STATUS_REPORT, deviceStatusReport.getType());
	}

}
