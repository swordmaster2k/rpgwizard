/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.rpgwizard.migrator.asset.version1.OldEvent;
import org.rpgwizard.migrator.asset.version2.AbstractAsset;
import org.rpgwizard.migrator.asset.version2.Event;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractAssetMapper {
    
    @AfterMapping
    protected void mapVersion(@MappingTarget AbstractAsset target) {
        target.setVersion("2.0.0");
    }
    
    @Mapping(source = "program", target = "script")
    public abstract Event mapEvent(OldEvent source);

}
