/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.cef.OS;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Project;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
import org.rpgwizard.common.assets.serialization.JsonProjectSerializer;
import org.rpgwizard.html5.engine.plugin.EngineRunnable;
import org.rpgwizard.html5.engine.plugin.browser.EmbeddedBrowser;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class Standalone {

    public static boolean STANDALONE_MODE = false;

    private static Thread ENGINE_THREAD;
    private static EngineRunnable ENGINE_RUNNABLE;
    private static EmbeddedBrowser EMBEDDED_BROWSER;

    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(new JFrame(), message, title, JOptionPane.ERROR_MESSAGE);
    }

    private static Project openProject(File projectFile) throws AssetException, IOException {
        AssetManager assetManager = AssetManager.getInstance();
        assetManager.registerResolver(new FileAssetHandleResolver());
        assetManager.registerSerializer(new JsonProjectSerializer());
        AssetHandle handle = AssetManager.getInstance().deserialize(new AssetDescriptor(projectFile.toURI()));
        return (Project) handle.getAsset();
    }

    private static void start(String resourceBase, Project project) {
        // Start the Embedded Jetty.
        ENGINE_RUNNABLE = new EngineRunnable(resourceBase);
        ENGINE_THREAD = new Thread(ENGINE_RUNNABLE);
        ENGINE_THREAD.start();

        javax.swing.SwingUtilities.invokeLater(() -> {
            // Show the JCEF browser window.
            EMBEDDED_BROWSER = new EmbeddedBrowser(project.getName(), "http://localhost:8080", OS.isLinux(), false,
                    project.getResolutionWidth(), project.getResolutionHeight());
            EMBEDDED_BROWSER.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        ENGINE_RUNNABLE.stop();
                        EMBEDDED_BROWSER.stop();
                    } catch (Exception ex) {
                        Logger.getLogger(Standalone.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // JCEF keeps hanging.
                    throw new RuntimeException("Forcefully shutting down JCEF - Can ignore this exception");
                }
            });
        });
    }

    public static void main(String[] args) {
        Standalone.STANDALONE_MODE = true;

        // System.setProperty("org.rpgwizard.execution.path", "D:/Documents/Software
        // Development/rpgwizard/distribution/target/rpgwizard-1.1.0-windows/rpgwizard-1.1.0/builds/RPGWizard 1.1.0 -
        // The Wizard's Tower-1507920535920");
        String resourceBase = System.getProperty("org.rpgwizard.execution.path") + File.separator + "data";
        File projectFile = new File(new File(resourceBase), "default.game");

        if (projectFile.exists()) {
            Project project;
            try {
                project = openProject(projectFile);
                start(resourceBase, project);
            } catch (AssetException | IOException ex) {
                Logger.getLogger(Standalone.class.getName()).log(Level.SEVERE, null, ex);
                showErrorDialog("Error on Game Start", "Could not open default.game!");
                Runtime.getRuntime().halt(1);
            }
        } else {
            showErrorDialog("Error on Game Start", "No Game File Found!");
            Runtime.getRuntime().halt(1);
        }
    }
}
