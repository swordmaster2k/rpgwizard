/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine.plugin;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import org.rpgwizard.pluginsystem.Engine;
import org.apache.commons.io.FileUtils;
import org.cef.OS;
import org.rpgwizard.html5.engine.plugin.browser.EmbeddedBrowser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.fortsoft.pf4j.Extension;
import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class Html5EnginePlugin extends Plugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(Html5EnginePlugin.class);

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
        public File compile(String projectName, File projectCopy, File executionPath, ProgressMonitor progressMonitor)
                throws Exception {
            return Compiler.compile(projectName, projectCopy, executionPath, progressMonitor);
        }

        @Override
        public void run(String projectName, int width, int height, File projectCopy, ProgressMonitor progressMonitor)
                throws Exception {
            // Ensure the engine isn't already running.
            try {
                stop(null);
            } catch (Exception ex) {
                LOGGER.warn("Failed to stop engine! reason=[{}]", ex.getMessage());
            }
            TEMP_PROJECT = projectCopy;
            Compiler.embedEngine(projectName, projectCopy, progressMonitor, false);
            startEmbeddedServer(projectCopy.getAbsolutePath());

            // 75%
            updateProgress(progressMonitor, 75);
            openEmbeddedBrowser(projectName, width, height);

            // 100%
            updateProgress(progressMonitor, 100);
        }

        @Override
        public void stop() throws Exception {
            if (ENGINE_RUNNABLE != null) {
                ENGINE_RUNNABLE.stop();
            }
            if (EMBEDDED_BROWSER != null) {
                EMBEDDED_BROWSER.stop();
            }
            if (TEMP_PROJECT != null) {
                FileUtils.deleteQuietly(TEMP_PROJECT);
            }
        }

        @Override
        public void stop(ProgressMonitor progressMonitor) throws Exception {
            if (ENGINE_RUNNABLE != null) {
                ENGINE_RUNNABLE.stop();
            }
            if (EMBEDDED_BROWSER != null) {
                EMBEDDED_BROWSER.conceal();
            }
            updateProgress(progressMonitor, 50);
            if (TEMP_PROJECT != null) {
                FileUtils.deleteQuietly(TEMP_PROJECT);
            }
            updateProgress(progressMonitor, 100);
        }

        private void startEmbeddedServer(String resourceBase) throws Exception {
            ENGINE_RUNNABLE = new EngineRunnable(resourceBase);
            ENGINE_THREAD = new Thread(ENGINE_RUNNABLE);
            ENGINE_THREAD.start();
        }

        private void openEmbeddedBrowser(String projectName, int width, int height) {
            SwingUtilities.invokeLater(() -> {
                if (EMBEDDED_BROWSER != null) {
                    EMBEDDED_BROWSER.display(URL, projectName, width, height);
                } else {
                    EMBEDDED_BROWSER = new EmbeddedBrowser(projectName, URL, OS.isLinux(), false, width, height);
                    EMBEDDED_BROWSER.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            EMBEDDED_BROWSER.setVisible(false);
                            EMBEDDED_BROWSER.getCefBrowser().loadURL("http://www.rpgwizard.org");
                        }
                    });
                }
            });
        }
    }

    private static void updateProgress(ProgressMonitor progressMonitor, int progress) {
        if (progressMonitor == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            progressMonitor.setProgress(progress);
        });
    }
}
