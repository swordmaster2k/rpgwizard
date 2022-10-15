/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.version2.animation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.migrator.asset.version2.AbstractAsset;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Animation extends AbstractAsset {
    
    private int width;
    private int height;
    private int frameRate;
    private String soundEffect;
    private SpriteSheet spriteSheet;
    
    public Animation() {
        spriteSheet = new SpriteSheet();
    }
    
}