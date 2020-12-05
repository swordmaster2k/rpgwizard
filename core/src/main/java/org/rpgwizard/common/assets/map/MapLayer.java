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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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

        this.loadedTiles = mapLayer.loadedTiles.clone();
        this.map = mapLayer.map;
        this.visible = mapLayer.visible;
        this.locked = mapLayer.locked;
        this.opacity = mapLayer.opacity;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Getters & Setters
    ////////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public int getNumber() {
        // REFACTOR: Optimise me
        for (int i = 0; i < map.getLayers().size(); i++) {
            MapLayer layer = map.getLayers().get(i);
            if (id.equals(layer.id)) {
                return i;
            }
        }
        return -1;
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
     * @param x
     *            x position of tile
     * @param y
     *            y position of tile
     * @return the tile
     */
    public Tile getTileAt(int x, int y) {
        return loadedTiles[x][y];
    }

    /**
     * Sets the tile at the specified coordinates
     *
     * @param x
     *            x position of tile
     * @param y
     *            y position of tile
     * @param tile
     *            the tile
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
     * @param x
     *            x position of tile
     * @param y
     *            y position of tile
     * @param tile
     *            the tile
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
     * @param x
     *            x position
     * @param y
     *            y position
     * @return true = yes, false = no
     */
    public boolean contains(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }

        return x < loadedTiles.length && y < loadedTiles[0].length;
    }

    public void findColliderAt(int x, int y) {
        // REFACTOR: implement me
    }

    public void removeColliderAt(int x, int y) {
        // REFACTOR: implement me
    }

    public void removeCollider(Collider collider) {

    }

    public void findTriggerAt(int x, int y) {
        // REFACTOR: implement me
    }

    public void removeTriggerAt(int x, int y) {
        // REFACTOR: implement me
    }

    public void removeTrigger(Trigger trigger) {
        // REFACTOR: implement me
    }

    public Pair<String, MapSprite> findSpriteAt(int x, int y, int width, int height) {
        for (String spriteId : sprites.keySet()) {
            MapSprite sprite = sprites.get(spriteId);

            // REFACTOR: Keep this?
            // BufferedImage image = sprite.getSouthImage();
            // if (image != null) {
            // width = image.getWidth();
            // height = image.getHeight();
            // }

            int x1 = sprite.getStartLocation().getX() - width / 2;
            int y1 = sprite.getStartLocation().getY() - height / 2;
            int x2 = x1 + width;
            int y2 = y1 + height;
            if (x1 < x && x < x2 && y1 < y && y < y2) {
                return new ImmutablePair<>(spriteId, sprite);
            }
        }

        return null;
    }

    public Pair<String, MapSprite> removeSpriteAt(int x, int y, int width, int height) {
        Pair<String, MapSprite> pair = findSpriteAt(x, y, width, height);

        if (pair != null) {
            sprites.remove(pair.getKey());
            map.fireMapChanged();
        }

        return pair;
    }

    public Pair<String, MapImage> findImageAt(int x, int y, int width, int height) {
        for (String imageId : images.keySet()) {
            MapImage mapImage = images.get(imageId);

            // REFACTOR: Keep this?
            // BufferedImage image = layerImage.getImage();
            // if (image != null) {
            // width = image.getWidth();
            // height = image.getHeight();
            // }

            int x1 = mapImage.getX();
            int y1 = mapImage.getY();
            int x2 = x1 + width;
            int y2 = y1 + height;
            if (x1 < x && x < x2 && y1 < y && y < y2) {
                return new ImmutablePair<>(imageId, mapImage);
            }
        }

        return null;
    }

    public Pair<String, MapImage> removeImageAt(int x, int y, int width, int height) {
        Pair<String, MapImage> pair = findImageAt(x, y, width, height);

        if (pair != null) {
            images.remove(pair.getKey());
            map.fireMapChanged();
        }

        return pair;
    }

}
