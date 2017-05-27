/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
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
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
import org.rpgwizard.common.assets.serialization.JsonAnimationSerializer;
import org.rpgwizard.common.assets.serialization.JsonBoardSerializer;
import org.rpgwizard.common.assets.serialization.JsonEnemySerializer;
import org.rpgwizard.common.assets.serialization.JsonItemSerializer;
import org.rpgwizard.common.assets.serialization.JsonPlayerSerializer;
import org.rpgwizard.common.assets.serialization.JsonProjectSerializer;
import org.rpgwizard.common.assets.serialization.JsonSpecialMoveSerializer;
import org.rpgwizard.common.assets.serialization.JsonTileSetSerializer;
import org.rpgwizard.common.assets.serialization.TextProgramSerializer;
import org.rpgwizard.common.assets.serialization.legacy.LegacyAnimatedTileSerializer;
import org.rpgwizard.editor.properties.EditorProperties;
import org.rpgwizard.editor.properties.EditorProperty;
import org.rpgwizard.editor.utilities.FileTools;
import org.rpgwizard.pluginsystem.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.JarPluginManager;
import ro.fortsoft.pf4j.PluginManager;

public class Driver {

	private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

	private static void redirectUncaughtExceptions() {
        try {
            Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
                LOGGER.error("Uncaught Exception detected in thread {}", t, e);
            });
        } catch (SecurityException e) {
            LOGGER.error("Could not set the Default Uncaught Exception Handler", e);
        }
    }
	private static void logSystemInfo() {
		LOGGER.info("---------------------------- System Info ----------------------------");
		LOGGER.info("Operating System: {}", System.getProperty("os.name"));
		LOGGER.info("System Architecture: {}", System.getProperty("os.arch"));
		LOGGER.info("Available Processors (cores): {}", Runtime.getRuntime()
				.availableProcessors());
		LOGGER.info("Free Memory (bytes): {}", Runtime.getRuntime()
				.freeMemory());
		LOGGER.info("Total Memory (bytes): {}", Runtime.getRuntime()
				.totalMemory());
		LOGGER.info("Max Memory (bytes): {}", Runtime.getRuntime().maxMemory());
		LOGGER.info("---------------------------------------------------------------------");
	}

	private static void registerResolvers() {
		LOGGER.debug("Registering asset resolvers.");

		AssetManager.getInstance().registerResolver(
				new FileAssetHandleResolver());
	}

	private static void registerSerializers() {
		LOGGER.debug("Registering asset serializers.");

		AssetManager assetManager = AssetManager.getInstance();

		// Legacy.
		assetManager.registerSerializer(new LegacyAnimatedTileSerializer());

		// JSON.
		assetManager.registerSerializer(new JsonAnimationSerializer());
		assetManager.registerSerializer(new JsonPlayerSerializer());
		assetManager.registerSerializer(new JsonBoardSerializer());
		assetManager.registerSerializer(new JsonProjectSerializer());
		assetManager.registerSerializer(new JsonSpecialMoveSerializer());
		assetManager.registerSerializer(new JsonEnemySerializer());
		assetManager.registerSerializer(new JsonItemSerializer());
		assetManager.registerSerializer(new TextProgramSerializer());
		assetManager.registerSerializer(new JsonTileSetSerializer());
	}

	private static PluginManager registerPlugins() throws URISyntaxException {
		String path = FileTools.getExecutionPath(Driver.class);

		path += File.separator
				+ EditorProperties
						.getProperty(EditorProperty.EDITOR_PLUGINS_DIRECOTRY)
				+ File.separator;

		System.setProperty("pf4j.pluginsDir", path);
		LOGGER.info(System.getProperty("pf4j.pluginsDir"));

		PluginManager pluginManager = new JarPluginManager();
		pluginManager.loadPlugins();
		pluginManager.startPlugins();

		return pluginManager;
	}

	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                LOGGER.info("Starting the RPGToolkit Editor...");
                redirectUncaughtExceptions();

                logSystemInfo();
                registerResolvers();
                registerSerializers();
                PluginManager pluginManager = registerPlugins();

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                MainWindow mainWindow = MainWindow.getInstance();
                mainWindow.setPluginManager(pluginManager);
                mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                mainWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent windowEvent) {
                        mainWindow.closeEditors();
                        mainWindow.dispose();

                        // Quietly stop any engines.
                        List<Engine> engines = pluginManager.getExtensions(Engine.class);
                        engines.forEach((engine) -> {
                            try {
                                engine.stop();
                            } catch (Exception ex) {
                                LOGGER.error("Failed to stop engine!", ex);
                            }
                        });

                        System.exit(0);
                    }
                });

                mainWindow.setVisible(true);
            } catch (ClassNotFoundException | InstantiationException
                    | IllegalAccessException | UnsupportedLookAndFeelException
                    | URISyntaxException ex) {
                LOGGER.error("Failed to start the editor!", ex);
            }
        });

    }
}
