package org.jboss.tools.forge.ui.ext.database;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.drivers.DriverInstance;
import org.eclipse.datatools.connectivity.drivers.DriverManager;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfile;
import org.jboss.forge.addon.database.tools.connections.ConnectionProfileManager;
import org.jboss.tools.forge.ui.ForgeUIPlugin;

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
	
	private static final String DRIVER_TEMPLATE = "org.eclipse.datatools.connectivity.db.generic.genericDriverTemplate";
	private static final String PROVIDER_ID = "org.eclipse.datatools.connectivity.db.generic.connectionProfile";
	
	@Override
	public Map<String, ConnectionProfile> loadConnectionProfiles() {
		IConnectionProfile[] connectionProfiles = ProfileManager
				.getInstance()
				.getProfiles();	
		Map<String, ConnectionProfile> result = new LinkedHashMap<String, ConnectionProfile>();
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
	
	public void saveConnectionProfile(ConnectionProfile profile) {
		DriverInstance driverInstance = 
				DriverManager.getInstance().getDriverInstanceByName(profile.getName());
		if (driverInstance != null) {
			saveExistingDriver(driverInstance, profile);
		}
		IConnectionProfile connectionProfile = 
				ProfileManager.getInstance().getProfileByName(profile.getName());
		if (connectionProfile != null) {
			saveExistingProfile(profile, connectionProfile);
		} else {
			createNewProfile(profile);
		}
	}
	
	private void saveExistingProfile(ConnectionProfile source, IConnectionProfile target) {
		try {
			Properties newProperties = createProperties(source);
			if (propertiesChanged(target.getBaseProperties(), newProperties)) {
				target.setBaseProperties(newProperties);
					ProfileManager.getInstance().modifyProfile(target);
			}
		} catch (ConnectionProfileException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private void saveExistingDriver(DriverInstance driverInstance, ConnectionProfile profile) {
		if (!profile.getPath().equals(driverInstance.getJarList()) || 
				!profile.getDriver().equals(driverInstance.getProperty(DRIVER_CLASS))) {
			DriverManager.getInstance().removeDriverInstance(driverInstance.getId());
			DriverManager.getInstance().createNewDriverInstance(
					DRIVER_TEMPLATE, 
					profile.getName(), 
					profile.getPath(), 
					profile.getDriver());
		}
	}
	
	private boolean propertiesChanged(Properties oldProps, Properties newProps) {
		boolean result = false;
		result = result || oldProps.getProperty(DRIVER_CLASS).equals(newProps.getProperty(DRIVER_CLASS));
		result = result || oldProps.getProperty(DRIVER_LOCATION).equals(newProps.getProperty(DRIVER_LOCATION));
		result = result || oldProps.getProperty(USER_NAME).equals(newProps.getProperty(USER_NAME));
		result = result || oldProps.getProperty(URL).equals(newProps.getProperty(URL));
		result = result || oldProps.getProperty(HIBERNATE_DIALECT).equals(newProps.getProperty(HIBERNATE_DIALECT));
		return result;
	}
	
	private void createNewProfile(ConnectionProfile profile) {
		try {
			DriverManager.getInstance().createNewDriverInstance(
					DRIVER_TEMPLATE, 
					profile.getName(), 
					profile.getPath(), 
					profile.getDriver());
			ProfileManager.getInstance().createProfile(
					profile.getName(), 
					"", 
					PROVIDER_ID, 
					createProperties(profile), 
					"", 
					false);
		} catch (ConnectionProfileException e) {
			ForgeUIPlugin.log(e);
		}
	}

	@Override
	public void saveConnectionProfiles(Collection<ConnectionProfile> connectionProfiles) {
		Map<String, ConnectionProfile> existingProfiles = loadConnectionProfiles();
		for (ConnectionProfile profile : connectionProfiles) {
			existingProfiles.remove(profile.getName());
			saveConnectionProfile(profile);
		}
		for (String name : existingProfiles.keySet()) {
			deleteConnectionProfile(name);
		}
	}
	
	private void deleteConnectionProfile(String name) {
		try {
			DriverInstance driverInstance = DriverManager.getInstance().getDriverInstanceByName(name);
			if (driverInstance != null) {
				DriverManager.getInstance().removeDriverInstance(driverInstance.getId());
			}
			IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(name);
			if (profile != null) {
				ProfileManager.getInstance().deleteProfile(profile);
			}
		} catch (ConnectionProfileException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	private Properties createProperties(ConnectionProfile profile) {
		Properties result = new Properties();
		result.setProperty(CONNECTION_PROPERTIES, "");
		result.setProperty(SAVE_PWD, "false");
		result.setProperty(DRIVER_DEFINITION_TYPE, DRIVER_TEMPLATE);
		result.setProperty(DRIVER_LOCATION, profile.getPath());
		result.setProperty(USER_NAME, profile.getUser());
		result.setProperty(DRIVER_CLASS, profile.getDriver());
		String driverId = getDriverId(profile.getName());
		if (driverId != null) {
			result.setProperty(DRIVER_DEFINITION_ID, driverId);
		}
		result.setProperty(DATABASE_NAME, profile.getName());
		result.setProperty(PASSWORD, profile.getPassword());
		result.setProperty(URL, profile.getUrl());
		result.setProperty(VERSION, "1.0");
		result.setProperty(VENDOR, "Generic JDBC");
		String dialect = profile.getDialect();
		if (dialect != null) {
			result.setProperty(HIBERNATE_DIALECT, dialect);
		}
		return result;
	}
	
	private String getDriverId(String driverName) {
		String result = null;
		DriverInstance driverInstance = getDriver(driverName);
		if (driverInstance != null) {
			result = driverInstance.getId();
		}
		return result;
	}
	
	private DriverInstance getDriver(String name) {
		System.out.println("getDriver(" + name + ")");
		return DriverManager.getInstance().getDriverInstanceByName(name);
	}

}
