package org.jboss.tools.forge.launching;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.jboss.tools.forge.ForgeUIPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ForgeRuntime implements IDebugEventSetListener {
	
	private static final String PREF_FORGE_INSTALLATIONS = "installations";
	
	private static List<ForgeInstallation> installations = null;
	private static ForgeInstallation defaultInstallation = null;
	
	public static ForgeInstallation[] getInstallations() {
		if (installations == null) {
			initializeInstallations();
		}
		return (ForgeInstallation[])installations.toArray(new ForgeInstallation[installations.size()]);
	}
	
	public static ForgeInstallation getDefaultInstallation() {
		if (installations == null) {
			initializeInstallations();
		}
		return defaultInstallation;
	}
	
	private static void initializeInstallations() {
		String installPrefsXml = ForgeUIPlugin.getDefault().getPreferenceStore().getString(PREF_FORGE_INSTALLATIONS);
		if (installPrefsXml == null || "".equals(installPrefsXml)) {
			createInitialInstallations();
			installPrefsXml = ForgeUIPlugin.getDefault().getPreferenceStore().getString(PREF_FORGE_INSTALLATIONS);
		}
		initializeFromXml(installPrefsXml);
	}
	
	private static void initializeFromXml(String installPrefsXml) {
		if (installPrefsXml == null) return;
		DocumentBuilder documentBuilder = newDocumentBuilder();
		if (documentBuilder == null) return;
		InputStream inputStream = createInputStream(installPrefsXml);
		if (inputStream == null) return;
		installations = new ArrayList<ForgeInstallation>();
		Document document = parseInstallations(documentBuilder, inputStream);	
		Element installationsElement = document.getDocumentElement();
		String defaultInstallationName = installationsElement.getAttribute("default");
		NodeList nodeList = installationsElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				String name = element.getAttribute("name");
				String location = element.getAttribute("location");
				ForgeInstallation newInstallation = new ForgeInstallation(name, location);
				installations.add(newInstallation);
				if (name.equals(defaultInstallationName)) {
					defaultInstallation = newInstallation;
				}
			}
		}
	}
	
	private static Document parseInstallations(DocumentBuilder documentBuilder, InputStream inputStream) {
		Document result = null;
		try {
			result = documentBuilder.parse(inputStream);
		} catch (SAXException e) {
			ForgeUIPlugin.log(e);
		} catch (IOException e) {
			ForgeUIPlugin.log(e);
		}
		return result;
	}
	
	private static InputStream createInputStream(String string) {
		InputStream result = null;
		try {
			result = new BufferedInputStream(new ByteArrayInputStream(string.getBytes("UTF8")));
		} catch (UnsupportedEncodingException e) {
			ForgeUIPlugin.log(e);
		}
		return result;
	}
	
	private static DocumentBuilder newDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			ForgeUIPlugin.log(e);
			return null;
		}
	}
	
	private static Document createEmptyDocument() {
		DocumentBuilder documentBuilder = newDocumentBuilder();
		if (documentBuilder == null) {
			return null;
		} else {
			return documentBuilder.newDocument();
		}
	}
	
	private static String serializeDocument(Document doc) throws TransformerException, IOException {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
		DOMSource source = new DOMSource(doc);
		StreamResult outputTarget = new StreamResult(s);
		transformer.transform(source, outputTarget);
		return s.toString("UTF8"); 		
	}
	
	private static void createInitialInstallations() {
		try {
			File file = FileLocator.getBundleFile(Platform.getBundle("org.jboss.tools.forge.runtime"));
			defaultInstallation = new ForgeInstallation("embedded", file.getAbsolutePath());
			installations = new ArrayList<ForgeInstallation>();
			installations.add(defaultInstallation);
			saveInstallations();
		} catch (IOException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	public static void setInstallations(ForgeInstallation[] installs, ForgeInstallation defaultInstall) {
		installations.clear();
		for (ForgeInstallation install : installs) {
			installations.add(install);
		}
		defaultInstallation = defaultInstall;
		saveInstallations();
	}
	
	private static void saveInstallations() {
		try {
			String xml = serializeDocument(createInstallationsDocument());
			ForgeUIPlugin.getDefault().getPreferenceStore().setValue(PREF_FORGE_INSTALLATIONS, xml);
		} catch (IOException e) {
			ForgeUIPlugin.log(e);
		} catch (TransformerException e) {
			ForgeUIPlugin.log(e);
		}
	}

	private static Document createInstallationsDocument() {
		Document document = createEmptyDocument();
		if (document == null) return null;
		Element main = document.createElement("forgeInstallations");
		document.appendChild(main);
		for (ForgeInstallation installation : installations) {
			Element element = document.createElement("installation");
			element.setAttribute("name", installation.getName());
			element.setAttribute("location", installation.getLocation());
			main.appendChild(element);
		}
		main.setAttribute("default", defaultInstallation.getName());
		return document;
	}

	
	public static final ForgeRuntime INSTANCE = new ForgeRuntime();
	public static final String STATE_NOT_RUNNING = "org.jboss.tools.forge.notRunning";
	public static final String STATE_RUNNING = "org.jboss.tools.forge.running";
	public static final String STATE_STARTING = "org.jboss.tools.forge.starting";
	public static final String STATE_STOPPING = "org.jboss.tools.forge.stopping";
	
	private IProcess forgeProcess = null;
	private String runtimeState = STATE_NOT_RUNNING;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private ForgeRuntime() {}
	
	public boolean isForgeRunning() {
		return forgeProcess != null && !forgeProcess.isTerminated();
	}
	
	public void startForge() {
		try {
			if (!isForgeRunning()) {
				setRuntimeState(STATE_STARTING);
				ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
				ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
				ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
				for (int i = 0; i < configurations.length; i++) {
					ILaunchConfiguration configuration = configurations[i];
					if (configuration.getName().equals("Forge")) {
						configuration.delete();
						break;
					}
				}
				ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "Forge");
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.jboss.forge.shell.Bootstrap");
				List<String> classpath = new ArrayList<String>();
//				Bundle bundle = Platform.getBundle("org.jboss.tools.forge");
				File file = null;
//				try {
//					file = FileLocator.getBundleFile(bundle);
					file = new File(ForgeRuntime.getDefaultInstallation().getLocation());
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				if (file == null) return;
				if (!file.exists()) {
					ForgeUIPlugin.log(new RuntimeException("Not a correct Forge runtime."));
					setRuntimeState(STATE_NOT_RUNNING);
					return;
				}
				File[] children = file.listFiles(new FilenameFilter() {					
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith("lib");
					}
				});
				if (children.length != 1) {
					ForgeUIPlugin.log(new RuntimeException("Not a correct Forge runtime."));
					setRuntimeState(STATE_NOT_RUNNING);
					return;
				}
				File forgeLibDir = children[0];
				
				File[] forgeLibFiles = forgeLibDir.listFiles(new FilenameFilter() {					
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith("jar");
					}
				});
				for (File libFile: forgeLibFiles) {
					IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(libFile.getAbsolutePath()));
					entry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
					classpath.add(entry.getMemento());
				}
				IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
				IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(
						systemLibsPath, 
						IRuntimeClasspathEntry.STANDARD_CLASSES);
				classpath.add(systemLibsEntry.getMemento());
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath);
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				IPath path = root.getLocation();
				File workingDir = path.toFile();
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, workingDir.getAbsolutePath());
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-Dforge.compatibility.IDE=true");
				ILaunchConfiguration configuration = workingCopy.doSave();
				ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE, null, false, false);
				IProcess[] processes = launch.getProcesses();
				if (processes.length == 1) {
					forgeProcess = processes[0];
				}
				DebugPlugin.getDefault().addDebugEventListener(this);
				setRuntimeState(STATE_RUNNING);
			}
		} catch (CoreException e) {
			ForgeUIPlugin.log(e);
		}
	}
	
	public void stopForge() {
		if (isForgeRunning()) {
			setRuntimeState(STATE_STOPPING);
			try {
				forgeProcess.terminate();
			} catch (DebugException e) {
				e.printStackTrace();
			}
			setRuntimeState(STATE_NOT_RUNNING);
			DebugPlugin.getDefault().removeDebugEventListener(this);
		}
	}
	
	public void setRuntimeState(String newRuntimeState) {
		String oldRuntimeState = this.runtimeState;
		this.runtimeState = newRuntimeState;
		propertyChangeSupport.firePropertyChange(
				new PropertyChangeEvent(
						this, 
						"runtimeState", 
						oldRuntimeState, 
						newRuntimeState));
	}
	
	public String getRuntimeState() {
		return runtimeState;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener("runtimeState", listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener("runtimeState", listener);
	}

	protected void finalize() throws Throwable {
		if (forgeProcess != null) {
			if (!forgeProcess.isTerminated()) {
				forgeProcess.terminate();
			}
			forgeProcess = null;
		}
		super.finalize();
	}
	
	public IProcess getProcess() {
		return forgeProcess;
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            DebugEvent event = events[i];
            if (event.getSource().equals(getProcess())) {
                if (event.getKind() == DebugEvent.TERMINATE) {
                	if (forgeProcess.isTerminated()) {
                		DebugPlugin.getDefault().asyncExec(new Runnable() {
							public void run() {
		                		setRuntimeState(STATE_NOT_RUNNING);
							}
						});
                	}
                    DebugPlugin.getDefault().removeDebugEventListener(this);
                }
            }
        }
	}
	
}
