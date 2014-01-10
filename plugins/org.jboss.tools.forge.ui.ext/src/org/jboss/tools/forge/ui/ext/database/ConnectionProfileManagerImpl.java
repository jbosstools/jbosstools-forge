package org.jboss.tools.forge.ui.ext.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.tools.forge.ui.ext.ForgeUIPlugin;

public class ConnectionProfileManagerImpl implements ConnectionProfileManager {

	private static final String CONNECTION_PROPERTIES = "org.eclipse.datatools.connectivity.db.connectionProperties";
	private static final String SAVE_PWD = "org.eclipse.datatools.connectivity.db.savePWD";
	private static final String DRIVER_DEFINITION_TYPE = "org.eclipse.datatools.connectivity.drivers.defnType";
	private static final String DRIVER_LOCATION = "jarList";
	private static final String USER_NAME = "org.eclipse.datatools.connectivity.db.username";
	private static final String DRIVER_CLASS = "org.eclipse.datatools.connectivity.db.driverClass";
	private static final String DRIVER_DEFINITION_ID = "org.eclipse.datatools.connectivity.driverDefinitionID";
	private static final String DATABASE_NAME = "org.eclipse.datatools.connectivity.db.databaseName";
	private static final String PASSWORD = "org.eclipse.datatools.connectivity.db.password";
	private static final String URL = "org.eclipse.datatools.connectivity.db.URL";
	private static final String VERSION = "org.eclipse.datatools.connectivity.db.version";
	private static final String VENDOR = "org.eclipse.datatools.connectivity.db.vendor";
	private static final String HIBERNATE_DIALECT = "org.jboss.tools.forge.hibernate.dialect";
	
	@Override
	public Map<String, ConnectionProfile> loadConnectionProfiles() {
		IConnectionProfile[] connectionProfiles = ProfileManager
				.getInstance()
				.getProfiles();	
		Map<String, ConnectionProfile> result = new HashMap<String, ConnectionProfile>();
		for (IConnectionProfile currentProfile : connectionProfiles) {
			ConnectionProfile profile = new ConnectionProfile();
			profile.setName(currentProfile.getName());
			Properties props = currentProfile.getBaseProperties();
			profile.setDriver(props.getProperty(DRIVER_CLASS));
			profile.setPath(props.getProperty(DRIVER_LOCATION));
			profile.setUser(props.getProperty(USER_NAME));
			profile.setUrl(props.getProperty(URL));
			profile.setDialect(props.getProperty(HIBERNATE_DIALECT));
			result.put(profile.getName(), profile);
		}
		return result;		
	}

	@Override
	public void saveConnectionProfiles(Collection<ConnectionProfile> connectionProfiles) {
		try {
			for (ConnectionProfile profile : connectionProfiles) {
				IConnectionProfile connectionProfile = 
						ProfileManager.getInstance().getProfileByName(profile.getName());
				Properties baseProps = new Properties();
				baseProps.setProperty(CONNECTION_PROPERTIES, "");
				baseProps.setProperty(SAVE_PWD, "false");
				baseProps.setProperty(
						DRIVER_DEFINITION_TYPE, 
						"org.eclipse.datatools.connectivity.db.generic.genericDriverTemplate");
				baseProps.setProperty(DRIVER_LOCATION, profile.getPath());
				baseProps.setProperty(USER_NAME, profile.getUser());
				baseProps.setProperty(DRIVER_CLASS, profile.getDriver());
				baseProps.setProperty(
						DRIVER_DEFINITION_ID, 
						"DriverDefn.org.eclipse.datatools.connectivity.db.generic.genericDriverTemplate.Generic JDBC Driver");
				baseProps.setProperty(DATABASE_NAME, "SAMPLE");
				baseProps.setProperty(PASSWORD, "");
				baseProps.setProperty(URL, profile.getUrl());
				baseProps.setProperty(VERSION, "1.0");
				baseProps.setProperty(VENDOR, "Generic JDBC");
				if (profile.getDialect() != null) {
					baseProps.setProperty(HIBERNATE_DIALECT, profile.getDialect());
				}
				if (connectionProfile == null) {
						connectionProfile = 
								ProfileManager.getInstance().createProfile(
										profile.getName(), 
										"", 
										"org.eclipse.datatools.connectivity.db.generic.connectionProfile", 
										baseProps, 
										"", 
										false);
				} else {
					connectionProfile.setBaseProperties(baseProps);
					ProfileManager.getInstance().modifyProfile(connectionProfile);
				}
			}
		} catch (ConnectionProfileException e) {
			ForgeUIPlugin.log(e);
		}
	}

}
