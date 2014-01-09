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

	private static final String DRIVER_CLASS = "org.eclipse.datatools.connectivity.db.driverClass";
	private static final String DRIVER_LOCATION = "jarList";
	private static final String USER_NAME = "org.eclipse.datatools.connectivity.db.username";
	private static final String URL = "org.eclipse.datatools.connectivity.db.URL";
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
				baseProps.setProperty(DRIVER_CLASS, profile.getDriver());
				baseProps.setProperty(DRIVER_LOCATION, profile.getPath());
				baseProps.setProperty(URL, profile.getUrl());
				baseProps.setProperty(USER_NAME, profile.getUser());
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
