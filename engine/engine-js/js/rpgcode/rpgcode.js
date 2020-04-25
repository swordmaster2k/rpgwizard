/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard, rpgcode, PATH_PROGRAM, PATH_ITEM, PATH_FONT, Crafty, Promise */

var rpgcode = null; // Setup inside of the engine.

/**
 * The engine RPGcode API.
 * 
 * @class
 * @constructor
 * 
 * @returns {RPGcode}
 */
function RPGcode() {
    // The last entity to trigger a program.
    this.source = {};

    // An array of programs that will be run each frame.
    this.runTimePrograms = [];

    this.canvases = {
        "renderNowCanvas": {
            canvas: rpgwizard.screen.renderNowCanvas,
            render: false,
            x: 0,
            y: 0
        }
    };

    // Global variable storage for user programs.
    this.globals = {};

    this.rgba = {r: 255, g: 255, b: 255, a: 1.0};
    this.gradient = null;
    /**
     * Font that will be used for all drawing operations.
     * 
     * @example 
     * // Normal
     * rpgcode.font = "20px Lucida Console"
     * //Bold
     * rpgcode.font = "bold 20px Lucida Console"
     * // Italic
     * rpgcode.font = "italic 20px Lucida Console"
     * // Both.
     * rpgcode.font = "italic bold 20px Lucida Console"
     * 
     * @type {String}
     */
    this.font = "14px Arial";
    /**
     * Alpha value that will be used for all drawing operations.
     * 
     * @example 
     * // 50% Alpha
     * rpgcode.globalAlpha = 0.5
     * // 100% Alpha
     * rpgcode.globalAlpha = 1.0
     * 
     * @type {Number}
     */
    this.globalAlpha = 1.0;
    /**
     * Controls whether or not all drawing operations will be scaled in fullscreen
     * mode, default setting is true.
     * 
     * @example 
     * // Turn global scaling of on all drawing operations
     * rpgcode.scale = false;
     * 
     * @type {Boolean}
     */
    this.scale = true;
    this.imageSmoothingEnabled = false;

    this.dialogPosition = {
        NORTH: "NORTH",
        SOUTH: "SOUTH"
    };

    this.dialogWindow = {
        visible: false,
        profile: null,
        background: null,
        paddingX: 5,
        paddingY: 5,
        position: "SOUTH",
        profileDimensions: {
            width: 100,
            height: 100
        },
        dialogDimensions: {
            width: Crafty.viewport.width - 100,
            height: 100
        }
    };
}

RPGcode.prototype._animateGeneric = function (generic, resetGraphics, callback) {
    var activeGraphics = generic.spriteGraphics.active;
    if (!activeGraphics.spriteSheet.frames) {
        // Never loaded it before.
        generic.prepareActiveAnimation();
    }

    var soundEffect = activeGraphics.soundEffect;
    var frameRate = activeGraphics.frameRate;
    var delay = (1.0 / activeGraphics.frameRate) * 1000; // Get number of milliseconds.
    var repeat = activeGraphics.spriteSheet.frames.length - 1;
    Crafty.e("Delay").delay(function () {
        generic.animate(frameRate);
    }, delay, repeat, function () {
        generic.spriteGraphics.active = resetGraphics;
        generic.spriteGraphics.elapsed = 0;
        if (callback) {
            callback();
        }
    });

    if (soundEffect) {
        rpgcode.playSound(soundEffect, false);
    }
};

RPGcode.prototype._convertCraftyId = function (craftyId) {
    return rpgwizard.craftyBoard.board.sprites.findIndex(function (entity) {
        return entity.getId() === craftyId;
    });
};

RPGcode.prototype._getSpriteType = function (spriteId) {
    var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
    if (entity && entity.sprite) {
        if (entity.sprite.enemy) {
            return entity.sprite.enemy;
        } else if (entity.sprite.npc) {
            return entity.sprite.npc;
        } else if (entity.sprite.character) {
            return entity.sprite.character;
        }
    }
    return null;
};

/**
 * Adds the layer image to the requested layer, it will be rendered immediately
 * after being added to the board.
 * 
 * Note: Layers images added this way will be lost the moment the board is reloaded.
 * 
 * @example
 * var image = {
 *  "src": "battle-background.png", // pre-loaded asset image
 *  "x": 50,                        // x location on board in pixels
 *  "y": 100,                       // y location on board in pixels
 *  "id": "battle.background"       // unique for this layer image
 * }
 * 
 * rpgcode.addLayerImage(image, 1); // Adds the image to layer 1 on the current board
 * 
 * @param {Object} image The layer image to add to the board.
 * @param {Number} layer Layer index on the board, first layer starts at 0.
 * @returns {undefined}
 */
RPGcode.prototype.addLayerImage = function (image, layer) {
    if (layer < rpgwizard.craftyBoard.board.layers.length) {
        rpgwizard.craftyBoard.board.layers[layer].images.push(image);
    }
};

/**
 * Adds a program that will be called at runtime for each frame. You 
 * should avoid doing any lengthy operations with these programs.
 * 
 * @example
 * rpgcode.addRunTimeProgram("UI/HUD.js");
 * 
 * @param {type} filename Filename of the JavaScript file stored in Programs folder.
 * @returns {undefined}
 */
RPGcode.prototype.addRunTimeProgram = function (filename) {
    this.runTimePrograms.push(filename);
    this.runTimePrograms = Array.from(new Set(this.runTimePrograms));
};

/**
 * Dynamically adds a sprite to the current board, when the sprite has been added
 * the callback function will be invoked, if any. Useful for spawning item
 * pickups or enemies.
 * 
 * @example
 * var sprite = {
 *  "name":"skeleton.enemy",
 *  "id":"test3",
 *  "thread":"AI/PassiveEnemy.js",
 *  "startingPosition":{
 *     "x":232,
 *     "y":120,
 *     "layer":1
 *  },
 *  "events":[
 *     {
 *       "program":"AI/BumpCharacter.js",
 *        "type":"overlap",
 *        "key":""
 *     }
 *  ]
 * };
 * 
 * rpgcode.addSprite(sprite, function() {
 *  rpgcode.log("Sprite added to board.");
 * });
 * 
 * @param {Object} sprite BoardSprite object to add.
 * @param {Callback} callback If defined, the function to invoke after the sprite has been added.
 * @returns {undefined}
 */
RPGcode.prototype.addSprite = async function (sprite, callback) {
    rpgwizard.craftyBoard.board.sprites[sprite.id] = await rpgwizard.loadSprite(sprite);
    rpgwizard.loadCraftyAssets(callback);
};

/**
 * Animates the sprite using the requested animation. The animationId must be 
 * available for the sprite.
 * 
 * @example
 * var spriteId = "mysprite";
 * var animationId = "DANCE";
 * rpgcode.animateSprite(spriteId, animationId, function() {
 *  rpgcode.log("Finished dancing");
 * });
 * 
 * @param {String} spriteId The ID set for the sprite as it appears in the editor.
 * @param {String} animationId The requested animation to play for the sprite.
 * @param {Callback} callback If defined, the function to invoke at the end of the animation.
 */
RPGcode.prototype.animateSprite = function (spriteId, animationId, callback) {
    var type = rpgcode._getSpriteType(spriteId);
    if (type) {
        var resetGraphics = type.spriteGraphics.active;
        rpgcode.setSpriteStance(spriteId, animationId);
        rpgcode._animateGeneric(type, resetGraphics, callback);
    } else {
        // Provide error feedback.
    }
};

/**
 * Animates the character using the requested animation. The animationId must be
 * available for the character.
 * 
 * @example
 * // Without a callback.
 * rpgcode.animateCharacter("Hero", "HURT_SOUTH");
 * 
 * // With a callback.
 * rpgcode.animateCharacter("Hero", "HURT_SOUTH", function(){
 *  rpgcode.log("Animated Hero!");
 * });
 * 
 * @param {String} characterId The label associated with the character. 
 * @param {String} animationId The requested animation to character for the character.
 * @param {Callback} callback If defined, the function to invoke at the end of the animation.
 */
RPGcode.prototype.animateCharacter = function (characterId, animationId, callback) {
    // TODO: characterId will be unused until parties with multiple characters are supported.
    var character = rpgwizard.craftyCharacter.character;
    var resetGraphics = character.spriteGraphics.active;
    rpgcode.setCharacterStance(characterId, animationId);
    rpgcode._animateGeneric(character, resetGraphics, callback);
};

/**
 * Changes a characters visible graphics at runtime by swapping the values stored
 * in the left-hand slots with those on the right. These keys can be reversed
 * to restore the previous values.
 * 
 * @example
 * var changes = {
 *  "NORTH": "BOAT_NORTH",
 *  "SOUTH": "BOAT_SOUTH",
 *  "EAST": "BOAT_EAST",
 *  "WEST": "BOAT_WEST",
 *  "NORTH_EAST": "BOAT_NORTH_EAST",
 *  "NORTH_WEST": "BOAT_NORTH_WEST",
 *  "SOUTH_EAST": "BOAT_SOUTH_EAST",
 *  "SOUTH_WEST": "BOAT_SOUTH_WEST"
 * };
 * rpgcode.changeCharacterGraphics("Hero", changes);
 * 
 * // To restore the defaults all we need to do is swap them around again.
 * var changes = {
 *  "BOAT_NORTH": "NORTH",
 *  "BOAT_SOUTH": "SOUTH",
 *  "BOAT_EAST": "EAST",
 *  "BOAT_WEST": "WEST",
 *  "BOAT_NORTH_EAST": "NORTH_EAST",
 *  "BOAT_NORTH_WEST": "NORTH_WEST",
 *  "BOAT_SOUTH_EAST": "SOUTH_EAST",
 *  "BOAT_SOUTH_WEST": "SOUTH_WEST"
 * };
 * rpgcode.changeCharacterGraphics("Hero", changes);
 * 
 * @param {String} characterId The label associated with the character. 
 * @param {Object} swaps An object containing the graphics to swap.
 * @returns {undefined}
 */
RPGcode.prototype.changeCharacterGraphics = function (characterId, swaps) {
    var character = rpgcode.getCharacter(characterId);
    character.StandardKeys.forEach(function (key) {
        if (key in swaps) {
            var customKey = swaps[key];
            if (customKey in character.spriteGraphics.custom) {
                var swap;
                switch (key) {
                    case "NORTH":
                        swap = character.spriteGraphics.north;
                        character.spriteGraphics.north = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "SOUTH":
                        swap = character.spriteGraphics.south;
                        character.spriteGraphics.south = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "EAST":
                        swap = character.spriteGraphics.east;
                        character.spriteGraphics.east = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "WEST":
                        swap = character.spriteGraphics.west;
                        character.spriteGraphics.west = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "NORTH_EAST":
                        swap = character.spriteGraphics.northEast;
                        character.spriteGraphics.northEast = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "NORTH_WEST":
                        swap = character.spriteGraphics.northWest;
                        character.spriteGraphics.northWest = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "SOUTH_EAST":
                        swap = character.spriteGraphics.southEast;
                        character.spriteGraphics.southEast = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "SOUTH_WEST":
                        swap = character.spriteGraphics.southWest;
                        character.spriteGraphics.southWest = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "ATTACK":
                        swap = character.spriteGraphics.attack;
                        character.spriteGraphics.attack = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "DEFEND":
                        swap = character.spriteGraphics.defend;
                        character.spriteGraphics.defend = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                    case "DIE":
                        swap = character.spriteGraphics.die;
                        character.spriteGraphics.die = character.spriteGraphics.custom[customKey];
                        character.spriteGraphics.custom[customKey] = swap;
                        break;
                }
            }
        }
    });
    rpgcode.setCharacterStance(characterId, rpgcode.getCharacterDirection(characterId));
};

/**
 * Clears an entire canvas and triggers a redraw.
 * 
 * @example
 * // Create a simple canvas and write some text on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.drawText(270, 300, "Hello world!", canvas);
 * rpgcode.renderNow(canvas);
 * 
 * // Clears the canvas after 5 seconds have elapsed.
 * rpgcode.delay(5000, function(){ 
 *  rpgcode.clearCanvas(canvas); 
 * });
 * 
 * @param {String} canvasId The canvas to clear, if undefined defaults to "renderNowCanas".
 */
RPGcode.prototype.clearCanvas = function (canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        var canvas = instance.canvas;
        canvas.getContext("2d").clearRect(0, 0, canvas.width, canvas.height);
        instance.render = false;
        Crafty.trigger("Invalidate");
    }
};

/**
 * Clears and hides the dialog box.
 * 
 * @example 
 * // Display some dialog.
 * rpgcode.showDialog("Hello world!");
 * 
 * // Clears the dialog after 5 seconds have elapsed.
 * rpgcode.delay(5000, function(){ 
 *  rpgcode.clearDialog();
 * });
 */
RPGcode.prototype.clearDialog = function () {
    rpgcode.dialogWindow.visible = false;
    rpgcode.dialogWindow.paddingY = 5;
    rpgcode.clearCanvas("renderNowCanvas");
};

/**
 * Creates a canvas with the specified width, height, and ID. This canvas will 
 * not be drawn until renderNow is called with its ID.
 * 
 * @example
 * // Create a simple canvas and write some text on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.drawText(270, 300, "Hello world!", canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} width In pixels.
 * @param {Number} height In pixels.
 * @param {String} canvasId The ID to be associated with the canvas.
 */
RPGcode.prototype.createCanvas = function (width, height, canvasId) {
    var canvas = document.createElement("canvas");
    canvas.width = width;
    canvas.height = height;
    // Scale parameters
    if (rpgcode.scale) {
        canvas.width *= rpgcode.getScale();
        canvas.height *= rpgcode.getScale();
    }
    rpgcode.canvases[canvasId] = {
        canvas: canvas,
        render: false,
        x: 0,
        y: 0
    };
};

/**
 * Delays a programs execution for a specified number of milliseconds, after 
 * which the callback function is invoked.
 * 
 * @example 
 * // Shows a dialog window after 5 seconds.
 * rpgcode.delay(5000, function(){ 
 *  rpgcode.showDialog("Hello world!");
 * });
 * 
 * @param {Number} ms Time to wait in milliseconds.
 * @param {Callback} callback Function to execute after the delay.
 * @param {Boolean} loop Should the call be indefinite?
 * @returns {Object} Reference to the delay object.
 */
RPGcode.prototype.delay = function (ms, callback, loop) {
    if (loop) {
        return Crafty.e("Delay").delay(callback, ms, -1);
    } else {
        return Crafty.e("Delay").delay(callback, ms);
    }
};

/**
 * Destroys the canvas with the specified ID removing it from the engine.
 * 
 * @example
 * // Create a simple canvas and write some text on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.drawText(270, 300, "Hello world!", canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * // Destroys the canvas that we just created.
 * rpgcode.destroyCanvas(canvas);
 * 
 * @param {String} canvasId The ID for the canvas to destroy.
 */
RPGcode.prototype.destroyCanvas = function (canvasId) {
    delete rpgcode.canvases[canvasId];
};

/**
 * Destroys a particular sprite instance and removes it from the engine.
 * 
 * @example
 * rpgcode.destroySprite("evil-eye-1");
 * 
 * @param {String} spriteId The ID set for the sprite as it appears in the editor.
 */
RPGcode.prototype.destroySprite = function (spriteId) {
    if (rpgwizard.craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
        var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
        entity.destroy();
        delete rpgwizard.craftyBoard.board.sprites[spriteId];
        Crafty.trigger("Invalidate");
    }
};

/**
 * Draws a circle onto the canvas.
 * 
 * @example
 * // Create a canvas and draw a red circle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.drawCircle(100, 100, 25, canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} x The start x postion.
 * @param {Number} y The start y postion.
 * @param {Number} radius In pixels.
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 */
RPGcode.prototype.drawCircle = function (x, y, radius, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        // Scale parameters
        if (rpgcode.scale) {
            x *= rpgcode.getScale();
            y *= rpgcode.getScale();
            radius *= rpgcode.getScale();
        }

        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var rgba = rpgcode.rgba;
        context.globalAlpha = rpgcode.globalAlpha;
        context.strokeStyle = "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.beginPath();
        context.arc(x, y, radius, 0, 2 * Math.PI);
        context.stroke();
    }
};

/**
 * Draws an image on the canvas specified or the default if none.
 * 
 * @example
 * // Draw the image on the smaller canvas.
 * rpgcode.drawImage("life.png", 0, 0, 32, 32, 0, "canvasId");
 * 
 * @param {String} fileName The relative path to the image.
 * @param {Number} x The start position x in pixels.
 * @param {Number} y The start position y in pixels.
 * @param {Number} width In pixels.
 * @param {Number} height In pixels.
 * @param {Number} rotation In radians.
 * @param {String} canvasId The ID of the canvas to put the image on.
 */
RPGcode.prototype.drawImage = function (fileName, x, y, width, height, rotation, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }
    // Scale parameters
    if (rpgcode.scale) {
        x *= rpgcode.getScale();
        y *= rpgcode.getScale();
        width *= rpgcode.getScale();
        height *= rpgcode.getScale();
    }
    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        var image = Crafty.asset(Crafty.__paths.images + fileName);
        if (image) {
            try {
                var context = instance.canvas.getContext("2d");
                context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
                context.globalAlpha = rpgcode.globalAlpha;
                x += width / 2; y += height / 2; // rotate around center point
                context.translate(x, y);
                context.rotate(rotation);
                context.drawImage(image, -width / 2, -height / 2, width, height);
                context.rotate(-rotation);
                context.translate(-x, -y);
            } catch (err) {
                console.log("Failed to setImage err=[%s]", err);
            }
        }
    }
};

/**
 * Draws part of an image on the canvas specified or the default if none.
 * 
 * @example
 * // Set part of the image onto the smaller canvas.
 * rpgcode.drawImagePart("objects.png", 64, 0, 16, 16, 8, 8, 16, 16, 0, "canvasId");
 * 
 * @param {String} fileName The relative path to the image.
 * @param {Number} srcX The start position x in pixels from the source image.
 * @param {Number} srcY The start position y in pixels from the source image.
 * @param {Number} srcWidth In pixels from the source image.
 * @param {Number} srcHeight In pixels from the source image.
 * @param {Number} destX The start position x in pixels on the destination canvas.
 * @param {Number} destY The start position y in pixels on the destination canvas.
 * @param {Number} destWidth In pixels on the destination canvas.
 * @param {Number} destHeight In pixels on the destination canvas.
 * @param {Number} rotation In radians.
 * @param {String} canvasId The ID of the canvas to put the image on.
 */
RPGcode.prototype.drawImagePart = function (fileName, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, rotation, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }
    // Scale parameters
    if (rpgcode.scale) {
        destX *= rpgcode.getScale();
        destY *= rpgcode.getScale();
        destWidth *= rpgcode.getScale();
        destHeight *= rpgcode.getScale();
    }
    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        var image = Crafty.asset(Crafty.__paths.images + fileName);
        if (image) {
            try {
                var context = instance.canvas.getContext("2d");
                context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
                context.globalAlpha = rpgcode.globalAlpha;
                destX += destWidth / 2; destY += destHeight / 2; // rotate around center point
                context.translate(destX, destY);
                context.rotate(rotation);
                context.drawImage(image, srcX, srcY, srcWidth, srcHeight, -destWidth / 2, -destHeight / 2, destWidth, destHeight);
                context.rotate(-rotation);
                context.translate(-destX, -destY);
            } catch (err) {
                console.log("Failed to setImage err=[%s]", err);
            }
        }
    }
};

/**
 * Draws a line onto the canvas.
 * 
 * @example
 * // Create a canvas and draw a red line on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.drawLine(25, 25, 50, 50, 1, canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} x1 In pixels.
 * @param {Number} y1 In pixels.
 * @param {Number} x2 In pixels.
 * @param {Number} y2 In pixels.
 * @param {Number} lineWidth In pixels.
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 */
RPGcode.prototype.drawLine = function (x1, y1, x2, y2, lineWidth, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        // Scale parameters
        if (rpgcode.scale) {
            x1 *= rpgcode.getScale();
            y2 *= rpgcode.getScale();
            x2 *= rpgcode.getScale();
            y2 *= rpgcode.getScale();
            lineWidth *= rpgcode.getScale();
        }

        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var rgba = rpgcode.rgba;
        context.globalAlpha = rpgcode.globalAlpha;
        context.lineWidth = lineWidth;
        context.strokeStyle = "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.beginPath();
        context.moveTo(x1, y1);
        context.lineTo(x2, y2);
        context.stroke();
    }
};

/**
 * Draws the source canvas onto the target canvas.
 * 
 * @example
 * // Canvas IDs.
 * var buffer = "buffer";
 * var lifeIcon = "life_icon";
 * 
 * // Assets to load up.
 * var assets = {
 *  "images": [
 *      "life.png"
 *  ]
 * };
 * 
 * // Load up the assets needed for this example.
 * rpgcode.loadAssets(assets, function () {
 *  // Smaller canvas.
 *  rpgcode.createCanvas(32, 32, lifeIcon);
 *
 *  // Canvas to draw onto.
 *  rpgcode.createCanvas(640, 480, buffer);
 *
 *  // Set the image on the smaller canvas.
 *  rpgcode.setImage("life.png", 0, 0, 32, 32, lifeIcon);
 *  
 *  // Draw 3 hearts onto the buffer canvas.
 *  for (var i = 1; i < 4; i++) {
 *      rpgcode.drawOntoCanvas(lifeIcon, i * 32, 430, 32, 32, buffer);
 *  }
 *  
 *  // Show the larger canvas with the smaller hearts on it.
 *  rpgcode.renderNow(buffer);
 * });
 * 
 * @param {String} sourceId The ID of the source canvas.
 * @param {Number} x The start position x in pixels.
 * @param {Number} y The start position y in pixels.
 * @param {Number} width In pixels.
 * @param {Number} height In pixels.
 * @param {String} targetId The ID of the target canvas.
 */
RPGcode.prototype.drawOntoCanvas = function (sourceId, x, y, width, height, targetId) {
    var source = rpgcode.canvases[sourceId];
    var target = rpgcode.canvases[targetId];

    if (source && target) {
        // Scale parameters
        if (rpgcode.scale) {
            x *= rpgcode.getScale();
            y *= rpgcode.getScale();
            width *= rpgcode.getScale();
            height *= rpgcode.getScale();
        }

        var sourceCanvas = source.canvas;
        var targetContext = target.canvas.getContext("2d");
        targetContext.drawImage(sourceCanvas, x, y, width, height);
    }
};

/**
 * Draws a rectangle onto the canvas.
 * 
 * @example
 * // Create a canvas and draw a red rectangle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.drawRect(0, 0, 100, 100, 1, canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} x The start x postion.
 * @param {Number} y The start y postion.
 * @param {Number} width In pixels.
 * @param {Number} height In pixels.
 * @param {Number} lineWidth In pixels.
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 */
RPGcode.prototype.drawRect = function (x, y, width, height, lineWidth, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        // Scale parameters
        if (rpgcode.scale) {
            x *= rpgcode.getScale();
            y *= rpgcode.getScale();
            width *= rpgcode.getScale();
            height *= rpgcode.getScale();
            lineWidth *= rpgcode.getScale();
        }

        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var rgba = rpgcode.rgba;
        context.globalAlpha = rpgcode.globalAlpha;
        context.lineWidth = lineWidth;
        context.strokeStyle = "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.strokeRect(x, y, width, height);
    }
};

/**
 * Draws a rectangle with rounded edges onto the canvas.
 * 
 * @example
 * // Create a canvas and draw a red rectangle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.drawRoundedRect(0, 0, 100, 100, 1, 5, canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} x The start x postion.
 * @param {Number} y The start y postion.
 * @param {Number} width In pixels.
 * @param {Number} height In pixels.
 * @param {Number} lineWidth In pixels.
 * @param {Number} radius In pixels.
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 */
RPGcode.prototype.drawRoundedRect = function (x, y, width, height, lineWidth, radius, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        // Scale parameters
        if (rpgcode.scale) {
            x *= rpgcode.getScale();
            y *= rpgcode.getScale();
            width *= rpgcode.getScale();
            height *= rpgcode.getScale();
            lineWidth *= rpgcode.getScale();
            radius *= rpgcode.getScale();
        }

        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var rgba = rpgcode.rgba;
        context.globalAlpha = rpgcode.globalAlpha;
        context.lineWidth = lineWidth;
        context.strokeStyle = "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.beginPath();
        context.moveTo(x + radius, y);
        context.lineTo(x + width - radius, y);
        context.quadraticCurveTo(x + width, y, x + width, y + radius);
        context.lineTo(x + width, y + height - radius);
        context.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
        context.lineTo(x + radius, y + height);
        context.quadraticCurveTo(x, y + height, x, y + height - radius);
        context.lineTo(x, y + radius);
        context.quadraticCurveTo(x, y, x + radius, y);
        context.closePath();
        context.stroke();
    }
};

/**
 * Draws the text on the canvas starting at the specified (x, y) position, if no 
 * canvas is specified it defaults to the "renderNowCanvas".
 * 
 * @example
 * // Display the text on the default canvas.
 * rpgcode.drawText(270, 300, "Hello world!");
 * rpgcode.renderNow();
 * 
 * // Display the text on a custom canvas.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.drawText(270, 300, "Hello world!", canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} x The start position x in pixels.
 * @param {Number} y The start postion y in pixels.
 * @param {String} text A string of text to draw.
 * @param {String} canvasId The ID of the canvas to draw onto, if undefined defaults to "renderNowCanvas".
 */
RPGcode.prototype.drawText = function (x, y, text, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        // Scale parameters
        if (rpgcode.scale) {
            x *= rpgcode.getScale();
            y *= rpgcode.getScale();
        }

        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var rgba = rpgcode.rgba;
        context.globalAlpha = rpgcode.globalAlpha;
        context.fillStyle = rpgcode.gradient ? rpgcode.gradient : "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.font = rpgcode.font;
        context.fillText(text, x, y);
    }
};

/**
 * Ends the current program and releases control back to the main game loop. If nextProgram is
 * specified the main loop will not resume. Execution will be immediately passed to the program
 * the user specified.
 * 
 * @example
 * // End the current program and release control back to the engine.
 * rpgcode.endProgram();
 * 
 * // End the current program, then run another.
 * rpgcode.endProgram("MyProgram.js");
 * 
 * @param {String} nextProgram The relative path to the next program to execute.
 */
RPGcode.prototype.endProgram = function (nextProgram) {
    if (nextProgram) {
        rpgwizard.endProgram(nextProgram);
    } else {
        rpgwizard.endProgram();
    }
};

/**
 * Fills a solid circle onto the canvas.
 * 
 * @example
 * // Create a canvas and draw a red circle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.fillCircle(100, 100, 25, canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} x The start x postion.
 * @param {Number} y The start y postion.
 * @param {Number} radius The start y postion.
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 */
RPGcode.prototype.fillCircle = function (x, y, radius, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        // Scale parameters
        if (rpgcode.scale) {
            x *= rpgcode.getScale();
            y *= rpgcode.getScale();
            radius *= rpgcode.getScale();
        }

        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var rgba = rpgcode.rgba;
        context.globalAlpha = rpgcode.globalAlpha;
        context.fillStyle = rpgcode.gradient ? rpgcode.gradient : "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.beginPath();
        context.arc(x, y, radius, 0, 2 * Math.PI);
        context.fill();
    }
};

/**
 * Fills a solid rectangle on the canvas.
 * 
 * @example
 * // Create a canvas and draw a red rectangle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.fillRect(0, 0, 100, 100, canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} x The start x postion.
 * @param {Number} y The start y postion.
 * @param {Number} width In pixels.
 * @param {Number} height In pixels.
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 */
RPGcode.prototype.fillRect = function (x, y, width, height, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        // Scale parameters
        if (rpgcode.scale) {
            x *= rpgcode.getScale();
            y *= rpgcode.getScale();
            width *= rpgcode.getScale();
            height *= rpgcode.getScale();
        }

        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var rgba = rpgcode.rgba;
        context.globalAlpha = rpgcode.globalAlpha;
        context.fillStyle = rpgcode.gradient ? rpgcode.gradient : "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.fillRect(x, y, width, height);
    }
};

/**
 * Fills a solid rounded rectangle on the canvas.
 * 
 * @example
 * // Create a canvas and draw a red rectangle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.fillRoundedRect(0, 0, 100, 100, 5, canvas);
 * rpgcode.renderNow(canvas); 
 * 
 * @param {Number} x The start x postion.
 * @param {Number} y The start y postion.
 * @param {Number} width In pixels.
 * @param {Number} height In pixels.
 * @param {Number} radius In pixels. 
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 */
RPGcode.prototype.fillRoundedRect = function (x, y, width, height, radius, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        // Scale parameters
        if (rpgcode.scale) {
            x *= rpgcode.getScale();
            y *= rpgcode.getScale();
            width *= rpgcode.getScale();
            height *= rpgcode.getScale();
            radius *= rpgcode.getScale();
        }

        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var rgba = rpgcode.rgba;
        context.globalAlpha = rpgcode.globalAlpha;
        context.fillStyle = rpgcode.gradient ? rpgcode.gradient : "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.beginPath();
        context.moveTo(x + radius, y);
        context.lineTo(x + width - radius, y);
        context.quadraticCurveTo(x + width, y, x + width, y + radius);
        context.lineTo(x + width, y + height - radius);
        context.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
        context.lineTo(x + radius, y + height);
        context.quadraticCurveTo(x, y + height, x, y + height - radius);
        context.lineTo(x, y + radius);
        context.quadraticCurveTo(x, y, x + radius, y);
        context.closePath();
        context.fill();
    }
};

/**
 * Fires a ray from its origin in the direction and report entities that intersect 
 * with it, given the parameter constraints.
 *
 * This will return any characters, enemies, NPCs, and SOLID vectors caught in the path of the 
 * raycast enclosing them inside an object.
 * 
 * If no layer is specified, the origin layer will be the player's current.
 * 
 * @example
 * var hits = rpgcode.fireRaycast({
 *     _x: location.x,
 *     _y: location.y,
 *     _layer: location.layer // Optional
 *  }, vector, 13);
 *  hits["characters"].forEach(function(character) {
 *      rpgcode.log(character);
 *  });
 *  hits["enemies"].forEach(function(sprite) {
 *      rpgcode.log(sprite.enemy);
 *  });
 *  hits["npcs"].forEach(function(sprite) {
 *      rpgcode.log(sprite.npc);
 *  });
 *  hits["solids"].forEach(function(solid) {
 *      rpgcode.log(solid.distance);
 *      rpgcode.log(solid.x);
 *      rpgcode.log(solid.y);
 *  });
 *  rpgcode.endProgram();
 * 
 * @param {type} origin The point of origin from which the ray will be cast. The object must contain the properties _x, _y, and optionally _layer.
 * @param {type} direction The direction the ray will be cast. It must be normalized. The object must contain the properties x and y.
 * @param {type} maxDistance The maximum distance up to which intersections will be found. This is an optional parameter defaulting to Infinity. If it's Infinity find all intersections. If it's negative find only first intersection (if there is one). If it's positive find all intersections up to that distance.
 * @returns {Object} An object containing all of the entities in the path of the raycast. 
 */
RPGcode.prototype.fireRaycast = function (origin, direction, maxDistance) {
    var hits;
    var results = {characters: [], enemies: [], npcs: [], solids: []};
    direction = new Crafty.math.Vector2D(direction.x, direction.y).normalize();
    if (maxDistance) {
        hits = Crafty.raycast(origin, direction, maxDistance, "Raycastable");
    } else {
        hits = Crafty.raycast(origin, direction, -1, "Raycastable");
    }
    var layerCheck = {
        obj: {
            layer: origin._layer === undefined || origin._layer === null ? rpgwizard.craftyCharacter.character.layer : origin._layer
        }
    };
    hits.forEach(function (hit) {
        if (hit.obj.sprite) {
            if (hit.obj.sprite.npc) {
                if (hit.obj.sprite.npc.onSameLayer(layerCheck) && !hit.obj.sprite.npc.baseVectorDisabled) {
                    results.npcs.push(hit.obj.sprite);
                }
            } else if (hit.obj.sprite.enemy) {
                if (hit.obj.sprite.enemy.onSameLayer(layerCheck) && !hit.obj.sprite.enemy.baseVectorDisabled) {
                    results.enemies.push(hit.obj.sprite);
                }
            }
        } else if (hit.obj.character) {
            if (hit.obj.character.onSameLayer(layerCheck) && !hit.obj.character.baseVectorDisabled) {
                results.characters.push(hit.obj.character);
            }
        } else if (hit.obj.vectorType === "SOLID") {
            if (hit.obj.layer === layerCheck.obj.layer) {
                results.solids.push({"distance": hit.distance, "x": hit.x, "y": hit.y});
            }
        }
    });

    return results;
};

/**
 * Gets the current board, useful for accessing certain properties e.g.
 * name, description etc.
 * 
 * @example
 * var board = rpgcode.getBoard();
 * rpgcode.log(board);
 * 
 * @returns {String} Name of the current board.
 */
RPGcode.prototype.getBoard = function () {
    return rpgwizard.craftyBoard.board;
};

/**
 * Gets the current board's name and returns it.
 * 
 * @example
 * var boardName = rpgcode.getBoardName();
 * rpgcode.log(boardName);
 * 
 * @returns {String} Name of the current board.
 */
RPGcode.prototype.getBoardName = function () {
    return rpgwizard.craftyBoard.board.name;
};

/**
 * Gets the angle between two points in radians.
 * 
 * @example
 * // Get the angle in radians between two points.
 * var angle = rpgcode.getAngleBetweenPoints(location.x, location.y, this.x, this.y);
 * 
 * @param {Number} x1
 * @param {Number} y1
 * @param {Number} x2
 * @param {Number} y2
 * @returns {Number} The angle between the points in radians.
 */
RPGcode.prototype.getAngleBetweenPoints = function (x1, y1, x2, y2) {
    var dx = x1 - x2;
    var dy = y1 - y2;
    return Math.atan2(dy, dx);
};

/**
 * Gets the active character object.
 * 
 * @example
 * var character = rpgcode.getCharacter();
 * rpgcode.log(character);
 * 
 * @returns {Object} Active character.
 */
RPGcode.prototype.getCharacter = function () {
    return rpgwizard.craftyCharacter.character;
};

/**
 * Gets the character's current direction.
 * 
 * var direction = rpgcode.getCharacterDirection();
 * rpgcode.log(direction);
 * 
 * @returns {String} A NORTH, SOUTH, EAST, or WEST value.
 */
RPGcode.prototype.getCharacterDirection = function () {
    var direction = rpgwizard.craftyCharacter.character.direction;

    // User friendly rewrite of Crafty constants.
    switch (direction) {
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
};

/**
 * Gets the character's current location, optionally including the visual offset
 * that happens when boards are smaller than the viewport dimensions.
 * 
 * @example
 * var location = rpgcode.getCharacterLocation();
 * rpgcode.log(location);
 * 
 * @param {Boolean} inTiles Should the location be in tiles, otherwise pixels.
 * @param {Boolean} includeOffset Should the location include the visual board offset.
 * @returns {Object} An object containing the characters location in the form {x, y, z}.
 */
RPGcode.prototype.getCharacterLocation = function (inTiles, includeOffset) {
    var instance = rpgwizard.craftyCharacter;

    if (includeOffset) {
        var x = instance.x + rpgwizard.craftyBoard.xShift + Crafty.viewport._x;
        var y = instance.y + rpgwizard.craftyBoard.yShift + Crafty.viewport._y;
    } else {
        var x = instance.x;
        var y = instance.y;
    }

    if (inTiles) {
        return {
            x: x / rpgwizard.craftyBoard.board.tileWidth,
            y: y / rpgwizard.craftyBoard.board.tileHeight,
            layer: instance.character.layer
        };
    } else {
        return {
            x: x,
            y: y,
            layer: instance.character.layer
        };
    }
};

/**
 * Gets the straight line distance between two points in pixels.
 * 
 * @example
 * // Get the distance between two points in pixels.
 * var distance = rpgcode.getDistanceBetweenPoints(location.x, location.y, this.x, this.y);
 * 
 * @param {Number} x1
 * @param {Number} y1
 * @param {Number} x2
 * @param {Number} y2
 * @returns {Number} The distance in pixels.
 */
RPGcode.prototype.getDistanceBetweenPoints = function (x1, y1, x2, y2) {
    var a = x1 - x2;
    var b = y1 - y2;
    return Math.sqrt(a * a + b * b); // Simple Pythagora's theorem.
};

/**
 * Gets the value of global variable.
 * 
 * @example
 * var swordActive = rpgcode.getGlobal("swordActive");
 * rpgcode.log(swordActive);
 * 
 * @param {String} id The ID associated with the global variable.
 * @returns {Object} Value of the requested global.
 */
RPGcode.prototype.getGlobal = function (id) {
    return rpgcode.globals[id];
};

/**
 * Gets the pixel ImageData at the (x, y) coordinate on the canvas.
 * 
 * @example
 * // Draw a rectangle on the default canvas and render it.
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.fillRect(0, 0, 100, 100);
 * rpgcode.renderNow();
 * 
 * // Get the red pixel at (50, 50) from the rectangle.
 * var imageData = rpgcode.getPixel(50, 50);
 * var rgba = imageData.data;
 * 
 * // Show the RGBA values of the pixel
 * alert("R, G, B, A (" + rgba[0] + ", " + rgba[1] + ", " + rgba[2] + ", " + rgba[3] + ")");
 * 
 * @param {Number} x In pixels.
 * @param {Number} y In pixels.
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 * @returns {Object} An ImageData object
 */
RPGcode.prototype.getPixel = function (x, y, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }
    
    // Scale parameters
    if (rpgcode.scale) {
        x *= rpgcode.getScale();
        y *= rpgcode.getScale();
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        return context.getImageData(x, y, 1, 1);
    }
    return null;
};

/**
 * Gets the image object if it has been loaded into the engine already, otherwise
 * it returns undefined.
 * 
 * @example
 * // Set the image on the smaller canvas.
 * var image = rpgcode.getImage("life.png");
 * rpgcode.log(image.width);
 * rpgcode.log(image.height);
 * 
 * @param {String} fileName The relative path to the image.
 * @returns {Object} Image object or undefined if none available.
 */
RPGcode.prototype.getImage = function (fileName) {
    return Crafty.asset(Crafty.__paths.images + fileName);
};

/**
 * Gets the item object and returns it to the caller
 * 
 * @example
 * rpgcode.getItem("apple.item", function(item) {
 *  rpgcode.log(item);
 * });
 * 
 * @param {String} fileName The relative path to the item.
 * @param {Callback} callback Invoked when the item has finished loading.
 * @returns {Object} Item object or undefined if none available.
 */
RPGcode.prototype.getItem = async function (fileName, callback) {
    if (callback) { // Use a callback to future proof for async loading.
        var item = await new Item(PATH_ITEM + fileName).load();
        callback(item);
    }
    return undefined;
};

/**
 * Gets a random number between the min and max inclusive.
 * 
 * @example 
 * var result = rpgcode.getRandom(1, 10);
 * rpgcode.log(result); // Will be between 1 and 10.
 * 
 * @param {Number} min Minimum value for the random number.
 * @param {Number} max Maximum value for the random number.
 * @returns {Number} A random number in the from the requested range. 
 */
RPGcode.prototype.getRandom = function (min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
};

/**
 * Gets the current running program as an Object.
 * 
 * @example
 * var program = rpgcode.getRunningProgram();
 * rpgcode.log(program);
 * 
 * @returns {Object} An object with the attributes inProgram (boolean) and the filename of the current running program, if any.
 */
RPGcode.prototype.getRunningProgram = function () {
    return {inProgram: rpgwizard.inProgram, currentProgram: rpgwizard.currentProgram};
};

/**
 * Gets the sprites's current location, optionally including the visual offset
 * that happens when boards are smaller than the viewport dimensions.
 * 
 * @example
 * var location = rpgcode.getSpriteLocation("rat");
 * rpgcode.log(location);
 * 
 * @param {String} spriteId ID associated with the sprite. 
 * @param {Boolean} inTiles Should the location be in tiles, otherwise pixels.
 * @param {Boolean} includeOffset Should the location include the visual board offset.
 * @returns {Object} An object containing the characters location in the form {x, y, z}.
 */
RPGcode.prototype.getSpriteLocation = function (spriteId, inTiles, includeOffset) {
    var entity = rpgwizard.craftyBoard.board.sprites[spriteId];

    if (includeOffset) {
        var x = entity.x + rpgwizard.craftyBoard.xShift + Crafty.viewport._x;
        var y = entity.y + rpgwizard.craftyBoard.yShift + Crafty.viewport._y;
    } else {
        var x = entity.x;
        var y = entity.y;
    }

    if (inTiles) {
        return {
            x: x / rpgwizard.craftyBoard.board.tileWidth,
            y: y / rpgwizard.craftyBoard.board.tileHeight,
            layer: entity.layer
        };
    } else {
        return {
            x: x,
            y: y,
            layer: entity.layer
        };
    }
};

/**
 * Gets the current scale factor that the renderer has been drawn with, useful
 * for creating responsive and scalable UIs.
 * 
 * @example
 * var scale = rpgcode.getScale();
 * rpgcode.log(scale);
 * 
 * @returns {Number}
 */
RPGcode.prototype.getScale = function () {
    return Crafty.viewport._scale;
};

/**
 * Gets the sprite associated with the ID set in the Board editor.
 * 
 * @example
 * var sprite = rpgcode.getSprite("MySprite");
 * 
 * @param {type} spriteId
 * @returns {Entity}
 */
RPGcode.prototype.getSprite = function (spriteId) {
    return rpgwizard.craftyBoard.board.sprites[spriteId];
};

/**
 * Gets the sprites's current direction.
 * 
 * var direction = rpgcode.getSpriteDirection();
 * rpgcode.log(direction);
 * 
 * @param {String} spriteId ID associated with the sprite.
 * @returns {String} A NORTH, SOUTH, EAST, or WEST value.
 */
RPGcode.prototype.getSpriteDirection = function (spriteId) {
    var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
    if (entity.sprite.npc) {
        var direction = entity.sprite.npc.direction;
    } else {
        var direction = entity.sprite.enemy.direction;
    }

    // User friendly rewrite of Crafty constants.
    switch (direction) {
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
};

/**
 * Gets the data associated with a tile on the board as set in the Editor's 
 * Tileset properties. 
 * 
 * Returns "null" if no data can be found. 
 * 
 * Throws an exception if either the layer or tile is out-of-bounds.
 * 
 * @example
 * // Get the tile data at (10, 5, 1), and log the output
 * const tileData = rpgcode.getTileData(10, 5, 1);
 * rpgcode.log(tileData.type);
 * rpgcode.log(tileData.defence);
 * rpgcode.log(tileData.custom);
 * 
 * @param {Number} tileX
 * @param {Number} tileY
 * @param {Number} layer
 * @returns {Object} Containing the properties: "type", "defence", "custom".
 * @throws "layer out of range" or "tile out of range"
 */
RPGcode.prototype.getTileData = function(tileX=0, tileY=0, layer=0) {
    const board = rpgwizard.craftyBoard.board;
    if (layer < 0 || board.layers.length < layer) {
        throw "layer out of range";
    }
    
    const boardLayer = board.layers[layer];
    const tileIndex = (tileY * board.width) + tileX;
    if (tileIndex < 0 || boardLayer.tiles.length < tileIndex) {
        throw "tile out of range";
    }
    
    const parts = boardLayer.tiles[tileIndex].split(":"); // tileSetIndex:tileIndex
    if (parts[0] === "-1" || parts[1] === "-1") {
        return null; // empty tile
    }
    
    const tileSet = rpgwizard.tilesets[board.tileSets[parts[0]]];
    return tileSet.tileData && tileSet.tileData[parts[1]] ? tileSet.tileData[parts[1]] : null;
};

/**
 * Gets the viewport object, this is useful for calculating the position of 
 * characters or sprites on the board relative to the RPGcode screen.
 * 
 * The viewport contains the (x, y) values of the upper left corner of screen
 * relative to a board's (x, y). It also returns the width and height of the
 * viewport, and the current visual offset (x, y) of the board and viewport.
 * 
 * @example
 * var viewport = rpgcode.getViewport();
 * rpgcode.log(viewport.x);
 * rpgcode.log(viewport.y);
 * rpgcode.log(viewport.width);
 * rpgcode.log(viewport.height);
 * rpgcode.log(viewport.offsetX);
 * rpgcode.log(viewport.offsetY);
 * 
 * @returns {Object}
 */
RPGcode.prototype.getViewport = function () {
    return {
        x: -Crafty.viewport.x,
        y: -Crafty.viewport.y,
        width: Math.round(Crafty.viewport.width / rpgcode.getScale()),
        height: Math.round(Crafty.viewport.height / rpgcode.getScale()),
        offsetX: Math.floor(rpgwizard.craftyBoard.xShift + Crafty.viewport._x),
        offsetY: Math.floor(rpgwizard.craftyBoard.yShift + Crafty.viewport._y)
    };
};

/**
 * Gives an item to a character, placing it in their inventory. 
 * 
 * @param {String} filename
 * @param {String} characterId
 * @param {Callback} callback Invoked when the item has finished loading assets.
 * @returns {undefined}
 */
RPGcode.prototype.giveItem = async function (filename, characterId, callback) {
    var item = await new Item(PATH_ITEM + filename).load();

    rpgwizard.loadItem(item);
    rpgwizard.loadCraftyAssets(function (e) {
        if (!e) {
            if (!rpgwizard.craftyCharacter.character.inventory[filename]) {
                rpgwizard.craftyCharacter.character.inventory[filename] = [];
            }
            rpgwizard.craftyCharacter.character.inventory[filename].push(item);
            callback();
        }
    });
};

/**
 * Hits the character dealing the requested damage while playing the corresponding
 * animation. Optionally a callback can be invoked when the hit animation
 * has finished playing.
 * 
 * @example
 * // Without a callback.
 * rpgcode.hitCharacter("Hero", 1, "DEFEND");
 * 
 * // With a callback.
 * rpgcode.hitCharacter("Hero", 1, "DEFEND", function() {
 *  // The animation has ended, do something.
 * });
 * 
 * @param {String} characterId ID of the character.
 * @param {Number} damage Amount of health to take away.
 * @param {String} animationId Animation to play while the character is hit.
 * @param {Callback} callback An optional function to invoke when the animation has ended.
 */
RPGcode.prototype.hitCharacter = function (characterId, damage, animationId, callback) {
    // characterId unused until multi-character parties are supported.
    rpgwizard.controlEnabled = false;
    var character = rpgwizard.craftyCharacter.character;
    character.health -= damage;
    character.isHit = true;
    rpgcode.animateCharacter(characterId, animationId, function () {
        character.isHit = false;
        rpgwizard.controlEnabled = true;
        if (callback) {
            callback();
        }
    });
};

/**
 * Hits the enemy dealing the requested damage while playing the corresponding
 * animation. Optionally a callback can be invoked when the hit animation
 * has finished playing.
 * 
 * @example
 * // Without a callback.
 * rpgcode.hitEnemy("rat-1", 1, "DEFEND");
 * 
 * // With a callback.
 * rpgcode.hitEnemy("rat-1", 1, "DEFEND", function() {
 *  // The animation has ended, do something.
 * });
 * 
 * @param {String} spriteId ID of the BoardSprite that represents the enemy.
 * @param {Number} damage Amount of health to take away.
 * @param {String} animationId Animation to play while the enemy is hit.
 * @param {Callback} callback An optional function to invoke when the animation has ended.
 * @returns {undefined}
 */
RPGcode.prototype.hitEnemy = function (spriteId, damage, animationId, callback) {
    var enemy = rpgcode._getSpriteType(spriteId);
    if (enemy) {
        enemy.health -= damage;
        enemy.isHit = true;
        rpgcode.animateSprite(spriteId, animationId, function () {
            if (callback) {
                callback();
            }
            enemy.isHit = false;
        });
    } else {
        // Provide error feedback.
    }
};

/**
 * Returns a true/false value indicating whether an asset has been loaded.
 * 
 * @example
 * rpgcode.log(rpgcode.isAssetLoaded("Hero/attack_east.png", "image")); // logs true
 * rpgcode.log(rpgcode.isAssetLoaded("intro", "audio")); // logs true
 * 
 * @param {String} asset Filename of the asset including any subfolders.
 * @param {String} type Either "image" or "audio".
 * @returns {Boolean}
 */
RPGcode.prototype.isAssetLoaded = function (asset, type) {
    type = type.toLowerCase();
    if (type === "image") {
        return !!Crafty.assets[Crafty.__paths.images + asset];
    }
    if (type === "audio") {
        return !!Crafty.assets[Crafty.__paths.audio + asset];
    }
    return false;
};

/**
 * Returns a true/false value indicating whether standard movement controls
 * are currently enabled in the engine.
 * 
 * @returns {Boolean}
 */
RPGcode.prototype.isControlEnabled = function () {
    return rpgwizard.controlEnabled;
};

/**
 * Loads the requested assets into the engine, when all of the assets have been loaded
 * the onLoad callback is invoked.
 * 
 * @example
 * // Game assets used in this program.
 * var assets = {
 *  "audio": {
 *      "intro": "intro.mp3"
 *  },
 *  "images": [
 *      "block.png",
 *	"mwin_small.png",
 *	"sword_profile_1_small.png",
 *	"startscreen.png"
 *  ]
 * };
 * 
 * // Load up the assets we need
 * rpgcode.loadAssets(assets, function () {
 *  // Assets we need are ready, continue on...
 * });
 * 
 * @param {Object} assets Object of assets to load.
 * @param {Callback} onLoad Callback to invoke after assets are loaded.
 */
RPGcode.prototype.loadAssets = function (assets, onLoad) {
    if (assets.fonts) {
        var fontPromises = [];
        for (const font of assets.fonts) {
            fontPromises.push(new FontFace(font.name, `url(${PATH_FONT + font.file})`).load());
        }
        Promise.all(fontPromises).then(function () {
            if (rpgwizard.debugEnabled) {
                console.log(`loadedFonts=[${JSON.stringify(assets.fonts)}]`);
            }
            delete assets.fonts;
            rpgcode.loadAssets(assets, onLoad);
        }).catch(function() {
            console.error(`Failed to load assets.fonts=[${JSON.stringify(assets.fonts)}]!`);
            delete assets.fonts;
            rpgcode.loadAssets(assets, onLoad);
        });
    } else if (assets.programs) {
        assets.programs.forEach(function (program, i) {
            assets.programs[i] = program.replace(/\.[^/.]+$/, "");
        });
        requirejs(assets.programs, function () {
            delete assets.programs;
            Crafty.load(assets, onLoad);
        });
    } else {
        Crafty.load(assets, onLoad);
    }
};

/**
 * Loads the requested JSON file data from the requested path and returns the
 * JSON text as it appears in the file.
 * 
 * @example
 * rpgcode.loadJSON(
 *      "Boards/start.board", 
 *      function(response) {
 *          // Success callback.
 *          console.log(response);
 *      }, 
 *      function(response) {
 *          // Failure callback.
 *          console.log(response);
 *      }
 *  );
 * 
 * @param {String} path File path to read from.
 * @param {Callback} successCallback Invoked if the load succeeded.
 * @param {Callback} failureCallback Invoked if the load failed.
 * @returns {undefined}
 */
RPGcode.prototype.loadJSON = async function (path, successCallback, failureCallback) {
    try {
        var response = await fetch("http://localhost:8080/engine/load", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify({"path": path}),
            headers: {
                "Content-Type": "application/json",
                "Cache-Control": "no-cache"
            }
        });
        successCallback(await response.json());
    } catch (err) {
        failureCallback(err);
    }
};

/**
 * Log a message to the console.
 * 
 * @example 
 * rpgcode.log("Hello world!");
 * 
 * @param {String} message Message to log.
 */
RPGcode.prototype.log = function (message) {
    console.log(message);
};

/**
 * Plays the supplied sound file, up to five sound channels can be active at once.
 * 
 * @example
 * // Game assets used in this program.
 * var assets = {
 *  "audio": {
 *      "intro": "intro.mp3"
 *  }
 * };
 * 
 * // Load up the assets we need
 * rpgcode.loadAssets(assets, function () {
 *  // The sound file is loaded play it in an infinite loop.
 *  rpgcode.playSound("intro", true);
 * }); 
 * 
 * @param {String} file Relative path to the sound file to play.
 * @param {Boolean} loop Should it loop indefinitely?
 * @param {Number} volume (Optional) Value ranging from 1.0 to 0.0, default is 1.0 (i.e. 100%).
 * @returns {Object} A HTML5 audio element representing the playing sound.
 */
RPGcode.prototype.playSound = function (file, loop, volume = 1.0) {
    var count = loop ? -1 : 1;
    return Crafty.audio.play(file, count, volume);
};

/**
 * Moves the sprite by the requested number of pixels in the given direction.
 * 
 * @example
 * // Move the sprite with id "mysprite" in the north direction by 50 pixels.
 * var spriteId = "mysprite";
 * var direction = "NORTH";
 * var distancePx = 50;
 * rpgcode.moveSprite(spriteId, direction, distancePx);
 * 
 * @param {String} spriteId The ID set for the sprite as it appears in the editor.
 * @param {String} direction The direction to push the sprite in e.g. NORTH, SOUTH, EAST, WEST.
 * @param {Number} distance Number of pixels to move.
 */
RPGcode.prototype.moveSprite = function (spriteId, direction, distance) {
    // Quick conversion to Crafty constants: n, s, e, w.
    direction = direction[0].toLowerCase();

    switch (spriteId) {
        case "source":
            rpgcode.source.move(direction, distance);
            Crafty.trigger("Invalidate");
            break;
        default:
            if (rpgwizard.craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
                var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
                entity.move(direction, distance);
                Crafty.trigger("Invalidate");
            }
    }
};

/**
 * Measures text as it would appear on a canvas using the current font, returning
 * the width and height of the text in pixels.
 * 
 * @example
 * var dimensions = rpgcode.measureText("Hello world");
 * rpgcode.log(dimensions.width);
 * rpgcode.log(dimensions.height);
 * 
 * @param {String} text
 * @returns {Object} An object containing the width and height of the text.
 */
RPGcode.prototype.measureText = function (text) {
    var instance = rpgcode.canvases["renderNowCanvas"];
    var context = instance.canvas.getContext("2d");
    context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
    context.globalAlpha = rpgcode.globalAlpha;
    context.font = rpgcode.font;
    return {
        width: Math.round(context.measureText(text).width / rpgcode.getScale()), 
        height: Math.round(parseInt(context.font) / rpgcode.getScale())
    };
};

/**
 * Moves the character by n pixels in the given direction.
 * 
 * @example 
 * // Move the character with the "Hero" ID north 50 pixels.
 * rpgcode.moveCharacter("Hero", "NORTH", 50);
 * 
 * @param {String} characterId The id of the character to move. (unused)
 * @param {String} direction The direction to push the character in.
 * @param {Number} distance Number of pixels to move.
 */
RPGcode.prototype.moveCharacter = function (characterId, direction, distance) {
    // TODO: characterId is unused until multiple party members are supported.

    rpgwizard.craftyCharacter.move(direction, distance);
};

/**
 * Moves the character to the (x, y) position, the character will travel for the
 * supplied duration (milliseconds).
 * 
 * A short duration will result in the character arriving quicker and vice versa.
 * 
 * @example
 * // Move towards (100, 150) for 50 milliseconds, this will animate the character.
 * var characterId = "hero";
 * var x = 100;
 * var y = 150;
 * var delay = 50;
 * rpgcode.moveCharacterTo(characterId, x, y, delay);
 * 
 * @param {String} characterId The name set for the character as it appears in the editor.
 * @param {Number} x A pixel coordinate.
 * @param {Number} y A pixel coordinate.
 * @param {Number} duration Time taken for the movement to complete (milliseconds).
 * @param {Callback} callback Function to invoke when the sprite has finished moving.
 */
RPGcode.prototype.moveCharacterTo = function (characterId, x, y, duration, callback) {
    rpgwizard.craftyCharacter.trigger("TweenEnd", {});
    rpgwizard.craftyCharacter.cancelTween({x: true, y: true});
    rpgwizard.craftyCharacter.tweenEndCallbacks.push(callback);

    var location = rpgcode.getCharacterLocation();
    if (location.x !== x && location.y !== y) {
        rpgwizard.craftyCharacter.tween({x: x, y: y}, duration);
    } else if (location.x !== x) {
        rpgwizard.craftyCharacter.tween({x: x}, duration);
    } else {
        rpgwizard.craftyCharacter.tween({y: y}, duration);
    }
};

/**
 * Moves the sprite to the (x, y) position, the sprite will travel for the
 * supplied duration (milliseconds).
 * 
 * A short duration will result in the sprite arriving quicker and vice versa.
 * 
 * @example
 * // Move towards (100, 150) for 50 milliseconds, this will animate the sprite.
 * var spriteId = "mysprite";
 * var x = 100;
 * var y = 150;
 * var delay = 50;
 * rpgcode.moveSpriteTo(spriteId, x, y, delay);
 * 
 * @param {String} spriteId The ID set for the sprite as it appears in the editor.
 * @param {Number} x A pixel coordinate.
 * @param {Number} y A pixel coordinate.
 * @param {Number} duration Time taken for the movement to complete (milliseconds).
 * @param {Callback} callback Function to invoke when the sprite has finished moving.
 */
RPGcode.prototype.moveSpriteTo = function (spriteId, x, y, duration, callback) {
    if (rpgwizard.craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
        var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
        entity.trigger("TweenEnd", {});
        entity.cancelTween({x: true, y: true});
        entity.tweenEndCallbacks.push(callback);
        entity.tween({x: x, y: y}, duration);
    }
};

/**
 * Resets activation checks for the requested character, useful for cases where
 * you want continually check a program activation.
 * 
 * @example
 * // An example of a pushable block that can only be moved when the character is
 * // facing EAST. This program would be attached to an NPCs EventProgram.
 * var direction = rpgcode.getCharacterDirection();
 * if (!rpgcode.getGlobal("dungeonState").room2.doorOpened && direction === "EAST") {
 *  var id = "pushable-rock";
 *  var movementTime = 500;
 *  var animationId = "SOUTH";
 *
 *  var loc = rpgcode.getSpriteLocation("pushable-rock");
 *  loc.x += 16;
 *
 *  rpgcode.moveSpriteTo("pushable-rock", loc.x, loc.y, movementTime, function() { 
 *      rpgcode.playSound("door");
 *      rpgcode.destroySprite("closed_door");
 *      rpgcode.getGlobal("dungeonState").room2.doorOpened=true;
 *      rpgcode.endProgram();
 *  });
 * } else {
 *  if (!rpgcode.getGlobal("dungeonState").room2.doorOpened) {
 *      rpgcode.resetActivationChecks("Hero");
 *  }
 *  rpgcode.endProgram();
 * }
 *
 * @param {type} characterId The identifier associated with the character.
 * @returns {undefined}
 */
RPGcode.prototype.resetActivationChecks = function (characterId) {
    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    rpgwizard.craftyCharacter.activationVector.resetHitChecks();
};

/**
 * Registers a keyDown listener for a specific key, for a list of valid key values see:
 *    http://craftyjs.com/api/Crafty-keys.html
 *    
 * The callback function will continue to be invoked for every keyDown event until it
 * is unregistered.
 * 
 * @example 
 * rpgcode.registerKeyDown("ENTER", function(e) {
 *  rpgcode.log(e.key + " key is down!");
 * });
 * 
 * @param {String} key The key to listen to.
 * @param {Callback} callback The callback function to invoke when the keyDown event fires.
 * @param {Boolean} globalScope Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerKeyDown = function (key, callback, globalScope) {
    if (globalScope) {
        rpgwizard.keyDownHandlers[Crafty.keys[key]] = callback;
    } else {
        rpgwizard.keyboardHandler.downHandlers[Crafty.keys[key]] = callback;
    }
};

/**
 * Registers a keyUp listener for a specific key, for a list of valid key values see:
 *    http://craftyjs.com/api/Crafty-keys.html
 *    
 * The callback function will continue to be invoked for every keyUp event until it
 * is unregistered.
 * 
 * @example
 * rpgcode.registerKeyUp("ENTER", function() {
 *  rpgcode.log("Enter key is up!");
 * });
 * 
 * @param {String} key The key to listen to.
 * @param {Callback} callback The callback function to invoke when the keyUp event fires.
 * @param {Boolean} globalScope Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerKeyUp = function (key, callback, globalScope) {
    if (globalScope) {
        rpgwizard.keyUpHandlers[Crafty.keys[key]] = callback;
    } else {
        rpgwizard.keyboardHandler.upHandlers[Crafty.keys[key]] = callback;
    }
};

/**
 * Registers a mouse down event callback, when the mouse is pressed down the supplied 
 * callback function will be called and provided with the current mouse state.
 *    
 * The callback function will continue to be invoked for every mouse move event 
 * until it is unregistered.
 * 
 * @example
 * rpgcode.registerMouseDown(function(e) {
 *  // Log the x and y coordinates of the mouse.
 *  rpgcode.log(e.realX);
 *  rpgcode.log(e.realY);
 *  
 *  // Log the mouse button that has been pressed down.
 *  rpgcode.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
 * });
 * 
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {Boolean} globalScope Is this for use outside of the program itself? 
 * @returns {undefined}
 */
RPGcode.prototype.registerMouseDown = function (callback, globalScope) {
    if (globalScope) {
        rpgwizard.mouseDownHandler = callback;
    } else {
        rpgwizard.mouseHandler.mouseDownHandler = callback;
    }
};

/**
 * Registers a mouse move event callback, when the mouse is moved the supplied 
 * callback function will be called and provided with the current mouse state.
 *    
 * The callback function will continue to be invoked for every mouse move event 
 * until it is unregistered.
 * 
 * @example
 * rpgcode.registerMouseUp(function(e) {
 *  // Log the x and y coordinates of the mouse.
 *  rpgcode.log(e.realX);
 *  rpgcode.log(e.realY);
 *  
 *  // Log the mouse button that has been released.
 *  rpgcode.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
 * });
 * 
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {Boolean} globalScope Is this for use outside of the program itself? 
 * @returns {undefined}
 */
RPGcode.prototype.registerMouseUp = function (callback, globalScope) {
    if (globalScope) {
        rpgwizard.mouseUpHandler = callback;
    } else {
        rpgwizard.mouseHandler.mouseUpHandler = callback;
    }
};

/**
 * Registers a mouse move event callback, when the mouse is moved the supplied 
 * callback function will be called and provided with the current mouse state.
 *    
 * The callback function will continue to be invoked for every mouse move event 
 * until it is unregistered.
 * 
 * @example
 * rpgcode.registerMouseClick(function(e) {
 *  // Log the x and y coordinates of the mouse.
 *  rpgcode.log(e.realX);
 *  rpgcode.log(e.realY);
 *  
 *  // Log the mouse button that has been clicked.
 *  rpgcode.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
 * });
 * 
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {Boolean} globalScope Is this for use outside of the program itself? 
 * @returns {undefined}
 */
RPGcode.prototype.registerMouseClick = function (callback, globalScope) {
    if (globalScope) {
        rpgwizard.mouseClickHandler = callback;
    } else {
        rpgwizard.mouseHandler.mouseClickHandler = callback;
    }
};

/**
 * Registers a mouse move event callback, when the mouse is moved the supplied 
 * callback function will be called and provided with the current mouse state.
 *    
 * The callback function will continue to be invoked for every mouse move event 
 * until it is unregistered.
 * 
 * @example
 * rpgcode.registerMouseDoubleClick(function(e) {
 *  // Log the x and y coordinates of the mouse.
 *  rpgcode.log(e.realX);
 *  rpgcode.log(e.realY);
 *  
 *  // Log the mouse button that has been double clicked.
 *  rpgcode.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
 * });
 * 
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {Boolean} globalScope Is this for use outside of the program itself? 
 * @returns {undefined}
 */
RPGcode.prototype.registerMouseDoubleClick = function (callback, globalScope) {
    if (globalScope) {
        rpgwizard.mouseDoubleClickHandler = callback;
    } else {
        rpgwizard.mouseHandler.mouseDoubleClickHandler = callback;
    }
};

/**
 * Registers a mouse move event callback, when the mouse is moved the supplied 
 * callback function will be called and provided with the current mouse state.
 *    
 * The callback function will continue to be invoked for every mouse move event 
 * until it is unregistered.
 * 
 * @example
 * rpgcode.registerMouseMove(function(e) {
 *  // Log the x and y coordinates of the mouse.
 *  rpgcode.log(e.realX);
 *  rpgcode.log(e.realY);
 * });
 * 
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {Boolean} globalScope Is this for use outside of the program itself? 
 * @returns {undefined}
 */
RPGcode.prototype.registerMouseMove = function (callback, globalScope) {
    if (globalScope) {
        rpgwizard.mouseMoveHandler = callback;
    } else {
        rpgwizard.mouseHandler.mouseMoveHandler = callback;
    }
};

/**
 * Removes assets from the engine and frees up the memory allocated to them.
 * 
 * @example
 * // Game assets to unload from the engine.
 * var assets = {
 *  "audio": {
 *      "intro": "intro.mp3"
 *  }
 * };
 * 
 * rpgcode.removeAssets(assets); 
 * 
 * @param {Object} assets The object containing the assets identifiers.
 */
RPGcode.prototype.removeAssets = function (assets) {
    Crafty.removeAssets(assets);
};

/**
 * Removes a globally scoped variable from the engine.
 * 
 * @param {String} id
 * @returns {undefined}
 */
RPGcode.prototype.removeGlobal = function (id) {
    if (rpgcode.globals[id]) {
        delete rpgcode.globals[id];
    }
};

/**
 * Renders the specified canvas, if none then the "renderNowCanvas" is shown.
 * 
 * @example
 * // Draw a rectangle on the default canvas and render it.
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.fillRect(0, 0, 100, 100);
 * rpgcode.renderNow();
 * 
 * // Create a canvas and draw a red rectangle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.fillRect(0, 0, 100, 100, canvas);
 * rpgcode.renderNow(canvas);  
 * 
 * @param {String} canvasId The ID of the canvas to render.
 */
RPGcode.prototype.renderNow = function (canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var canvas = rpgcode.canvases[canvasId];
    if (canvas) {
        canvas.render = true;
        Crafty.trigger("Invalidate");
    }
};

/**
 * Replaces a tile at the supplied (x, y, z) position.
 * 
 * @example
 * // Places the tile at (x: 11, y: 10, z: 0) with the 81st tile 
 * // from the tileset "tileset1.tileset".
 * rpgcode.replaceTile(11, 10, 0, "tileset1.tileset", 81);
 * 
 * @param {Number} tileX The x position in tiles.
 * @param {Number} tileY The y postion in tiles.
 * @param {Number} layer The layer the tile is on.
 * @param {String} tileSet The name of the TileSet of the replacement tile.
 * @param {Number} tileIndex The index of the tile in the replacement set.
 */
RPGcode.prototype.replaceTile = function (tileX, tileY, layer, tileSet, tileIndex) {
    var tile = rpgwizard.tilesets[tileSet].getTile(tileIndex);
    rpgwizard.craftyBoard.board.replaceTile(tileX, tileY, layer, tile);
};

/**
 * Removes the layer image with the ID on the specified layer. If the image does
 * not exist on the layer then there is no effect.
 * 
 * @example
 * rpgcode.removeLayerImage("battle.background", 1); // Remove the image with ID "battle.background" on layer 1 
 * 
 * @param {String} id Unique ID of the layer image to remove.
 * @param {Number} layer Layer index on the board, first layer starts at 0.
 * @returns {undefined}
 */
RPGcode.prototype.removeLayerImage = function (id, layer) {
    if (layer < rpgwizard.craftyBoard.board.layers.length) {
        var boardLayer = rpgwizard.craftyBoard.board.layers[layer];
        var length = boardLayer.images.length;
        for (var i = 0; i < length; i++) {
            var image = boardLayer.images[i];
            if (image.id === id) {
                boardLayer.images.splice(i, 1);
                break;
            }
        }
    }
};

/**
 * Removes a run time program from the engine, if the program is currently
 * executing it will be allowed to finish first.
 * 
 * @example
 * rpgcode.removeRunTimeProgram("UI/HUD.js");
 * 
 * @param {type} filename
 * @returns {undefined}
 */
RPGcode.prototype.removeRunTimeProgram = function (filename) {
    var index = this.runTimePrograms.indexOf(filename);

    if (index > -1) {
        this.runTimePrograms.splice(index, 1);
    }
};

/**
 * Removes the specified tile from the board.
 * 
 * @example
 * // Removes the tile at (x: 11, y: 9, z: 1).
 * rpgcode.removeTile(11, 9, 1); 
 * 
 * @param {Number} tileX The x position in tiles.
 * @param {Number} tileY The y postion in tiles.
 * @param {Number} layer The layer the tile is on.
 */
RPGcode.prototype.removeTile = function (tileX, tileY, layer) {
    rpgwizard.craftyBoard.board.removeTile(tileX, tileY, layer);
};

/**
 * Restarts the game by refreshing the browser page.
 * 
 * @example
 * rpgcode.restart(); // Will refresh the browser page.
 */
RPGcode.prototype.restart = function () {
    location.reload(); // Cheap way to implement game restart for the moment.
};

/**
 * Runs the requested program, movement is disabled for the programs duration.
 * 
 * @example
 * rpgcode.runProgram("MyProgram.js");
 * 
 * @param {String} filename
 */
RPGcode.prototype.runProgram = function (filename) {
    rpgwizard.runProgram(PATH_PROGRAM + filename, rpgcode, null);
};

/**
 * Sets the position of a canvas relative to its parent.
 * 
 * @example
 * // Create a canvas and draw a red rectangle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.fillRect(0, 0, 100, 100, canvas);
 * rpgcode.renderNow(canvas);
 * 
 * // Now move it to a new position relative to its parent.
 * rpgcode.setCanvasPosition(100, 100, canvas);
 * 
 * @param {type} x
 * @param {type} y
 * @param {type} canvasId
 * @returns {undefined}
 */
RPGcode.prototype.setCanvasPosition = function (x, y, canvasId) {
    var rpgcodeCanvas = rpgcode.canvases[canvasId];
    // Scale parameters
    if (rpgcode.scale) {
        x *= rpgcode.getScale();
        y *= rpgcode.getScale();
    }
    if (rpgcodeCanvas) {
        rpgcodeCanvas.x = x;
        rpgcodeCanvas.y = y;
    }
};

/**
 * Saves the requested JSON data to the file specified, if the file does not
 * exist it is created. If the file does exist it is overwritten.
 * 
 * @example
 * rpgcode.saveJSON(
 *  {
 *      path: "Boards/start2.board", // Subdirectory and file to save.
 *      data: {"Test": "Hello world!"} // JSON data to store.
 *  },
 *  function(response) {
 *     // Success callback.
 *     console.log(response);
 *     rpgcode.endProgram();
 *  }, 
 *  function(response) {
 *     // Failure callback.
 *     console.log(response);
 *     rpgcode.endProgram();
 *  }
 * );
 * 
 * @param {Object} data JSON object containing path and data properties.
 * @param {Callback} successCallback Invoked if the file save succeeded.
 * @param {Callback} failureCallback Invoked if the file save failed.
 * @returns {undefined}
 */
RPGcode.prototype.saveJSON = async function (data, successCallback, failureCallback) {
    try {
        await fetch("http://localhost:8080/engine/save", {
            method: "POST",
            credentials: "include",
            body: JSON.stringify(data),
            headers: {
                "Content-Type": "application/json",
                "Cache-Control": "no-cache"
            }
        });
        successCallback("success");
    } catch (err) {
        failureCallback(err);
    }
};

/**
 * Sends the character to a board and places them at the given (x, y) position in tiles.
 * 
 * @example
 * rpgcode.sendToBoard("Room1.board", 11.5, 18);
 * 
 * @param {String} boardName The board to send the character to.
 * @param {Number} tileX The x position to place the character at, in tiles.
 * @param {Number} tileY The y position to place the character at, in tiles.
 * @param {Number} layer The layer to place the character on.
 */
RPGcode.prototype.sendToBoard = async function (boardName, tileX, tileY, layer) {
    if (layer === undefined || layer === null || layer < 0) {
        // Backwards compatability check.
        layer = rpgwizard.craftyCharacter.character.layer;
    }
    await rpgwizard.switchBoard(boardName, tileX, tileY, layer);
};

/**
 * Sets the RGBA color for all drawing operations to use.
 * 
 * @example
 * // Set the color to red.
 * rpgcode.setColor(255, 0, 0, 1.0);
 * 
 * @param {Number} r
 * @param {Number} g
 * @param {Number} b
 * @param {Number} a
 */
RPGcode.prototype.setColor = function (r, g, b, a) {
    rpgcode.rgba = {r: r, g: g, b: b, a: a};
};

/**
 * Sets the engine font to the specified size, and font family.
 *
 * @example
 * // Set the global font to 8px Lucida Console
 * rpgcode.setFont(8, family);
 *   
 * @param {Number} size in pixels
 * @param {String} family E.g. Arial, Comic Sans, etc.
 */
RPGcode.prototype.setFont = function (size, family) {
    rpgcode.font = Math.round(size * rpgcode.getScale()) + "px " + family;
};

/**
 * Sets a global value in the engine, if it doesn't exist it is created.
 *
 * @example
 * // Store a simple boolean.
 * rpgcode.setGlobal("swordactive", false);
 * 
 * // Store a string.
 * rpgcode.setGlobal("name", "Bob");
 * 
 * // Store an object.
 * rpgcode.setGlobal("shield", {def: 10, price: 100}); 
 *   
 * @param {String} id The ID to use for this global.
 * @param {Object} value The value this global holds.
 */
RPGcode.prototype.setGlobal = function (id, value) {
    rpgcode.globals[id] = value;
};

/**
 * Sets the global drawing alpha for all subsequent canvas drawing operation. 
 * Useful for drawing transparent elements to a canvas.
 * 
 * For more details see:
 *  https://www.w3schools.com/Tags/canvas_globalalpha.asp
 * 
 * @param {type} alpha
 * @returns {undefined}
 */
RPGcode.prototype.setGlobalAlpha = function (alpha) {
    rpgcode.globalAlpha = alpha;
};

/**
 * @deprecated since 1.7.0, use "drawImage" instead.
 */
RPGcode.prototype.setImage = function (fileName, x, y, width, height, canvasId) {
    rpgcode.drawImage(fileName, x, y, width, height, 0, canvasId);
};

/**
 * @deprecated since 1.7.0, use "drawImagePart" instead.
 */
RPGcode.prototype.setImagePart = function (fileName, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, canvasId) {
    rpgcode.drawImagePart(fileName, srcX, srcY, srcWidth, srcHeight, destX, destY, destWidth, destHeight, 0, canvasId);
};

/**
 * Sets the pixel ImageData at the (x, y) coordinate on the canvas.
 * 
 * @example
 * // Draw a rectangle on the default canvas and render it.
 * rpgcode.setColor(255, 0, 0, 1.0);
 * rpgcode.fillRect(0, 0, 100, 100);
 * rpgcode.renderNow();
 * 
 * // Set a pixel to green at (50, 50) from the rectangle.
 * rpgcode.setColor(0, 255, 0, 1.0);
 * rpgcode.setPixel(50, 50);
 * 
 * @param {Number} x In pixels.
 * @param {Number} y In pixels.
 * @param {String} canvasId The ID of the canvas to draw on, defaults to "renderNowCanvas" if none specified.
 */
RPGcode.prototype.setPixel = function (x, y, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }
    // Scale parameters
    if (rpgcode.scale) {
        x *= rpgcode.getScale();
        y *= rpgcode.getScale();
    }
    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        var context = instance.canvas.getContext("2d");
        context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
        var imageData = context.getImageData(x, y, 1, 1);
        var rgba = rpgcode.rgba;
        imageData.data[0] = rgba.r;
        imageData.data[1] = rgba.g;
        imageData.data[2] = rgba.b;
        imageData.data[3] = rgba.a * 255;
        context.putImageData(imageData, x, y);
    }
};

/**
 * Sets the dialog box's profile and text area dimensions.
 * 
 * @example
 * // Set the profile picture to use and the background dialog image.
 * var profileDimensions = {width: 100, height: 100};
 * var dialogDimensions = {width: 640, height: 480};
 * rpgcode.setDialogDimensions(profileDimensions, dialogDimensions);
 * 
 * @param {Object} profileDimensions Dimensions object containing a width and height.
 * @param {Object} dialogDimensions Dimensions object containing a width and height.
 */
RPGcode.prototype.setDialogDimensions = function (profileDimensions, dialogDimensions) {
    rpgcode.dialogWindow.profileDimensions = profileDimensions;
    rpgcode.dialogWindow.dialogDimensions = dialogDimensions;
};

/**
 * Sets the dialog box's speaker profile image and the background image.
 * 
 * @example
 * // Set the profile picture to use and the background dialog image.
 * rpgcode.setDialogGraphics("sword_profile_1_small.png", "mwin_small.png");
 * 
 * @param {String} profileImage The relative path to the profile image.
 * @param {String} backgroundImage The relative path to the background image.
 */
RPGcode.prototype.setDialogGraphics = function (profileImage, backgroundImage) {
    rpgcode.dialogWindow.profile = profileImage;
    rpgcode.dialogWindow.background = backgroundImage;
};

/**
 * Sets the padding from the top left corner of the dialog window for
 * the x and/or y values. This can be used to prevent text from overlapping
 * the start or end of the dialog window graphics. The default padding value
 * is 5px for both x and y.
 * 
 * @example
 * // Set just the x padding value.
 * rpgcode.setDialogPadding({x: 10});
 * 
 * // Set just the y padding value.
 * rpgcode.setDialogPadding({y: 10});
 * 
 * // Set both.
 * rpgcode.setDialogPadding({x: 10, y: 10});
 * 
 * @param {Object} padding An object containing x and/or y padding values in pixels.
 * @returns {undefined}
 */
RPGcode.prototype.setDialogPadding = function (padding) {
    if (padding) {
        if (padding.x) {
            rpgcode.dialogWindow.paddingX = padding.x;
        }
        if (padding.y) {
            rpgcode.dialogWindow.paddingY = padding.y;
        }
    } else {
        // Provide error feedback.
    }
};

/**
 * Sets the position of the dialog window for the next call to showDialog.
 * The dialog window can either appear at the top of the screen i.e. "NORTH", or
 * the bottom of the screen i.e. "SOUTH". The default position is "SOUTH".
 * 
 * @example
 * rpgcode.setDialogPosition(rpgcode.dialogPosition.NORTH);
 * rpgcode.showDialog("Hello north!");
 * 
 * rpgcode.delay(5000, function() {
 *  rpgcode.clearDialog();
 *  rpcode.setDialogPosition(rpgcode.dialogPosition.SOUTH);
 *  rpgcode.showDialog("Hello south!");
 * });
 * 
 * @param {String} position Either NORTH (top of screen) or SOUTH (bottom of screen).
 * @returns {undefined}
 */
RPGcode.prototype.setDialogPosition = function (position) {
    if (position) {
        if (position === rpgcode.dialogPosition.NORTH || position === rpgcode.dialogPosition.SOUTH) {
            rpgcode.dialogWindow.position = position;
        } else {
            // Provide error feedback.    
        }
    } else {
        // Provide error feedback.
    }
};

/**
 * Sets the location of the sprite.
 * 
 * @example
 * // Place a sprite at the tile coordinates (10, 10, 1).
 * var spriteId = "mysprite";
 * var x = 10;
 * var y = 10;
 * var layer = 1;
 * var inTiles = true;
 * rpgcode.setSpriteLocation(spriteId, x, y, layer, inTiles);
 * 
 * @param {String} spriteId The ID set for the sprite as it appears in the editor.
 * @param {Number} x In pixels by default.
 * @param {Number} y In pixels by default.
 * @param {Number} layer Target layer to put the sprite on.
 * @param {Boolean} inTiles Is (x, y) in tile coordinates, defaults to pixels.
 */
RPGcode.prototype.setSpriteLocation = function (spriteId, x, y, layer, inTiles) {
    if (inTiles) {
        x *= rpgwizard.craftyBoard.board.tileWidth;
        y *= rpgwizard.craftyBoard.board.tileHeight;
    }

    var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
    if (entity) {
        entity.x = x;
        entity.y = y;
        entity.layer = layer;
        entity.sprite.layer = layer;
        Crafty.trigger("Invalidate");
    }
};

/**
 * Sets the sprite's current stance, uses the first frame in the animation.
 * 
 * @example
 * // Set the sprite with ID "mysprite" to its idle stance.
 * var spriteId = "mysprite";
 * var stanceId = "IDLE";
 * rpgcode.setSpriteStance(spriteId, stanceId);
 * 
 * @param {String} spriteId The ID set for the sprite as it appears in the editor.
 * @param {String} stanceId The stanceId (animationId) to use.
 */
RPGcode.prototype.setSpriteStance = function (spriteId, stanceId) {
    if (rpgwizard.craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
        var type = rpgcode._getSpriteType(spriteId);
        if (type) {
            type.changeGraphics(stanceId);
        }
    }
};

/**
 * Sets the character's location without triggering any animation.
 * 
 * @example
 * var characterId = "Hero";
 * var x = 10;
 * var y = 10;
 * var layer = 1;
 * var isTiles = true;
 * rpgcode.setCharacterLocation(characterId, x, y, layer, isTiles);
 * 
 * @param {String} characterId The identifier associated with character to move.
 * @param {Number} x In pixels by default.
 * @param {Number} y In pixels by default.
 * @param {Number} layer Target layer to put the character on.
 * @param {Boolean} isTiles Is (x, y) in tile coordinates, defaults to pixels.
 */
RPGcode.prototype.setCharacterLocation = function (characterId, x, y, layer, isTiles) {
    if (isTiles) {
        x *= rpgwizard.craftyBoard.board.tileWidth;
        y *= rpgwizard.craftyBoard.board.tileHeight;
    }

    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    rpgwizard.craftyCharacter.x = x;
    rpgwizard.craftyCharacter.y = y;
    rpgwizard.craftyCharacter.character.layer = layer;
};

/**
 * Sets the character speed by proportionally applying the change, can be used
 * to increase or decrease the character speed by some factor 
 * i.e. 3 times faster or 3 times slower. Positive change values are interpreted 
 * as an increase and negative values as a decrease.
 * 
 * @example
 * // Increase the character's movement speed by 3 times.
 * rpgcode.setCharacterSpeed("Hero", 3);
 * 
 * // Decrease the character's movement speed by 3 times, returning it to normal.
 * rpgcode.setCharacterSpeed("Hero", -3);
 * 
 * @param {String} characterId The index of the character on the board.
 * @param {String} change Factor to change the character speed by.
 */
RPGcode.prototype.setCharacterSpeed = function (characterId, change) {
    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    if (change === 0) {
        return;
    } else if (change > 0) {
        rpgwizard.craftyCharacter._speed *= change;
        rpgwizard.craftyCharacter._diagonalSpeed *= change;
    } else if (change < 0) {
        rpgwizard.craftyCharacter._speed /= -change;
        rpgwizard.craftyCharacter._diagonalSpeed /= -change;
    }
};

/**
 * Sets the character's current stance, uses the first frame in the animation.
 * 
 * @example
 * // Set the sprite with ID 5 to its idle stance.
 * var characterId = "Hero";
 * var stanceId = "IDLE";
 * rpgcode.setCharacterStance(characterId, stanceId); 
 * 
 * @param {String} characterId The index of the character on the board.
 * @param {String} stanceId The stanceId (animationId) to use.
 */
RPGcode.prototype.setCharacterStance = function (characterId, stanceId) {
    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    rpgwizard.craftyCharacter.character.changeGraphics(stanceId);
    Crafty.trigger("Invalidate");
};

/**
 * Shows the dialog window and adds the dialog to it if it is already 
 * visible the dialog is just appended to the current window.
 * 
 * Note the dialog window is drawn on the default "renderNowCanvas".
 * 
 * @example
 * rpgcode.showDialog("Pssst, Over here");
 * 
 * @param {String} dialog The dialog to output.
 */
RPGcode.prototype.showDialog = function (dialog) {
    var dialogWindow = rpgcode.dialogWindow;
    var fullWidth = dialogWindow.profileDimensions.width + dialogWindow.dialogDimensions.width;
    var x1, y1, x2, y2;
    if (dialogWindow.position === rpgcode.dialogPosition.NORTH) {
        // Draw from the top left.
        x1 = Math.round((rpgcode.getViewport().width - fullWidth) / 2);
        x2 = x1 + dialogWindow.profileDimensions.width;
        y1 = y2 = 0;
    } else {
        // Draw from the bottom left.
        x1 = Math.round((rpgcode.getViewport().width - fullWidth) / 2);
        x2 = x1 + dialogWindow.profileDimensions.width;
        y1 = Crafty.viewport.height - dialogWindow.profileDimensions.width;
        y2 = y1;
    }

    if (!dialogWindow.visible) {
        rpgcode.setImage(
                dialogWindow.profile, x1, y1,
                dialogWindow.profileDimensions.width,
                dialogWindow.profileDimensions.width
                );
        rpgcode.setImage(
                dialogWindow.background, x2, y2,
                dialogWindow.dialogDimensions.width,
                dialogWindow.dialogDimensions.height
                );
        dialogWindow.visible = true;
    }

    dialogWindow.paddingY += parseInt(rpgcode.font);
    rpgcode.drawText(dialogWindow.paddingX + x2, dialogWindow.paddingY + y1, dialog);
    rpgcode.renderNow();
};

/**
 * Stop playing a specific sound file, if no file is set stop all sounds.
 * 
 * @example
 * rpgcode.stopSound("intro");
 * 
 * @param {String} file The relative path of the sound file to stop.
 */
RPGcode.prototype.stopSound = function (file) {
    if (file) {
        Crafty.audio.stop(file);
    } else {
        Crafty.audio.stop();
    }
};

/**
 * Takes the item from the specified character's inventory.
 * 
 * @param {String} filename The filename of the item.
 * @param {String} characterId The character to remove it from.
 * @returns {undefined}
 */
RPGcode.prototype.takeItem = function (filename, characterId) {
    var inventory = rpgwizard.craftyCharacter.character.inventory;
    if (inventory[filename]) {
        inventory[filename].pop();
        if (inventory[filename].length === 0) {
            delete inventory[filename];
        }
    }
};

/**
 * Removes a previously registered KeyDown listener.
 * 
 * @example
 * rpgcode.unregisterKeyDown("ENTER");
 * 
 * @param {String} key The key associated with the listener.
 * @param {Boolean} globalScope Is this a global scope key down handler.
 */
RPGcode.prototype.unregisterKeyDown = function (key, globalScope) {
    if (globalScope) {
        delete rpgwizard.keyDownHandlers[Crafty.keys[key]];
    } else {
        delete rpgwizard.keyboardHandler.downHandlers[Crafty.keys[key]];
    }
};

/**
 * Removes a previously registered KeyUp listener.
 * 
 * @example
 * rpgcode.unregisterKeyUp("ENTER");
 * 
 * @param {String} key The key associated with the listener.
 * @param {Boolean} globalScope Is this a global scope key up handler.
 */
RPGcode.prototype.unregisterKeyUp = function (key, globalScope) {
    if (globalScope) {
        delete rpgwizard.keyUpHandlers[Crafty.keys[key]];
    } else {
        delete rpgwizard.keyboardHandler.upHandlers[Crafty.keys[key]];
    }
};

/**
 * Removes a previously registered mouse down handler.
 * 
 * @example
 * // Removes the mouse down handler local to this program.
 * rpgcode.unregisterMouseDown();
 * 
 * // Removes the global engine mouse down handler.
 * rpgcode.unregisterMouseDown(true);
 * 
 * @param {Boolean} globalScope Is this a global scope mouse handler.
 * @returns {undefined}
 */
RPGcode.prototype.unregisterMouseDown = function (globalScope) {
    if (globalScope) {
        rpgwizard.mouseDownHandler = null;
    } else {
        rpgwizard.mouseHandler.mouseDownHandler = null;
    }
};

/**
 * Removes a previously registered mouse up handler.
 * 
 * @example
 * // Removes the mouse up handler local to this program.
 * rpgcode.unregisterMouseUp();
 * 
 * // Removes the global engine mouse move handler.
 * rpgcode.unregisterMouseUp(true);
 * 
 * @param {Boolean} globalScope Is this a global scope mouse handler.
 * @returns {undefined}
 */
RPGcode.prototype.unregisterMouseUp = function (globalScope) {
    if (globalScope) {
        rpgwizard.mouseUpHandler = null;
    } else {
        rpgwizard.mouseHandler.mouseUpHandler = null;
    }
};

/**
 * Removes a previously registered mouse click handler.
 * 
 * @example
 * // Removes the mouse click handler local to this program.
 * rpgcode.unregisterMouseClick();
 * 
 * // Removes the global engine mouse click handler.
 * rpgcode.unregisterMouseClick(true);
 * 
 * @param {Boolean} globalScope Is this a global scope mouse handler.
 * @returns {undefined}
 */
RPGcode.prototype.unregisterMouseClick = function (globalScope) {
    if (globalScope) {
        rpgwizard.mouseClickHandler = null;
    } else {
        rpgwizard.mouseHandler.mouseClickHandler = null;
    }
};

/**
 * Removes a previously registered mouse double click handler.
 * 
 * @example
 * // Removes the mouse double click handler local to this program.
 * rpgcode.unregisterMouseDoubleClick();
 * 
 * // Removes the global engine mouse double click handler.
 * rpgcode.unregisterMouseDoubleClick(true);
 * 
 * @param {Boolean} globalScope Is this a global scope mouse handler.
 * @returns {undefined}
 */
RPGcode.prototype.unregisterMouseDoubleClick = function (globalScope) {
    if (globalScope) {
        rpgwizard.mouseDoubleClickHandler = null;
    } else {
        rpgwizard.mouseHandler.mouseDoubleClickHandler = null;
    }
};

/**
 * Removes a previously registered mouse move handler.
 * 
 * @example
 * // Removes the mouse move handler local to this program.
 * rpgcode.unregisterMouseMove();
 * 
 * // Removes the global engine mouse move handler.
 * rpgcode.unregisterMouseMove(true);
 * 
 * @param {Boolean} globalScope Is this a global scope mouse handler.
 * @returns {undefined}
 */
RPGcode.prototype.unregisterMouseMove = function (globalScope) {
    if (globalScope) {
        rpgwizard.mouseMoveHandler = null;
    } else {
        rpgwizard.mouseHandler.mouseMoveHandler = null;
    }
};

/**
 * Update the layer image with the ID on the specified layer. If the image does
 * not exist on the layer then there is no effect.
 * 
 * @example
 * var image = {
 *  "src": "battle-background.png", // pre-loaded asset image
 *  "x": 150,                       // x location on board in pixels
 *  "y": 100,                       // y location on board in pixels
 *  "id": "battle.background"       // unique for this layer image
 * };
 * 
 * rpgcode.updateLayerImage(image, 1); // Update the image with ID "battle.background" on layer 1 
 * 
 * @param {Object} image The layer image to update on the board.
 * @param {Number} layer Layer index on the board, first layer starts at 0.
 * @returns {undefined}
 */
RPGcode.prototype.updateLayerImage = function (image, layer) {
    if (layer < rpgwizard.craftyBoard.board.layers.length) {
        var boardLayer = rpgwizard.craftyBoard.board.layers[layer];
        var length = boardLayer.images.length;
        for (var i = 0; i < length; i++) {
            if (boardLayer.images[i].id === image.id) {
                boardLayer.images[i] = image;
                break;
            }
        }
    }
};