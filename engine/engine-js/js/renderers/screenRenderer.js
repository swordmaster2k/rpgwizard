/* global rpgtoolkit, rpgcode */

function ScreenRenderer() {
    this.renderNowCanvas = document.createElement("canvas");
    this.renderNowCanvas.width = Crafty.viewport._width;
    this.renderNowCanvas.height = Crafty.viewport._height;
}

ScreenRenderer.prototype.render = function (context) {
    var x = -Crafty.viewport._x;
    var y = -Crafty.viewport._y;
    var width = Crafty.viewport._width;
    var height = Crafty.viewport._height;

    if (/Edge/.test(navigator.userAgent)) {
        // Handle Edge bug when drawing up to the bounds of a canvas.
        width -= 2;
        height -= 2;
    }

    // Shorthand reference.
    var character = rpgtoolkit.craftyCharacter.character;
    character.x = rpgtoolkit.craftyCharacter.x;
    character.y = rpgtoolkit.craftyCharacter.y;

    if (rpgtoolkit.craftyBoard.show) {
        this.board = rpgtoolkit.craftyBoard.board;

        // Draw a black background.  
        context.fillStyle = "#000000";
        context.fillRect(x, y, width, height);

        if (!this.board.layerCache.length) {
            this.board.generateLayerCache();
        }

        // Loop through layers.
        for (var i = 0; i < this.board.layers.length; i++) {
            /*
             * Render this layer. 
             */
            context.drawImage(this.board.layerCache[i], x, y, width, height, x, y, width, height);

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
                        var x = parseInt(sprite.x - (frame.width / 2));
                        var y = parseInt(sprite.y - (frame.height / 2));
                        context.drawImage(frame, x, y);
                    }

                    if (rpgtoolkit.showVectors) {
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
                                context.moveTo(x, y);
                                moved = true;
                            } else {
                                context.lineTo(x, y);
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
                                context.moveTo(x, y);
                                moved = true;
                            } else {
                                context.lineTo(x, y);
                            }
                        }
                        context.closePath();
                        context.stroke();
                    }
                }
            });

            if (rpgtoolkit.showVectors) {
                /*
                 * (Optional) Render Vectors.
                 */
                this.board.layers[i].vectors.forEach(function (vector) {
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
                            context.moveTo(point.x, point.y);
                            haveMoved = true;
                        } else {
                            context.lineTo(point.x, point.y);
                        }
                    }, this);
                    context.stroke();
                }, this);
            }
        }
    }

    /*
     * Render rpgcode canvases.
     */
    var canvases = rpgcode.canvases;
    for (var property in canvases) {
        if (canvases.hasOwnProperty(property)) {
            var element = canvases[property];
            if (element.render) {
                context.drawImage(element.canvas, x, y);
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
        var item = sprite.item;
        if (layer === sprite.layer && item.renderReady) {
            sprite.item.x = entity.x;
            sprite.item.y = entity.y;
            sprite.item.layer = entity.layer;
            layerSprites.push(sprite.item);
        }
    });

    layerSprites.sort(function (a, b) {
        return a.y - b.y;
    });

    return layerSprites;
};