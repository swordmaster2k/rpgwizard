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
import { Collider, MapImage, Trigger } from "./asset/dto/asset-subtypes.js";
import { Point } from "./rpgcode/rpgcode.js";
import { EngineUtil } from "./util/util.js";

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

        this._mapEntity = this.createMap(map);

        await Promise.all(this._mapEntity.map.tilesets.map(async(file: string) => {
            let tileset: Tileset = Core.getInstance().cache.get(file);
            if (tileset === null) {
                tileset = await Factory.build(Core.PATH_TILESET + file) as Tileset;
                Core.getInstance().cache.put(file, tileset);
            }
        }));

        map.generateLayerCache();

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
            /*
             * Setup images
             */
            for (const id in mapLayer.images) {
                const mapImage: MapImage = mapLayer.images[id];
                await Framework.loadAssets({ images: [mapImage.image] });
            }
            /*
            * Setup sprites
            */
            for (const spriteId in mapLayer.sprites) {
                const mapSprite: Runtime.MapSprite = mapLayer.sprites[spriteId];
                mapSprite.entity = await this.loadSprite(mapSprite);
            }
        }
        /*
         * Play background music.
         */
        if (map.music) {
            const audioAssets: any = {};
            audioAssets[map.music] = map.music;
            await Framework.loadAssets({ audio: audioAssets });
            // Framework.playAudio(map.music, 1);
        }
    }

    public async switchMap(file: string, tileX: number, tileY: number, layer: number) {
        if (Core.getInstance().debugEnabled) {
            console.debug("Switching board to boardName=[%s], tileX=[%d], tileY=[%d], layer=[%d]",
                file, tileX, tileY);
        }
        this.mapEntity.show = false;

        Framework.destroyEntities([Framework.EntityType.Collider, Framework.EntityType.Trigger, Framework.EntityType.Map, Framework.EntityType.MapSprite]);

        // Load in the next map
        let map: Map = Core.getInstance().cache.get(Core.PATH_MAP + file);
        if (map === null) {
            map = await Factory.build(Core.PATH_MAP + file) as Map;
            Core.getInstance().cache.put(file, map);
        }

        await this.loadMap(map);
        this.mapEntity.show = true;
    }

    private async setupMap() {

    }

    private async loadSprite(mapSprite: Runtime.MapSprite) {
        if (Core.getInstance().debugEnabled) {
            console.debug("Loading sprite=[%s]", JSON.stringify(mapSprite));
        }

        // REFACTOR
        const sprite: Sprite = await Factory.build(Core.PATH_SPRITE + mapSprite.asset) as Sprite;
        // if (newSprite === null) {
        //     newSprite = await Factory.build(Core.PATH_SPRITE + sprite.asset) as Sprite;
        //     this._cache.put(sprite.asset, newSprite);
        // }

        sprite.x = mapSprite.startLocation.x;
        sprite.y = mapSprite.startLocation.y;
        sprite.layer = mapSprite.startLocation.layer;
        sprite.thread = mapSprite.thread;

        const componentData: any = {
            sprite: sprite
        };
        Framework.defineComponent(Framework.EntityType.MapSprite, componentData);
        const entityData: any = {
            sprite: sprite
        };
        return Framework.createEntity(Framework.EntityType.MapSprite, entityData);
    }

    private createMap(map: Map): any {
        if (Core.getInstance().debugEnabled) {
            console.debug("Creating Crafty board=[%s]", JSON.stringify(map));
        }

        let width = map.width * map.tileWidth;
        let height = map.height * map.tileHeight;

        const viewport = Framework.getViewport();
        const vWidth = viewport._width;
        const vHeight = viewport._height;
        const scale = viewport._scale;

        let xShift = 0;
        let yShift = 0;
        if (width < vWidth) {
            const sWidth = width * scale;
            xShift = Math.max(((vWidth - sWidth) / 2) / scale, 0);
            if (xShift < 1) {
                Math.max(sWidth - vWidth, 0);
            }
            width = vWidth;
        }
        if (height < vHeight) {
            const sHeight = height * scale;
            yShift = Math.max(((vHeight - sHeight) / 2) / scale, 0);
            if (yShift < 1) {
                Math.max(sHeight - vHeight, 0);
            }
            height = vHeight;
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
        const bounds: any = EngineUtil.getPolygonBounds(points);

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
        const bounds: any = EngineUtil.getPolygonBounds(points);

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
