/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.tileset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;

/**
 * This class is responsible for managing a tilset inside the editor It stores all of the tiles in the set in a big
 * Array of tiles!
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Tileset extends AbstractAsset {

    private String name;
    private int tileWidth;
    private int tileHeight;
    private String image;
    private Map<String, Map<String, String>> tileData;

    @JsonIgnore
    private BufferedImage bufferedImage;
    @JsonIgnore
    private LinkedList<Tile> tiles;

    public Tileset() {
        tiles = new LinkedList<>();
        tileData = new HashMap<>();
    }

    public Tileset(AssetDescriptor descriptor) {
        super(descriptor);
        image = null;
        tiles = new LinkedList<>();
        tileData = new HashMap<>();
    }

    /**
     * Creates a new TileSet with a descriptor.
     *
     * @param descriptor
     * @param tileWidth
     * @param tileHeight
     */
    public Tileset(AssetDescriptor descriptor, int tileWidth, int tileHeight) {
        super(descriptor);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        image = null;
        tiles = new LinkedList<>();
        tileData = new HashMap<>();
    }

    /**
     * Gets a tile from a specified location in the array.
     *
     * @param index
     *            Index of the array to get the tile from
     * @return Tile object representing the tile from the requested index
     */
    @JsonIgnore
    public Tile getTile(int index) {
        return tiles.get(index);
    }

    @JsonIgnore
    public int getTileIndex(Tile tile) {
        return tiles.indexOf(tile);
    }

    /**
     * Adds a new tile to the tiles, it will add the tile at the end of the array
     *
     * @param newTile
     *            Tile object to add to the array
     */
    public void addTile(Tile newTile) {
        tiles.add(newTile);
    }

    /**
     * Add metadata about a tile to this tileset.
     * 
     * @param index
     * @param data
     */
    public void addTileData(int index, Map<String, String> data) {
        tileData.put(String.valueOf(index), data);
    }

    /**
     * Read the tile metadata for the specific index, if any.
     * 
     * @param index
     * @return
     */
    public Map<String, String> readTileData(int index) {
        if (tileData.containsKey(String.valueOf(index))) {
            return tileData.get(String.valueOf(index));
        }
        return Map.of();
    }

}
