package org.jboss.tools.forge.core.preferences;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.tools.forge.core.ForgeCorePlugin;
import org.jboss.tools.forge.core.process.ForgeEmbeddedRuntime;
import org.jboss.tools.forge.core.process.ForgeExternalRuntime;
import org.jboss.tools.forge.core.process.ForgeRuntime;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ForgeRuntimesPreferences {
	
	static final String PREF_FORGE_RUNTIMES = "org.jboss.tools.forge.core.runtimes";
	
	public static final ForgeRuntimesPreferences INSTANCE = new ForgeRuntimesPreferences();
	
	private List<ForgeRuntime> runtimes = null;
	private ForgeRuntime defaultRuntime = null;
	
	private ForgeRuntimesPreferences() {}
	
	public ForgeRuntime[] getRuntimes() {
		if (runtimes == null) {
			initializeRuntimes();
		}
		return (ForgeRuntime[])runtimes.toArray(new ForgeRuntime[runtimes.size()]);
	}
	
	public ForgeRuntime getDefault() {
		if (defaultRuntime == null) {
			initializeRuntimes();
		}
		return defaultRuntime;
	}
	
	private IEclipsePreferences getForgeCorePreferences() {
		return InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID);
	}
	
	private String getForgeRuntimesPreference() {
		return getForgeCorePreferences().get(
				PREF_FORGE_RUNTIMES, 
				ForgePreferencesInitializer.INITIAL_RUNTIMES_PREFERENCE);
	}
	
	private void initializeRuntimes() {
		initializeFromXml(getForgeRuntimesPreference());
	}
	
	private void initializeFromXml(String xml) {
		DocumentBuilder documentBuilder = newDocumentBuilder();
		if (documentBuilder == null) return;
		InputStream inputStream = createInputStream(xml);
		if (inputStream == null) return;
		runtimes = new ArrayList<ForgeRuntime>();
		Document document = parseRuntimes(documentBuilder, inputStream);	
		Element runtimeElement = document.getDocumentElement();
		String defaultRuntimeName = runtimeElement.getAttribute("default");
		NodeList nodeList = runtimeElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				String type = element.getAttribute("type");
				ForgeRuntime runtime = null;
				if ("embedded".equals(type)) {
					runtime = ForgeEmbeddedRuntime.INSTANCE;
				} else if ("external".equals(type)) {
					String name = element.getAttribute("name");
					String location = element.getAttribute("location");
					runtime = new ForgeExternalRuntime(name, location);
				}
				if (runtime == null) continue;
				runtimes.add(runtime);
				if (defaultRuntimeName.equals(runtime.getName())) {
					defaultRuntime = runtime;
				}
			}
		}
	}
	
	private Document parseRuntimes(DocumentBuilder documentBuilder, InputStream inputStream) {
		Document result = null;
		try {
			result = documentBuilder.parse(inputStream);
		} catch (SAXException e) {
			ForgeCorePlugin.log(e);
		} catch (IOException e) {
			ForgeCorePlugin.log(e);
		}
		return result;
	}
	
	private InputStream createInputStream(String string) {
		InputStream result = null;
		try {
			result = new BufferedInputStream(new ByteArrayInputStream(string.getBytes("UTF8")));
		} catch (UnsupportedEncodingException e) {
			ForgeCorePlugin.log(e);
		}
		return result;
	}
	
	private DocumentBuilder newDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			ForgeCorePlugin.log(e);
			return null;
		}
	}
		
	public void setRuntimes(ForgeRuntime[] runtimes, ForgeRuntime defaultRuntime) {
		this.runtimes.clear();
		for (ForgeRuntime runtime : runtimes) {
			this.runtimes.add(runtime);
		}
		this.defaultRuntime = defaultRuntime;
		saveRuntimes();
	}
	
	private void saveRuntimes() {
		try {
			IEclipsePreferences eclipsePreferences = getForgeCorePreferences();
			String xml = serializeDocument(createRuntimesDocument());
			eclipsePreferences.put(PREF_FORGE_RUNTIMES, xml);
			eclipsePreferences.flush();
		} catch (IOException e) {
			ForgeCorePlugin.log(e);
		} catch (TransformerException e) {
			ForgeCorePlugin.log(e);
		} catch (BackingStoreException e) {
			ForgeCorePlugin.log(e);
		}
	}
	
	private Document createRuntimesDocument() {
		Document document = createEmptyDocument();
		if (document == null)
			return null;
		Element main = document.createElement("forgeRuntimes");
		document.appendChild(main);
		for (ForgeRuntime runtime : runtimes) {
			Element element = document.createElement("runtime");
			element.setAttribute("name", runtime.getName());
			if (!(runtime instanceof ForgeEmbeddedRuntime)) {
				element.setAttribute("location", runtime.getLocation());
			}
			element.setAttribute("type", runtime.getType());
			main.appendChild(element);
		}
		if (defaultRuntime != null) {
			main.setAttribute("default", defaultRuntime.getName());
		}
		return document;
	}
	
	private Document createEmptyDocument() {
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

}
