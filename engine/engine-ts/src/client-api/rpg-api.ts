/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

// To enable JSDoc grouping of functions
/** @namespace Draw2D       */
/** @namespace Geometry     */
/**
 * For more information, see [Asset Management]{@tutorial 03-Asset-Management}
 * @namespace Asset
 */
/** @namespace Global       */
/** @namespace Sprite       */
/** @namespace Script       */
/** @namespace Map          */
/** @namespace Canvas       */
/** @namespace Audio        */
/** @namespace Keyboard     */
/** @namespace Mouse        */
/** @namespace File         */
/** @namespace Util         */

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

    get keys(): any {
        return Framework.getKeys();
    }

    get gradient(): string {
        return this._gradient;
    }

    set gradient(gradient: string) {
        this._gradient = gradient;
    }

    // ------------------------------------------------------------------------
    // Asset
    // ------------------------------------------------------------------------

    /**
     * Returns a true or false value indicating whether an asset is currently loaded.
     *
     * Note: For audio files use the identifier key, not the filename.
     *
     * @example
     * console.log(rpg.isAssetLoaded("Hero/attack_east.png", "image"));
     * console.log(rpg.isAssetLoaded("intro", "audio"));
     *
     * @memberof Asset
     * @alias isAssetLoaded
     * @param {string} assetId Id of the asset including any subfolders.
     * @param {string} type Either "image" or "audio".
     * @returns {boolean}
     */
    public isAssetLoaded(assetId: string, type: string): boolean {
        return Framework.isAssetLoaded(assetId, type);
    }

    /**
     * Loads the requested assets into the engine.
     *
     * For more information, see [Asset Management]{@tutorial 03-Asset-Management}
     *
     * @example
     * // Assets we want to load
     * const assets = {
     *  "audio": {
     *      "intro": "intro.mp3"
     *  },
     *  "images": [
     *      "block.png",
     *      "mwin_small.png",
     *      "sword_profile_1_small.png",
     *      "startscreen.png"
     *  ]
     * };
     *
     * // Wait for the assets to load
     * await rpg.loadAssets(assets);
     *
     * @memberof Asset
     * @alias loadAssets
     * @param {Object} assets
     */
    public async loadAssets(assets: Framework.Assets) {
        await Framework.loadAssets(assets);
    }

    /**
     * Removes assets from the engine and frees up the memory allocated to them.
     *
     * @example
     * // Assets we want to remove
     * const assets = {
     *  "audio": {
     *      "intro": "intro.mp3"
     *  },
     *  "images": [
     *      "block.png",
     *      "mwin_small.png",
     *      "sword_profile_1_small.png",
     *      "startscreen.png"
     *  ]
     * };
     *
     * // Remove some assets after use
     * rpg.removeAssets(assets);
     *
     * @memberof Asset
     * @alias removeAssets
     * @param {Object} assets TODO
     */
    public removeAssets(assets: Framework.Assets) {
        Framework.removeAssets(assets);
    }

    /**
     * Gets the image object if it has been loaded into the engine already, otherwise
     * it returns undefined.
     *
     * @example
     * const image = rpg.getImage("life.png");
     * console.log(image.width);
     * console.log(image.height);
     *
     * @memberof Asset
     * @alias getImage
     * @param {string} file The relative file path to the image.
     * @returns {Image.ImageInfo} An object containing information about the image.
     */
    public getImage(file: string): ImageBitmap {
        return Framework.getImage(file);
    }

    /**
     * TODO
     *
     * @example
     * // TODO
     *
     * @memberof Asset
     * @alias loadJson
     * @param {string} path TODO
     * @returns TODO
     */
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

    /**
     * TODO
     *
     * @example
     * // TODO
     *
     * @memberof Asset
     * @alias saveJson
     * @param {string} json TODO
     */
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

    /**
     * Creates a canvas with the specified width, height, and ID. This canvas will
     * not be drawn until render is called with its ID.
     *
     * @example
     * rpg.createCanvas("my-canvas", 640, 480);
     * rpg.drawText("my-canvas", 270, 300, "Hello world!");
     * rpg.render("my-canvas");
     *
     * @memberof Canvas
     * @alias createCanvas
     * @param {string} canvasId ID to assign to the canvas.
     * @param {number} width In pixels.
     * @param {number} height In pixels.
     */
    public createCanvas(canvasId: string, width: number, height: number) {
        const canvasElement: HTMLCanvasElement = document.createElement("canvas");
        canvasElement.width = width;
        canvasElement.height = height;
        const canvas: Canvas = new Canvas(canvasElement, false, 0, 0);
        this._canvases[canvasId] = canvas;
    }

    /**
     * Removes the canvas with the assigned ID from the engine.
     *
     * @example
     * rpg.removeCanvas("my-canvas");
     *
     * @memberof Canvas
     * @alias removeCanvas
     * @param {string} canvasId ID of the canvas to remove.
     */
    public removeCanvas(canvasId: string) {
        delete this._canvases[canvasId];
    }

    /**
     * Renders a canvas to the screen.
     *
     * @example
     * rpg.render("my-canvas");
     *
     * @memberof Canvas
     * @alias render
     * @param {string} canvasId ID of the canvas to render.
     */
    public render(canvasId: string) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            canvas.render = true;
            Framework.trigger(Framework.EventType.Invalidate);
        }
    }

    /**
     * Clears the contents of a canvas.
     *
     * @example
     * rpg.clear("my-canvas");
     *
     * @memberof Canvas
     * @alias clear
     * @param {string} canvasId ID of the canvas to clear.
     */
    public clear(canvasId: string) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const canvasElement: HTMLCanvasElement = canvas.canvasElement;
            canvasElement.getContext("2d").clearRect(0, 0, canvasElement.width, canvasElement.height);
            canvas.render = true;
            Framework.trigger(Framework.EventType.Invalidate);
        }
    }

    /**
     * Sets the canvas's position relative to the viewport.
     *
     * @example
     * rpg.setCanvasPosition("my-canvas", 100, 100);
     *
     * @memberof Canvas
     * @alias setCanvasPosition
     * @param {string} canvasId ID of the canvas to move.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     */
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

    /**
     * Sets the current color to use for all drawing operations.
     *
     * @example
     * rpg.setColor(255, 0, 0, 1.0); // Solid red
     *
     * @memberof Draw2D
     * @alias setColor
     * @param {number} r
     * @param {number} g
     * @param {number} b
     * @param {number} a
     */
    public setColor(r: number, g: number, b: number, a: number) {
        this._rgba = new Rgba(r, g, b, a);
    }

    /**
     * Sets the global alpha for all drawing operations.
     *
     * @example
     * rpg.setAlpha(0.5);
     *
     * @memberof Draw2D
     * @alias setColor
     * @param {number} alpha
     */
    public setAlpha(alpha: number) {
        this._rgba.a = alpha;
    }

    /**
     * Sets the global font for all drawing operations.
     *
     * @example
     * rpg.setFont(8, "Lucida Console");
     *
     * @memberof Draw2D
     * @alias setFont
     * @param {number} size In pixels.
     * @param {string} family E.g. Arial, Comic Sans, etc.
     */
    public setFont(size: number, family: string) {
        this._font = size + "px " + family;
    }

    /**
     * Gets the pixel ImageData at the (x, y) coordinate on the canvas.
     *
     * @example
     * // Draw a rectangle on the canvas and render it
     * rpg.createCanvas("my-canvas", 640, 480);
     * rpg.fillRect("my-canvas", 0, 0, 100, 100);
     * rpg.render("my-canvas");
     *
     * // Get the red pixel at (50, 50) from the rectangle
     * const imageData = rpg.getPixel("my-canvas", 50, 50);
     * const rgba = imageData.data;
     *
     * // Show the RGBA values of the pixel
     * alert("R, G, B, A (" + rgba[0] + ", " + rgba[1] + ", " + rgba[2] + ", " + rgba[3] + ")");
     *
     * @memberof Draw2D
     * @alias getPixel
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @returns {Draw2D.ImageData} An ImageData object.
     */
    public getPixel(canvasId: string, x: number, y: number): any {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const context: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            return context.getImageData(x, y, 1, 1);
        }
    }

    /**
     * Sets the pixel ImageData at the (x, y) coordinate on the canvas.
     *
     * @example
     * // Draw a rectangle on the canvas and render it
     * rpg.createCanvas("my-canvas", 640, 480);
     * rpg.fillRect("my-canvas", 0, 0, 100, 100);
     * rpg.render("my-canvas");
     *
     * // Set a pixel to green at (50, 50) from the rectangle
     * rpg.setColor(0, 255, 0, 1.0);
     * rpg.setPixel("my-canvas", 50, 50);
     *
     * @memberof Draw2D
     * @alias setPixel
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     */
    public setPixel(canvasId: string, x: number, y: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            Draw2D.putImageData(ctx, x, y, this._rgba);
        }
    }

    /**
     * Draws the source canvas onto the target canvas. Useful for combining multiple
     * canvases together.
     *
     * @example
     * // Load assets
     * const assets = {
     *      "images": [ "life.png" ]
     * };
     * await rpg.loadAssets(assets);

     * // Source canvas
     * rpg.createCanvas("life-icon", 32, 32);
     * rpg.drawImage("life.png", 0, 0, 32, 32, 0);
     *
     * // Target canvas
     * rpg.createCanvas("buffer", 640, 480);
     *
     * // Draw 3 hearts onto the buffer canvas
     * for (let i = 1; i < 4; i++) {
     *      rpg.drawOntoCanvas("buffer", "life-icon", i * 32, 430, 32, 32);
     * }
     *
     * rpg.render("buffer");
     *
     * @memberof Draw2D
     * @alias drawOntoCanvas
     * @param {string} targetId The ID of the target canvas.
     * @param {string} sourceId The ID of the source canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {number} width In pixels.
     * @param {number} height In pixels.
     */
    public drawOntoCanvas(targetId: string, sourceId: string, x: number, y: number, width: number, height: number) {
        const target: Canvas = this._canvases[targetId];
        const source: Canvas = this._canvases[sourceId];
        if (target && source) {
            const targetCtx: CanvasRenderingContext2D = this._getDrawingContext(target);
            targetCtx.drawImage(source.canvasElement, x, y, width, height);
        }
    }

    /**
     * Draws a circle onto the canvas.
     *
     * @example
     * // Create a canvas and draw a red circle on it
     * rpgcode.createCanvas("my-canvas", 640, 480);
     * rpgcode.setColor(255, 0, 0, 1.0);
     * rpgcode.drawCircle("my-canvas", 100, 100, 25);
     * rpgcode.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias drawCircle
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {number} radius In pixels.
     */
    public drawCircle(canvasId: string, x: number, y: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.strokeStyle = this._getStrokeStyle();
            Draw2D.drawCircle(ctx, x, y, radius);
        }
    }

    /**
     *  Draws an image onto a canvas.
     *
     * @example
     * rpg.drawImage("life-icon", 0, 0, 32, 32, 0);
     * rpg.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias drawImage
     * @param {string} canvasId ID of the canvas.
     * @param {string} file The relative path to the image.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {number} width In pixels.
     * @param {number} height In pixels.
     * @param {number} rotation In radians.
     */
    public drawImage(canvasId: string, file: string, x: number, y: number, width: number, height: number, rotation: number) {
        const canvas: Canvas = this._canvases[canvasId];
        const image: ImageBitmap = Framework.getImage(file);
        if (canvas && image) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            Draw2D.drawImage(ctx, image, x, y, width, height, rotation);
        }
    }

    /**
     * Draws part of image an onto a canvas.
     *
     * @example
     * // Draw part of the image onto the canvas we specified
     * rpg.drawImagePart("life-icon", "life.png", 0, 0, 8, 8, 0, 0, 32, 32, 0);
     * rpg.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias drawImagePart
     * @param {string} canvasId ID of the canvas.
     * @param {string} file The relative path to the image.
     * @param {number} srcX The start position x in pixels from the source image.
     * @param {number} srcY The start position y in pixels from the source image.
     * @param {number} srcWidth In pixels from the source image.
     * @param {number} srcHeight In pixels from the source image.
     * @param {number} destX The start position x in pixels on the destination canvas.
     * @param {number} destY The start position y in pixels on the destination canvas.
     * @param {number} destWidth In pixels on the destination canvas.
     * @param {number} destHeight In pixels on the destination canvas.
     * @param {number} rotation In radians.
     */
    public drawImagePart(canvasId: string, file: string, srcX: number, srcY: number, srcWidth: number, srcHeight: number, destX: number, destY: number, destWidth: number, destHeight: number, rotation: number) {
        const canvas: Canvas = this._canvases[canvasId];
        const image: ImageBitmap = Framework.getImage(file);
        if (canvas && image) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            Draw2D.drawImagePart(ctx, image, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, rotation);
        }
    }

    /**
     * Draws a line onto the canvas.
     *
     * @example
     * rpg.drawLine("my-canvas", 25, 25, 50, 50, 1);
     * rpg.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias drawLine
     * @param {string} canvasId ID of the canvas.
     * @param {number} x1 In pixels.
     * @param {number} y1 In pixels.
     * @param {number} x2 In pixels.
     * @param {number} y2 In pixels.
     * @param {number} lineWidth In pixels.
     */
    public drawLine(canvasId: string, x1: number, y1: number, x2: number, y2: number, lineWidth: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.strokeStyle = this._getStrokeStyle();
            Draw2D.drawLine(ctx, x1, y1, x2, y2, lineWidth);
        }
    }

    /**
     * Draws a rectangle onto the canvas.
     *
     * @example
     * rpg.drawRect("my-canvas", 0, 0, 100, 100, 1);
     * rpg.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias drawRect
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {number} width In pixels.
     * @param {number} height In pixels.
     * @param {number} lineWidth In pixels.
     */
    public drawRect(canvasId: string, x: number, y: number, width: number, height: number, lineWidth: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.strokeStyle = this._getStrokeStyle();
            Draw2D.drawRect(ctx, x, y, width, height, lineWidth);
        }
    }

    /**
     * Draws a rectangle with rounded edges onto the canvas.
     *
     * @example
     * rpg.drawRoundedRect("my-canvas", 0, 0, 100, 100, 1, 5);
     * rpg.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias drawRoundedRect
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {number} width In pixels.
     * @param {number} height In pixels.
     * @param {number} lineWidth In pixels
     * @param {number} radius In radians.
     */
    public drawRoundedRect(canvasId: string, x: number, y: number, width: number, height: number, lineWidth: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.strokeStyle = this._getStrokeStyle();
            Draw2D.drawRoundedRect(ctx, x, y, width, height, lineWidth, radius);
        }
    }

    /**
     * Draws the text on the canvas starting at the specified (x, y) position.
     *
     * @example
     * rpg.drawText("my-canvas", 100, 100, "Hello world!");
     * rpg.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias drawText
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {string} text The text.
     */
    public drawText(canvasId: string, x: number, y: number, text: string) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            Draw2D.drawText(ctx, x, y, text, this._font);
        }
    }

    /**
     * Fills a solid circle onto the canvas.
     *
     * @example
     * rpg.fillCirlce("my-canvas", 100, 100, 25);
     * rpg.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias fillCircle
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {number} radius In pixels.
     */
    public fillCircle(canvasId: string, x: number, y: number, radius: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            Draw2D.drawCircle(ctx, x, y, radius);
        }
    }

    /**
     * Fills a solid rectangle onto the canvas.
     *
     * @example
     * rpg.fillRect("my-canvas", 50, 50, 100, 100);
     * rpg.render("my-canvas");
     *
     * @memberof Draw2D
     * @alias fillRect
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {number} width In pixels.
     * @param {number} height In pixels.
     */
    public fillRect(canvasId: string, x: number, y: number, width: number, height: number) {
        const canvas: Canvas = this._canvases[canvasId];
        if (canvas) {
            const ctx: CanvasRenderingContext2D = this._getDrawingContext(canvas);
            ctx.fillStyle = this._getFillStyle();
            Draw2D.fillRect(ctx, x, y, width, height);
        }
    }

    /**
     * Fills a solid rounded rectangle on the canvas.
     *
     * @example
     * rpg.fillRoundedRect("my-canvas", 50, 50, 100, 100, 5);
     *
     * @memberof Draw2D
     * @alias fillRoundedRect
     * @param {string} canvasId ID of the canvas.
     * @param {number} x In pixels.
     * @param {number} y In pixels.
     * @param {number} width In pixels.
     * @param {number} height In pixels.
     * @param {number} radius In pixels.
     */
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
        ctx.imageSmoothingEnabled = false;
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

    /**
     * Gets the currently loaded Map asset.
     *
     * @example
     * const currMap = rpg.getMap();
     * console.log(currMap);
     *
     * @memberof Map
     * @alias getMap
     * @returns {Asset.MapAsset}
     */
    public getMap(): Map {
        return this._mapController.mapEntity.map;
    }

    /**
     * Loads the requested map.
     *
     * @example
     * await rpg.loadMap("my-world.map");
     *
     * @memberof Map
     * @alias loadMap
     * @param {string} map Name of the map to load.
     */
    public async loadMap(map: string) {
        await this._mapController.switchMap(map, 5, 5, 1);
    }

    /**
     * Adds the layer image to the requested layer, it will be rendered immediately
     * after being added to the map.
     *
     * @example
     * // TODO
     *
     * @memberof Map
     * @alias addLayerImage
     * @param {string} imageId ID of the layer image.
     * @param {string} layer Layer index on the map, the first layer starts at 0.
     * @param {string} image The name of the image.
     */
    public async addLayerImage(imageId: string, layer: number, image: MapImage) {
        if (layer < this.getMap().layers.length) {
            const mapLayer: MapLayer = this.getMap().layers[layer];
            mapLayer.images[imageId] = image;
        }
    }

    /**
     * Removes the layer image from the map.
     *
     * @example
     * rpg.removeLayerImage("my-image", 1);
     *
     * @memberof Map
     * @alias removeLayerImage
     * @param {string} imageId ID of the layer image.
     * @param {string} layer Layer index on the map, the first layer starts at 0.
     */
    public removeLayerImage(imageId: string, layer: number) {
        if (layer < this.getMap().layers.length) {
            const mapLayer: MapLayer = this.getMap().layers[layer];
            delete mapLayer.images[imageId];
        }
    }

    /**
     * Gets the data associated with a tile on the map as set in the Editor's
     * Tileset properties.
     *
     * @example
     * // Get the tile data at (10, 5, 1), and log the output
     * const tileData = rpg.getTileData(10, 5, 1);
     * console.log(tileData);
     *
     * @memberof Map
     * @alias getTileData
     * @param {number} x The x position in tiles.
     * @param {number} y The y position in tiles.
     * @param {number} layer The layer the tile is on.
     * @returns {Map.TileData} An object containing the tile's properties.
     * @throws "layer out of range" or "tile out of range"
     */
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

    /**
     * Replaces a tile at the supplied (x, y, layer) position.
     *
     * @example
     * rpg.replaceTile(11, 10, 0, "tileset1.tileset", 81);
     *
     * @memberof Map
     * @alias replaceTile
     * @param {number} x The x position in tiles.
     * @param {number} y The y position in tiles.
     * @param {number} layer The layer the tile is on.
     * @param {string} tilesetFile The name of the Tileset of the replacement tile.
     * @param {number} tileIndex The index of the tile in the replacement Tileset.
     */
    public replaceTile(x: number, y: number, layer: number, tilesetFile: string, tileIndex: number) {
        const tileset: Tileset = this._core.cache.get(tilesetFile);
        const tile: ImageData = tileset.getTile(tileIndex);
        this.getMap().replaceTile(x, y, layer, tile);
    }

    /**
     * Removes the specified tile from the map.
     *
     * @example
     * // Removes the tile at (x: 11, y: 9, layer: 1)
     * rpg.removeTile(11, 9, 1);
     *
     * @memberof Map
     * @alias removeTile
     * @param {number} x TODO
     * @param {number} y TODO
     * @param {number} layer TODO
     */
    public removeTile(x: number, y: number, layer: number) {
        this.getMap().removeTile(x, y, layer);
    }

    // ------------------------------------------------------------------------
    // Sprite
    // ------------------------------------------------------------------------

    /**
     * Gets the sprite by its ID.
     *
     * @example
     * const sprite = rpg.getSprite("my-sprite");
     * console.log(sprite);
     *
     * @memberof Sprite
     * @alias getSprite
     * @param {string} spriteId
     * @returns {Asset.Sprite}
     */
    public getSprite(spriteId: string): Sprite {
        const entity = this._mapController.findEntity(spriteId);
        if (entity && entity.sprite) {
            return entity.sprite;
        }
        return null;
    }

    /**
     * Adds a sprite to the current map.
     *
     * @example
     * // TODO
     *
     * @memberof Sprite
     * @alias addSprite
     * @param {string} spriteId TODO
     * @param {Object} sprite TODO
     */
    public async addSprite(spriteId: string, sprite: MapSprite) {
        if (sprite.startLocation.layer < this.getMap().layers.length) {
            sprite.entity = await this._mapController.loadSprite(spriteId, sprite);
            const mapLayer: MapLayer = this.getMap().layers[sprite.startLocation.layer];
            mapLayer.sprites[spriteId] = sprite;
        }
    }

    /**
     * Removes a sprite from the current map.
     *
     * @example
     * rpg.removeSprite("my-sprite");
     *
     * @memberof Sprite
     * @alias removeSprite
     * @param {string} spriteId TODO
     */
    public removeSprite(spriteId: string) {
        const sprite: Sprite = this.getSprite(spriteId);
        if (sprite) {
            const entity = this._mapController.findEntity(spriteId);
            if (entity && entity.sprite) {
                entity.destroy();
            }
            delete this.getMap().layers[sprite.layer].sprites[spriteId];
        }
    }

    /**
     * Gets the sprites's current direction.
     *
     * @example
     * const direction = rpg.getSpriteDirection("my-sprite");
     * console.log(direction);
     *
     * @memberof Sprite
     * @alias getSpriteDirection
     * @param {string} spriteId TODO
     * @returns {('NORTH'|'SOUTH'|'EAST'|'WEST'|'NORTH_EAST'|'SOUTH_EAST'|'NORTH_WEST'|'SOUTH_WEST')}
     */
    public getSpriteDirection(spriteId: string): string {
        const sprite: Sprite = this.getSprite(spriteId);
        if (sprite) {
            // User friendly rewrite of Crafty constants.
            switch (sprite.direction) {
            case "n":
                return "NORTH";
            case "s":
                return "SOUTH";
            case "e":
                return "EAST";
            case "w":
                return "WEST";
            case "ne":
                return "NORTH_EAST";
            case "se":
                return "SOUTH_EAST";
            case "nw":
                return "NORTH_WEST";
            case "sw":
                return "SOUTH_WEST";
            }
        }
    }

    /**
     * Gets the sprites's current location, optionally including the visual offset
     * that happens when maps are smaller than the viewport dimensions.
     *
     * @example
     * const location = rpg.getSpriteLocation("my-sprite", false, false);
     * console.log(location);
     *
     * @memberof Sprite
     * @alias getSpriteLocation
     * @param {string} spriteId ID of the sprite.
     * @param {boolean} inTiles Should the location be in tiles, otherwise pixels.
     * @param {boolean} includeOffset Should the location include the visual map offset.
     * @returns {Geometry.Location} An object containing the sprite's location.
     */
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

    /**
     * Animates the sprite using the requested animation.
     *
     * @example
     * await rpg.animateSprite("my-sprite", "DANCE");
     *
     * @memberof Sprite
     * @alias animateSprite
     * @param {string} spriteId ID of the sprite.
     * @param {string} animationId ID of the animation on the sprite.
     */
    public async animateSprite(spriteId: string, animationId: string) {
        const sprite: Sprite = this.getSprite(spriteId);
        if (sprite) {
            await Framework.animateSprite(spriteId, animationId, sprite);
        }
    }

    /**
     * Moves the sprite the sprite to the (x, y) location.
     *
     * @example
     * await rpg.moveSprite("my-sprite", 100, 100, 1000)
     *
     * @memberof Sprite
     * @alias moveSprite
     * @param {string} spriteId ID of the sprite.
     * @param {number} x TODO
     * @param {number} y TODO
     * @param {number} duration TODO
     */
    public async moveSprite(spriteId: string, x: number, y: number, duration: number) {
        const entity: any = this._mapController.findEntity(spriteId);
        if (entity) {
            await Framework.moveEntity(entity, x, y, duration);
        }
    }

    /**
     * TODO
     *
     * @example
     * // TODO
     *
     * @memberof Sprite
     * @alias resetTriggers
     * @param {string} spriteId TODO
     */
    public resetTriggers(spriteId: string) {
        const entity: any = this._mapController.findEntity(spriteId);
        if (entity && entity.sprite) {
            const sprite: Sprite = entity.sprite;
            sprite.triggerEntity.resetHitChecks();
        }
    }

    /**
     * Sets the location of the sprite.
     *
     * @example
     * rpg.setSpriteLocation("my-sprite", 10, 10, 1, true);
     *
     * @memberof Sprite
     * @alias setSpriteLocation
     * @param {string} spriteId ID of the sprite.
     * @param {number} x In pixels by default.
     * @param {number} y In pixels by default.
     * @param {number} layer Target layer to put the sprite on.
     * @param {boolean} inTiles Is (x, y) in tile coordinates, defaults to pixels.
     */
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

    /**
     * Sets the sprite's current active animation.
     *
     * @example
     * rpg.setSpriteAnimation("my-sprite", "DANCE");
     *
     * @memberof Sprite
     * @alias setSpriteAnimation
     * @param {string} spriteId ID of the sprite.
     * @param {string} animationId ID of the animation on the sprite.
     */
    public setSpriteAnimation(spriteId: string, animationId: string) {
        const sprite: Sprite = this.getSprite(spriteId);
        if (sprite) {
            sprite.changeGraphics(animationId);
        }
    }

    /**
     * TODO
     *
     * @example
     * // TODO
     *
     * @memberof Sprite
     * @alias attachController
     * @param {string} spriteId TODO
     * @param {Object} controller TODO
     */
    public attachController(spriteId: string, controller: any) {
        const entity = this._mapController.findEntity(spriteId);
        if (entity) {
            Framework.createController(controller);
            entity.addComponent("CustomControls");
        }
    }

    // ------------------------------------------------------------------------
    // Geometry
    // ------------------------------------------------------------------------

    /**
     * Use -1 for infinity
     *
     * @example
     * // TODO
     *
     * @memberof Geometry
     * @alias raycast
     * @param {Object} origin TODO
     * @param {Object} direction TODO
     * @param {Object} maxDistance TODO
     */
    public raycast(origin: any, direction: any, maxDistance: any): any {
        return Framework.raycast(origin, direction, maxDistance);
    }

    /**
     * Gets the angle between two points in radians.
     *
     * @example
     * // Get the angle in radians between two points.
     * const angle = rpg.getAngleBetweenPoints(location.x, location.y, this.x, this.y);
     *
     * @memberof Geometry
     * @alias getAngleBetweenPoints
     * @param {number} x1
     * @param {number} y1
     * @param {number} x2
     * @param {number} y2
     * @returns {number} The angle between the points in radians.
     */
    public getAngleBetweenPoints(x1: number, y1: number, x2: number, y2: number): number {
        return Math.atan2(y1 - y2, x1 - x2);
    }

    /**
     * Gets the straight line distance between two points in pixels.
     *
     * @example
     * // Get the distance between two points in pixels.
     * const distance = rpg.getDistanceBetweenPoints(location.x, location.y, this.x, this.y);
     *
     * @memberof Geometry
     * @alias getDistanceBetweenPoints
     * @param {number} x1
     * @param {number} y1
     * @param {number} x2
     * @param {number} y2
     * @returns {number} The distance in pixels.
     */
    public getDistanceBetweenPoints(x1: number, y1: number, x2: number, y2: number): number {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)); // Simple Pythagora's theorem.
    }

    // ------------------------------------------------------------------------
    // Global
    // ------------------------------------------------------------------------

    /**
     * Gets the value of a global variable.
     *
     * @example
     * const swordActive = rpgcode.getGlobal("swordActive");
     * console.log(swordActive);
     *
     * @memberof Global
     * @alias getGlobal
     * @param {string} id ID of the global variable.
     * @returns {Object} Value of the global variable.
     */
    public getGlobal(id: string): any {
        return this._globals[id];
    }

    /**
     * Sets a global value in the engine, if it doesn't exist it is created.
     *
     * @example
     * // Store a simple boolean.
     * rpg.setGlobal("swordactive", false);
     *
     * // Store a string.
     * rpg.setGlobal("name", "Bob");
     *
     * // Store an object.
     * rpg.setGlobal("shield", {def: 10, price: 100});
     *
     * @memberof Global
     * @alias setGlobal
     * @param {string} id ID of the global variable.
     * @param {Object} value Value of the global variable.
     */
    public setGlobal(id: string, value: any) {
        this._globals[id] = value;
    }

    /**
     * Removes a global variable from the engine.
     *
     * @example
     * rpg.removeGlobal("swordActive");
     *
     * @memberof Global
     * @alias removeGlobal
     * @param {string} id ID of the global variable.
     */
    public removeGlobal(id: string) {
        delete this._globals[id];
    }

    // ------------------------------------------------------------------------
    // Keyboard
    // ------------------------------------------------------------------------

    /**
     * Registers a KeyDown listener for a specific key, for a list of valid key values see:
     *    http://craftyjs.com/api/Crafty-keys.html
     *
     * The callback function will continue to be invoked for every KeyDown event until it
     * is unregistered.
     *
     * @example
     * rpg.registerKeyDown("ENTER", async function(e) {
     *  console.log(e.key + " key is down!");
     * });
     *
     * @memberof Keyboard
     * @alias registerKeyDown
     * @param {string} key The key to listen to.
     * @param {Callback} callback The callback function to invoke when the keyDown event fires.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public registerKeyDown(key: string, callback: any, global: boolean) {
        if (global) {
            this._core.keyDownHandlers[Framework.getKey(key)] = callback;
        } else {
            this._core.keyboardHandler.downHandlers[Framework.getKey(key)] = callback;
        }
    }

    /**
     * Removes a previously registered KeyDown listener.
     *
     * @example
     * rpg.unregisterKeyDown("ENTER");
     *
     * @memberof Keyboard
     * @alias unregisterKeyDown
     * @param {string} key The key to listen to.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public unregisterKeyDown(key: string, global: boolean) {
        if (global) {
            delete this._core.keyDownHandlers[Framework.getKey(key)];
        } else {
            delete this._core.keyboardHandler.downHandlers[Framework.getKey(key)];
        }
    }

    /**
     * Registers a KeyUp listener for a specific key, for a list of valid key values see:
     *    http://craftyjs.com/api/Crafty-keys.html
     *
     * The callback function will continue to be invoked for every KeyUp event until it
     * is unregistered.
     *
     * @example
     * rpg.registerKeyUp("ENTER", async function(e) {
     *  console.log(e.key + " key is up!");
     * });
     *
     * @memberof Keyboard
     * @alias registerKeyUp
     * @param {string} key The key to listen to.
     * @param {Callback} callback The callback function to invoke when the keyDown event fires.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public registerKeyUp(key: string, callback: any, global: boolean) {
        if (global) {
            this._core.keyUpHandlers[Framework.getKey(key)] = callback;
        } else {
            this._core.keyboardHandler.upHandlers[Framework.getKey(key)] = callback;
        }
    }

    /**
     * Removes a previously registered KeyUp listener.
     *
     * @example
     * rpg.unregisterKeyUp("ENTER");
     *
     * @memberof Keyboard
     * @alias unregisterKeyUp
     * @param {string} key The key to listen to.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
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

    /**
     * Registers a mouse down event callback, when the mouse is pressed down the supplied
     * callback function will be called and provided with the current mouse state.
     *
     * The callback function will continue to be invoked for every mouse move event
     * until it is unregistered.
     *
     * @example
     * rpg.registerMouseDown(async function(e) {
     *  // Log the x and y coordinates of the mouse.
     *  console.log(e.realX);
     *  console.log(e.realY);
     *
     *  // Log the mouse button that has been pressed down.
     *  console.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
     * });
     *
     * @memberof Mouse
     * @alias registerMouseDown
     * @param {Callback} callback The callback function to invoke when the event fires.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public registerMouseDown(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseDownHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseDownHandler = callback;
        }
    }

    /**
     * Removes a previously registered mouse down handler.
     *
     * @example
     * // Removes the mouse down handler local to this script.
     * rpg.unregisterMouseDown();
     *
     * // Removes the global engine mouse down handler.
     * rpg.unregisterMouseDown(true);
     *
     * @memberof Mouse
     * @alias unregisterMouseDown
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public unregisterMouseDown(global: boolean) {
        if (global) {
            Core.getInstance().mouseDownHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseDownHandler = null;
        }
    }

    /**
     * Registers a mouse move event callback, when the mouse is moved the supplied
     * callback function will be called and provided with the current mouse state.
     *
     * The callback function will continue to be invoked for every mouse move event
     * until it is unregistered.
     *
     * @example
     * rpg.registerMouseUp(async function(e) {
     *  // Log the x and y coordinates of the mouse.
     *  console.log(e.realX);
     *  console.log(e.realY);
     *
     *  // Log the mouse button that has been released.
     *  console.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
     * });
     *
     * @memberof Mouse
     * @alias registerMouseUp
     * @param {Callback} callback The callback function to invoke when the event fires.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public registerMouseUp(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseUpHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseUpHandler = callback;
        }
    }

    /**
     * Removes a previously registered mouse up handler.
     *
     * @example
     * // Removes the mouse up handler local to this script.
     * rpg.unregisterMouseUp();
     *
     * // Removes the global engine mouse up handler.
     * rpg.unregisterMouseUp(true);
     *
     * @memberof Mouse
     * @alias unregisterMouseUp
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public unregisterMouseUp(global: boolean) {
        if (global) {
            Core.getInstance().mouseUpHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseUpHandler = null;
        }
    }

    /**
     * Registers a mouse move event callback, when the mouse is moved the supplied
     * callback function will be called and provided with the current mouse state.
     *
     * The callback function will continue to be invoked for every mouse move event
     * until it is unregistered.
     *
     * @example
     * rpg.registerMouseClick(async function(e) {
     *  // Log the x and y coordinates of the mouse.
     *  console.log(e.realX);
     *  console.log(e.realY);
     *
     *  // Log the mouse button that has been clicked.
     *  console.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
     * });
     *
     * @memberof Mouse
     * @alias registerMouseClick
     * @param {Callback} callback The callback function to invoke when the event fires.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public registerMouseClick(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseClickHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseClickHandler = callback;
        }
    }

    /**
     * Removes a previously registered mouse click handler.
     *
     * @example
     * // Removes the mouse click handler local to this script.
     * rpg.unregisterMouseClick();
     *
     * // Removes the global engine mouse click handler.
     * rpg.unregisterMouseClick(true);
     *
     * @memberof Mouse
     * @alias unregisterMouseClick
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public unregisterMouseClick(global: boolean) {
        if (global) {
            Core.getInstance().mouseClickHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseClickHandler = null;
        }
    }

    /**
     * Registers a mouse move event callback, when the mouse is moved the supplied
     * callback function will be called and provided with the current mouse state.
     *
     * The callback function will continue to be invoked for every mouse move event
     * until it is unregistered.
     *
     * @example
     * rpg.registerMouseDoubleClick(function(e) {
     *  // Log the x and y coordinates of the mouse.
     *  console.log(e.realX);
     *  console.log(e.realY);
     *
     *  // Log the mouse button that has been double clicked.
     *  console.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
     * });
     *
     * @memberof Mouse
     * @alias registerMouseDoubleClick
     * @param {Callback} callback The callback function to invoke when the event fires.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public registerMouseDoubleClick(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseDoubleClickHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseDoubleClickHandler = callback;
        }
    }

    /**
     * Removes a previously registered mouse double click handler.
     *
     * @example
     * // Removes the mouse double click handler local to this script.
     * rpg.unregisterMouseDoubleClick();
     *
     * // Removes the global engine mouse double click handler.
     * rpg.unregisterMouseDoubleClick(true);
     *
     * @memberof Mouse
     * @alias unregisterMouseDoubleClick
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public unregisterMouseDoubleClick(global: boolean) {
        if (global) {
            Core.getInstance().mouseDoubleClickHandler = null;
        } else {
            Core.getInstance().mouseHandler.mouseDoubleClickHandler = null;
        }
    }

    /**
     * Registers a mouse move event callback, when the mouse is moved the supplied
     * callback function will be called and provided with the current mouse state.
     *
     * The callback function will continue to be invoked for every mouse move event
     * until it is unregistered.
     *
     * @example
     * rpg.registerMouseMove(async function(e) {
     *  // Log the x and y coordinates of the mouse.
     *  console.log(e.realX);
     *  console.log(e.realY);
     * });
     *
     * @memberof Mouse
     * @alias registerMouseMove
     * @param {Callback} callback The callback function to invoke when the event fires.
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
    public registerMouseMove(callback: any, global: boolean) {
        if (global) {
            Core.getInstance().mouseMoveHandler = callback;
        } else {
            Core.getInstance().mouseHandler.mouseMoveHandler = callback;
        }
    }

    /**
     * Removes a previously registered mouse move handler.
     *
     * @example
     * // Removes the mouse move handler local to this script.
     * rpg.unregisterMouseMove();
     *
     * // Removes the global engine mouse move handler.
     * rpg.unregisterMouseMove(true);
     *
     * @memberof Mouse
     * @alias unregisterMouseMove
     * @param {boolean} [global=false] Whether this should be globally applied in the engine.
     */
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

    /**
     * Plays the supplied audio file, up to five audio channels can be active at once.
     *
     * @example
     * rpg.playAudio("intro", false, 1.0)
     *
     * @memberof Audio
     * @alias playAudio
     * @param {string} id ID of the audio file.
     * @param {boolean} loop Whether to loop this audio.
     * @param {number} [volume=1.0] Value ranging from 1.0 to 0.0, default is 1.0 (i.e. 100%).
     */
    public playAudio(id: string, loop: boolean, volume: number = 1.0) {
        const repeatCount: number = loop ? -1 : 1;
        Framework.playAudio(id, repeatCount, volume);
    }

    /**
     * Stops the audio, if no id is specified then all audio is stopped.
     *
     * @example
     * // Stop all audio
     * rpg.stopAudio();
     *
     * // Stop a specific audio channel
     * rpg.stopAudio("intro");
     *
     * @memberof Audio
     * @alias stopAudio
     * @param {string} id ID of the audio file.
     */
    public stopAudio(id?: string) {
        Framework.stopAudio(id);
    }

    // ------------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------------

    /**
     * Sleeps for the specified duration.
     *
     * @example
     * // Sleep for 1 second
     * await rpg.sleep(1000);
     *
     * @memberof Util
     * @alias sleep
     * @param {number} duration In milliseconds.
     */
    public async sleep(duration: number) {
        await new Promise(resolve => setTimeout(resolve, duration));
    }

    /**
     * Gets a random number between the min and max inclusive.
     *
     * @example
     * const result = rpg.getRandom(1, 10);
     * console.log(result); // Will be between 1 and 10.
     *
     * @memberof Util
     * @alias getRandom
     * @param {number} min Minimum value for the random number.
     * @param {number} max Maximum value for the random number.
     * @returns {number} A random number in the from the requested range.
     */
    public getRandom(min: number, max: number): number {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    }

    /**
     * Gets the current scale factor that the renderer has been drawn with, useful
     * for creating responsive and scalable UIs.
     *
     * @example
     * const scale = rpg.getScale();
     * console.log(scale);
     *
     * @memberof Util
     * @alias getScale
     * @returns {number}
     */
    public getScale(): number {
        return Framework.getViewport()._scale;
    }

    /**
     * Gets the viewport object, this is useful for calculating the position of
     * sprites on the map relative to the screen.
     *
     * The viewport contains the (x, y) values of the upper left corner of screen
     * relative to a map's (x, y). It also returns the width and height of the
     * viewport, and the current visual offset (x, y) of the map and viewport.
     *
     * @example
     * const viewport = rpg.getViewport();
     * console.log(viewport.x);
     * console.log(viewport.y);
     * console.log(viewport.width);
     * console.log(viewport.height);
     * console.log(viewport.offsetX);
     * console.log(viewport.offsetY);
     *
     * @memberof Util
     * @alias getViewport
     * @returns {Util.Viewport}
     */
    public getViewport(): any {
        return Framework.getViewport();
    }

    /**
     * Measures text as it would appear on a canvas using the current font, returning
     * the width and height of the text in pixels.
     *
     * @example
     * const dimensions = rpg.measureText("Hello world");
     * console.log(dimensions.width);
     * console.log(dimensions.height);
     *
     * @memberof Util
     * @alias measureText
     * @param text TODO
     * @returns TODO
     */
    public measureText(text: string): any {
        const ctx: CanvasRenderingContext2D = this._getDrawingContext(this._canvases.default);
        ctx.font = this._font;
        return {
            width: Math.round(ctx.measureText(text).width),
            height: Math.round(parseInt(ctx.font))
        };
    }

    /**
     * Restarts the game engine.
     *
     * @memberof Util
     * @alias restart
     */
    public restart() {
        location.reload(); // Cheap way to implement game restart for the moment.
    }

}
