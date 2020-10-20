/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import * as Asset from "./dto/assets.js";
import { StartLocation, MapLayer } from "./dto/asset-subtypes.js";

export class Map implements Asset.Map {

    // Implemented
    name: string;
    width: number;
    height: number;
    tileWidth: number;
    tileHeight: number;
    music: string;
    tilesets: string[];
    entryScript: string;
    startLocation: StartLocation;
    layers: MapLayer[];
    version: string;

    constructor(asset: Asset.Map) {
        // Copy over values
        this.name = asset.name;
        this.width = asset.width;
        this.height = asset.height;
        this.tileWidth = asset.tileWidth;
        this.tileHeight = asset.tileHeight;
        this.music = asset.music;
        this.tilesets = asset.tilesets;
        this.entryScript = asset.entryScript;
        this.startLocation = asset.startLocation;
        this.layers = asset.layers;
        this.version = asset.version;
    }

}
