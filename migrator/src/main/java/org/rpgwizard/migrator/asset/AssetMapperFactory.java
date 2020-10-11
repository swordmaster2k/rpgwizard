/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset;

import java.util.Optional;
import org.mapstruct.factory.Mappers;
import org.rpgwizard.migrator.asset.mapper.OldAnimationToAnimationMapper;
import org.rpgwizard.migrator.asset.mapper.OldBoardToMapMapper;
import org.rpgwizard.migrator.asset.mapper.OldCharacterToSpriteMapper;
import org.rpgwizard.migrator.asset.mapper.OldEnemyToSpriteMapper;
import org.rpgwizard.migrator.asset.mapper.OldGameToGameMapper;
import org.rpgwizard.migrator.asset.mapper.OldNpcToSpriteMapper;
import org.rpgwizard.migrator.asset.mapper.OldTilesetToTilesetMapper;
import org.rpgwizard.migrator.asset.version1.OldAbstractAsset;
import org.rpgwizard.migrator.asset.version1.animation.OldAnimation;
import org.rpgwizard.migrator.asset.version1.board.OldBoard;
import org.rpgwizard.migrator.asset.version1.character.OldCharacter;
import org.rpgwizard.migrator.asset.version1.enemy.OldEnemy;
import org.rpgwizard.migrator.asset.version1.game.OldGame;
import org.rpgwizard.migrator.asset.version1.npc.OldNpc;
import org.rpgwizard.migrator.asset.version1.tileset.OldTileset;
import org.rpgwizard.migrator.asset.version2.AbstractAsset;

/**
 *
 * @author Joshua Michael Daly
 */
public class AssetMapperFactory {
    
    public static Optional<AbstractAsset> map(OldAbstractAsset oldAsset) {
        if (oldAsset instanceof OldGame) {
            
            var mapper = Mappers.getMapper(OldGameToGameMapper.class);
            return Optional.of(mapper.map((OldGame) oldAsset));
            
        } else if (oldAsset instanceof OldAnimation) {
            
            var mapper = Mappers.getMapper(OldAnimationToAnimationMapper.class);
            return Optional.of(mapper.map((OldAnimation) oldAsset));
            
        } else if (oldAsset instanceof OldBoard) {
            
            var mapper = Mappers.getMapper(OldBoardToMapMapper.class);
            return Optional.of(mapper.map((OldBoard) oldAsset));
            
        } else if (oldAsset instanceof OldCharacter) {
            
            var mapper = Mappers.getMapper(OldCharacterToSpriteMapper.class);
            return Optional.of(mapper.map((OldCharacter) oldAsset));
            
        } else if (oldAsset instanceof OldEnemy) {
            
            var mapper = Mappers.getMapper(OldEnemyToSpriteMapper.class);
            return Optional.of(mapper.map((OldEnemy) oldAsset));
            
        } else if (oldAsset instanceof OldNpc) {
            
            var mapper = Mappers.getMapper(OldNpcToSpriteMapper.class);
            return Optional.of(mapper.map((OldNpc) oldAsset));
            
        } else if (oldAsset instanceof OldTileset) {
            
            var mapper = Mappers.getMapper(OldTilesetToTilesetMapper.class);
            return Optional.of(mapper.map((OldTileset) oldAsset));
            
        } 
        
        return Optional.empty();
    }
    
}
