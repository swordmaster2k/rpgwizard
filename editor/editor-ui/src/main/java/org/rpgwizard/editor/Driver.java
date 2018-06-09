/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor;

import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel;
import org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
import org.rpgwizard.common.assets.serialization.JsonAnimationSerializer;
import org.rpgwizard.common.assets.serialization.JsonBoardSerializer;
import org.rpgwizard.common.assets.serialization.JsonEnemySerializer;
import org.rpgwizard.common.assets.serialization.JsonNPCSerializer;
import org.rpgwizard.common.assets.serialization.JsonCharacterSerializer;
import org.rpgwizard.common.assets.serialization.JsonItemSerializer;
import org.rpgwizard.common.assets.serialization.JsonProjectSerializer;
import org.rpgwizard.common.assets.serialization.JsonSpecialMoveSerializer;
import org.rpgwizard.common.assets.serialization.JsonTileSetSerializer;
import org.rpgwizard.common.assets.serialization.TextProgramSerializer;
import org.rpgwizard.editor.properties.EditorProperties;
import org.rpgwizard.editor.properties.EditorProperty;
import org.rpgwizard.editor.properties.user.UserPreference;
import org.rpgwizard.editor.properties.user.UserPreferencesProperties;
import org.rpgwizard.editor.ui.Theme;
import org.rpgwizard.editor.utilities.FileTools;
import org.rpgwizard.pluginsystem.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.JarPluginManager;
import ro.fortsoft.pf4j.PluginManager;

public class Driver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

    public static void redirectUncaughtExceptions() {
        try {
            Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
                LOGGER.error("Uncaught Exception detected in thread {}", t, e);
            });
        } catch (SecurityException e) {
            LOGGER.error("Could not set the Default Uncaught Exception Handler", e);
        }
    }

    public static void logSystemInfo() {
        LOGGER.info("---------------------------- System Info ----------------------------");
        LOGGER.info("Operating System: {}", System.getProperty("os.name"));
        LOGGER.info("System Architecture: {}", System.getProperty("os.arch"));
        LOGGER.info("Available Processors (cores): {}", Runtime.getRuntime().availableProcessors());
        LOGGER.info("Free Memory (bytes): {}", Runtime.getRuntime().freeMemory());
        LOGGER.info("Total Memory (bytes): {}", Runtime.getRuntime().totalMemory());
        LOGGER.info("Max Memory (bytes): {}", Runtime.getRuntime().maxMemory());
        LOGGER.info("---------------------------------------------------------------------");
    }

    public static void registerResolvers() {
        LOGGER.debug("Registering asset resolvers.");

        AssetManager.getInstance().registerResolver(new FileAssetHandleResolver());
    }

    public static void registerSerializers() {
        LOGGER.debug("Registering asset serializers.");

        AssetManager assetManager = AssetManager.getInstance();
        assetManager.registerSerializer(new JsonAnimationSerializer());
        assetManager.registerSerializer(new JsonCharacterSerializer());
        assetManager.registerSerializer(new JsonBoardSerializer());
        assetManager.registerSerializer(new JsonProjectSerializer());
        assetManager.registerSerializer(new JsonSpecialMoveSerializer());
        assetManager.registerSerializer(new JsonEnemySerializer());
        assetManager.registerSerializer(new JsonItemSerializer());
        assetManager.registerSerializer(new JsonNPCSerializer());
        assetManager.registerSerializer(new TextProgramSerializer());
        assetManager.registerSerializer(new JsonTileSetSerializer());
    }

    public static PluginManager registerPlugins() throws URISyntaxException {
        String path = FileTools.getExecutionPath(Driver.class);
        path += File.separator + EditorProperties.getProperty(EditorProperty.EDITOR_PLUGINS_DIRECOTRY) + File.separator;
        System.setProperty("pf4j.pluginsDir", path);
        LOGGER.info(System.getProperty("pf4j.pluginsDir"));

        PluginManager pluginManager = new JarPluginManager();
        pluginManager.loadPlugins();
        pluginManager.startPlugins();
        return pluginManager;
    }

    public static void loadUserPreferences() {
        Theme theme = Theme
                .valueOf(UserPreferencesProperties.getProperty(UserPreference.USER_PREFERENCE_THEME).toUpperCase());
        final LookAndFeel laf;
        switch (theme) {
        case LIGHT:
            laf = new SubstanceNebulaLookAndFeel();
            break;
        case DARK:
        default:
            laf = new SubstanceGraphiteAquaLookAndFeel();
        }
        try {
            UIManager.setLookAndFeel(laf);
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        } catch (UnsupportedLookAndFeelException ex) {
            LOGGER.error("Failed to set look and feel theme=[{}]!", ex, theme);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                LOGGER.info("Starting the RPGWizard Editor...");
                redirectUncaughtExceptions();

                logSystemInfo();
                registerResolvers();
                registerSerializers();
                PluginManager pluginManager = registerPlugins();
                loadUserPreferences();

                MainWindow mainWindow = MainWindow.getInstance();
                mainWindow.setPluginManager(pluginManager);
                mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                mainWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        mainWindow.tearDown();
                        mainWindow.dispose();

                        // Quietly stop any engines.
                        List<Engine> engines = pluginManager.getExtensions(Engine.class);
                        engines.forEach((engine) -> {
                            try {
                                engine.stop();
                            } catch (Exception ex) {
                                LOGGER.warn("Failed to stop engine! reason=[{}]", ex.getMessage());
                            }
                        });

                        // Write out user preferences.
                        UserPreferencesProperties.save();
                        LOGGER.info("Stopping the RPGWizard Editor...");
                    }
                });
                mainWindow.setVisible(true);
            } catch (URISyntaxException ex) {
                LOGGER.error("Failed to start the editor!", ex);
            }
        });

    }
}
