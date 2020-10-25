/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import { Map } from "../asset/map.js";
import { MapLayer, MapSprite } from "../asset/runtime/asset-subtypes.js";
import { Sprite } from "../asset/sprite.js";
import { Core } from "../core.js";
import { Framework } from "../framework.js";

export class ScreenRenderer {

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
        let width: number = Core.getInstance().mapEntity.width;
        let height: number = Core.getInstance().mapEntity.height;

        if (/Edge/.test(navigator.userAgent)) {
            // Handle Edge bug when drawing up to the bounds of a canvas.
            width -= 2;
            height -= 2;
        }

        // Shorthand reference.
        // REFACTOR: Remove this
        // var character = Core.getInstance().craftyCharacter.character;
        // character.x = Core.getInstance().craftyCharacter.x;
        // character.y = Core.getInstance().craftyCharacter.y;

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

                // REFACTOR: Update this
                /*
                 * Render layer images.
                 */
                // boardLayer.images.forEach(function (image) {
                //     var layerImage = Crafty.assets[Crafty.__paths.images + image.src];
                //     context.drawImage(layerImage, x + image.x, y + image.y);
                // }, this);

                // REFACTOR: Update this
                /*
                 * Sort sprites for depth.
                 */
                const layerSprites = this.sortSprites(mapLayer);

                // REFACTOR: Update this
                /*
                 * Render sprites.
                 */
                layerSprites.forEach(function (sprite) {
                    if (sprite && sprite.layer === i && sprite.renderReady) {
                        const frame = sprite.getActiveFrame();
                        if (frame) {
                            const x = parseInt(sprite.x - (frame.width / 2) + xShift);
                            const y = parseInt(sprite.y - (frame.height / 2) + yShift);
                            ctx.drawImage(frame, x, y);
                        }

                        if (Core.getInstance().showVectors) {
                            // Draw collision ploygon.
                            var x, y, moved = false;
                            var points = sprite.collisionPoints;
                            ctx.beginPath();
                            ctx.lineWidth = "2";
                            ctx.strokeStyle = "#FF0000";
                            for (var j = 0; j < points.length - 1; j += 2) {
                                x = sprite.x + points[j];
                                y = sprite.y + points[j + 1];
                                if (!moved) {
                                    ctx.moveTo(x + xShift, y + yShift);
                                    moved = true;
                                } else {
                                    ctx.lineTo(x + xShift, y + yShift);
                                }
                            }
                            ctx.closePath();
                            ctx.stroke();

                            // Draw activation ploygon.
                            moved = false;
                            points = sprite.activationPoints;
                            ctx.beginPath();
                            ctx.lineWidth = "2";
                            ctx.strokeStyle = "#FFFF00";
                            for (var j = 0; j < points.length - 1; j += 2) {
                                x = sprite.x + points[j] + sprite.activationOffset.x;
                                y = sprite.y + points[j + 1] + sprite.activationOffset.y;
                                if (!moved) {
                                    ctx.moveTo(x + xShift, y + yShift);
                                    moved = true;
                                } else {
                                    ctx.lineTo(x + xShift, y + yShift);
                                }
                            }
                            ctx.closePath();
                            ctx.stroke();
                        }
                    }
                });

                if (Core.getInstance().showVectors) {
                    /*
                     * (Optional) Render Vectors.
                     */
                    mapLayer.vectors.forEach(function (vector) {
                        var haveMoved = false;
                        if (vector.type === "SOLID") {
                            ctx.strokeStyle = "#FF0000";
                        } else if (vector.type === "PASSABLE") {
                            ctx.strokeStyle = "#FFFF00";
                        }
                        ctx.lineWidth = 2.0;
                        ctx.beginPath();
                        for (var i = 0; i < vector.points.length - 1; i++) {
                            var p1 = vector.points[i];
                            var p2 = vector.points[i + 1];
                            if (!haveMoved) {
                                ctx.moveTo(p1.x + xShift, p1.y + yShift);
                                haveMoved = true;
                            }
                            ctx.lineTo(p2.x + xShift, p2.y + yShift);
                        }
                        if (vector.isClosed) {
                            ctx.lineTo(vector.points[0].x + xShift, vector.points[0].y + yShift);
                        }
                        ctx.stroke();
                    }, this);
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
