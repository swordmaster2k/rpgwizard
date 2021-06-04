/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Trigger extends AbstractPolygon {

    private List<Event> events;

    public Trigger() {
        enabled = true;
        points = new ArrayList<>();
        events = new ArrayList<>();
    }

    /**
     * Copy constructor.
     *
     * @param trigger
     */
    public Trigger(Trigger trigger) {
        this.enabled = trigger.enabled;
        this.x = trigger.x;
        this.y = trigger.y;
        this.points = trigger.points;
        this.events = trigger.events;
    }

}
