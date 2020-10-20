/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import { SpriteSheet, StartLocation, MapLayer, Collider, Trigger } from "./asset-subtypes";

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

export interface Map extends Base {
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
}

export interface Sprite extends Base {
    name: string;
    description: string;
    animations: object;
    collider: Collider;
    trigger: Trigger;
    data: object;
}

export interface Tileset extends Base {
    tileWidth: number;
    tileHeight: number;
    image: string;
    tileData: object;
}
