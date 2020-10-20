/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
export interface SpriteSheet {
    image: string;
    x: number;
    y: number;
    width: number;
    height: number;
}

export interface Point {
    x: number;
    y: number;
}

export interface Event {
    type: string;
    script: string;
    key: any;
}

export interface Trigger {
    enabled: boolean;
    x: number;
    y: number;
    points: Point[];
    events: Event[];
}

export interface Collider {
    enabled: boolean;
    x: number;
    y: number;
    points: Point[];
}

export interface StartLocation {
    x: number;
    y: number;
    layer: number;
}

export interface MapSprite {
    asset: string;
    thread: string;
    startLocation: StartLocation;
    events: Event[];
}

export interface MapImage {
    image: string;
    x: number;
    y: number;
}

export interface MapLayer {
    id: string;
    tiles: string[];
    colliders: Record<string, Collider>;
    triggers: Record<string, Trigger>;
    sprites: Record<string, MapSprite>;
    images: Record<string, MapImage>;
}
