/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.common.assets.game.Game;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.common.assets.tileset.Tileset;

/**
 *
 * @author Joshua Michael Daly
 */
public class CoreProperties {

    private static final CoreProperties INSTANCE = new CoreProperties();
    private final Properties properties = new Properties();
    private String[] directories;

    private CoreProperties() {
        try (InputStream in = CoreProperties.class.getResourceAsStream("/core/properties/rpgwizard.properties")) {
            properties.load(in);

            directories = new String[] { properties.getProperty("rpgwizard.directory.textures"),
                    properties.getProperty("rpgwizard.directory.map"),
                    properties.getProperty("rpgwizard.directory.sounds"),
                    properties.getProperty("rpgwizard.directory.animations"),
                    properties.getProperty("rpgwizard.directory.script"),
                    properties.getProperty("rpgwizard.directory.tilesets"),
                    properties.getProperty("rpgwizard.directory.fonts"),
                    properties.getProperty("rpgwizard.directory.sprites") };
        } catch (IOException ex) {
            Logger.getLogger(CoreProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getProperty(String key) {
        return INSTANCE.properties.getProperty(key);
    }

    public static String getFullExtension(String key) {
        return "." + INSTANCE.properties.getProperty(key);
    }

    public static String[] getDirectories() {
        return INSTANCE.directories;
    }

    public static String getDefaultExtension(Class<? extends AbstractAsset> type) {
        if (type == Animation.class) {
            return getFullExtension("rpgwizard.animation.extension.default");
        } else if (type == Map.class) {
            return getFullExtension("rpgwizard.map.extension.default");
        } else if (type == Sprite.class) {
            return getFullExtension("rpgwizard.sprite.extension.default");
        } else if (type == Game.class) {
            return getFullExtension("rpgwizard.project.extension.default");
        } else if (type == Tileset.class) {
            return getFullExtension("rpgwizard.tileset.extension.default");
        } else if (type == Script.class) {
            return getFullExtension("rpgwizard.script.extension.default");
        } else {
            return null;
        }
    }

}
