/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.mapper;

import org.mapstruct.Mapper;
import org.rpgwizard.migrator.asset.version1.animation.OldAnimation;
import org.rpgwizard.migrator.asset.version2.animation.Animation;

/**
 *
 * @author Joshua Michael Daly
 */
@Mapper
public abstract class OldAnimationToAnimationMapper extends AbstractAssetMapper {
    
    public abstract Animation map(OldAnimation source);

}
