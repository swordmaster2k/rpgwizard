/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.rpgwizard.migrator.asset.version1.game.OldGame;
import org.rpgwizard.migrator.asset.version2.game.Game;

/**
 *
 * @author Joshua Michael Daly
 */
@Mapper
public abstract class OldGameToGameMapper extends AbstractAssetMapper {
    
    @BeforeMapping
    protected void mapEmbeddedTypes(OldGame source, @MappingTarget Game target) {
        var viewport = target.getViewport();
        viewport.setWidth(source.getResolutionWidth());
        viewport.setHeight(source.getResolutionHeight());
        viewport.setFullScreen(source.isFullScreen());
        
        var debug = target.getDebug();
        debug.setShowColliders(source.isShowVectors());
        debug.setShowTriggers(source.isShowVectors());
    }
    
    public abstract Game map(OldGame source);

}
