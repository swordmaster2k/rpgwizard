/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.common.Selectable;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.Location;
import org.rpgwizard.common.assets.events.MapChangedEvent;
import org.rpgwizard.common.assets.listeners.MapChangeListener;
import org.rpgwizard.common.assets.tileset.Tileset;
import org.rpgwizard.common.utilities.TileSetCache;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public final class Map extends AbstractAsset implements Selectable {

    private String name;
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private String music;
    private List<String> tilesets;
    private String entryScript;
    private Location startLocation;
    private List<MapLayer> layers;

    @JsonIgnore
    protected LinkedList<MapChangeListener> changeListeners = new LinkedList<>();
    @JsonIgnore
    private boolean selected; // TODO: This is editor specific, move it!

    public Map() {
        name = "";
        music = "";
        entryScript = "";
        tilesets = new ArrayList<>();
        startLocation = new Location();
        layers = new ArrayList<>();
    }

    public Map(AssetDescriptor descriptor) {
        this();

        this.descriptor = descriptor;
    }

    public Map(AssetDescriptor descriptor, int width, int height, int tileWidth, int tileHeight) {
        this();

        this.descriptor = descriptor;
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        startLocation.setX((width * tileWidth) / 2);
        startLocation.setY((height * tileHeight) / 2);

        addLayer();
    }

    /**
     * Copy constructor.
     * 
     * @param map
     */
    public Map(Map map) {

    }

    ////////////////////////////////////////////////////////////////////////////
    // Getters & Setters
    ////////////////////////////////////////////////////////////////////////////

    @JsonIgnore
    public List<String> getLayerIds() {
        List<String> ids = new ArrayList<>();
        layers.forEach(layer -> {
            ids.add(layer.getId());
        });
        return ids;
    }

    @JsonIgnore
    public String getLayerId(int index) {
        return layers.get(index).getId();
    }

    @JsonIgnore
    public void setLayerId(int index, String id) {
        layers.get(index).setId(id);
        fireMapChanged();
    }

    // REFACTOR: setTilesets?

    /**
     * Gets the maps width and height in pixels.
     * 
     * @return
     */
    @JsonIgnore
    public Dimension getMapPixelDimensions() {
        return new Dimension(width * tileWidth, height * tileHeight);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Model Operations
    ////////////////////////////////////////////////////////////////////////////

    public void init() {
        loadTiles();
        loadSprites();
    }

    private void loadTiles() {
        for (MapLayer layer : layers) {
            int count = width * height;
            int x = 0;
            int y = 0;
            layer.init(this);
            List<String> tilePointers = layer.getTiles();
            for (int j = 0; j < count; j++) {
                // In the form "tileset-idx:tile-idx"
                String[] parts = tilePointers.get(j).split(":");

                int tilesetIdx = Integer.parseInt(parts[0]);
                int tileIdx = Integer.parseInt(parts[1]);
                if (tilesetIdx < 0 || tileIdx < 0) {
                    layer.getLoadedTiles()[x][y] = null; // Blank tile
                } else {
                    Tileset tileset = TileSetCache.getTileSet(getTilesets().get(tilesetIdx));
                    layer.getLoadedTiles()[x][y] = tileset.getTile(tileIdx);
                }

                x++;
                if (x == width) {
                    x = 0;
                    y++;
                    if (y == height) {
                        break;
                    }
                }
            }
        }
    }

    private void loadSprites() {
        for (MapLayer layer : layers) {
            for (MapSprite sprite : layer.getSprites().values()) {
                sprite.prepareSprite();
            }
        }
    }

    /**
     * Add a new blank layer to this map.
     */
    public void addLayer() {
        MapLayer layer = new MapLayer(this);
        layer.setId("Untitled Layer " + layers.size());
        layers.add(layer);

        fireMapLayerAdded(layer);
    }

    public void addSprite(int layer, String id, MapSprite sprite) {
        MapLayer mapLayer = layers.get(layer);
        mapLayer.getSprites().put(id, sprite);
        fireMapSpriteAdded(sprite);
    }

    public MapSprite removeSprite(int layer, String id) {
        MapLayer mapLayer = layers.get(layer);
        MapSprite sprite = mapLayer.getSprites().remove(id);
        fireMapSpriteRemoved(sprite);
        return sprite;
    }

    public void addLayerImage(int layer, String id, MapImage image) {
        MapLayer mapLayer = layers.get(layer);
        mapLayer.getImages().put(id, image);
        fireMapImageAdded(image);
    }

    public MapImage removeLayerImage(int layer, String id) {
        MapLayer mapLayer = layers.get(layer);
        MapImage image = mapLayer.getImages().remove(id);
        fireMapImageRemoved(image);
        return image;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Layer Actions
    ////////////////////////////////////////////////////////////////////////////

    public boolean moveLayerUp(int index) {
        // Highest possible index, can't be move up!
        if (index == layers.size()) {
            return false;
        }

        MapLayer down = layers.get(index + 1);
        MapLayer up = layers.get(index);
        layers.set(index + 1, up);
        layers.set(index, down);

        fireMapLayerMovedUp(up);

        return true;
    }

    public boolean moveLayerDown(int index) {
        // Lowest possible layer, can't be move down!
        if (index == 0) {
            return false;
        }

        MapLayer down = layers.get(index);
        MapLayer up = layers.get(index - 1);
        layers.set(index - 1, down);
        layers.set(index, up);

        fireMapLayerMovedDown(down);

        return true;
    }

    public void cloneLayer(int index) {
        MapLayer clone = new MapLayer(layers.get(index));
        layers.add(index + 1, clone);

        fireMapLayerCloned(clone);
    }

    public void deleteLayer(int index) {
        MapLayer removedLayer = layers.get(index);
        layers.remove(index);

        fireMapLayerDeleted(removedLayer);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Selection Listeners
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Is this map selected in the editor?
     *
     * @return selected state
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set the selected state of this map in the editor
     *
     * @param state
     *            new state
     */
    @Override
    public void setSelectedState(boolean state) {
        selected = state;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Listeners & Events
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Add a new <code>MapChangeListener</code> for this map.
     *
     * @param listener
     *            new change listener
     */
    public void addMapChangeListener(MapChangeListener listener) {
        changeListeners.add(listener);
    }

    /**
     * Remove an existing <code>MapChangeListener</code> for this map.
     *
     * @param listener
     *            change listener
     */
    public void removeMapChangeListener(MapChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     */
    public void fireMapChanged() {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
            }

            ((MapChangeListener) iterator.next()).mapChanged(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     * 
     * @param event
     */
    public void fireMapChanged(MapChangedEvent event) {
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            ((MapChangeListener) iterator.next()).mapChanged(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param layer
     *            new layer
     */
    public void fireMapLayerAdded(MapLayer layer) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setLayer(layer);
            }

            ((MapChangeListener) iterator.next()).mapLayerAdded(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param layer
     *            effected layer
     */
    public void fireMapLayerMovedUp(MapLayer layer) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setLayer(layer);
            }

            ((MapChangeListener) iterator.next()).mapLayerMovedUp(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param layer
     *            effected layer
     */
    public void fireMapLayerMovedDown(MapLayer layer) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setLayer(layer);
            }

            ((MapChangeListener) iterator.next()).mapLayerMovedDown(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param layer
     *            cloned layer
     */
    public void fireMapLayerCloned(MapLayer layer) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setLayer(layer);
            }

            ((MapChangeListener) iterator.next()).mapLayerCloned(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param layer
     *            deleted layer
     */
    public void fireMapLayerDeleted(MapLayer layer) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setLayer(layer);
            }

            ((MapChangeListener) iterator.next()).mapLayerDeleted(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param sprite
     */
    public void fireMapSpriteAdded(MapSprite sprite) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setMapSprite(sprite);
            }

            ((MapChangeListener) iterator.next()).mapSpriteAdded(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param sprite
     */
    public void fireMapSpriteRemoved(MapSprite sprite) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setMapSprite(sprite);
            }

            ((MapChangeListener) iterator.next()).mapSpriteRemoved(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param image
     */
    public void fireMapImageAdded(MapImage image) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setMapImage(image);
            }

            ((MapChangeListener) iterator.next()).mapImageAdded(event);
        }
    }

    /**
     * Fires the <code>MapChangedEvent</code> informs all the listeners that this map has changed.
     *
     * @param image
     */
    public void fireMapImageRemoved(MapImage image) {
        MapChangedEvent event = null;
        Iterator iterator = changeListeners.iterator();

        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapChangedEvent(this);
                event.setMapImage(image);
            }

            ((MapChangeListener) iterator.next()).mapImageRemoved(event);
        }
    }

}
