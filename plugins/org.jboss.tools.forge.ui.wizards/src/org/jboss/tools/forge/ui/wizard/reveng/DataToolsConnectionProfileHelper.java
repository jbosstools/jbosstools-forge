package org.jboss.tools.forge.ui.wizard.reveng;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;



public class DataToolsConnectionProfileHelper {
	
	private static final String DRIVER_CLASS = "org.eclipse.datatools.connectivity.db.driverClass";
	private static final String DRIVER_LOCATION = "jarList";
	private static final String USER_NAME = "org.eclipse.datatools.connectivity.db.username";
	private static final String URL = "org.eclipse.datatools.connectivity.db.URL";
	
	private GenerateEntitiesWizardPage wizardPage;
	
	DataToolsConnectionProfileHelper(GenerateEntitiesWizardPage wizardPage) {
		this.wizardPage = wizardPage;
	}
		
	boolean retrieveConnectionProfiles() {
		ArrayList<ConnectionProfileDescriptor> connectionProfileList = getConnectionProfiles();
		wizardPage.refreshConnectionProfiles(
				connectionProfileList.toArray(
						new ConnectionProfileDescriptor[connectionProfileList.size()]));
		return true;
	}
	
	
	
	private ArrayList<ConnectionProfileDescriptor> getConnectionProfiles() {
		IConnectionProfile[] connectionProfiles = ProfileManager
				.getInstance()
				.getProfiles();	
		ArrayList<ConnectionProfileDescriptor> result = new ArrayList<ConnectionProfileDescriptor>();
		for (IConnectionProfile connectionProfile : connectionProfiles) {
			ConnectionProfileDescriptor descriptor = new ConnectionProfileDescriptor();
			fillDescriptor(descriptor, connectionProfile);
			result.add(descriptor);
		}
		return result;
	}
	
	void saveConnectionProfile(ConnectionProfileDescriptor descriptor) {
		IConnectionProfile connectionProfile = 
				ProfileManager.getInstance().getProfileByName(descriptor.name);
		if (connectionProfile != null) {
			Properties baseProps = connectionProfile.getBaseProperties();
			baseProps.setProperty(DRIVER_CLASS, descriptor.driverClass);
			baseProps.setProperty(DRIVER_LOCATION, descriptor.driverLocation);
			baseProps.setProperty(URL, descriptor.url);
			baseProps.setProperty(USER_NAME, descriptor.user);
		}
	}
	
	void revertConnectionProfile(ConnectionProfileDescriptor descriptor) {
		IConnectionProfile connectionProfile = 
				ProfileManager.getInstance().getProfileByName(descriptor.name);
		if (connectionProfile != null) {
			fillDescriptor(descriptor, connectionProfile);
			wizardPage.updateConnectionProfileDetails();
		}
	}
	
	private void fillDescriptor(
			ConnectionProfileDescriptor descriptor,
			IConnectionProfile connectionProfile) {
		Properties props = connectionProfile.getBaseProperties();
		descriptor.name = connectionProfile.getName();
		descriptor.driverClass = props.getProperty(DRIVER_CLASS);
		descriptor.driverLocation = props.getProperty(DRIVER_LOCATION);
		descriptor.url = props.getProperty(URL);
		descriptor.user = props.getProperty(USER_NAME);		
	}
	
//	boolean testConnectionProfile(ConnectionProfileDescriptor descriptor) {
//		boolean result = false;
//		IConnectionProfile connectionProfile = ProfileManager
//				.getInstance()
//				.getProfileByName(descriptor.name);
//		try {
//			IStatus status = connectionProfile.connectWithoutJob();
//			if (status.getCode() == IStatus.OK) {
//				result = true;
//			}
//		} finally {
//			connectionProfile.disconnect();
//		}
//		return result;
//	}

}
