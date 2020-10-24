/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import { Framework } from "./framework.js";

import { Core } from "./core.js";
import * as Factory from "./asset/asset-factory.js";

import { Map } from "./asset/map";
import { Tileset } from "./asset/tileset";

import { MapLayer } from "./asset/dto/asset-subtypes.js";
import { Sprite } from "./asset/sprite.js";

export class MapController {

    private _craftyMap: any;

    constructor() {
        this._craftyMap = {};
    }

    get map(): any {
        return this._craftyMap;
    }

    public async loadMap(map: Map) {
        if (Core.getInstance().debugEnabled) {
            console.debug("Loading board=[%s]", JSON.stringify(map));
        }

        this._craftyMap = this.createCraftyMap(map);
        var assets = { images: [], audio: {} };

        await Promise.all(this._craftyMap.map.tilesets.map(async(file: string) => {
            let tileset: Tileset = Core.getInstance().cache.get(file);
            if (tileset === null) {
                tileset = await Factory.build(PATH_TILESET + file) as Tileset;
                Core.getInstance().cache.put(file, tileset);
            }
        }));

        map.generateLayerCache(); // REFACTOR: Move this?

        for (let layer: number = 0; layer < map.layers.length; layer++) {
            const mapLayer: MapLayer = map.layers[layer];

            // REFACTOR: Update this
            /*
             * Setup vectors.
             */
            // boardLayer.vectors.forEach(function (vector) {
            //     var id = vector.id;
            //     var type = vector.type;
            //     var points = vector.points;
            //     var events = vector.events;

            //     var len = points.length;
            //     var collision = [];
            //     for (var i = 0; i < len; i++) {
            //         collision.push(points[i].x, points[i].y);
            //     }
            //     this.createVectorPolygon(id, collision, layer, type, events);
            // }, this);

            // REFACTOR: Update this
            /*
             * Layer images.
             */
            // boardLayer.images.forEach(function (image) {
            //     assets.images.push(image.src);
            // }, this);

            /*
            * Setup board sprites.
            */
            var sprites = {};
            for (const [key, value] of Object.entries(mapLayer.sprites)) {
                sprites[key] = await this.loadSprite(value);
            }
            mapLayer.sprites = sprites;
        }

        // REFACTOR: Update this
        /*
         * Setup board sprites.
         */
        // var sprites = {};
        // var len = board.sprites.length;
        // for (var i = 0; i < len; i++) {
        //     var sprite = board.sprites[i];
        //     sprites[sprite.id] = await this.loadSprite(sprite);
        // }

        // Change board sprites to an object set.
        // board.sprites = sprites;

        // REFACTOR: Update this
        /*
         * Play background music.
         */
        // var backgroundMusic = board.backgroundMusic;
        // if (backgroundMusic) {
        //     assets.audio[board.backgroundMusic] = board.backgroundMusic;
        // }

        // this.queueCraftyAssets(assets, craftyBoard.board);
    }

    public async switchMap(file: string, tileX: number, tileY: number, layer: number) {
        if (Core.getInstance().debugEnabled) {
            console.debug("Switching board to boardName=[%s], tileX=[%d], tileY=[%d], layer=[%d]",
                file, tileX, tileY);
        }
        this.map.show = false;

        // REFACTOR
        // this.controlEnabled = false;

        Framework.destroyEntities(["SOLID", "PASSABLE", "Map", "MapSprite"]);

        // REFACTOR
        // May not be a last board if it is being set in a startup program.
        // if (this._mapController.map.map) {
        //     this.lastBackgroundMusic = this._mapController.map.map.backgroundMusic;
        // }

        // Load in the next map
        let map: Map = Core.getInstance().cache.get(PATH_BOARD + file);
        if (map === null) {
            map = await Factory.build(PATH_BOARD + file) as Map;
            Core.getInstance().cache.put(file, map);
        }

        await this.loadMap(map);

        this.map.show = true;

        // REFACTOR
        // if (Core.getInstance().debugEnabled) {
        //     console.debug("Switching board player location set to x=[%d], y=[%d], layer=[%d]",
        //         this.craftyCharacter.x, this.craftyCharacter.y, this.craftyCharacter.layer);
        // }
    }

    private async setupMap() {

    }

    private async loadSprite(sprite: any) {
        if (Core.getInstance().debugEnabled) {
            console.debug("Loading sprite=[%s]", JSON.stringify(sprite));
        }

        // REFACTOR
        var asset;
        const newSprite: Sprite = await Factory.build(PATH_SPRITE + sprite.asset) as Sprite;
        // if (newSprite === null) {
        //     newSprite = await Factory.build(PATH_SPRITE + sprite.asset) as Sprite;
        //     this._cache.put(sprite.asset, newSprite);
        // }
        asset = sprite.enemy = newSprite;
        sprite.collisionPoints = sprite.enemy.collisionPoints;

        sprite.x = sprite.startLocation.x;
        sprite.y = sprite.startLocation.y;
        sprite.layer = sprite.startLocation.layer;

        // TODO: width and height of npc must contain the collision polygon.
        if (sprite.thread) {
            sprite.thread = await Core.getInstance().scriptVM.open(PATH_PROGRAM + sprite.thread);
        }

        // REFACTOR: get rid of this hacky code?
        var entity;

        const bounds = engineUtil.getPolygonBounds(asset.activationPoints);
        const activatonData: any = {
            mapSprite: sprite,
            asset: asset,
            bounds: bounds,
            entity: entity
        };
        var activationVector = Framework.createEntity("ActivationVector", activatonData);

        const componentData: any = {
            mapSprite: sprite,
            asset: asset,
            isEnemy: true, // REFACTOR: Get rid of this
            events: [], // REFACTOR: Fix me
            activationVector: activationVector, // REFACTOR: Fix me
            entity: entity
        };
        Framework.defineComponent("MapSprite", componentData);

        const entityData: any = {
            sprite: newSprite
        };
        entity = Framework.createEntity("MapSprite", entityData);

        return entity;
    }

    // REFACTOR
    private createCraftyMap(map: Map): any {
        if (Core.getInstance().debugEnabled) {
            console.debug("Creating Crafty board=[%s]", JSON.stringify(map));
        }

        var width = map.width * map.tileWidth;
        var height = map.height * map.tileHeight;

        const viewport = Framework.getViewport();
        var vWidth = viewport._width;
        var vHeight = viewport._height;
        var scale = viewport._scale;

        var xShift = 0;
        var yShift = 0;
        if (width < vWidth) {
            var sWidth = width * scale;
            xShift = Math.max(((vWidth - sWidth) / 2) / scale, 0);
            if (xShift < 1) {
                Math.max(sWidth - vWidth, 0);
            }
            width = vWidth;
        }
        if (height < vHeight) {
            var sHeight = height * scale;
            yShift = Math.max(((vHeight - sHeight) / 2) / scale, 0);
            if (yShift < 1) {
                Math.max(sHeight - vHeight, 0);
            }
            height = vHeight;
        }

        if (Core.getInstance().debugEnabled) {
            console.debug("width=" + width);
            console.debug("height=" + height);
            console.debug("xShift=" + xShift);
            console.debug("yShift=" + yShift);
        }

        const mapDefinition: any = {
            width: width,
            height: height,
            xShift: xShift,
            yShift: yShift,
            map: map
        };
        Framework.defineComponent("Map", mapDefinition);

        return Framework.createEntity("Map", {});
    }

}
