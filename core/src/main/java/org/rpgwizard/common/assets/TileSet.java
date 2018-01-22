/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is responsible for managing a tilset inside the editor It stores all of the tiles in the set in a big
 * Array of tiles!
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class TileSet extends AbstractAsset {

    private String name;

    private int tileWidth;
    private int tileHeight;

    private String image;
    private BufferedImage bufferedImage;

    private LinkedList<Tile> tiles;

    public TileSet() {
        super(null);
        image = null;
        tiles = new LinkedList<>();
    }

    /**
     * Creates a new TileSet with a descriptor.
     *
     * @param descriptor
     * @param tileWidth
     * @param tileHeight
     */
    public TileSet(AssetDescriptor descriptor, int tileWidth, int tileHeight) {
        super(descriptor);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        image = null;
        tiles = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets a tile from a specified location in the array.
     *
     * @param index
     *            Index of the array to get the tile from
     * @return Tile object representing the tile from the requested index
     */
    public Tile getTile(int index) {
        return tiles.get(index);
    }

    public int getTileIndex(Tile tile) {
        return tiles.indexOf(tile);
    }

    /**
     * Returns an array of all the tiles in the tiles
     *
     * @return Object array of all the tiles in the tiles
     */
    public LinkedList<Tile> getTiles() {
        return tiles;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public void setTiles(LinkedList<Tile> tiles) {
        this.tiles = tiles;
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

}
