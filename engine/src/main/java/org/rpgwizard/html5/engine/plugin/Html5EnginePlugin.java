/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine.plugin;

import java.awt.Desktop;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import javax.swing.ProgressMonitor;
import net.lingala.zip4j.core.ZipFile;
import org.rpgwizard.pluginsystem.Engine;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.cef.OS;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.rpgwizard.html5.engine.plugin.browser.EmbeddedBrowser;

import ro.fortsoft.pf4j.Extension;
import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class Html5EnginePlugin extends Plugin {

	private static final String URL = "http://localhost:8080";

	private static EmbeddedBrowser EMBEDDED_BROWSER;
	private static Thread ENGINE_THREAD;
	private static EngineRunnable ENGINE_RUNNABLE;
	private static File TEMP_PROJECT;

	public Html5EnginePlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Extension
	public static class Html5Engine implements Engine {

		public Html5Engine() {

		}

		@Override
		public void run(String projectName, File projectCopy,
				ProgressMonitor progressMonitor) throws Exception {
			embedEngine(projectName, projectCopy, progressMonitor);
			startEmbeddedServer(projectCopy.getAbsolutePath());

			// 75%
			progressMonitor.setProgress(75);
			openEmbeddedBrowser(projectName);

			// 100%
			progressMonitor.setProgress(100);
		}

		@Override
		public void stop() throws Exception {
			ENGINE_RUNNABLE.stop();
			EMBEDDED_BROWSER.stop();
		}

		@Override
		public void stop(ProgressMonitor progressMonitor) throws Exception {
			ENGINE_RUNNABLE.stop();
			EMBEDDED_BROWSER.conceal();
			progressMonitor.setProgress(50);
			FileUtils.deleteQuietly(TEMP_PROJECT);
			progressMonitor.setProgress(100);
		}

		private void embedEngine(String title, File destination,
				ProgressMonitor progressMonitor) throws Exception {
			TEMP_PROJECT = destination;
			String destinationPath = destination.getAbsolutePath();

			// Copy and extract engine zip in destination directory.
			String engineZipName = "engine-rpgwizard.zip";
			File engineZip = new File(destinationPath + "/" + engineZipName);
			FileUtils.copyInputStreamToFile(
					getClass().getResourceAsStream("/" + engineZipName),
					engineZip);

			ZipFile zipFile = new ZipFile(destinationPath + "/"
					+ engineZip.getName());
			zipFile.extractAll(destinationPath);

			// 25%
			progressMonitor.setProgress(25);

			// Clean up the zip.
			FileUtils.deleteQuietly(new File(destinationPath + "/"
					+ engineZip.getName()));

			// Find project game file.
			Collection<File> files = FileUtils.listFiles(destination,
					new String[]{"game"}, false);
			if (files.isEmpty()) {
				throw new Exception(
						"No game file present in project directory!");
			}

			// Just take the first available.
			File gameFile = files.iterator().next();
			File renamedGameFile = new File(destination.getAbsolutePath()
					+ "/default.game");

			// Rename game file.
			try {
				FileUtils.moveFile(gameFile, renamedGameFile);
			} catch (FileExistsException ex) {
				// Their project is called "default".
			}

			// 50%
			progressMonitor.setProgress(50);

			// Modify index.html file for this project.
			File indexFile = new File(destinationPath + "/index.html");
			Document document = Jsoup.parse(indexFile, null);
			document.title(title);

			// Write out modified index.html.
			FileUtils.writeStringToFile(indexFile, document.outerHtml(),
					"UTF-8");
		}

		private void startEmbeddedServer(String resourceBase) throws Exception {
			ENGINE_RUNNABLE = new EngineRunnable(resourceBase);
			ENGINE_THREAD = new Thread(ENGINE_RUNNABLE);
			ENGINE_THREAD.start();
		}

		private void openEmbeddedBrowser(String projectName) {
			if (EMBEDDED_BROWSER != null) {
				EMBEDDED_BROWSER.getCefBrowser().loadURL(URL);
                                EMBEDDED_BROWSER.setTitle(projectName);
				EMBEDDED_BROWSER.setVisible(true);
			} else {
				EMBEDDED_BROWSER = new EmbeddedBrowser(projectName, URL,
						OS.isLinux(), false);
				EMBEDDED_BROWSER.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						EMBEDDED_BROWSER.setVisible(false);
						EMBEDDED_BROWSER.getCefBrowser().loadURL(
								"http://www.rpgwizard.org");
					}
				});
			}
		}

		private void openDefaultBrowser() throws Exception {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(URL));
			} else {
				throw new Exception("Cannot open default browser!");
			}
		}

	}

	public static void main(String[] args) throws Exception {
		Html5EnginePlugin.Html5Engine engine = new Html5EnginePlugin.Html5Engine();
		engine.run("Test", new File("C:/Users/user/Desktop/Engine_Test"),
				new ProgressMonitor(null, "Test", "Test", 0, 0));
	}

}
