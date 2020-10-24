/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard, rpgcode, Crafty */

import { Core } from "../core.js";

export function ScreenRenderer() {
    this.renderNowCanvas = document.createElement("canvas");
    this.renderNowCanvas.width = Crafty.viewport._width;
    this.renderNowCanvas.height = Crafty.viewport._height;
}

ScreenRenderer.prototype.renderBoard = function (context) {
    context.imageSmoothingEnabled = false;
    
    var xShift = Core.getInstance().map.xShift;
    var yShift = Core.getInstance().map.yShift;
    var x = Core.getInstance().map.x + xShift;
    var y = Core.getInstance().map.y + yShift;
    var width = Core.getInstance().map.width;
    var height = Core.getInstance().map.height;

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

    if (Core.getInstance().map.show) {
        this.board = Core.getInstance().map.map;

        // Draw a black background
        context.fillStyle = "#000000";
        context.fillRect(x, y, width, height);

        if (!this.board.layerCache.length) {
            this.board.generateLayerCache();
        }

        // Loop through layers.
        for (var i = 0; i < this.board.layers.length; i++) {
            var boardLayer = this.board.layers[i];
            
            /*
             * Render layer tiles. 
             */
            context.drawImage(this.board.layerCache[i], 0, 0, width, height, x, y, width, height);

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
            var layerSprites = this.sortSprites(boardLayer);

            // REFACTOR: Update this
            /*
             * Render sprites.
             */
            layerSprites.forEach(function (sprite) {
                if (sprite && sprite.layer === i && sprite.renderReady) {
                    var frame = sprite.getActiveFrame();
                    if (frame) {
                        var x = parseInt(sprite.x - (frame.width / 2) + xShift);
                        var y = parseInt(sprite.y - (frame.height / 2) + yShift);
                        context.drawImage(frame, x, y);
                    }

                    if (Core.getInstance().showVectors) {
                        // Draw collision ploygon.
                        var x, y, moved = false;
                        var points = sprite.collisionPoints;
                        context.beginPath();
                        context.lineWidth = "2";
                        context.strokeStyle = "#FF0000";
                        for (var j = 0; j < points.length - 1; j += 2) {
                            x = sprite.x + points[j];
                            y = sprite.y + points[j + 1];
                            if (!moved) {
                                context.moveTo(x + xShift, y + yShift);
                                moved = true;
                            } else {
                                context.lineTo(x + xShift, y + yShift);
                            }
                        }
                        context.closePath();
                        context.stroke();

                        // Draw activation ploygon.
                        moved = false;
                        points = sprite.activationPoints;
                        context.beginPath();
                        context.lineWidth = "2";
                        context.strokeStyle = "#FFFF00";
                        for (var j = 0; j < points.length - 1; j += 2) {
                            x = sprite.x + points[j] + sprite.activationOffset.x;
                            y = sprite.y + points[j + 1] + sprite.activationOffset.y;
                            if (!moved) {
                                context.moveTo(x + xShift, y + yShift);
                                moved = true;
                            } else {
                                context.lineTo(x + xShift, y + yShift);
                            }
                        }
                        context.closePath();
                        context.stroke();
                    }
                }
            });

            if (Core.getInstance().showVectors) {
                /*
                 * (Optional) Render Vectors.
                 */
                boardLayer.vectors.forEach(function (vector) {
                    var haveMoved = false;
                    if (vector.type === "SOLID") {
                        context.strokeStyle = "#FF0000";
                    } else if (vector.type === "PASSABLE") {
                        context.strokeStyle = "#FFFF00";
                    }
                    context.lineWidth = 2.0;
                    context.beginPath();
                    for (var i = 0; i < vector.points.length - 1; i++) {
                        var p1 = vector.points[i];
                        var p2 = vector.points[i + 1];
                        if (!haveMoved) {
                            context.moveTo(p1.x + xShift, p1.y + yShift);
                            haveMoved = true;
                        }
                        context.lineTo(p2.x + xShift, p2.y + yShift);
                    }
                    if (vector.isClosed) {
                        context.lineTo(vector.points[0].x + xShift, vector.points[0].y + yShift);
                    }
                    context.stroke();
                }, this);
            }
        }
    }
};

ScreenRenderer.prototype.renderUI = function (context) {
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
};

ScreenRenderer.prototype.sortSprites = function (layer, player) {
    var layerSprites = [];

    Object.keys(layer.sprites).forEach(function (key) {
        // REFACTOR: Update this
        var entity = layer.sprites[key];
        var asset = entity.sprite.enemy;
        if (asset && asset.renderReady) {
            asset.x = entity.x;
            asset.y = entity.y;
            asset.layer = entity.layer;
            layerSprites.push(asset);
        }
    });

    layerSprites.sort(function (a, b) {
        return a.y - b.y;
    });

    return layerSprites;
};