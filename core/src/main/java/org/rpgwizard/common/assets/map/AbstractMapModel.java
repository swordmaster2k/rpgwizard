/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rpgwizard.common.assets.map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Iterator;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.common.assets.events.MapModelEvent;
import org.rpgwizard.common.assets.listeners.MapModelChangeListener;

/**
 *
 * @author Joshua Michael Daly
 */
@EqualsAndHashCode
@ToString(callSuper = true, includeFieldNames = true)
public class AbstractMapModel {

    @JsonIgnore
    protected LinkedList<MapModelChangeListener> changeListeners = new LinkedList<>();

    /**
     * Add a new <code>MapChangeListener</code> for this map.
     *
     * @param listener
     *            new change listener
     */
    public void addMapChangeListener(MapModelChangeListener listener) {
        changeListeners.add(listener);
    }

    /**
     * Remove an existing <code>MapChangeListener</code> for this map.
     *
     * @param listener
     *            change listener
     */
    public void removeMapChangeListener(MapModelChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * Fires the <code>MapModelEvent</code> informs all the listeners that this model has changed.
     */
    public void fireModelChanged() {
        MapModelEvent event = null;
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapModelEvent(this);
            }
            ((MapModelChangeListener) iterator.next()).modelChanged(event);
        }
    }

    /**
     * Fires the <code>MapModelEvent</code> informs all the listeners that this model has moved on the map.
     */
    public void fireModelMoved() {
        MapModelEvent event = null;
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            if (event == null) {
                event = new MapModelEvent(this);
            }
            ((MapModelChangeListener) iterator.next()).modelMoved(event);
        }
    }

}
