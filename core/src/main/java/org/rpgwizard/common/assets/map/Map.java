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
import org.rpgwizard.common.assets.tileset.Tile;
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
    private boolean selectedState; // TODO: This is editor specific, move it!

    public Map() {
        name = "";
        music = "";
        entryScript = "";
        tilesets = new ArrayList<>();
        startLocation = new Location();
        layers = new ArrayList<>();
    }
    
    public Map(AssetDescriptor descriptor, int width, int height, int tileWidth, int tileHeight) {
        this();
        
        this.descriptor = descriptor;
        this.width = width;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        
        startLocation.setX((width * tileWidth) / 2);
        startLocation.setY((height * tileHeight) / 2);
        
        addLayer();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Model Operations
    ////////////////////////////////////////////////////////////////////////////
    
    public void loadTiles() {
        for (MapLayer layer : layers) {
            int count = width * height;
            int x = 0;
            int y = 0;
            Tile[][] tiles = layer.getLoadedTiles();
            for (int j = 0; j < count; j++) {
                Tile tile = tiles[x][y];

                if (tile.getTileSet() != null) {
                    // When first loaded they'll only have the name.
                    tile.setTileSet(TileSetCache.getTileSet(tile.getTileSet().getName()));
                    tiles[x][y] = tile.getTileSet().getTile(tile.getIndex());
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
    
    /**
     * Add a new blank layer to this map.
     */
    public void addLayer() {
        MapLayer layer = new MapLayer();
        layer.setId("Untitled Layer " + layers.size());
        layers.add(layer);

        fireMapLayerAdded(layer);
    }
    
    public void addSprite(int layer, String id, MapSprite sprite) {
        MapLayer mapLayer = layers.get(layer);
        mapLayer.getSprites().put(id, sprite);
        fireMapSpriteAdded(sprite);
    }

    public void removeSprite(int layer, String id) {
        MapLayer mapLayer = layers.get(layer);
        MapSprite sprite = mapLayer.getSprites().remove(id);
        fireMapSpriteRemoved(sprite);
    }

    public void addLayerImage(int layer, String id, MapImage image) {
        MapLayer mapLayer = layers.get(layer);
        mapLayer.getImages().put(id, image);
        fireMapImageAdded(image);
    }

    public void removeLayerImage(int layer, String id) {
        MapLayer mapLayer = layers.get(layer);
        MapImage image = mapLayer.getImages().remove(id);
        fireMapImageRemoved(image);
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
        return selectedState;
    }

    /**
     * Set the selected state of this map in the editor
     *
     * @param state
     *            new state
     */
    @Override
    public void setSelectedState(boolean state) {
        selectedState = state;
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
