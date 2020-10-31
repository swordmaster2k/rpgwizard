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
import { Assets, TileData } from "../rpgcode/rpgcode.js";
import { Framework } from "../framework.js";

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

    public async loadAssets(assets: Assets) {
        await Framework.loadAssets(assets);
    }

    public removeAssets(assets: Assets) {
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
            Framework.trigger("Invalidate");
        }
    }

    public clear(canvasId: string) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const canvasElement: HTMLCanvasElement = canvas.canvasElement;
            canvasElement.getContext("2d").clearRect(0, 0, canvasElement.width, canvasElement.height);
            canvas.render = true;
            Framework.trigger("Invalidate");
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
            const imageData: ImageData = ctx.getImageData(x, y, 1, 1);
            imageData.data[0] = this._rgba.r;
            imageData.data[1] = this._rgba.g;
            imageData.data[2] = this._rgba.b;
            imageData.data[3] = this._rgba.a * 255;
            ctx.putImageData(imageData, x, y);
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
            ctx.beginPath();
            ctx.arc(x, y, radius, 0, 2 * Math.PI);
            ctx.stroke();
        }
    }

    public drawImage(canvasId: string, file: string, x: number, y: number, width: number, height: number, rotation: number) {
        const canvas: Canvas = this._canvases[canvasId];
        const image: ImageBitmap = Framework.getImage(file);
        if (canvas && image) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            // rotate around center point
            x += width / 2;
            y += height / 2;
            ctx.translate(x, y);
            ctx.rotate(rotation);
            ctx.drawImage(image, -width / 2, -height / 2, width, height);
            ctx.rotate(-rotation);
            ctx.translate(-x, -y);
        }
    }

    public drawImagePart(canvasId: string, file: string, srcX: number, srcY: number, srcWidth: number, srcHeight: number, destX: number, destY: number, destWidth: number, destHeight: number, rotation: number) {
        const canvas: Canvas = this._canvases[canvasId];
        const image: ImageBitmap = Framework.getImage(file);
        if (canvas && image) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            // rotate around center point
            destX += destWidth / 2;
            destY += destHeight / 2;
            ctx.translate(destX, destY);
            ctx.rotate(rotation);
            ctx.drawImage(image, srcX, srcY, srcWidth, srcHeight, -destWidth / 2, -destHeight / 2, destWidth, destHeight);
            ctx.rotate(-rotation);
            ctx.translate(-destX, -destY);
        }
    }

    public drawLine(canvasId: string, x1: number, y1: number, x2: number, y2: number, lineWidth: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.lineWidth = lineWidth;
            ctx.strokeStyle = this._getStrokeStyle();
            ctx.beginPath();
            ctx.moveTo(x1, y1);
            ctx.lineTo(x2, y2);
            ctx.stroke();
        }
    }

    public drawRect(canvasId: string, x: number, y: number, width: number, height: number, lineWidth: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.lineWidth = lineWidth;
            ctx.strokeStyle = this._getStrokeStyle();
            ctx.strokeRect(x, y, width, height);
        }
    }

    public drawRoundedRect(canvasId: string, x: number, y: number, width: number, height: number, lineWidth: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.lineWidth = lineWidth;
            ctx.strokeStyle = this._getStrokeStyle();
            ctx.beginPath();
            ctx.moveTo(x + radius, y);
            ctx.lineTo(x + width - radius, y);
            ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
            ctx.lineTo(x + width, y + height - radius);
            ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
            ctx.lineTo(x + radius, y + height);
            ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
            ctx.lineTo(x, y + radius);
            ctx.quadraticCurveTo(x, y, x + radius, y);
            ctx.closePath();
            ctx.stroke();
        }
    }

    public drawText(canvasId: string, x: number, y: number, text: string) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            ctx.font = this._font;
            ctx.fillText(text, x, y);
        }
    }

    public fillCircle(canvasId: string, x: number, y: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            ctx.beginPath();
            ctx.arc(x, y, radius, 0, 2 * Math.PI);
            ctx.fill();
        }
    }

    public fillRect(canvasId: string, x: number, y: number, width: number, height: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            ctx.fillRect(x, y, width, height);
        }
    }

    public fillRoundedRect(canvasId: string, x: number, y: number, width: number, height: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            ctx.beginPath();
            ctx.moveTo(x + radius, y);
            ctx.lineTo(x + width - radius, y);
            ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
            ctx.lineTo(x + width, y + height - radius);
            ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
            ctx.lineTo(x + radius, y + height);
            ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
            ctx.lineTo(x, y + radius);
            ctx.quadraticCurveTo(x, y, x + radius, y);
            ctx.closePath();
            ctx.fill();
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

    public async addLayerImage(image: any, layer: number) {

    }

    public removeLayerImage(imageId: string, layer: number) {

    }

    public getTileData(x: number, y: number, layer: number): TileData {
        return null;
    }

    public replaceTile(x: number, y: number, layer: number, tileset: string, tileIndex: number) {

    }

    public removeTile(x: number, y: number, layer: number) {

    }

    // ------------------------------------------------------------------------
    // Sprite
    // ------------------------------------------------------------------------

    public async addSprite(sprite: any) {

    }

    public removeSprite(spriteId: string) {

    }

    public getSpriteLocation(spriteId: string, inTiles: boolean, includeOffset: boolean): any {

    }

    public async animateSprite(spriteId: string, animationId: string) {

    }

    public async moveSprite(spriteId: string, direction: string, distance: number) {

    }

    public async moveSpriteTo(spriteId: string, x: number, y: number, duration: number) {

    }

    public resetTriggers(spriteId: string) {

    }

    public setSpriteLocation(spriteId: string, x: number, y: number, layer: number, inTiles: boolean) {

    }

    public setSpriteAnimation(spriteId: string, animationId: string) {

    }

    // ------------------------------------------------------------------------
    // Geometry
    // ------------------------------------------------------------------------

    public raycast(origin: any, direction: any, maxDistance: any): any {

    }

    public getAngleBetweenPoints(x1: number, y1: number, x2: number, y2: number): number {
        return 0;
    }

    public getDistanceBetweenPoints(x1: number, y1: number, x2: number, y2: number): number {
        return 0;
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
    // Utility
    // ------------------------------------------------------------------------

    public getRandom(min: number, max: number): number {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    public getScale(): any {

    }

    public getViewport(): any {

    }

    public measureText(text: string): any {

    }

    public restart() {
        location.reload(); // Cheap way to implement game restart for the moment.
    }

    // ------------------------------------------------------------------------
    // Keyboard
    // ------------------------------------------------------------------------

    public registerKeyDown(key: string, callback: any, global: boolean) {

    }

    public unregisterKeyDown(key: string, global: boolean) {

    }

    public registerKeyUp(key: string, callback: any, global: boolean) {

    }

    public unregisterKeyUp(key: string, global: boolean) {

    }

    // ------------------------------------------------------------------------
    // Mouse
    // ------------------------------------------------------------------------

    public registerMouseDown(callback: any, global: boolean) {

    }

    public unregisterMouseDown(global: boolean) {

    }

    public registerMouseUp(callback: any, global: boolean) {

    }

    public unregisterMouseUp(global: boolean) {

    }

    public registerMouseClick(callback: any, global: boolean) {

    }

    public unregisterMouseClick(global: boolean) {

    }

    public registerMouseDoubleClick(callback: any, global: boolean) {

    }

    public unregisterMouseDoubleClick(global: boolean) {

    }

    public registerMouseMove(callback: any, global: boolean) {

    }

    public unregisterMouseMove(global: boolean) {

    }

    // ------------------------------------------------------------------------
    // Audio
    // ------------------------------------------------------------------------

    public playAudio(id: string, loop: boolean, volume: number) {
        const repeatCount: number = loop ? -1 : 1;
        Framework.playAudio(id, repeatCount, volume);
    }

    public stopAudio(id?: string) {
        Framework.stopAudio(id);
    }

    // ------------------------------------------------------------------------
    // Misc
    // ------------------------------------------------------------------------

    // REFACTOR: use a getLayerImage() instead?
    public updateLayerImage(image: any, layer: number) {

    }

    public async loadMap(map: string) {
        await this._mapController.switchMap(map, 5, 5, 1);
    }

    // REFACTOR: Stick all sprite attrs onto entity?
    public getEntity(id: string): any {
        return this._mapController.findEntity(id);
    }

    public getSprite(id: string): Sprite {
        const entity = this._mapController.findEntity(id);
        if (entity && entity.sprite) {
            return entity.sprite;
        }
        return null;
    }

    public attachControls(id: string) {
        const entity = this._mapController.findEntity(id);
        if (entity) {
            entity.addComponent("CustomControls");
        }
    }

}
