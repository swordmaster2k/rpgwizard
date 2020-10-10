/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.rpgwizard.migrator.asset.version1.OldEvent;
import org.rpgwizard.migrator.asset.version1.board.OldBoard;
import org.rpgwizard.migrator.asset.version1.board.OldBoardImage;
import org.rpgwizard.migrator.asset.version1.board.OldBoardLayer;
import org.rpgwizard.migrator.asset.version1.board.OldBoardSprite;
import org.rpgwizard.migrator.asset.version1.board.OldBoardVector;
import org.rpgwizard.migrator.asset.version1.board.OldStartingPosition;
import org.rpgwizard.migrator.asset.version2.Collider;
import org.rpgwizard.migrator.asset.version2.Event;
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
    
    @Mapping(source = "tileSets", target = "tilesets")
    @Mapping(source = "backgroundMusic", target = "music")
    @Mapping(source = "firstRunProgram", target = "entryScript")
    @Mapping(source = "startingPosition", target = "startLocation")
    public abstract Map map(OldBoard source);
    
    @AfterMapping
    protected void mapSprites(OldBoard source, @MappingTarget Map target) {
        for (OldBoardSprite boardSprite : source.getSprites()) {
            var id = boardSprite.getId();
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString();
            }
            
            var mapSprite = mapSprite(boardSprite);
            
            var layerIdx = mapSprite.getStartLocation().getLayer();
            if (layerIdx < target.getLayers().size()) {
                var layer = target.getLayers().get(layerIdx);
                layer.getSprites().put(id, mapSprite);
            }
        }
    }
    
    protected List<MapLayer> oldBoardLayerListToMapLayerList(List<OldBoardLayer> list) {
        if (list == null) {
            return null;
        }

        List<MapLayer> list1 = new ArrayList<>(list.size());
        for (OldBoardLayer oldBoardLayer : list) {
            list1.add(oldBoardLayerToMapLayer(oldBoardLayer));
        }

        return list1;
    }
    
    protected MapLayer oldBoardLayerToMapLayer(OldBoardLayer oldBoardLayer) {
        var layer = new MapLayer();
        
        layer.setId(oldBoardLayer.getName());
        layer.setTiles(oldBoardLayer.getTiles());
        layer.setColliders(mapColliders(oldBoardLayer.getVectors()));
        layer.setTriggers(mapTriggers(oldBoardLayer.getVectors()));
        layer.setSprites(new HashMap<>());
        layer.setImages(mapImages(oldBoardLayer.getImages()));
        
        return layer;
    }
    
    protected java.util.Map<String, Collider> mapColliders(List<OldBoardVector> source) {
        var colliders = new HashMap<String, Collider>();
        
        for (var oldVector : source) {
            if ("SOLID".equals(oldVector.getType())) {
                var id = oldVector.getId();
                if (id == null || id.isBlank()) {
                    id = UUID.randomUUID().toString();
                }
                
                var collider = new Collider();
                collider.setEnabled(true);
                collider.setPoints(oldVector.getPoints());
                collider.setX(0);
                collider.setY(0);
                
                colliders.put(id, collider);
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

                var trigger = new Trigger();
                trigger.setEnabled(true);
                trigger.setPoints(oldVector.getPoints());
                trigger.setX(0);
                trigger.setY(0);
                trigger.setEvents(mapEvents(oldVector.getEvents()));

                triggers.put(id, trigger);
            }
        }

        return triggers;
    }
    
    protected List<Event> mapEvents(List<OldEvent> source) {
        var events = new ArrayList<Event>();
        
        for (var oldEvent : source) {
            var event = new Event();
            event.setType(oldEvent.getType());
            event.setScript(oldEvent.getProgram());
            event.setKey(oldEvent.getKey());
            
            events.add(event);
        }
        
        return events;
    }
    
    protected java.util.Map<String, MapImage> mapImages(List<OldBoardImage> source) {
        var images = new HashMap<String, MapImage>();

        for (var oldImage : source) {
            var id = oldImage.getId();
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString();
            }

            var mapImage = new MapImage();
            mapImage.setImage(oldImage.getSrc());
            mapImage.setX(oldImage.getX());
            mapImage.setY(oldImage.getY());

            images.put(id, mapImage);
        }

        return images;
    }
    
    protected MapSprite mapSprite(OldBoardSprite source) {
        var sprite = new MapSprite();
        
        var asset = source.getName().replace(".npc", ".sprite").replace(".enemy", ".sprite");
        
        sprite.setAsset(asset);
        sprite.setThread(source.getThread());
        sprite.setStartLocation(mapLocation(source.getStartingPosition()));
        sprite.setEvents(mapEvents(source.getEvents()));
        
        return sprite;
    }
    
    public abstract Location mapLocation(OldStartingPosition source);

}
