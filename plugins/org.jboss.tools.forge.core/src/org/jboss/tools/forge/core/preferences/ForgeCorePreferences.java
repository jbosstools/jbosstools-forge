/**
 * Copyright (c) Red Hat, Inc., contributors and others 2004 - 2014. All rights reserved
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.tools.forge.core.preferences;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.tools.forge.core.furnace.FurnaceRuntime;
import org.jboss.tools.forge.core.internal.ForgeCorePlugin;
import org.jboss.tools.forge.core.internal.preferences.ForgeCorePreferencesInitializer;
import org.jboss.tools.forge.core.internal.runtime.ForgeEmbeddedRuntime;
import org.jboss.tools.forge.core.internal.runtime.ForgeExternalRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntime;
import org.jboss.tools.forge.core.runtime.ForgeRuntimeType;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ForgeCorePreferences {

	public static final String PREF_FORGE_RUNTIMES = "org.jboss.tools.forge.core.runtimes";
	public static final String PREF_FORGE_STARTUP = "org.jboss.tools.forge.core.startup";
	public static final String PREF_FORGE_START_IN_DEBUG = "org.jboss.tools.forge.core.startInDebug";
	public static final String PREF_FORGE_VM_ARGS = "org.jboss.tools.forge.core.vmArgs";
	private static final String PREF_FORGE_ADDON_DIR = "org.jboss.tools.forge.ext.core.addon_dir";

	public static final ForgeCorePreferences INSTANCE = new ForgeCorePreferences();

	private List<ForgeRuntime> runtimes = null;
	private ForgeRuntime defaultRuntime = null;
	private String defaultRuntimeName = null;

	private ForgeCorePreferences() {
	}

	public ForgeRuntime[] getRuntimes() {
		if (runtimes == null) {
			initializeRuntimes();
		}
		return (ForgeRuntime[]) runtimes.toArray(new ForgeRuntime[runtimes
				.size()]);
	}

	public ForgeRuntime getDefaultRuntime() {
		if (defaultRuntime == null) {
			initializeRuntimes();
		}
		return defaultRuntime;
	}

	public boolean getStartup() {
		return getForgeCorePreferences().getBoolean(PREF_FORGE_STARTUP, false);

	}

	public boolean getStartInDebug() {
		return getForgeCorePreferences().getBoolean(PREF_FORGE_START_IN_DEBUG,
				false);
	}

	public String getVmArgs() {
		return getForgeCorePreferences().get(PREF_FORGE_VM_ARGS, "");
	}

	private IEclipsePreferences getForgeCorePreferences() {
		return InstanceScope.INSTANCE.getNode(ForgeCorePlugin.PLUGIN_ID);
	}

	private String getForgeRuntimesPreference() {
		return getForgeCorePreferences().get(PREF_FORGE_RUNTIMES,
				ForgeCorePreferencesInitializer.INITIAL_RUNTIMES_PREFERENCE);
	}

	private void initializeRuntimes() {
		runtimes = new ArrayList<ForgeRuntime>();
		runtimes.add(FurnaceRuntime.INSTANCE);
		runtimes.add(ForgeEmbeddedRuntime.INSTANCE);
		addFromXml(getForgeRuntimesPreference());
		initializeDefaultRuntime();
	}
	
	private void initializeDefaultRuntime() {
		for (ForgeRuntime runtime : runtimes) {
			if (runtime.getName().equals(defaultRuntimeName)) {
				defaultRuntime = runtime;
				break;
			}
		}
		if (defaultRuntime == null) {
			if ("embedded".equals(defaultRuntimeName)) {
				defaultRuntime = ForgeEmbeddedRuntime.INSTANCE;
			} else {
				defaultRuntime = FurnaceRuntime.INSTANCE;
			}
			defaultRuntimeName = defaultRuntime.getName();
		}
	}

	private void addFromXml(String xml) {
		DocumentBuilder documentBuilder = newDocumentBuilder();
		if (documentBuilder == null) return;
		InputStream inputStream = createInputStream(xml);
		if (inputStream == null) return;
		Document document = parseRuntimes(documentBuilder, inputStream);
		Element runtimeElement = document.getDocumentElement();
		defaultRuntimeName = runtimeElement.getAttribute("default");
		NodeList nodeList = runtimeElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				String type = element.getAttribute("type").toUpperCase();
				if (ForgeRuntimeType.valueOf(type).equals(
						ForgeRuntimeType.EXTERNAL)) {
					String name = element.getAttribute("name");
					String location = element.getAttribute("location");
					runtimes.add(new ForgeExternalRuntime(name, location));
				}
			}
		}
	}

	private Document parseRuntimes(DocumentBuilder documentBuilder,
			InputStream inputStream) {
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
			result = new BufferedInputStream(new ByteArrayInputStream(
					string.getBytes("UTF8")));
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

	public void setStartup(boolean startup) {
		try {
			setBoolean(PREF_FORGE_STARTUP, startup);
		} catch (BackingStoreException e) {
			ForgeCorePlugin.log(e);
		}
	}

	public void setStartInDebug(boolean startup) {
		try {
			setBoolean(PREF_FORGE_START_IN_DEBUG, startup);
		} catch (BackingStoreException e) {
			ForgeCorePlugin.log(e);
		}
	}

	public void setVmArgs(String vmArgsText) {
		try {
			IEclipsePreferences eclipsePreferences = getForgeCorePreferences();
			if (vmArgsText == null) {
				eclipsePreferences.remove(PREF_FORGE_VM_ARGS);
			} else {
				eclipsePreferences.put(PREF_FORGE_VM_ARGS, vmArgsText);
			}
			eclipsePreferences.flush();
		} catch (BackingStoreException e) {
			ForgeCorePlugin.log(e);
		}
	}

	private void setBoolean(String prefKey, boolean startup)
			throws BackingStoreException {
		IEclipsePreferences eclipsePreferences = getForgeCorePreferences();
		eclipsePreferences.putBoolean(prefKey, startup);
		eclipsePreferences.flush();
	}

	public void addPreferenceChangeListener(IPreferenceChangeListener listener) {
		getForgeCorePreferences().addPreferenceChangeListener(listener);
	}

	public void removePreferenceChangeListener(
			IPreferenceChangeListener listener) {
		getForgeCorePreferences().removePreferenceChangeListener(listener);
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
			element.setAttribute("type", runtime.getType().name().toLowerCase());
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

	private static String serializeDocument(Document doc)
			throws TransformerException, IOException {
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

	public String getAddonDir() {
		IEclipsePreferences prefs = getForgeCorePreferences();
		return prefs.get(PREF_FORGE_ADDON_DIR,
				new File(OperatingSystemUtils.getUserForgeDir(), "addons")
						.getAbsolutePath());
	}

	public void setAddonDir(String addonDir) {
		IEclipsePreferences prefs = getForgeCorePreferences();
		prefs.put(PREF_FORGE_ADDON_DIR, addonDir);
		try {
			prefs.flush();
			restartForge();
		} catch (BackingStoreException bse) {
			ForgeCorePlugin.log(bse);
		} catch (BundleException e) {
			ForgeCorePlugin.log(e);
		}
	}

	private void restartForge() throws BundleException {
		Bundle bundle = Platform.getBundle(ForgeCorePlugin.PLUGIN_ID);
		bundle.stop();
		bundle.start();
	}
}
