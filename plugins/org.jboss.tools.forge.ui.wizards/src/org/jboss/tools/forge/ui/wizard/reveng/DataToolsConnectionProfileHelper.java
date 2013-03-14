package org.jboss.tools.forge.ui.wizard.reveng;

import java.util.ArrayList;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;



public class DataToolsConnectionProfileHelper {
	
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
			descriptor.name = connectionProfile.getName();
			result.add(descriptor);
		}
		return result;
	}

}
