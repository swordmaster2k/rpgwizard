/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.version1.board;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.rpgwizard.migrator.asset.version1.OldAbstractAsset;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OldBoard extends OldAbstractAsset {
    
    private String backgroundMusic;
    private List<String> tileSets;
    private String name;
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private List<OldBoardLayer> layers;
    private String firstRunProgram;
    private OldStartingPosition startingPosition;
    private List<OldBoardSprite> sprites;
    
}
