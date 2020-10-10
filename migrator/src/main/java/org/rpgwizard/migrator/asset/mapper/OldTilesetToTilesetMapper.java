/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.mapper;

import java.util.HashMap;
import java.util.Map;
import org.mapstruct.Mapper;
import org.rpgwizard.migrator.asset.version1.tileset.OldTileData;
import org.rpgwizard.migrator.asset.version1.tileset.OldTileset;
import org.rpgwizard.migrator.asset.version2.tileset.Tileset;

/**
 *
 * @author Joshua Michael Daly
 */
@Mapper
public abstract class OldTilesetToTilesetMapper extends AbstractAssetMapper {
    
    protected Map<String, Map<String, String>> mapTileData(Map<String, OldTileData> source) {
        var tileData = new HashMap<String, Map<String, String>>();
        
        source.keySet().forEach((key) -> {
            var data = source.get(key);
            var value = new HashMap<String, String>();
            value.put("type", data.getType());
            value.put("defence", data.getDefence());
            value.put("custom", data.getCustom());
            tileData.put(key, value);
        });
        
        return tileData;
    }
    
    public abstract Tileset map(OldTileset source);

}
