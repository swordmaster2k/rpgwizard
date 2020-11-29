/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.Trigger;

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
        id = UUID.randomUUID().toString();
        tiles = new ArrayList<>();
        colliders = new HashMap<>();
        triggers = new HashMap<>();
        sprites = new HashMap<>();
        images = new HashMap<>();
    }
    
    /**
     * Copy constructor.
     *
     * @param mapLayer
     */
    public MapLayer(MapLayer mapLayer) {
        this();
        
        this.id = mapLayer.id;
        mapLayer.tiles.forEach(s -> {
            tiles.add(s);
        });
        mapLayer.colliders.entrySet().forEach(e -> {
            colliders.put(e.getKey(), new Collider(e.getValue()));
        });
        mapLayer.triggers.entrySet().forEach(e -> {
            triggers.put(e.getKey(), new Trigger(e.getValue()));
        });
        mapLayer.sprites.entrySet().forEach(e -> {
            sprites.put(e.getKey(), new MapSprite(e.getValue()));
        });
        mapLayer.images.entrySet().forEach(e -> {
            images.put(e.getKey(), new MapImage(e.getValue()));
        });
    }
    
}
