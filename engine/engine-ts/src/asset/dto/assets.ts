/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import { SpriteSheet, Location, MapLayer, Collider, Trigger, Viewport, Debug } from "./asset-subtypes";

// Base type
export interface Base {
    version: string;
}

// Main Asset types
export interface Animation extends Base {
    width: number;
    height: number;
    frameRate: number;
    soundEffect: string;
    spriteSheet: SpriteSheet;
}

export interface Game extends Base {
    name: string;
    viewport: Viewport;
    debug: Debug;
    version: string;
}

export interface Map extends Base {
    name: string;
    width: number;
    height: number;
    tileWidth: number;
    tileHeight: number;
    music: string;
    tilesets: string[];
    entryScript: string;
    startLocation: Location;
    layers: MapLayer[];
}

export interface Sprite extends Base {
    name: string;
    description: string;
    animations: Record<string, string>;
    collider: Collider;
    trigger: Trigger;
    data: any;
}

export interface Tileset extends Base {
    tileWidth: number;
    tileHeight: number;
    image: string;
    tileData: object;
}
