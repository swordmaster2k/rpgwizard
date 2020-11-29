/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.tileset.Tile;

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
    
    @JsonIgnore
    private Tile[][] loadedTiles;
    @JsonIgnore
    private Map map;
    @JsonIgnore
    private boolean visible;
    @JsonIgnore
    private boolean locked;
    @JsonIgnore
    private float opacity;

    public MapLayer() {
        id = UUID.randomUUID().toString();
        tiles = new ArrayList<>();
        colliders = new HashMap<>();
        triggers = new HashMap<>();
        sprites = new HashMap<>();
        images = new HashMap<>();
        
        visible = true;
        locked = false;
        opacity = 1.0f;
    }
 
    public MapLayer(Map map) {
        this();
        
        this.map = map;
        this.loadedTiles = new Tile[map.getWidth()][map.getHeight()];
        
        clearTiles();
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
    
    ////////////////////////////////////////////////////////////////////////////
    // Model Operations
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Clears this layers tiles.
     */
    private void clearTiles() {
        int count = map.getWidth() * map.getHeight();
        Tile blankTile = new Tile();
        int x = 0;
        int y = 0;

        for (int i = 0; i < count; i++) {
            loadedTiles[x][y] = blankTile;

            x++;
            if (x == map.getWidth()) {
                x = 0;
                y++;
                if (y == map.getHeight()) {
                    break;
                }
            }
        }
    }
    
    /**
     * Gets the tile at the specified coordinates.
     *
     * @param x x position of tile
     * @param y y position of tile
     * @return the tile
     */
    public Tile getTileAt(int x, int y) {
        return loadedTiles[x][y];
    }

    /**
     * Sets the tile at the specified coordinates
     *
     * @param x x position of tile
     * @param y y position of tile
     * @param tile the tile
     * @return
     */
    public boolean setTileAt(int x, int y, Tile tile) {
        if (x >= 0 && x < loadedTiles.length) {
            if (y >= 0 && y < loadedTiles[x].length) {
                if (!loadedTiles[x][y].equals(tile)) {
                    loadedTiles[x][y] = tile;
                    map.fireMapChanged();
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Pours the tile at the specified coordinates
     *
     * @param x x position of tile
     * @param y y position of tile
     * @param tile the tile
     * @return
     */
    public boolean pourTileAt(int x, int y, Tile tile) {
        if (x >= 0 && x < loadedTiles.length) {
            if (y >= 0 && y < loadedTiles[x].length) {
                if (!loadedTiles[x][y].equals(tile)) {
                    loadedTiles[x][y] = tile;
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Does this layer contain the coordinates.
     *
     * @param x x position
     * @param y y position
     * @return true = yes, false = no
     */
    public boolean contains(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }

        return x < loadedTiles.length && y < loadedTiles[0].length;
    }


}
