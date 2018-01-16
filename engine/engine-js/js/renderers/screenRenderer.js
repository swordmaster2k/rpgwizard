/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard, rpgcode, Crafty */

function ScreenRenderer() {
    this.renderNowCanvas = document.createElement("canvas");
    this.renderNowCanvas.width = Crafty.viewport._width;
    this.renderNowCanvas.height = Crafty.viewport._height;
}

ScreenRenderer.prototype.renderBoard = function (context) {
    var xShift = rpgwizard.craftyBoard.xShift;
    var yShift = rpgwizard.craftyBoard.yShift;
    var x = rpgwizard.craftyBoard.x + xShift;
    var y = rpgwizard.craftyBoard.y + yShift;
    var width = rpgwizard.craftyBoard.width;
    var height = rpgwizard.craftyBoard.height;

    if (/Edge/.test(navigator.userAgent)) {
        // Handle Edge bug when drawing up to the bounds of a canvas.
        width -= 2;
        height -= 2;
    }

    // Shorthand reference.
    var character = rpgwizard.craftyCharacter.character;
    character.x = rpgwizard.craftyCharacter.x;
    character.y = rpgwizard.craftyCharacter.y;

    if (rpgwizard.craftyBoard.show) {
        this.board = rpgwizard.craftyBoard.board;

        // Draw a black background.  
        context.fillStyle = "#000000";
        context.fillRect(x, y, width, height);

        if (!this.board.layerCache.length) {
            this.board.generateLayerCache();
        }

        // Loop through layers.
        for (var i = 0; i < this.board.layers.length; i++) {
            var boardLayer = this.board.layers[i];
            
            /*
             * Render this layer. 
             */
            context.drawImage(this.board.layerCache[i], 0, 0, width, height, x, y, width, height);

            /*
             * Render layer images.
             */
            boardLayer.images.forEach(function (image) {
                var layerImage = Crafty.assets[Crafty.__paths.images + image.src];
                context.drawImage(layerImage, x + image.x, y + image.y);
            }, this);

            /*
             * Sort sprites for depth.
             */
            var layerSprites = this.sortSprites(i, character);

            /*
             * Render sprites.
             */
            layerSprites.forEach(function (sprite) {
                if (sprite.layer === i) {
                    var frame = sprite.getActiveFrame();
                    if (frame) {
                        var x = parseInt(sprite.x - (frame.width / 2) + xShift);
                        var y = parseInt(sprite.y - (frame.height / 2) + yShift);
                        context.drawImage(frame, x, y);
                    }

                    if (rpgwizard.showVectors) {
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
                    }
                }
            });

            if (rpgwizard.showVectors) {
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
                    vector.points.forEach(function (point) {
                        if (!haveMoved) {
                            context.moveTo(point.x + xShift, point.y + yShift);
                            haveMoved = true;
                        } else {
                            context.lineTo(point.x + xShift, point.y + yShift);
                        }
                    }, this);
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
    var canvases = rpgcode.canvases;
    for (var property in canvases) {
        if (canvases.hasOwnProperty(property)) {
            var element = canvases[property];
            if (element.render) {
                context.drawImage(element.canvas, element.x, element.y);
            }
        }
    }
};

ScreenRenderer.prototype.sortSprites = function (layer, player) {
    var layerSprites = [];
    if (player.layer === layer && player.renderReady) {
        layerSprites.push(player);
    }

    var board = this.board;
    Object.keys(this.board.sprites).forEach(function (key) {
        var entity = board.sprites[key];
        var sprite = entity.sprite;
        var asset = sprite.enemy !== undefined ? sprite.enemy : sprite.npc;
        if (layer === entity.layer && asset.renderReady) {
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