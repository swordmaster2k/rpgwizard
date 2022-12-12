/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.ArrayList;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Collider extends AbstractShape {

    public Collider() {
        enabled = true;
        points = new ArrayList<>();
    }

    /**
     * Copy constructor.
     *
     * @param collider
     */
    public Collider(Collider collider) {
        this.enabled = collider.enabled;
        this.x = collider.x;
        this.y = collider.y;
        this.points = collider.points;
    }

}
