/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.properties.user;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.actions.EastPanelAction;
import org.rpgwizard.editor.ui.actions.SouthPanelAction;
import org.rpgwizard.editor.utilities.FileTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class UserPreferencesProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesProperties.class);

    private static final String PREFERENCES_FILE = "preferences.properties";
    private static final UserPreferencesProperties INSTANCE = new UserPreferencesProperties();
    private Properties properties = new Properties();
    private File preferences;

    private UserPreferencesProperties() {
        try {
            preferences = new File(
                    FileTools.getExecutionPath(UserPreferencesProperties.class) + File.separator + PREFERENCES_FILE);
            if (!preferences.exists()) {
                setup(preferences, properties);
            } else {
                try (InputStream in = new FileInputStream(preferences)) {
                    properties.load(in);
                }
            }
        } catch (IOException | URISyntaxException ex) {
            LOGGER.error("Failed to load user properties file.", ex);
            properties = null;
        }
    }

    public static String getProperty(UserPreference property) {
        if (INSTANCE.properties == null) {
            return property.getDefaultValue();
        }
        if (!INSTANCE.properties.containsKey(property.toString())) {
            INSTANCE.properties.setProperty(property.toString(), property.getDefaultValue());
        }
        return INSTANCE.properties.getProperty(property.toString());
    }

    public static void setProperty(UserPreference property, String value) {
        if (INSTANCE.properties == null) {
            return;
        }
        INSTANCE.properties.setProperty(property.toString(), value);
    }

    public static void save(File preferences, Properties properties) {
        if (preferences != null && properties != null) {
            LOGGER.info("Saving user preferences=[{}], properties=[{}]", preferences.getAbsolutePath(), properties);
            try (FileOutputStream out = new FileOutputStream(preferences)) {
                properties.store(out, null);
            } catch (IOException ex) {
                LOGGER.error("Failed to save user properties file.", ex);
            }
        } else {
            LOGGER.error("Could not save user preferences=[{}], properties=[{}]", preferences, properties);
        }
    }

    public static void save() {
        UserPreferencesProperties.save(INSTANCE.preferences, INSTANCE.properties);
    }

    public static void apply() {
        if (UserPreferencesProperties.getProperty(UserPreference.EAST_PANEL_ACTIVE).equals("true")) {
            new EastPanelAction().actionPerformed(new ActionEvent(MainWindow.getInstance(), 0, ""));
        }
        if (UserPreferencesProperties.getProperty(UserPreference.SOUTH_PANEL_ACTIVE).equals("false")) {
            new SouthPanelAction().actionPerformed(new ActionEvent(MainWindow.getInstance(), 0, ""));
        }
    }

    private static void setup(File preferences, Properties properties) throws FileNotFoundException, IOException {
        preferences.createNewFile();
        try (InputStream in = new FileInputStream(preferences)) {
            properties.load(in);
            properties.setProperty(UserPreference.USER_PREFERENCE_THEME.toString(), "DARK");
        }
        save(preferences, properties);
    }
}
