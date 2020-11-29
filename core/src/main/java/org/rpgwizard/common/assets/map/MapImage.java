/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapImage {
    
    private String image;
    private int x;
    private int y;
    
    /**
     * Copy constructor.
     *
     * @param mapImage
     */
    public MapImage(MapImage mapImage) {
        this.image = mapImage.image;
        this.x = mapImage.x;
        this.y = mapImage.y;
    }
    
}
