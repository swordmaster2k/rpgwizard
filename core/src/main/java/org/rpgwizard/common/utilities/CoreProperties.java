/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
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
import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.common.assets.Character;
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
      properties.getProperty("toolkit.directory.graphics"),
      properties.getProperty("toolkit.directory.board"),
      properties.getProperty("toolkit.directory.character"),
      properties.getProperty("toolkit.directory.enemy"),
      properties.getProperty("toolkit.directory.npc"),
      properties.getProperty("toolkit.directory.sounds"),
      properties.getProperty("toolkit.directory.animations"),
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
		} else if (type == NPC.class) {
			return getFullExtension("toolkit.npc.extension.default");
		} else if (type == Character.class) {
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
