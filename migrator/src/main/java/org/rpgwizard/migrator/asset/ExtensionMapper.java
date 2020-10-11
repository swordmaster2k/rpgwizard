/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Joshua Michael Daly
 */
@Slf4j
public class ExtensionMapper {
    
    public static String map(String path) {
        if (path.endsWith(".animation")) {
            return path.replace(".animation", ".animation");
        } else if (path.endsWith(".board")) {
            return path.replace(".board", ".map");
        } else if (path.endsWith(".character")) {
            return path.replace(".character", ".sprite");
        } else if (path.endsWith(".enemy")) {
            return path.replace(".enemy", ".sprite");
        } else if (path.endsWith(".npc")) {
            return path.replace(".npc", ".sprite");
        } else if (path.endsWith(".tileset")) {
            return path.replace(".tileset", ".tileset");
        }
        return path;
    }
    
}
