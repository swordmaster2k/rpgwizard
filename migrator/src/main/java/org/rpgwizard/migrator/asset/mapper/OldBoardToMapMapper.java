/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net
 * <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.rpgwizard.migrator.asset.version1.board.OldBoard;
import org.rpgwizard.migrator.asset.version1.board.OldBoardImage;
import org.rpgwizard.migrator.asset.version1.board.OldBoardLayer;
import org.rpgwizard.migrator.asset.version1.board.OldBoardSprite;
import org.rpgwizard.migrator.asset.version1.board.OldBoardVector;
import org.rpgwizard.migrator.asset.version1.board.OldStartingPosition;
import org.rpgwizard.migrator.asset.version2.Collider;
import org.rpgwizard.migrator.asset.version2.Location;
import org.rpgwizard.migrator.asset.version2.Trigger;
import org.rpgwizard.migrator.asset.version2.map.Map;
import org.rpgwizard.migrator.asset.version2.map.MapImage;
import org.rpgwizard.migrator.asset.version2.map.MapLayer;
import org.rpgwizard.migrator.asset.version2.map.MapSprite;

/**
 *
 * @author Joshua Michael Daly
 */
@Mapper
public abstract class OldBoardToMapMapper extends AbstractAssetMapper {

    @AfterMapping
    protected void mapSprites(OldBoard source, @MappingTarget Map target) {
        // Go through the sprites after since they were stored separate to
        // the layers in the old format
        for (OldBoardSprite boardSprite : source.getSprites()) {
            var id = boardSprite.getId();
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString();
            }

            var asset = boardSprite.getName().replace(".npc", ".sprite").replace(".enemy", ".sprite");
            var mapSprite = mapSprite(boardSprite);
            mapSprite.setAsset(asset);

            var layerIdx = mapSprite.getStartLocation().getLayer();
            if (layerIdx < target.getLayers().size()) {
                var layer = target.getLayers().get(layerIdx);
                layer.getSprites().put(id, mapSprite);
            }
        }
    }

    @Mapping(source = "tileSets", target = "tilesets")
    @Mapping(source = "backgroundMusic", target = "music")
    @Mapping(source = "firstRunProgram", target = "entryScript")
    @Mapping(source = "startingPosition", target = "startLocation")
    public abstract Map map(OldBoard source);

    @Mapping(source = "name", target = "id")
    @Mapping(source = "vectors", target = "colliders")
    @Mapping(source = "vectors", target = "triggers")
    protected abstract MapLayer mapLayer(OldBoardLayer oldBoardLayer);

    @Mapping(target = "enabled", constant = "true")
    protected abstract Collider mapCollider(OldBoardVector source);

    @Mapping(target = "enabled", constant = "true")
    protected abstract Trigger mapTrigger(OldBoardVector source);

    @Mapping(source = "src", target = "image")
    protected abstract MapImage mapImage(OldBoardImage source);

    public abstract Location mapLocation(OldStartingPosition source);
    
    @Mapping(source = "startingPosition", target = "startLocation")
    protected abstract MapSprite mapSprite(OldBoardSprite source);

    // Additional mapping methods to deal with some specifics to the old format
    protected java.util.Map<String, Collider> mapColliders(List<OldBoardVector> source) {
        var colliders = new HashMap<String, Collider>();

        for (var oldVector : source) {
            if ("SOLID".equals(oldVector.getType())) {
                var id = oldVector.getId();
                if (id == null || id.isBlank()) {
                    id = UUID.randomUUID().toString();
                }
                colliders.put(id, mapCollider(oldVector));
            }
        }

        return colliders;
    }

    protected java.util.Map<String, Trigger> mapTriggers(List<OldBoardVector> source) {
        var triggers = new HashMap<String, Trigger>();

        for (var oldVector : source) {
            if ("PASSABLE".equals(oldVector.getType())) {
                var id = oldVector.getId();
                if (id == null || id.isBlank()) {
                    id = UUID.randomUUID().toString();
                }
                triggers.put(id, mapTrigger(oldVector));
            }
        }

        return triggers;
    }

    protected java.util.Map<String, MapImage> mapImages(List<OldBoardImage> source) {
        var images = new HashMap<String, MapImage>();

        for (var oldImage : source) {
            var id = oldImage.getId();
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString();
            }
            images.put(id, mapImage(oldImage));
        }

        return images;
    }

}
