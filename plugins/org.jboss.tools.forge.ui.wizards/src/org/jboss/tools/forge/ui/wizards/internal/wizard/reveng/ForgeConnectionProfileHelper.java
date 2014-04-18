package org.jboss.tools.forge.ui.wizards.internal.wizard.reveng;

import java.util.ArrayList;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.forge.ui.util.ForgeHelper;

public class ForgeConnectionProfileHelper {
	
	private static final String CONNECTION_PROFILE = "Connection profile \"";
	private static final String DIALECT = "dialect:";
	private static final String DRIVER_CLASS = "driver class:";
	private static final String DRIVER_LOCATION = "driver location:";
	private static final String URL = "url:";
	private static final String USER = "user:";
	
	private GenerateEntitiesWizardPage wizardPage;
	
	ForgeConnectionProfileHelper(GenerateEntitiesWizardPage wizardPage) {
		this.wizardPage = wizardPage;
	}
	
	boolean retrieveConnectionProfiles() {
		Job job = new WorkspaceJob("Retrieving connection profiles") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				String rawString = ForgeHelper.getDefaultRuntime().sendCommand("connection-profiles list");				
				refreshConnectionProfiles(getConnectionProfiles(rawString));
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		return true;
	}
	
	private void refreshConnectionProfiles(final ConnectionProfileDescriptor[] connectionProfiles) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				wizardPage.refreshConnectionProfiles(connectionProfiles);
			}			
		});
	}

	private ConnectionProfileDescriptor[] getConnectionProfiles(String raw) {
		ArrayList<ConnectionProfileDescriptor> result = new ArrayList<ConnectionProfileDescriptor>();
		int start = -1;
		int end = raw.indexOf(CONNECTION_PROFILE);
		while (end < raw.length()) {
			start = end;
			end = raw.indexOf(CONNECTION_PROFILE, start + CONNECTION_PROFILE.length());
			end = end == -1 ? raw.length() : end;
			result.add(parseConnectionProfile(raw.substring(start, end).trim()));
		}
		// add an unnamed connection profile
		ConnectionProfileDescriptor unnamed = new ConnectionProfileDescriptor();
		unnamed.name = "";
		result.add(unnamed);
		return result.toArray(new ConnectionProfileDescriptor[result.size()]);
	}
	
	private ConnectionProfileDescriptor parseConnectionProfile(String raw) {
		ConnectionProfileDescriptor result = new ConnectionProfileDescriptor();
		String lineSeparator = System.getProperty("line.separator");
		int start = 0;
		int end = -1;
		// get the connection profile name
		start = raw.indexOf(CONNECTION_PROFILE) + CONNECTION_PROFILE.length();
		end = raw.indexOf("\":", start);
		result.name = raw.substring(start, end);
		// get the dialect
		start = raw.indexOf(DIALECT, end) + DIALECT.length();
		end = raw.indexOf(lineSeparator, start);
		result.dialect = raw.substring(start, end).trim();
		// get the driver class
		start = raw.indexOf(DRIVER_CLASS, end) + DRIVER_CLASS.length();
		end = raw.indexOf(lineSeparator, start);
		result.driverClass = raw.substring(start, end).trim();
		// get the driver location
		start = raw.indexOf(DRIVER_LOCATION, end) + DRIVER_LOCATION.length();
		end = raw.indexOf(lineSeparator, start);
		result.driverLocation = raw.substring(start, end).trim();
		// get the url
		start = raw.indexOf(URL, end) + URL.length();
		end = raw.indexOf(lineSeparator, start);
		result.url = raw.substring(start, end).trim();
		// get the user
		start = raw.indexOf(USER, end) + USER.length();
		end = raw.indexOf(lineSeparator, start);
		end = end == -1 ? raw.length() : end;
		result.user = raw.substring(start, end).trim();
		return result;
	}
	
}
