/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.version2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
public class Trigger {
    
    private boolean enabled;
    private int x;
    private int y;
    private List<Point> points;
    private List<Event> events;
    
    public Trigger() {
        enabled = true;
        points = new ArrayList<>();
        events = new ArrayList<>();
    }
    
}
