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
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.rpgwizard.migrator.asset.version1.character.OldCharacter;
import org.rpgwizard.migrator.asset.version2.sprite.Sprite;

/**
 *
 * @author Joshua Michael Daly
 */
@Mapper
public abstract class OldCharacterToSpriteMapper extends AbstractAssetMapper {
    
    @BeforeMapping
    protected void handleColliderEnabled(OldCharacter source, @MappingTarget Sprite target) {
        target.getCollider().setEnabled(!source.isBaseVectorDisabled());
    }
    
    @BeforeMapping
    protected void handleTriggerEnabled(OldCharacter source, @MappingTarget Sprite target) {
        target.getTrigger().setEnabled(!source.isActivationVectorDisabled());
    }
    
    @Mapping(source = "baseVector", target = "collider")
    @Mapping(source = "baseVectorOffset.x", target = "collider.x")
    @Mapping(source = "baseVectorOffset.y", target = "collider.y")
    @Mapping(source = "activationVector", target = "trigger")
    @Mapping(source = "activationOffset.x", target = "trigger.x")
    @Mapping(source = "activationOffset.y", target = "trigger.y")
    @Mapping(target = "description", constant = "")
    public abstract Sprite map(OldCharacter source);

}
