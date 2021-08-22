/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

export enum Direction {
    // eslint-disable-next-line no-unused-vars
    NORTH = "n",
    // eslint-disable-next-line no-unused-vars
    SOUTH = "s",
    // eslint-disable-next-line no-unused-vars
    EAST = "e",
    // eslint-disable-next-line no-unused-vars
    WEST = "w",
    // eslint-disable-next-line no-unused-vars
    NORTH_EAST = "ne",
    // eslint-disable-next-line no-unused-vars
    NORTH_WEST = "nw",
    // eslint-disable-next-line no-unused-vars
    SOUTH_EAST = "se",
    // eslint-disable-next-line no-unused-vars
    SOUTH_WEST = "sw",
    // eslint-disable-next-line no-unused-vars
    ATTACK = "ATTACK",
    // eslint-disable-next-line no-unused-vars
    DEFEND = "DEFEND",
    // eslint-disable-next-line no-unused-vars
    DIE = "DIE"
}

export const StandardKeys: Array<string> = [
    "SOUTH", "NORTH", "EAST", "WEST", "NORTH_EAST", "NORTH_WEST",
    "SOUTH_EAST", "SOUTH_WEST", "ATTACK", "DEFEND", "SPECIAL_MOVE", "DIE",
    "REST", "SOUTH_IDLE", "NORTH_IDLE", "EAST_IDLE", "WEST_IDLE",
    "NORTH_EAST_IDLE", "NORTH_WEST_IDLE", "SOUTH_EAST_IDLE", "SOUTH_WEST_IDLE"
];

export enum EventType {
    // eslint-disable-next-line no-unused-vars
    OVERLAP = "overlap",
    // eslint-disable-next-line no-unused-vars
    KEYPRESS = "keypress",
    // eslint-disable-next-line no-unused-vars
    THREAD = "thread",
    // eslint-disable-next-line no-unused-vars
    FUNCTION = "function"
}

export interface Viewport {
    width: number;
    height: number;
    fullScreen: boolean;
}

export interface Debug {
    showColliders: boolean;
    showTriggers: boolean;
}

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
    id: string;
    enabled: boolean;
    x: number;
    y: number;
    layer: number;
    points: Point[];
    events: Event[];
}

export interface Collider {
    id: string;
    enabled: boolean;
    x: number;
    y: number;
    layer: number;
    points: Point[];
}

export interface Location {
    x: number;
    y: number;
    layer: number;
}

export interface MapSprite {
    asset: string;
    thread: string;
    startLocation: Location;
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
