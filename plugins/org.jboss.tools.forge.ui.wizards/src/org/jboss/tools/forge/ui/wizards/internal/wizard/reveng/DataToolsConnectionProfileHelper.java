/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.ui.wizards.internal.wizard.reveng;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.jboss.tools.forge.ui.wizards.internal.WizardsPlugin;



public class DataToolsConnectionProfileHelper {
	
	private static final String DRIVER_CLASS = "org.eclipse.datatools.connectivity.db.driverClass";
	private static final String DRIVER_LOCATION = "jarList";
	private static final String USER_NAME = "org.eclipse.datatools.connectivity.db.username";
	private static final String URL = "org.eclipse.datatools.connectivity.db.URL";
	private static final String HIBERNATE_DIALECT = "org.jboss.tools.forge.hibernate.dialect";
	
	private GenerateEntitiesWizardPage wizardPage;
	private ConnectionProfileDescriptor unnamed = new ConnectionProfileDescriptor();
	
	DataToolsConnectionProfileHelper(GenerateEntitiesWizardPage wizardPage) {
		this.wizardPage = wizardPage;
		this.unnamed.name = "";
	}
		
	boolean retrieveConnectionProfiles() {
		ArrayList<ConnectionProfileDescriptor> connectionProfileList = getConnectionProfiles();
		connectionProfileList.add(unnamed);
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
		try {
			IConnectionProfile connectionProfile = 
					ProfileManager.getInstance().getProfileByName(descriptor.name);
			Properties baseProps = new Properties();
			baseProps.setProperty(DRIVER_CLASS, descriptor.driverClass);
			baseProps.setProperty(DRIVER_LOCATION, descriptor.driverLocation);
			baseProps.setProperty(URL, descriptor.url);
			baseProps.setProperty(USER_NAME, descriptor.user);
			baseProps.setProperty(HIBERNATE_DIALECT, descriptor.dialect);
			if (connectionProfile == null) {
					connectionProfile = 
							ProfileManager.getInstance().createProfile(
									descriptor.name, 
									"", 
									"org.eclipse.datatools.connectivity.db.generic.connectionProfile", 
									baseProps, 
									"", 
									false);
			} else {
				connectionProfile.setBaseProperties(baseProps);
				ProfileManager.getInstance().modifyProfile(connectionProfile);
			}
		} catch (ConnectionProfileException e) {
			WizardsPlugin.log(e);
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
		descriptor.dialect = props.getProperty(HIBERNATE_DIALECT);
	}
	
}
