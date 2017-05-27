/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AnimatedTile;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.common.assets.Item;
import org.rpgwizard.common.assets.Player;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.common.assets.Project;
import org.rpgwizard.common.assets.SpecialMove;
import org.rpgwizard.common.assets.StatusEffect;
import org.rpgwizard.common.assets.TileSet;

/**
 *
 * @author Joshua Michael Daly
 */
public class CoreProperties {

	private static final CoreProperties INSTANCE = new CoreProperties();
	private final Properties properties = new Properties();
	private String[] directories;

	private CoreProperties() {
    try (InputStream in = CoreProperties.class.
            getResourceAsStream("/core/properties/toolkit.properties")) {
      properties.load(in);
      
      directories = new String[] {
      properties.getProperty("toolkit.directory.bitmap"),
      properties.getProperty("toolkit.directory.board"),
      properties.getProperty("toolkit.directory.character"),
      properties.getProperty("toolkit.directory.enemy"),
      properties.getProperty("toolkit.directory.item"),
      properties.getProperty("toolkit.directory.media"),
      properties.getProperty("toolkit.directory.misc"),
      properties.getProperty("toolkit.directory.program"),
      properties.getProperty("toolkit.directory.tileset")};
    }
    catch (IOException ex) {
      Logger.getLogger(CoreProperties.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
	public static String getProperty(String key) {
		return INSTANCE.properties.getProperty(key);
	}

	public static String getFullExtension(String key) {
		return "." + INSTANCE.properties.getProperty(key);
	}

	public static String getProjectsDirectory() {
		return System.getProperty("user.home") + File.separator
				+ INSTANCE.properties.getProperty("toolkit.directory.projects");
	}

	public static String[] getDirectories() {
		return INSTANCE.directories;
	}

	public static String getDefaultExtension(Class<? extends AbstractAsset> type) {
		if (type == Animation.class) {
			return getFullExtension("toolkit.animation.extension.default");
		} else if (type == AnimatedTile.class) {
			return getFullExtension("toolkit.animatedtile.extension.default");
		} else if (type == Board.class) {
			return getFullExtension("toolkit.board.extension.default");
		} else if (type == Enemy.class) {
			return getFullExtension("toolkit.enemy.extension.default");
		} else if (type == Item.class) {
			return getFullExtension("toolkit.item.extension.default");
		} else if (type == Player.class) {
			return getFullExtension("toolkit.character.extension.default");
		} else if (type == Project.class) {
			return getFullExtension("toolkit.project.extension.default");
		} else if (type == StatusEffect.class) {
			return getFullExtension("toolkit.statuseffect.extension.default");
		} else if (type == TileSet.class) {
			return getFullExtension("toolkit.tileset.extension.default");
		} else if (type == SpecialMove.class) {
			return getFullExtension("toolkit.specialmove.extension.default");
		} else if (type == Program.class) {
			return getFullExtension("toolkit.program.extension.default");
		} else {
			return null;
		}
	}

}
