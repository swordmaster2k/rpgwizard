/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.map;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.rpgwizard.common.assets.Event;
import org.rpgwizard.common.assets.Location;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
public class MapSprite {
    
    private String asset;
    private String thread;
    private Location startLocation;
    private List<Event> events;
    
    public MapSprite() {
        startLocation = new Location();
        events = new ArrayList<>();
    }
    
    /**
     * Copy constructor.
     *
     * @param mapSprite
     */
    public MapSprite(MapSprite mapSprite) {
        this.asset = mapSprite.asset;
        this.thread = mapSprite.thread;
        this.startLocation = new Location(mapSprite.startLocation);
        events = new ArrayList<>();
        mapSprite.events.forEach(e -> {
            events.add(new Event(e));
        });
    }
    
}
