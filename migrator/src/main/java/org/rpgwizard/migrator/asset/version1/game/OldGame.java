/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.version1.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.rpgwizard.migrator.asset.version1.OldAbstractAsset;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OldGame extends OldAbstractAsset {
    
    private String initialCharacter;
    private String gameOverProgram;
    private String startupProgram;
    private boolean showVectors;
    private int resolutionHeight;
    private String projectIcon;
    private String name;
    @JsonProperty("isFullScreen")
    private boolean isFullScreen;
    private int resolutionWidth;
    private String initialBoard;
    
}
