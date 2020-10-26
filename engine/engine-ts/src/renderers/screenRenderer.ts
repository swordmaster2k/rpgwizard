/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import { Collider, MapImage, Trigger } from "../asset/dto/asset-subtypes.js";
import { Map } from "../asset/map.js";
import { MapLayer, MapSprite } from "../asset/runtime/asset-subtypes.js";
import { Sprite } from "../asset/sprite.js";
import { Core } from "../core.js";
import { Framework } from "../framework.js";
import { Point } from "../rpgcode/rpgcode.js";

export class ScreenRenderer {
w
    private _renderNowCanvas: HTMLCanvasElement;

    constructor() {
        this._renderNowCanvas = document.createElement("canvas");

        const viewport = Framework.getViewport();
        this._renderNowCanvas.width = viewport._width;
        this._renderNowCanvas.height = viewport._height;
    }

    public renderBoard(ctx: CanvasRenderingContext2D) {
        ctx.imageSmoothingEnabled = false;

        const xShift: number = Core.getInstance().mapEntity.xShift;
        const yShift: number = Core.getInstance().mapEntity.yShift;
        const x: number = Core.getInstance().mapEntity.x + xShift;
        const y: number = Core.getInstance().mapEntity.y + yShift;
        const width: number = Core.getInstance().mapEntity.width;
        const height: number = Core.getInstance().mapEntity.height;

        if (Core.getInstance().mapEntity.show) {
            const map: Map = Core.getInstance().mapEntity.map;

            // Draw a black background
            ctx.fillStyle = "#000000";
            ctx.fillRect(x, y, width, height);

            if (!map.layerCache.length) {
                map.generateLayerCache();
            }

            // Loop through layers.
            for (var i = 0; i < map.layers.length; i++) {
                const mapLayer: MapLayer = map.layers[i];
                /*
                 * Render layer tiles.
                 */
                ctx.drawImage(map.layerCache[i], 0, 0, width, height, x, y, width, height);
                /*
                 * Render layer images.
                 */
                for (const id in mapLayer.images) {
                    const image: MapImage = mapLayer.images[id];
                    const imageBitmap: ImageBitmap = Framework.getImage(image.image);
                    ctx.drawImage(imageBitmap, x + image.x, y + image.y);
                }
                /*
                 * Sort sprites for depth.
                 */
                const layerSprites = this.sortSprites(mapLayer);
                /*
                 * Render sprites.
                 */
                for (const sprite of layerSprites) {
                    if (sprite && sprite.layer === i && sprite.renderReady) {
                        const frame = sprite.getActiveFrame();
                        if (frame) {
                            const x: number = sprite.x - (frame.width / 2) + xShift;
                            const y: number = sprite.y - (frame.height / 2) + yShift;
                            ctx.drawImage(frame, x, y);
                        }
                        if (Core.getInstance().game.debug.showColliders) {
                            const x: number = sprite.x + xShift + sprite.collider.x;
                            const y: number = sprite.y + yShift + sprite.collider.y;
                            this.drawCollider(ctx, sprite.collider, x, y);
                        }
                        if (Core.getInstance().game.debug.showTriggers) {
                            const x: number = sprite.x + xShift + sprite.trigger.x;
                            const y: number = sprite.y + yShift + sprite.trigger.y;
                            this.drawTrigger(ctx, sprite.trigger, x, y);
                        }
                    }
                }
                /*
                 * Render Colliders
                 */
                if (Core.getInstance().game.debug.showColliders) {
                    for (const id in mapLayer.colliders) {
                        const collider: Collider = mapLayer.colliders[id];
                        this.drawCollider(ctx, collider, xShift, yShift);
                    }
                }
                /*
                 * Render Triggers
                 */
                if (Core.getInstance().game.debug.showTriggers) {
                    for (const id in mapLayer.triggers) {
                        const trigger: Trigger = mapLayer.triggers[id];
                        this.drawTrigger(ctx, trigger, xShift, yShift);
                    }
                }
            }
        }
    }

    private sortSprites(layer: MapLayer) {
        const layerSprites: Array<Sprite> = [];

        Object.keys(layer.sprites).forEach(function (key) {
            // REFACTOR: Update this
            const mapSprite: MapSprite = layer.sprites[key];
            const entity: any = mapSprite.entity;
            const sprite: Sprite = entity.sprite;

            sprite.x = entity.x;
            sprite.y = entity.y;

            if (sprite && sprite.renderReady) {
                layerSprites.push(sprite);
            }
        });

        layerSprites.sort(function (a, b) {
            return a.y - b.y;
        });

        return layerSprites;
    }

    private drawCollider(ctx: CanvasRenderingContext2D, collider: Collider, xShift: number, yShift: number) {
        this.drawPolygon(ctx, collider.points, xShift, yShift, "#FF0000");
    }

    private drawTrigger(ctx: CanvasRenderingContext2D, trigger: Trigger, xShift: number, yShift: number) {
        this.drawPolygon(ctx, trigger.points, xShift, yShift, "#FFFF00");
    }

    private drawPolygon(ctx: CanvasRenderingContext2D, points: Array<Point>, xShift: number, yShift: number, color: string) {
        let haveMoved = false;
        ctx.strokeStyle = color;
        ctx.lineWidth = 2.0;
        ctx.beginPath();

        for (let i = 0; i < points.length - 1; i++) {
            const p1 = points[i];
            const p2 = points[i + 1];
            if (!haveMoved) {
                ctx.moveTo(p1.x + xShift, p1.y + yShift);
                haveMoved = true;
            }
            ctx.lineTo(p2.x + xShift, p2.y + yShift);
        }

        ctx.stroke();
    }

    private renderUI(ctx: CanvasRenderingContext2D) {
        /*
        * Render rpgcode canvases.
        */
        // REFACTOR: Decouple me
        // var canvases = rpgcode.canvases;
        // for (var property in canvases) {
        //     if (canvases.hasOwnProperty(property)) {
        //         var element = canvases[property];
        //         if (element.render) {
        //             context.drawImage(element.canvas, element.x, element.y);
        //         }
        //     }
        // }
    }

}
