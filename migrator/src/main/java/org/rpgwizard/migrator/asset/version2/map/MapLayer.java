/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.version2.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Data;
import org.rpgwizard.migrator.asset.version2.Collider;
import org.rpgwizard.migrator.asset.version2.Trigger;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
public class MapLayer {
    
    private String id;
    private List<String> tiles;
    private java.util.Map<String, Collider> colliders;
    private java.util.Map<String, Trigger> triggers;
    private java.util.Map<String, MapSprite> sprites;
    private java.util.Map<String, MapImage> images;

    public MapLayer() {
        tiles = new ArrayList<>();
        colliders = new HashMap<>();
        triggers = new HashMap<>();
        sprites = new HashMap<>();
        images = new HashMap<>();
    }
    
}
