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
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.Location;
/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Map extends AbstractAsset {
    
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
    
    public Map() {
        tilesets = new ArrayList<>();
        startLocation = new Location();
        layers = new ArrayList<>();
    }
    
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
     * Fires the <code>MapEvent</code> informs all the listeners that this map has changed.
     */
    public void fireModelChanged() {
        MapEvent event = null;
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapEvent(this);
            }
            ((MapChangeListener) iterator.next()).modelChanged(event);
        }
    }

    /**
     * Fires the <code>MapEvent</code> informs all the listeners that this model has moved on the map.
     */
    public void fireModelMoved() {
        MapEvent event = null;
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapEvent(this);
            }
            ((MapChangeListener) iterator.next()).modelMoved(event);
        }
    }
    
}
