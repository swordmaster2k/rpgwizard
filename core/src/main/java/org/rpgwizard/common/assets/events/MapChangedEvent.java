/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.events;

import java.util.EventObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.common.assets.map.Map;
import org.rpgwizard.common.assets.map.MapImage;
import org.rpgwizard.common.assets.map.MapLayer;
import org.rpgwizard.common.assets.map.MapSprite;

/**
 * An <code>EventObject</code> used to contain information of a change that has happened on a map.
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class MapChangedEvent extends EventObject {

    private int layerIndex;
    private MapLayer layer;
    private MapSprite MapSprite;
    private MapImage MapImage;

    private boolean opacityChanged;
    private boolean layerVisibilityToggled;

    /**
     * Creates a new event.
     *
     * @param map
     *            map the event happened on
     */
    public MapChangedEvent(Map map) {
        super(map);
    }

}
