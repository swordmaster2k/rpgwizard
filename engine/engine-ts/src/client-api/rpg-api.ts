/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import { Core } from "../core.js";
import { Sprite } from "../asset/sprite.js";
import { MapController } from "../map-controller.js";
import { Map } from "../asset/map.js";
import { Framework } from "../framework.js";
import { MapLayer, MapSprite } from "../asset/runtime/asset-subtypes.js";
import { MapImage, Location } from "../asset/dto/asset-subtypes.js";
import { Tileset } from "../asset/tileset.js";
import { Draw2D } from "../view/draw-2d.js";

export class Canvas {
    canvasElement: HTMLCanvasElement;
    render: boolean;
    x: number;
    y: number;

    constructor(canvasElement: HTMLCanvasElement, render: boolean, x: number, y: number) {
        this.canvasElement = canvasElement;
        this.render = render;
        this.x = x;
        this.y = y;
    }
}

export class Rgba {
    r: number;
    g: number;
    b: number;
    a: number;

    constructor(r: number, g: number, b: number, a: number) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}

export class Rpg {

    private readonly _core: Core;
    private readonly _mapController: MapController

    private _globals: Record<string, any>;
    private _canvases: Record<string, Canvas>;
    private _rgba: Rgba;
    private _gradient: string;
    private _font: string;

    constructor(core: Core) {
        this._core = core;
        this._mapController = core.mapController;

        this._globals = {};
        this._rgba = new Rgba(255, 255, 255, 1.0);
        this._font = "14px Arial";

        this._canvases = {};
        this._canvases.default = new Canvas(core.screen.defaultCanvas, false, 0, 0);
    }

    get canvases(): Record<string, Canvas> {
        return this._canvases;
    }

    // ------------------------------------------------------------------------
    // Asset
    // ------------------------------------------------------------------------

    public isAssetLoaded(assetId: string, type: string): boolean {
        return Framework.isAssetLoaded(assetId, type);
    }

    public async loadAssets(assets: Framework.Assets) {
        await Framework.loadAssets(assets);
    }

    public removeAssets(assets: Framework.Assets) {
        Framework.removeAssets(assets);
    }

    public getImage(file: string): ImageBitmap {
        return Framework.getImage(file);
    }

    public async loadJson(path: string): Promise<any> {
        // REFACTOR: Switch to GET request
        const response = await fetch("http://localhost:8080/engine/load", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({ path: path }),
            headers: {
                "Content-Type": "application/json",
                "Cache-Control": "no-cache"
            }
        });
        return await response.json();
    }

    public async saveJson(json: any) {
        await fetch("http://localhost:8080/engine/save", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify(json),
            headers: {
                "Content-Type": "application/json",
                "Cache-Control": "no-cache"
            }
        });
    }

    // ------------------------------------------------------------------------
    // Canvas
    // ------------------------------------------------------------------------

    public createCanvas(canvasId: string, width: number, height: number) {
        const canvasElement: HTMLCanvasElement = document.createElement("canvas");
        canvasElement.width = width;
        canvasElement.height = height;
        const canvas: Canvas = new Canvas(canvasElement, false, 0, 0);
        this._canvases[canvasId] = canvas;
    }

    public removeCanvas(canvasId: string) {
        delete this._canvases[canvasId];
    }

    public render(canvasId: string) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            canvas.render = true;
            Framework.trigger(Framework.EventType.Invalidate);
        }
    }

    public clear(canvasId: string) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const canvasElement: HTMLCanvasElement = canvas.canvasElement;
            canvasElement.getContext("2d").clearRect(0, 0, canvasElement.width, canvasElement.height);
            canvas.render = true;
            Framework.trigger(Framework.EventType.Invalidate);
        }
    }

    public setCanvasPosition(canvasId: string, x: number, y: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            canvas.x = x;
            canvas.y = y;
        }
    }

    // ------------------------------------------------------------------------
    // Drawing
    // ------------------------------------------------------------------------

    public setColor(r: number, g: number, b: number, a: number) {
        this._rgba = new Rgba(r, g, b, a);
    }

    public setAlpha(alpha: number) {
        this._rgba.a = alpha;
    }

    public setFont(size: number, family: string) {
        this._font = size + "px " + family;
    }

    public getPixel(canvasId: string, x: number, y: number): any {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const context: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            return context.getImageData(x, y, 1, 1);
        }
    }

    public setPixel(canvasId: string, x: number, y: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            Draw2D.putImageData(ctx, x, y, this._rgba);
        }
    }

    public drawOntoCanvas(targetId: string, sourceId: string, x: number, y: number, width: number, height: number) {
        const target: Canvas = this._canvases[targetId];
        const source: Canvas = this._canvases[sourceId];
        if (target && source) {
            const targetCtx: CanvasRenderingContext2D = this._getDrawingContext(target);
            targetCtx.drawImage(source.canvasElement, x, y, width, height);
        }
    }

    public drawCircle(canvasId: string, x: number, y: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.strokeStyle = this._getStrokeStyle();
            Draw2D.drawCircle(ctx, x, y, radius);
        }
    }

    public drawImage(canvasId: string, file: string, x: number, y: number, width: number, height: number, rotation: number) {
        const canvas: Canvas = this._canvases[canvasId];
        const image: ImageBitmap = Framework.getImage(file);
        if (canvas && image) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            Draw2D.drawImage(ctx, image, x, y, width, height, rotation);
        }
    }

    public drawImagePart(canvasId: string, file: string, srcX: number, srcY: number, srcWidth: number, srcHeight: number, destX: number, destY: number, destWidth: number, destHeight: number, rotation: number) {
        const canvas: Canvas = this._canvases[canvasId];
        const image: ImageBitmap = Framework.getImage(file);
        if (canvas && image) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            Draw2D.drawImagePart(ctx, image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, rotation);
        }
    }

    public drawLine(canvasId: string, x1: number, y1: number, x2: number, y2: number, lineWidth: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.strokeStyle = this._getStrokeStyle();
            Draw2D.drawLine(ctx, x1, y1, x2, y2, lineWidth);
        }
    }

    public drawRect(canvasId: string, x: number, y: number, width: number, height: number, lineWidth: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.strokeStyle = this._getStrokeStyle();
            Draw2D.drawRect(ctx, x, y, width, height, lineWidth);
        }
    }

    public drawRoundedRect(canvasId: string, x: number, y: number, width: number, height: number, lineWidth: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.strokeStyle = this._getStrokeStyle();
            Draw2D.drawRoundedRect(ctx, x, y, width, height, lineWidth, radius);
        }
    }

    public drawText(canvasId: string, x: number, y: number, text: string) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            Draw2D.drawText(ctx, x, y, text, this._font);
        }
    }

    public fillCircle(canvasId: string, x: number, y: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            Draw2D.drawCircle(ctx, x, y, radius);
        }
    }

    public fillRect(canvasId: string, x: number, y: number, width: number, height: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            Draw2D.fillRect(ctx, x, y, width, height);
        }
    }

    public fillRoundedRect(canvasId: string, x: number, y: number, width: number, height: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            Draw2D.fillRoundedRect(ctx, x, y, width, height, radius);
        }
    }

    private _getDrawingContext(canvas: Canvas): CanvasRenderingContext2D {
        const ctx: CanvasRenderingContext2D = canvas.canvasElement.getContext("2d");
        ctx.imageSmoothingEnabled = true;
        ctx.globalAlpha = this._rgba.a;
        return ctx;
    }

    private _getStrokeStyle(): string {
        return "rgba(" + this._rgba.r + "," + this._rgba.g + "," + this._rgba.b + "," + this._rgba.a + ")";
    }

    private _getFillStyle(): string {
        return this._gradient ? this._gradient : "rgba(" + this._rgba.r + "," + this._rgba.g + "," + this._rgba.b + "," + this._rgba.a + ")";
    }

    // ------------------------------------------------------------------------
    // Map
    // ------------------------------------------------------------------------

    public getMap(): Map {
        return this._mapController.mapEntity.map;
    }

    public async loadMap(map: string) {
        await this._mapController.switchMap(map, 5, 5, 1);
    }

    public async addLayerImage(imageId: string, layer: number, image: MapImage) {
        if (layer < this.getMap().layers.length) {
            const mapLayer: MapLayer = this.getMap().layers[layer];
            mapLayer.images[imageId] = image;
        }
    }

    public removeLayerImage(imageId: string, layer: number) {
        if (layer < this.getMap().layers.length) {
            const mapLayer: MapLayer = this.getMap().layers[layer];
            delete mapLayer.images[imageId];
        }
    }

    public getTileData(x: number, y: number, layer: number): any {
        const map: Map = this.getMap();
        if (layer < 0 || map.layers.length < layer) {
            throw new Error("layer out of range");
        }

        const mapLayer: MapLayer = map.layers[layer];
        const tileIndex: number = (y * map.width) + x;
        if (tileIndex < 0 || mapLayer.tiles.length < tileIndex) {
            throw new Error("tile out of range");
        }

        const parts = mapLayer.tiles[tileIndex].split(":");
        if (parts[0] === "-1" || parts[1] === "-1") {
            return null; // empty tile
        }

        const tileset: Tileset = this._core.cache.get(map.tilesets[parts[0]]);
        return tileset.tileData && tileset.tileData[parts[1]] ? tileset.tileData[parts[1]] : null;
    }

    public replaceTile(x: number, y: number, layer: number, tilesetFile: string, tileIndex: number) {
        const tileset: Tileset = this._core.cache.get(tilesetFile);
        const tile: ImageData = tileset.getTile(tileIndex);
        this.getMap().replaceTile(x, y, layer, tile);
    }

    public removeTile(x: number, y: number, layer: number) {
        this.getMap().removeTile(x, y, layer);
    }

    // ------------------------------------------------------------------------
    // Sprite
    // ------------------------------------------------------------------------

    public getSprite(id: string): Sprite {
        const entity = this._mapController.findEntity(id);
        if (entity && entity.sprite) {
            return entity.sprite;
        }
        return null;
    }

    public async addSprite(spriteId: string, layer: number, sprite: MapSprite) {
        if (layer < this.getMap().layers.length) {
            const mapLayer: MapLayer = this.getMap().layers[layer];
            mapLayer.sprites[spriteId] = sprite;
        }
    }

    public removeSprite(spriteId: string) {
        const sprite: Sprite = this.getSprite(spriteId);
        if (sprite) {
            delete this.getMap().layers[sprite.layer].sprites[spriteId];
        }
    }

    public getSpriteLocation(spriteId: string, inTiles: boolean, includeOffset: boolean): Location {
        const sprite: Sprite = this.getSprite(spriteId);
        if (sprite) {
            let x = sprite.x;
            let y = sprite.y;
            if (includeOffset) {
                x += this._mapController.mapEntity.xShift + this.getViewport()._x;
                y += this._mapController.mapEntity.yShift + this.getViewport()._y;
            }
            if (inTiles) {
                return {
                    x: Math.floor(x / this.getMap().tileWidth),
                    y: Math.floor(y / this.getMap().tileHeight),
                    layer: sprite.layer
                };
            } else {
                return {
                    x: x,
                    y: y,
                    layer: sprite.layer
                };
            }
        }
    }

    public async animateSprite(spriteId: string, animationId: string) {
        const sprite: Sprite = this.getSprite(spriteId);
        if (sprite) {
            await Framework.animateSprite(spriteId, animationId, sprite);
        }
    }

    public async moveSprite(spriteId: string, x: number, y: number, duration: number) {
        const entity: any = this._mapController.findEntity(spriteId);
        if (entity) {
            await Framework.moveEntity(entity, x, y, duration);
        }
    }

    public resetTriggers(spriteId: string) {
        const entity: any = this._mapController.findEntity(spriteId);
        if (entity && entity.sprite) {
            const sprite: Sprite = entity.sprite;
            sprite.triggerEntity.resetHitChecks();
        }
    }

    public setSpriteLocation(spriteId: string, x: number, y: number, layer: number, inTiles: boolean) {
        const entity: any = this._mapController.findEntity(spriteId);
        if (entity) {
            if (inTiles) {
                x *= this.getMap().tileWidth;
                y *= this.getMap().tileHeight;
            }
            entity.x = x;
            entity.y = y;
            entity.sprite.layer = layer;
            Framework.trigger(Framework.EventType.Invalidate);
        }
    }

    public setSpriteAnimation(spriteId: string, animationId: string) {
        const sprite: Sprite = this.getSprite(spriteId);
        if (sprite) {
            sprite.changeGraphics(animationId);
        }
    }

    // ------------------------------------------------------------------------
    // Geometry
    // ------------------------------------------------------------------------

    public raycast(origin: any, direction: any, maxDistance: any): any {
        // REFACTOR: Implement me
    }

    public getAngleBetweenPoints(x1: number, y1: number, x2: number, y2: number): number {
        return Math.atan2(y1 - y2, x1 - x2);
    }

    public getDistanceBetweenPoints(x1: number, y1: number, x2: number, y2: number): number {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)); // Simple Pythagora's theorem.
    }

    // ------------------------------------------------------------------------
    // Global
    // ------------------------------------------------------------------------

    public getGlobal(id: string): any {
        return this._globals[id];
    }

    public setGlobal(id: string, value: any) {
        this._globals[id] = value;
    }

    public removeGlobal(id: string) {
        delete this._globals[id];
    }

    // ------------------------------------------------------------------------
    // Keyboard
    // ------------------------------------------------------------------------

    public registerKeyDown(key: string, callback: any, global: boolean) {
        if (global) {
            this._core.keyDownHandlers[Framework.getKey(key)] = callback;
        } else {
            this._core.keyboardHandler.downHandlers[Framework.getKey(key)] = callback;
        }
    }

    public unregisterKeyDown(key: string, global: boolean) {
        if (global) {
            delete this._core.keyDownHandlers[Framework.getKey(key)];
        } else {
            delete this._core.keyboardHandler.downHandlers[Framework.getKey(key)];
        }
    }

    public registerKeyUp(key: string, callback: any, global: boolean) {
        if (global) {
            this._core.keyUpHandlers[Framework.getKey(key)] = callback;
        } else {
            this._core.keyboardHandler.upHandlers[Framework.getKey(key)] = callback;
        }
    }

    public unregisterKeyUp(key: string, global: boolean) {
        if (global) {
            delete this._core.keyUpHandlers[Framework.getKey(key)];
        } else {
            delete this._core.keyboardHandler.upHandlers[Framework.getKey(key)];
        }
    }

    // ------------------------------------------------------------------------
    // Mouse
    // ------------------------------------------------------------------------

    public registerMouseDown(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseDownHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseDownHandler = callback;
        }
    }

    public unregisterMouseDown(global: boolean) {
        if (global) {
            Core.getInstance().mouseDownHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseDownHandler = null;
        }
    }

    public registerMouseUp(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseUpHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseUpHandler = callback;
        }
    }

    public unregisterMouseUp(global: boolean) {
        if (global) {
            Core.getInstance().mouseUpHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseUpHandler = null;
        }
    }

    public registerMouseClick(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseClickHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseClickHandler = callback;
        }
    }

    public unregisterMouseClick(global: boolean) {
        if (global) {
            Core.getInstance().mouseClickHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseClickHandler = null;
        }
    }

    public registerMouseDoubleClick(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseDoubleClickHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseDoubleClickHandler = callback;
        }
    }

    public unregisterMouseDoubleClick(global: boolean) {
        if (global) {
            Core.getInstance().mouseDoubleClickHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseDoubleClickHandler = null;
        }
    }

    public registerMouseMove(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseMoveHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseMoveHandler = callback;
        }
    }

    public unregisterMouseMove(global: boolean) {
        if (global) {
            Core.getInstance().mouseMoveHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseMoveHandler = null;
        }
    }

    // ------------------------------------------------------------------------
    // Audio
    // ------------------------------------------------------------------------

    public playAudio(id: string, loop: boolean, volume: number = 1.0) {
        const repeatCount: number = loop ? -1 : 1;
        Framework.playAudio(id, repeatCount, volume);
    }

    public stopAudio(id?: string) {
        Framework.stopAudio(id);
    }

    // ------------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------------

    public async sleep(duration: number) {
        await new Promise(resolve => setTimeout(resolve, duration));
    }

    public getRandom(min: number, max: number): number {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    public getScale(): number {
        return Framework.getViewport()._scale;
    }

    public getViewport(): any {
        return Framework.getViewport();
    }

    public measureText(text: string): any {
        const ctx: CanvasRenderingContext2D = this._getDrawingContext(this._canvases.default);
        ctx.font = this._font;
        return {
            width: Math.round(ctx.measureText(text).width),
            height: Math.round(parseInt(ctx.font))
        };
    }

    public restart() {
        location.reload(); // Cheap way to implement game restart for the moment.
    }

    // ------------------------------------------------------------------------
    // Misc
    // ------------------------------------------------------------------------

    public attachControls(id: string) {
        const entity = this._mapController.findEntity(id);
        if (entity) {
            entity.addComponent("CustomControls");
        }
    }

}
