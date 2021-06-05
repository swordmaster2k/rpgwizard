/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.SystemUtils;
import org.pf4j.JarPluginManager;
import org.pf4j.PluginManager;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.files.FileAssetHandleResolver;
import org.rpgwizard.common.assets.serialization.ImageSerializer;
import org.rpgwizard.common.assets.serialization.JsonAnimationSerializer;
import org.rpgwizard.common.assets.serialization.JsonGameSerializer;
import org.rpgwizard.common.assets.serialization.JsonMapSerializer;
import org.rpgwizard.common.assets.serialization.JsonSpriteSerializer;
import org.rpgwizard.common.assets.serialization.JsonTilesetSerializer;
import org.rpgwizard.common.assets.serialization.ScriptSerializer;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.properties.EditorProperties;
import org.rpgwizard.editor.properties.EditorProperty;
import org.rpgwizard.editor.properties.user.UserPreference;
import org.rpgwizard.editor.properties.user.UserPreferencesProperties;
import org.rpgwizard.editor.ui.Theme;
import org.rpgwizard.editor.utilities.FileTools;
import org.rpgwizard.editor.utilities.ProjectUpgrader;
import org.rpgwizard.pluginsystem.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        LOGGER.info("Java Version: {}", System.getProperty("java.version"));
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
        assetManager.registerSerializer(new JsonGameSerializer());
        assetManager.registerSerializer(new JsonMapSerializer());
        assetManager.registerSerializer(new JsonSpriteSerializer());
        assetManager.registerSerializer(new ScriptSerializer());
        assetManager.registerSerializer(new JsonTilesetSerializer());
        assetManager.registerSerializer(new ImageSerializer());
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

    public static void loadUserTheme() {
        Theme theme = Theme
                .valueOf(UserPreferencesProperties.getProperty(UserPreference.USER_PREFERENCE_THEME).toUpperCase());
        switch (theme) {
        case LIGHT:
            FlatLightLaf.install();
            break;
        case DARK:
        default:
            FlatDarkLaf.install();
        }
    }

    public static void loadLastProject() {
        String lastProject = UserPreferencesProperties.getProperty(UserPreference.LAST_OPEN_PROJECT);
        File file = new File(lastProject);
        if (lastProject.equals("Sample")) {
            // Load default from "projects" directory.
            file = new File(FileTools.getProjectsDirectory() + File.separator + "Sample" + File.separator + "default"
                    + CoreProperties.getDefaultExtension(Game.class));
        }
        if (file.exists()) {
            MainWindow mainWindow = MainWindow.getInstance();
            Game project = mainWindow.openProject(file);
            if (project != null) {
                mainWindow.setProjectPath(file.getParent());
                ProjectUpgrader.upgrade(file.getParentFile());
                mainWindow.setupProject(project, false);
            } else {
                LOGGER.warn("Could not find previous, lastProject=[{}]", lastProject);
            }
        }
    }

    public static void saveLastProject() {
        Game project = MainWindow.getInstance().getActiveProject();
        if (project != null && project.getFile() != null) {
            String lastProject = project.getFile().getAbsolutePath();
            LOGGER.info("Saving last open project, lastProject=[{}]", lastProject);
            UserPreferencesProperties.setProperty(UserPreference.LAST_OPEN_PROJECT, lastProject);
        }
    }

    public static void addLibraryPath(String pathToAdd) throws Exception {
        String path = FileTools.getExecutionPath(Driver.class);
        path += File.separator + pathToAdd;
        System.setProperty("java.library.path", path);
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            LOGGER.info("Starting the RPGWizard Editor...");
            redirectUncaughtExceptions();
            logSystemInfo();

            SplashScreen splashScreen = new SplashScreen();
            splashScreen.display();

            SwingUtilities.invokeLater(() -> {
                try {
                    registerResolvers();
                    registerSerializers();
                    PluginManager pluginManager = registerPlugins();
                    loadUserTheme();

                    // Add the correct lib based on the platform.
                    if (SystemUtils.IS_OS_WINDOWS) {
                        addLibraryPath("lib/jcef-win");
                    }

                    MainWindow mainWindow = MainWindow.getInstance();
                    mainWindow.setPluginManager(pluginManager);
                    mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    mainWindow.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                            splashScreen.dispose();
                            UserPreferencesProperties.apply();
                            for (Window window : Window.getWindows()) {
                                SwingUtilities.updateComponentTreeUI(window);
                            }
                        }

                        @Override
                        public void windowClosing(WindowEvent windowEvent) {
                            if (!mainWindow.tearDown()) {
                                LOGGER.info("User cancelled close!");
                                return;
                            }
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
                            saveLastProject();
                            UserPreferencesProperties.save();
                            LOGGER.info("Stopping the RPGWizard Editor...");
                        }
                    });

                    try {
                        loadLastProject();
                    } catch (Exception ex) {
                        LOGGER.error("Failed to open last project! reason=[{}]", ex.getMessage());
                    }

                    mainWindow.setVisible(true);
                } catch (Exception ex) {
                    LOGGER.error("Failed to start the editor!", ex);
                }
            });
        });

    }
}
