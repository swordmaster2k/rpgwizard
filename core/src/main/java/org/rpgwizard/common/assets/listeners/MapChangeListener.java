/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.listeners;

import org.rpgwizard.common.assets.events.MapChangedEvent;
import java.util.EventListener;

/**
 * Implementors of this interface will use the contained method definitions to inform their listeners of new event on a
 * <code>Map</code>.
 * 
 * @author Joshua Michael Daly
 */
public interface MapChangeListener extends EventListener {
    /**
     * A general map changed event.
     * 
     * @param e
     */
    public void mapChanged(MapChangedEvent e);

    /**
     * A new layer has been added to the map.
     * 
     * @param e
     */
    public void mapLayerAdded(MapChangedEvent e);

    /**
     * A layer has been moved up on this map.
     * 
     * @param e
     */
    public void mapLayerMovedUp(MapChangedEvent e);

    /**
     * A layer has been moved down on this map.
     * 
     * @param e
     */
    public void mapLayerMovedDown(MapChangedEvent e);

    /**
     * A layer has been cloned on this map.
     * 
     * @param e
     */
    public void mapLayerCloned(MapChangedEvent e);

    /**
     * A layer has been deleted on this map.
     * 
     * @param e
     */
    public void mapLayerDeleted(MapChangedEvent e);

    /**
     * A MapSprite has been added.
     * 
     * @param e
     */
    public void mapSpriteAdded(MapChangedEvent e);

    /**
     * A MapSprite has been removed.
     * 
     * @param e
     */
    public void mapSpriteRemoved(MapChangedEvent e);

    /**
     * A MapLayerImage has been added.
     * 
     * @param e
     */
    public void mapImageAdded(MapChangedEvent e);

    /**
     * A MapLayerImage has been removed.
     * 
     * @param e
     */
    public void mapImageRemoved(MapChangedEvent e);

}
