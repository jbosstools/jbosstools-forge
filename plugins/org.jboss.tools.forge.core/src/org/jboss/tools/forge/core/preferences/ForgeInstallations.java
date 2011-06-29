package org.jboss.tools.forge.core.preferences;

import org.jboss.tools.forge.core.process.ForgeEmbeddedRuntime;
import org.jboss.tools.forge.core.process.ForgeRuntime;

public class ForgeInstallations {
	
	static final String PREF_FORGE_INSTALLATIONS = "org.jboss.tools.forge.core.installations";
	
//	private static List<ForgeRuntime> installations = null;
	private static ForgeRuntime DEFAULT_INSTALLATION = null;
	
//	public static ForgeRuntime[] getInstallations() {
//		if (installations == null) {
//			initializeInstallations();
//		}
//		return (ForgeRuntime[])installations.toArray(new ForgeRuntime[installations.size()]);
//	}
	
	public static ForgeRuntime getDefault() {
		if (DEFAULT_INSTALLATION == null) {
			initializeInstallations();
		}
		return DEFAULT_INSTALLATION;
	}
	
//	private static IEclipsePreferences getForgeCorePreferences() {
//		return InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID);
//	}
	
//	private static void initializeInstallations() {
//		initializeEmbeddedRuntime();
//	}
	
//	private static void initializeEmbeddedRuntime() {
		
//	}
	
	private static void initializeInstallations() {
		DEFAULT_INSTALLATION = ForgeEmbeddedRuntime.INSTANCE;
	}
	
//	private static void initializeInstallations() {
//		String installPrefsXml = getForgeCorePreferences().get(PREF_FORGE_INSTALLATIONS, null);
//		if (installPrefsXml == null || "".equals(installPrefsXml)) {
//			createInitialInstallations();
//			installPrefsXml = getForgeCorePreferences().get(PREF_FORGE_INSTALLATIONS, null);
//		}
//		initializeFromXml(installPrefsXml);
//	}
//	
//	private static void initializeFromXml(String installPrefsXml) {
//		if (installPrefsXml == null) return;
//		DocumentBuilder documentBuilder = newDocumentBuilder();
//		if (documentBuilder == null) return;
//		InputStream inputStream = createInputStream(installPrefsXml);
//		if (inputStream == null) return;
//		installations = new ArrayList<ForgeRuntime>();
//		Document document = parseInstallations(documentBuilder, inputStream);	
//		Element installationsElement = document.getDocumentElement();
//		String defaultInstallationName = installationsElement.getAttribute("default");
//		NodeList nodeList = installationsElement.getChildNodes();
//		for (int i = 0; i < nodeList.getLength(); i++) {
//			Node node = nodeList.item(i);
//			if (node.getNodeType() == Node.ELEMENT_NODE) {
//				Element element = (Element)node;
//				String name = element.getAttribute("name");
//				String location = element.getAttribute("location");
//				ForgeRuntime newInstallation = new ForgeRuntime(name, location);
//				installations.add(newInstallation);
//				if (name.equals(defaultInstallationName)) {
//					defaultInstallation = newInstallation;
//				}
//			}
//		}
//	}
//	
//	private static Document parseInstallations(DocumentBuilder documentBuilder, InputStream inputStream) {
//		Document result = null;
//		try {
//			result = documentBuilder.parse(inputStream);
//		} catch (SAXException e) {
//			ForgeUIPlugin.log(e);
//		} catch (IOException e) {
//			ForgeUIPlugin.log(e);
//		}
//		return result;
//	}
//	
//	private static InputStream createInputStream(String string) {
//		InputStream result = null;
//		try {
//			result = new BufferedInputStream(new ByteArrayInputStream(string.getBytes("UTF8")));
//		} catch (UnsupportedEncodingException e) {
//			ForgeUIPlugin.log(e);
//		}
//		return result;
//	}
//	
//	private static DocumentBuilder newDocumentBuilder() {
//		try {
//			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
//		} catch (ParserConfigurationException e) {
//			ForgeUIPlugin.log(e);
//			return null;
//		}
//	}
//	
//	private static Document createEmptyDocument() {
//		DocumentBuilder documentBuilder = newDocumentBuilder();
//		if (documentBuilder == null) {
//			return null;
//		} else {
//			return documentBuilder.newDocument();
//		}
//	}
//	
//	private static String serializeDocument(Document doc) throws TransformerException, IOException {
//		ByteArrayOutputStream s = new ByteArrayOutputStream();
//		TransformerFactory factory = TransformerFactory.newInstance();
//		Transformer transformer = factory.newTransformer();
//		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
//		DOMSource source = new DOMSource(doc);
//		StreamResult outputTarget = new StreamResult(s);
//		transformer.transform(source, outputTarget);
//		return s.toString("UTF8"); 		
//	}
//	
//	private static void createInitialInstallations() {
//		try {
//			File file = FileLocator.getBundleFile(ForgeUIPlugin.getDefault().getBundle());
//			defaultInstallation = new ForgeInstallation("embedded", file.getAbsolutePath());
//			installations = new ArrayList<ForgeInstallation>();
//			installations.add(defaultInstallation);
//			saveInstallations();
//		} catch (IOException e) {
//			ForgeUIPlugin.log(e);
//		}
//	}
//	
//	public static void setInstallations(ForgeInstallation[] installs, ForgeInstallation defaultInstall) {
//		installations.clear();
//		for (ForgeInstallation install : installs) {
//			installations.add(install);
//		}
//		defaultInstallation = defaultInstall;
//		saveInstallations();
//	}
//	
//	private static void saveInstallations() {
//		try {
//			String xml = serializeDocument(createInstallationsDocument());
//			ForgeUIPlugin.getDefault().getPreferenceStore().setValue(PREF_FORGE_INSTALLATIONS, xml);
//		} catch (IOException e) {
//			ForgeUIPlugin.log(e);
//		} catch (TransformerException e) {
//			ForgeUIPlugin.log(e);
//		}
//	}
//
//	private static Document createInstallationsDocument() {
//		Document document = createEmptyDocument();
//		if (document == null) return null;
//		Element main = document.createElement("forgeInstallations");
//		document.appendChild(main);
//		for (ForgeInstallation installation : installations) {
//			Element element = document.createElement("installation");
//			element.setAttribute("name", installation.getName());
//			element.setAttribute("location", installation.getLocation());
//			main.appendChild(element);
//		}
//		main.setAttribute("default", defaultInstallation.getName());
//		return document;
//	}

}
