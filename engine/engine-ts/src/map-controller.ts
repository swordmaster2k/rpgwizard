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

import * as Runtime from "./asset/runtime/asset-subtypes.js";
import { Sprite } from "./asset/sprite.js";
import { Collider, Trigger } from "./asset/dto/asset-subtypes.js";
import { Point } from "./rpgcode/rpgcode.js";

export class MapController {

    private _mapEntity: any;

    constructor() {
        this._mapEntity = {};
    }

    get mapEntity(): any {
        return this._mapEntity;
    }

    public findEntity(id: string): any {
        // TODO: Implement lookup table to make this O(1)
        const map: Map = this._mapEntity.map;
        for (const layer of map.layers) {
            if (layer.sprites[id]) {
                return layer.sprites[id].entity;
            }
        }
        return null;
    }

    public async loadMap(map: Map) {
        if (Core.getInstance().debugEnabled) {
            console.debug("Loading board=[%s]", JSON.stringify(map));
        }

        this._mapEntity = this.createCraftyMap(map);
        var assets = { images: [], audio: {} };

        await Promise.all(this._mapEntity.map.tilesets.map(async(file: string) => {
            let tileset: Tileset = Core.getInstance().cache.get(file);
            if (tileset === null) {
                tileset = await Factory.build(PATH_TILESET + file) as Tileset;
                Core.getInstance().cache.put(file, tileset);
            }
        }));

        map.generateLayerCache(); // REFACTOR: Move this?

        for (let layer: number = 0; layer < map.layers.length; layer++) {
            const mapLayer: Runtime.MapLayer = map.layers[layer];

            /*
             * Setup colliders
             */
            for (const id in mapLayer.colliders) {
                this.createCollider(mapLayer.colliders[id]);
            }

            /*
             * Setup triggers
             */
            for (const id in mapLayer.triggers) {
                this.createTrigger(mapLayer.triggers[id]);
            }

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
            for (const spriteId in mapLayer.sprites) {
                const mapSprite: Runtime.MapSprite = mapLayer.sprites[spriteId];
                mapSprite.entity = await this.loadSprite(mapSprite);
            }
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
        this.mapEntity.show = false;

        // REFACTOR
        // this.controlEnabled = false;

        Framework.destroyEntities(["SOLID", "PASSABLE", Framework.EntityType.Map, Framework.EntityType.MapSprite]);

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

        this.mapEntity.show = true;

        // REFACTOR
        // if (Core.getInstance().debugEnabled) {
        //     console.debug("Switching board player location set to x=[%d], y=[%d], layer=[%d]",
        //         this.craftyCharacter.x, this.craftyCharacter.y, this.craftyCharacter.layer);
        // }
    }

    private async setupMap() {

    }

    private async loadSprite(mapSprite: Runtime.MapSprite) {
        if (Core.getInstance().debugEnabled) {
            console.debug("Loading sprite=[%s]", JSON.stringify(mapSprite));
        }

        // REFACTOR
        const sprite: Sprite = await Factory.build(PATH_SPRITE + mapSprite.asset) as Sprite;
        // if (newSprite === null) {
        //     newSprite = await Factory.build(PATH_SPRITE + sprite.asset) as Sprite;
        //     this._cache.put(sprite.asset, newSprite);
        // }

        sprite.x = mapSprite.startLocation.x;
        sprite.y = mapSprite.startLocation.y;
        sprite.layer = mapSprite.startLocation.layer;
        sprite.thread = mapSprite.thread;

        // TODO: width and height of npc must contain the collision polygon.
        if (sprite.thread) {
            sprite.thread = await Core.getInstance().scriptVM.open(PATH_PROGRAM + sprite.thread);
        }

        // REFACTOR: get rid of this hacky code?
        var entity;

        const componentData: any = {
            sprite: sprite,
            isEnemy: true, // REFACTOR: Get rid of this
            events: [], // REFACTOR: Fix me
            entity: entity
        };
        Framework.defineComponent(Framework.EntityType.MapSprite, componentData);

        const entityData: any = {
            sprite: sprite
        };
        entity = Framework.createEntity(Framework.EntityType.MapSprite, entityData);

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
        Framework.defineComponent(Framework.EntityType.Map, mapDefinition);

        return Framework.createEntity(Framework.EntityType.Map, {});
    }

    private createCollider(collider: Collider) {
        const points: Array<number> = this.pointsToArray(collider.points);
        const bounds: any = engineUtil.getPolygonBounds(points);

        if (points[0] === points[points.length - 2] && points[1] === points[points.length - 1]) {
            // Start and end points are the same, Crafty does not like that.
            points.pop(); // Remove last y.
            points.pop(); // Remove last x.
        }

        const data: any = {
            x: bounds.x,
            y: bounds.y,
            w: bounds.width,
            h: bounds.height,
            collider: collider,
            points: points
        };

        Framework.createEntity(Framework.EntityType.Collider, data);
    }

    private createTrigger(trigger: Trigger) {
        const points: Array<number> = this.pointsToArray(trigger.points);
        const bounds: any = engineUtil.getPolygonBounds(points);

        if (points[0] === points[points.length - 2] && points[1] === points[points.length - 1]) {
            // Start and end points are the same, Crafty does not like that.
            points.pop(); // Remove last y.
            points.pop(); // Remove last x.
        }

        const data: any = {
            x: bounds.x,
            y: bounds.y,
            w: bounds.width,
            h: bounds.height,
            trigger: trigger,
            points: points
        };

        Framework.createEntity(Framework.EntityType.Trigger, data);
    }

    private pointsToArray(points: Point[]): Array<number> {
        const pointsArr: Array<number> = [];
        for (const point of points) {
            pointsArr.push(point.x);
            pointsArr.push(point.y);
        }
        return pointsArr;
    }

}
