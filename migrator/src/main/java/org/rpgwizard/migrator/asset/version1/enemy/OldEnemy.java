/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.version1.enemy;

import java.util.Map;
import lombok.Data;
import org.rpgwizard.migrator.asset.version1.ActivationOffset;
import org.rpgwizard.migrator.asset.version1.ActivationVector;
import org.rpgwizard.migrator.asset.version1.BaseVector;
import org.rpgwizard.migrator.asset.version1.BaseVectorOffset;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
public class OldEnemy {
    
    private int frameRate;
    private BaseVector baseVector;
    private ActivationOffset activationOffset;
    private ActivationVector activationVector;
    private BaseVectorOffset baseVectorOffset;
    private boolean baseVectorDisabled;
    private Map<String, String> animations;
    private String name;
    private String description;
    private Map<String, String> graphics;
    private boolean activationVectorDisabled;
    private double version;
    
    // Specific to old enemy asset
    private int magic;
    private int defence;
    private int experienceReward;
    private int goldReward;
    private int attack;
    private int level;
    private int health;
    
}
