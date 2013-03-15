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
			Properties props = connectionProfile.getBaseProperties();
			descriptor.name = connectionProfile.getName();
			descriptor.driverClass = props.getProperty(DRIVER_CLASS);
			descriptor.driverLocation = props.getProperty(DRIVER_LOCATION);
			descriptor.url = props.getProperty(URL);
			descriptor.user = props.getProperty(USER_NAME);
//			System.out.println("connection profile: " + connectionProfile.getName());
//			for (Entry<Object, Object> entry : props.entrySet()) {
//				System.out.println("  key: " + entry.getKey());
//				System.out.println("  value: " + entry.getValue());
//			}
			result.add(descriptor);
		}
		return result;
	}

}
