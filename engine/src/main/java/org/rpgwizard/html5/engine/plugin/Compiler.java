/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine.plugin;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Collection;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class Compiler {

	private static final String PLUGINS_DIRECTORY = "plugins";
	private static final String BUILDS_DIRECTORY = "builds";
	private static final String JRE_DIRECTORY = "jre";
	private static final String OUTPUT_DIRECTORY = "output";
	private static final String DATA_DIRECTORY = "data";
	private static final String ENGINE_PLUGIN = "html5-engine-jar-with-dependencies.jar";

	public static void embedEngine(String title, File destination,
			ProgressMonitor progressMonitor, boolean isCompileMode)
			throws Exception {
		String destinationPath = destination.getAbsolutePath();

		// Copy and extract engine zip in destination directory.
		String engineZipName = "engine-rpgwizard.zip";
		File engineZip = new File(destinationPath + "/" + engineZipName);
		FileUtils.copyInputStreamToFile(
				Compiler.class.getResourceAsStream("/" + engineZipName),
				engineZip);

		ZipFile zipFile = new ZipFile(destinationPath + "/"
				+ engineZip.getName());
		zipFile.extractAll(destinationPath);

		if (!isCompileMode) {
			updateProgress(progressMonitor, 25);
		}

		// Clean up the zip.
		FileUtils.deleteQuietly(new File(destinationPath + "/"
				+ engineZip.getName()));

		// Find project game file.
		Collection<File> files = FileUtils.listFiles(destination,
				new String[]{"game"}, false);
		if (files.isEmpty()) {
			throw new Exception("No game file present in project directory!");
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

		if (!isCompileMode) {
			updateProgress(progressMonitor, 50);
		}

		// Modify index.html file for this project.
		File indexFile = new File(destinationPath + "/index.html");
		Document document = Jsoup.parse(indexFile, null);
		document.title(title);

		// Write out modified index.html.
		FileUtils.writeStringToFile(indexFile, document.outerHtml(), "UTF-8");
	}

	public static File compile(String projectName, File projectCopy,
			File executionPath, ProgressMonitor progressMonitor)
			throws Exception {
		String randomName = projectName + "-" + System.currentTimeMillis();
		File resultDirectory = null;
		File outputDirectory = null;

		try {
			File pluginsDirectory = new File(executionPath, PLUGINS_DIRECTORY);
			File buildsDirectory = new File(executionPath, BUILDS_DIRECTORY);
			File jreDirectory = new File(executionPath, JRE_DIRECTORY);

			// 1: Create the output directory for this build.
			outputDirectory = new File(buildsDirectory, OUTPUT_DIRECTORY);
			FileUtils.forceMkdir(outputDirectory);
			updateProgress(progressMonitor, 10);

			// 2: Copy the JRE into the output directory.
			FileUtils.copyDirectoryToDirectory(jreDirectory, outputDirectory);
			updateProgress(progressMonitor, 20);

			// 3: Copy the HTML5 engine plugin into the output directory.
			File html5Engine = new File(pluginsDirectory, ENGINE_PLUGIN);
			File html5EngineCopy = new File(outputDirectory, ENGINE_PLUGIN);
			FileUtils.copyFile(html5Engine, html5EngineCopy);
			updateProgress(progressMonitor, 30);

			// 4: Copy the project supplied by the editor into the output
			// directory.
			File dataDirectory = new File(outputDirectory, DATA_DIRECTORY);
			FileUtils.copyDirectory(projectCopy, dataDirectory);
			updateProgress(progressMonitor, 40);

			// 5: Embed the HTML5 engine framework into the project copy.
			embedEngine(projectName, dataDirectory, progressMonitor, true);
			updateProgress(progressMonitor, 50);

			// 6: Invoke Launch4J to create the executable.
			Process process = Runtime.getRuntime().exec(
					"cmd /c start /B package.bat", null, executionPath);
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new Exception(
						getErrorFromStream(process.getErrorStream()));
			}

			updateProgress(progressMonitor, 60);

			// 7: Wait for 60 seconds for the EXE to exist.
			File exeFile = new File(outputDirectory, "engine.exe");
			long start = System.currentTimeMillis();
			long last = start;
			int timeOut = 60000;
			while (!exeFile.exists() && last - start < timeOut) {
				// Wait.
				last = System.currentTimeMillis();
			}
			if (!exeFile.exists()) {
				throw new Exception(
						"Timed out waiting for game EXE file to be created!");
			}

			updateProgress(progressMonitor, 70);

			// 8: Create directory to copy to.
			resultDirectory = new File(outputDirectory.getParentFile(),
					randomName);
			while (resultDirectory.exists()) {
				randomName = projectName + "-" + System.currentTimeMillis();
				resultDirectory = new File(outputDirectory.getParentFile(),
						randomName);
			}
			FileUtils.forceMkdir(resultDirectory);

			updateProgress(progressMonitor, 80);

			// 9: Try to copy the outputDirectory contents to the
			// resultDirectory.
			boolean copied = false;
			while (!copied && last - start < timeOut) {
				try {
					FileUtils.copyDirectory(outputDirectory, resultDirectory);
					copied = true;
				} catch (IOException exception) {
					// Weaker hardware with slow drives e.g. a netbook can take
					// time to write everything to outputDirectory.
					// It may throw something like
					// "Failed to copy full contents from".
					last = System.currentTimeMillis();
				}
			}
			if (!copied) {
				throw new Exception(
						"Could not copy outputDirectory to resultDirectory!");
			}

			updateProgress(progressMonitor, 90);

			return resultDirectory;
		} catch (Exception ex) {
			throw ex;
		} finally {
			// 10: Clean up the result.
			FileUtils.deleteQuietly(new File(resultDirectory, randomName));
			FileUtils.deleteQuietly(new File(resultDirectory, ENGINE_PLUGIN));
			FileUtils.deleteQuietly(outputDirectory);
			FileUtils.deleteQuietly(projectCopy);

			updateProgress(progressMonitor, 100);
		}
	}

	private static String getErrorFromStream(InputStream in) throws IOException {
        String result = "Error from command stream: \n";
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result += line + "\n";
            }
        } catch (final IOException e) {
            throw e;
        }
        return result;
    }
	private static void updateProgress(ProgressMonitor progressMonitor, int progress) {
            SwingUtilities.invokeLater(() -> {
                progressMonitor.setProgress(progress);
            });
        }
	public static void main(String[] args) throws Exception {
		File executionPath = new File("D:/Desktop/compile_test");
		File projectCopy = new File(
				"D:/Desktop/standalone/builds/output/jfowjfwjfowjf");

		File result = Compiler.compile("Test", projectCopy, executionPath,
				new ProgressMonitor(null, "Test", "Test", 0, 0));

		Desktop.getDesktop().open(result);
	}

}
