package org.jboss.tools.forge.ui.wizard.reveng;

import java.util.ArrayList;

import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ConnectionProfileHelper {
	
	private static final String CONNECTION_PROFILE = "Connection profile \"";
	private static final String DIALECT = "dialect:";
	private static final String DRIVER_CLASS = "driver class:";
	private static final String DRIVER_LOCATION = "driver location:";
	private static final String URL = "url:";
	private static final String USER = "user:";
	
	static ConnectionProfileDescriptor[] getConnectionProfiles() {
		ArrayList<ConnectionProfileDescriptor> result = new ArrayList<ConnectionProfileDescriptor>();
		String rawConnectionProfileString = 
				ForgeHelper.getDefaultRuntime().sendCommand("connection-profiles list");
		ArrayList<String> rawDescriptors = parse(new ArrayList<String>(), rawConnectionProfileString);
		for (String rawDescriptor : rawDescriptors) {
			result.add(parse(rawDescriptor));
		}
		return result.toArray(new ConnectionProfileDescriptor[result.size()]);
	}

	private static ArrayList<String> parse(ArrayList<String> list, String raw) {
		int i = raw.indexOf(CONNECTION_PROFILE);
		if (i != -1) {
			list.add(raw.substring(0, i));
			return parse(list, raw.substring(i + 1));
		} else {
			return list;
		}
	}
	
	private static ConnectionProfileDescriptor parse(String raw) {
		ConnectionProfileDescriptor result = new ConnectionProfileDescriptor();
		int start, end = -1;
		// get the connection profile name
		start = raw.indexOf(CONNECTION_PROFILE) + CONNECTION_PROFILE.length() + 1;
		end = raw.indexOf("\":", start);
		result.name = raw.substring(start, end);
		// get the dialect
		start = raw.indexOf(DIALECT, end) + DIALECT.length() + 1;
		end = raw.indexOf(System.lineSeparator(), start);
		result.dialect = raw.substring(start, end).trim();
		// get the driver class
		start = raw.indexOf(DRIVER_CLASS, end) + DRIVER_CLASS.length() + 1;
		end = raw.indexOf(System.lineSeparator(), start);
		result.driverClass = raw.substring(start, end).trim();
		// get the driver location
		start = raw.indexOf(DRIVER_LOCATION, end) + DRIVER_LOCATION.length() + 1;
		end = raw.indexOf(System.lineSeparator(), start);
		result.driverLocation = raw.substring(start, end).trim();
		// get the url
		start = raw.indexOf(URL, end) + URL.length() + 1;
		end = raw.indexOf(System.lineSeparator(), start);
		result.url = raw.substring(start, end).trim();
		// get the user
		start = raw.indexOf(USER, end) + USER.length() + 1;
		end = raw.indexOf(System.lineSeparator(), start);
		result.user = raw.substring(start, end).trim();
		return result;
	}
}
