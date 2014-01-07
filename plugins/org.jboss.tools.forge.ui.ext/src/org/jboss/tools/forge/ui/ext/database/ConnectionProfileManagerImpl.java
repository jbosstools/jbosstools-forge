package org.jboss.tools.forge.ui.ext.database;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.hibernate.forge.addon.connections.ConnectionProfile;
import org.hibernate.forge.addon.connections.ConnectionProfileManager;
import org.jboss.forge.furnace.proxy.Proxies;
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
			profile.name = currentProfile.getName();
			Properties props = currentProfile.getBaseProperties();
			profile.driver = props.getProperty(DRIVER_CLASS);
			profile.path = props.getProperty(DRIVER_LOCATION);
			profile.user = props.getProperty(USER_NAME);
			profile.url = props.getProperty(URL);
			profile.dialect = props.getProperty(HIBERNATE_DIALECT);
			result.put(profile.name, profile);
		}
		return result;		
	}

	@Override
	public void saveConnectionProfiles(Collection<ConnectionProfile> connectionProfiles) {
		try {
			Collection<ConnectionProfile> unwrapped = Proxies.unwrap(connectionProfiles);
			for (ConnectionProfile profile : unwrapped) {
				IConnectionProfile connectionProfile = 
						ProfileManager.getInstance().getProfileByName(profile.name);
				Properties baseProps = new Properties();
				baseProps.setProperty(DRIVER_CLASS, profile.driver);
				baseProps.setProperty(DRIVER_LOCATION, profile.path);
				baseProps.setProperty(URL, profile.url);
				baseProps.setProperty(USER_NAME, profile.user);
				if (profile.dialect != null) {
					baseProps.setProperty(HIBERNATE_DIALECT, profile.dialect);
				}
				if (connectionProfile == null) {
						connectionProfile = 
								ProfileManager.getInstance().createProfile(
										profile.name, 
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
