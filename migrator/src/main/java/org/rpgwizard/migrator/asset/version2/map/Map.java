/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.migrator.asset.version2.map;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.migrator.asset.version2.AbstractAsset;
import org.rpgwizard.migrator.asset.version2.Location;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Map extends AbstractAsset {
    
    private String name;
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private String music;
    private List<String> tilesets;
    private String entryScript;
    private Location startLocation;
    private List<MapLayer> layers;
    
    public Map() {
        tilesets = new ArrayList<>();
        startLocation = new Location();
        layers = new ArrayList<>();
    }
    
}
