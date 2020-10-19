/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard, rpgcode, PATH_PROGRAM, PATH_ITEM, PATH_FONT, Crafty, Promise */

import { Core } from "../core.js";

// To enable JSDoc grouping of functions
/** @namespace Draw2D       */
/** @namespace Geometry     */
/** 
 * For more information, see [Asset Management]{@tutorial 03-Asset-Management}
 * @namespace Asset        
 */
/** @namespace Global       */
/** @namespace Character    */
/** @namespace Sprite       */
/** @namespace Item         */
/** @namespace Program      */
/** @namespace Board        */
/** @namespace Canvas       */
/** @namespace Image        */
/** @namespace Text         */
/** @namespace Sound        */
/** @namespace Keyboard     */
/** @namespace Mouse        */
/** @namespace File         */
/** @namespace Util         */

/**
 * The engine RPGcode API.
 * 
 * @class
 * @constructor
 * 
 * @returns {RPGcode}
 */
export function RPGcode() {};

RPGcode.prototype.setup = function() {
    // The last entity to trigger a program.
    this.source = {};

    // An array of programs that will be run each frame.
    this.runTimePrograms = [];

    this.canvases = {
        "renderNowCanvas": {
            canvas: Core.getInstance().screen.renderNowCanvas,
            render: false,
            x: 0,
            y: 0
        }
    };

    // Global variable storage for user programs.
    this.globals = {};

    this.rgba = { r: 255, g: 255, b: 255, a: 1.0 };
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
    return Core.getInstance().craftyBoard.board.sprites.findIndex(function (entity) {
        return entity.getId() === craftyId;
    });
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
 * @memberof Image
 * @alias addLayerImage
 * @param {Object} image The layer image to add to the board.
 * @param {number} layer Layer index on the board, first layer starts at 0.
 */
RPGcode.prototype.addLayerImage = function (image, layer) {
    if (layer < Core.getInstance().craftyBoard.board.layers.length) {
        Core.getInstance().craftyBoard.board.layers[layer].images.push(image);
    }
};

/**
 * Adds a program that will be called at runtime for each frame. You 
 * should avoid doing any lengthy operations with these programs.
 * 
 * @example
 * rpgcode.addRunTimeProgram("UI/HUD.js");
 * 
 * @memberof Program
 * @alias addRunTimeProgram
 * @param {string} filename Filename of the JavaScript file stored in Programs folder.
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
 * @memberof Sprite
 * @alias addSprite
 * @param {Object} sprite BoardSprite object to add.
 * @param {Callback} callback If defined, the function to invoke after the sprite has been added.
 */
RPGcode.prototype.addSprite = async function (sprite, callback) {
    Core.getInstance().craftyBoard.board.sprites[sprite.id] = await Core.getInstance().loadSprite(sprite);
    Core.getInstance().loadCraftyAssets(callback);
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
 * @memberof Sprite
 * @alias animateSprite
 * @param {string} spriteId The ID set for the sprite as it appears in the editor.
 * @param {string} animationId The requested animation to play for the sprite.
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
 * @memberof Character
 * @alias animateCharacter
 * @param {string} characterId The label associated with the character. 
 * @param {string} animationId The requested animation to character for the character.
 * @param {Callback} callback If defined, the function to invoke at the end of the animation.
 */
RPGcode.prototype.animateCharacter = function (characterId, animationId, callback) {
    // TODO: characterId will be unused until parties with multiple characters are supported.
    var character = Core.getInstance().craftyCharacter.character;
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
 * @memberof Character
 * @alias changeCharacterGraphics
 * @param {string} characterId The label associated with the character. 
 * @param {Object} swaps An object containing the graphics to swap.
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
 * @memberof Canvas
 * @alias clearCanvas
 * @param {string} [canvasId=renderNowCanvas] The canvas to clear.
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
 * @memberof Util
 * @alias clearDialog
 * @deprecated
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
 * @memberof Canvas
 * @alias createCanvas
 * @param {number} width In pixels.
 * @param {number} height In pixels.
 * @param {string} canvasId The ID to be associated with the canvas.
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
 * Delays the execution of a callback for a specified number of milliseconds, 
 * after which the callback function is invoked.
 * 
 * @example 
 * // Logs to the console after 5 seconds
 * rpgcode.delay(5000, function(){ 
 *  rpgcode.log("Hello world!");
 * });
 * 
 * @memberof Util
 * @alias delay
 * @param {number} ms Time to wait in milliseconds.
 * @param {Callback} callback Function to execute after the delay.
 * @param {boolean} loop Should the call be indefinite?
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
 * Destroys the canvas with the specified ID, removing it from the engine.
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
 * @memberof Canvas
 * @alias destroyCanvas
 * @param {string} canvasId The ID for the canvas to destroy.
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
 * @memberof Sprite
 * @alias destroySprite
 * @param {string} spriteId The ID set for the sprite as it appears in the editor.
 */
RPGcode.prototype.destroySprite = function (spriteId) {
    if (Core.getInstance().craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
        var entity = Core.getInstance().craftyBoard.board.sprites[spriteId];
        entity.destroy();
        delete Core.getInstance().craftyBoard.board.sprites[spriteId];
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
 * @memberof Draw2D
 * @alias drawCircle
 * @param {number} x The start x postion.
 * @param {number} y The start y postion.
 * @param {number} radius In pixels.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
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
 * Draws an image onto a canvas, if no canvas is specified the default canvas will be used.
 * 
 * @example
 * // Draw the image onto the default canvas.
 * rpgcode.drawImage("life.png", 0, 0, 32, 32, 0);
 * 
 * // Draw the image onto the canvas we specified.
 * rpgcode.drawImage("life.png", 0, 0, 32, 32, 0, "myCanvas");
 * 
 * @memberof Image
 * @alias drawImage
 * @param {string} fileName The relative path to the image.
 * @param {number} x The start position x in pixels.
 * @param {number} y The start position y in pixels.
 * @param {number} width In pixels.
 * @param {number} height In pixels.
 * @param {number} rotation In radians.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to put the image on.
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
 * Draws part of image an onto a canvas, if no canvas is specified the default canvas will be used.
 * 
 * @example
 * // Draw part of the image onto the default canvas.
 * rpgcode.drawImagePart("objects.png", 64, 0, 16, 16, 8, 8, 16, 16, 0);
 * 
 * // Draw part of the image onto the canvas we specified.
 * rpgcode.drawImagePart("objects.png", 64, 0, 16, 16, 8, 8, 16, 16, 0, "myCanvas");
 * 
 * @memberof Image
 * @alias drawImagePart
 * @param {string} fileName The relative path to the image.
 * @param {number} srcX The start position x in pixels from the source image.
 * @param {number} srcY The start position y in pixels from the source image.
 * @param {number} srcWidth In pixels from the source image.
 * @param {number} srcHeight In pixels from the source image.
 * @param {number} destX The start position x in pixels on the destination canvas.
 * @param {number} destY The start position y in pixels on the destination canvas.
 * @param {number} destWidth In pixels on the destination canvas.
 * @param {number} destHeight In pixels on the destination canvas.
 * @param {number} rotation In radians.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to put the image on.
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
 * @memberof Draw2D
 * @alias drawLine
 * @param {number} x1 In pixels.
 * @param {number} y1 In pixels.
 * @param {number} x2 In pixels.
 * @param {number} y2 In pixels.
 * @param {number} lineWidth In pixels.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
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
 * Draws the source canvas onto the target canvas. Useful for combining multiple
 * canvases together.
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
 * @memberof Canvas
 * @alias drawOntoCanvas
 * @param {string} sourceId The ID of the source canvas.
 * @param {number} x Position in pixels on the target canvas.
 * @param {number} y Position y in pixels on the target canvas.
 * @param {number} width In pixels.
 * @param {number} height In pixels.
 * @param {string} targetId The ID of the target canvas.
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
 * @memberof Draw2D
 * @alias drawRect
 * @param {number} x The start x postion.
 * @param {number} y The start y postion.
 * @param {number} width In pixels.
 * @param {number} height In pixels.
 * @param {number} lineWidth In pixels.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
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
 * @memberof Draw2D
 * @alias drawRoundedRect
 * @param {number} x The start x postion.
 * @param {number} y The start y postion.
 * @param {number} width In pixels.
 * @param {number} height In pixels.
 * @param {number} lineWidth In pixels.
 * @param {number} radius In pixels.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
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
 * @memberof Text
 * @alias drawText
 * @param {number} x The start position x in pixels.
 * @param {number} y The start postion y in pixels.
 * @param {string} text A string of text to draw.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw onto.
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
 * @memberof Program
 * @alias endProgram
 * @param {string} nextProgram The relative path to the next program to execute.
 */
RPGcode.prototype.endProgram = function (nextProgram) {
    // if (nextProgram) {
    //     Core.getInstance().endProgram(nextProgram);
    // } else {
    //     Core.getInstance().endProgram();
    // }
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
 * @memberof Draw2D
 * @alias fillCircle
 * @param {number} x The start x postion.
 * @param {number} y The start y postion.
 * @param {number} radius The start y postion.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
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
 * @memberof Draw2D
 * @alias fillRect
 * @param {number} x The start x postion.
 * @param {number} y The start y postion.
 * @param {number} width In pixels.
 * @param {number} height In pixels.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
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
 * @memberof Draw2D
 * @alias fillRoundedRect
 * @param {number} x The start x postion.
 * @param {number} y The start y postion.
 * @param {number} width In pixels.
 * @param {number} height In pixels.
 * @param {number} radius In pixels. 
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
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
 * @memberof Geometry
 * @alias fireRaycast
 * @param {type} origin The point of origin from which the ray will be cast. The object must contain the properties _x, _y, and optionally _layer.
 * @param {type} direction The direction the ray will be cast. It must be normalized. The object must contain the properties x and y.
 * @param {type} maxDistance The maximum distance up to which intersections will be found. This is an optional parameter defaulting to Infinity. If it's Infinity find all intersections. If it's negative find only first intersection (if there is one). If it's positive find all intersections up to that distance.
 * @returns {Geometry.RaycastResult} An object containing all of the entities in the path of the raycast. 
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
            layer: origin._layer === undefined || origin._layer === null ? Core.getInstance().craftyCharacter.character.layer : origin._layer
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
 * @memberof Board
 * @alias getBoard
 * @returns {Asset.BoardAsset} Current board object.
 */
RPGcode.prototype.getBoard = function () {
    return Core.getInstance().craftyBoard.board;
};

/**
 * Gets the current board's name and returns it.
 * 
 * @example
 * var boardName = rpgcode.getBoardName();
 * rpgcode.log(boardName);
 * 
 * @memberof Board
 * @alias getBoardName
 * @returns {string} Name of the current board.
 */
RPGcode.prototype.getBoardName = function () {
    return Core.getInstance().craftyBoard.board.name;
};

/**
 * Gets the angle between two points in radians.
 * 
 * @example
 * // Get the angle in radians between two points.
 * var angle = rpgcode.getAngleBetweenPoints(location.x, location.y, this.x, this.y);
 * 
 * @memberof Geometry
 * @alias getAngleBetweenPoints
 * @param {number} x1
 * @param {number} y1
 * @param {number} x2
 * @param {number} y2
 * @returns {number} The angle between the points in radians.
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
 * @memberof Character
 * @alias getCharacter
 * @returns {Asset.CharacterAsset} Active character.
 */
RPGcode.prototype.getCharacter = function () {
    return Core.getInstance().craftyCharacter.character;
};

/**
 * Gets the character's current direction.
 * 
 * @example
 * var direction = rpgcode.getCharacterDirection();
 * rpgcode.log(direction);
 * 
 * @memberof Character
 * @alias getCharacterDirection
 * @returns {("NORTH"|"SOUTH"|"EAST"|"WEST"|"NORTH_EAST"|"SOUTH_EAST"|"NORTH_WEST"|"SOUTH_WEST")}
 */
RPGcode.prototype.getCharacterDirection = function () {
    var direction = Core.getInstance().craftyCharacter.character.direction;

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
 * rpgcode.log(location.x);
 * rpgcode.log(location.y);
 * rpgcode.log(location.layer);
 * 
 * @memberof Character
 * @alias getCharacterLocation
 * @param {boolean} inTiles Should the location be in tiles, otherwise pixels.
 * @param {boolean} includeOffset Should the location include the visual board offset.
 * @returns {Geometry.Location} An object containing the characters location.
 */
RPGcode.prototype.getCharacterLocation = function (inTiles, includeOffset) {
    var instance = Core.getInstance().craftyCharacter;

    if (includeOffset) {
        var x = instance.x + Core.getInstance().craftyBoard.xShift + Crafty.viewport._x;
        var y = instance.y + Core.getInstance().craftyBoard.yShift + Crafty.viewport._y;
    } else {
        var x = instance.x;
        var y = instance.y;
    }

    if (inTiles) {
        return {
            x: x / Core.getInstance().craftyBoard.board.tileWidth,
            y: y / Core.getInstance().craftyBoard.board.tileHeight,
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
 * @memberof Geometry
 * @alias getDistanceBetweenPoints
 * @param {number} x1
 * @param {number} y1
 * @param {number} x2
 * @param {number} y2
 * @returns {number} The distance in pixels.
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
 * @memberof Global
 * @alias getGlobal
 * @param {string} id The ID associated with the global variable.
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
 * @memberof Draw2D
 * @alias getPixel
 * @param {number} x In pixels.
 * @param {number} y In pixels.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
 * @returns {Draw2D.ImageData} An ImageData object.
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
 * @memberof Image
 * @alias getImage
 * @param {string} fileName The relative path to the image.
 * @returns {Image.ImageInfo} An object containing information about the image.
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
 * @memberof Item
 * @alias getItem
 * @param {string} fileName The relative path to the item.
 * @param {Callback} callback Invoked when the item has finished loading.
 * @returns {Asset.Item|undefined} Item object or undefined if none available.
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
 * @memberof Util
 * @alias getRandom
 * @param {number} min Minimum value for the random number.
 * @param {number} max Maximum value for the random number.
 * @returns {number} A random number in the from the requested range. 
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
 * @memberof Program
 * @alias getRunningProgram
 * @returns {Program.RunningProgram} An object with the attributes inProgram (boolean) and the filename of the current running program, if any.
 */
RPGcode.prototype.getRunningProgram = function () {
    return {inProgram: Core.getInstance().inProgram, currentProgram: Core.getInstance().currentProgram};
};

/**
 * Gets the sprites's current location, optionally including the visual offset
 * that happens when boards are smaller than the viewport dimensions.
 * 
 * @example
 * var location = rpgcode.getSpriteLocation("rat");
 * rpgcode.log(location);
 * 
 * @memberof Sprite
 * @alias getSpriteLocation
 * @param {string} spriteId ID associated with the sprite. 
 * @param {boolean} inTiles Should the location be in tiles, otherwise pixels.
 * @param {boolean} includeOffset Should the location include the visual board offset.
 * @returns {Geometry.Location} An object containing the sprite's location.
 */
RPGcode.prototype.getSpriteLocation = function (spriteId, inTiles, includeOffset) {
    var entity = Core.getInstance().craftyBoard.board.sprites[spriteId];

    if (includeOffset) {
        var x = entity.x + Core.getInstance().craftyBoard.xShift + Crafty.viewport._x;
        var y = entity.y + Core.getInstance().craftyBoard.yShift + Crafty.viewport._y;
    } else {
        var x = entity.x;
        var y = entity.y;
    }

    if (inTiles) {
        return {
            x: x / Core.getInstance().craftyBoard.board.tileWidth,
            y: y / Core.getInstance().craftyBoard.board.tileHeight,
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
 * @memberof Util
 * @alias getScale
 * @returns {number}
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
 * @memberof Sprite
 * @alias getSprite
 * @param {string} spriteId
 * @returns {Asset.Sprite}
 */
RPGcode.prototype.getSprite = function (spriteId) {
    return Core.getInstance().craftyBoard.board.sprites[spriteId];
};

/**
 * Gets the sprites's current direction.
 * 
 * @example
 * var direction = rpgcode.getSpriteDirection();
 * rpgcode.log(direction);
 * 
 * @memberof Sprite
 * @alias getSpriteDirection
 * @param {string} spriteId ID associated with the sprite.
 * @returns {("NORTH"|"SOUTH"|"EAST"|"WEST"|"NORTH_EAST"|"SOUTH_EAST"|"NORTH_WEST"|"SOUTH_WEST")}
 */
RPGcode.prototype.getSpriteDirection = function (spriteId) {
    var entity = Core.getInstance().craftyBoard.board.sprites[spriteId];
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
 * @memberof Board
 * @alias getTileData
 * @param {number} tileX
 * @param {number} tileY
 * @param {number} layer
 * @returns {Board.TileData} An object containing the tile's properties.
 * @throws "layer out of range" or "tile out of range"
 */
RPGcode.prototype.getTileData = function(tileX=0, tileY=0, layer=0) {
    const board = Core.getInstance().craftyBoard.board;
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
    
    const tileSet = Core.getInstance().tilesets[board.tileSets[parts[0]]];
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
 * @memberof Util
 * @alias getViewport
 * @returns {Util.Viewport}
 */
RPGcode.prototype.getViewport = function () {
    return {
        x: -Crafty.viewport.x,
        y: -Crafty.viewport.y,
        width: Math.round(Crafty.viewport.width / rpgcode.getScale()),
        height: Math.round(Crafty.viewport.height / rpgcode.getScale()),
        offsetX: Math.floor(Core.getInstance().craftyBoard.xShift + Crafty.viewport._x),
        offsetY: Math.floor(Core.getInstance().craftyBoard.yShift + Crafty.viewport._y)
    };
};

/**
 * Gives an item to a character, placing in their inventory. 
 * 
 * @memberof Item
 * @alias giveItem
 * @param {string} filename
 * @param {string} characterId
 * @param {Callback} callback Invoked when the item has finished loading assets.
 */
RPGcode.prototype.giveItem = async function (filename, characterId, callback) {
    var item = await new Item(PATH_ITEM + filename).load();

    Core.getInstance().loadItem(item);
    Core.getInstance().loadCraftyAssets(function (e) {
        if (!e) {
            if (!Core.getInstance().craftyCharacter.character.inventory[filename]) {
                Core.getInstance().craftyCharacter.character.inventory[filename] = [];
            }
            Core.getInstance().craftyCharacter.character.inventory[filename].push(item);
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
 * @memberof Util
 * @alias hitCharacter
 * @param {string} characterId ID of the character.
 * @param {number} damage Amount of health to take away.
 * @param {string} animationId Animation to play while the character is hit.
 * @param {Callback} callback An optional function to invoke when the animation has ended.
 */
RPGcode.prototype.hitCharacter = function (characterId, damage, animationId, callback) {
    // characterId unused until multi-character parties are supported.
    Core.getInstance().controlEnabled = false;
    var character = Core.getInstance().craftyCharacter.character;
    character.health -= damage;
    character.isHit = true;
    rpgcode.animateCharacter(characterId, animationId, function () {
        character.isHit = false;
        Core.getInstance().controlEnabled = true;
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
 * @memberof Util
 * @alias hitEnemy
 * @param {string} spriteId ID of the BoardSprite that represents the enemy.
 * @param {number} damage Amount of health to take away.
 * @param {string} animationId Animation to play while the enemy is hit.
 * @param {Callback} callback An optional function to invoke when the animation has ended.
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
 * Returns a true or false value indicating whether an asset is currently loaded.
 * 
 * Note: For audio files use the identifier key, not the filename.
 * 
 * @example
 * rpgcode.log(rpgcode.isAssetLoaded("Hero/attack_east.png", "image")); // logs true
 * rpgcode.log(rpgcode.isAssetLoaded("intro", "audio")); // logs true
 * 
 * @memberof Asset
 * @alias isAssetLoaded
 * @param {string} asset Filename of the asset including any subfolders.
 * @param {string} type Either "image" or "audio".
 * @returns {boolean}
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
 * Returns a true or false value indicating whether standard movement controls
 * are currently enabled in the engine.
 * 
 * @example
 * var controlEnabled = rpgcode.isControlEnabled();
 * rpgcode.log(controlEnabled);
 * 
 * @memberof Util
 * @alias isControlEnabled
 * @returns {boolean} true if the player has control, false otherwise
 */
RPGcode.prototype.isControlEnabled = function () {
    return Core.getInstance().controlEnabled;
};

/**
 * Loads the requested assets into the engine, when all of the assets have been loaded
 * the onLoad callback is invoked.
 * 
 * For more information, see [Asset Management]{@tutorial 03-Asset-Management}
 * 
 * @example
 * // Game assets used in this program.
 * var assets = {
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
 * // Load up the assets we need
 * rpgcode.loadAssets(assets, function () {
 *  // Assets we need are ready, continue on...
 * });
 * 
 * @memberof Asset
 * @alias loadAssets
 * @param {Asset.Assets} assets Object of assets to load.
 * @param {Callback} onLoad Callback to invoke after assets are loaded.
 */
RPGcode.prototype.loadAssets = function (assets, onLoad) {
    if (assets.fonts) {
        var fontPromises = [];
        for (const font of assets.fonts) {
            fontPromises.push(new FontFace(font.name, `url(${PATH_FONT + font.file})`).load());
        }
        Promise.all(fontPromises).then(function () {
            if (Core.getInstance().debugEnabled) {
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
 * @memberof File
 * @alias loadJSON
 * @param {string} path File path to read from.
 * @param {Callback} successCallback Invoked if the load succeeded.
 * @param {Callback} failureCallback Invoked if the load failed.
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
 * @memberof Util
 * @alias log
 * @param {string} message Message to log.
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
 * @memberof Sound
 * @alias playSound
 * @param {string} file Relative path to the sound file to play.
 * @param {boolean} loop Should it loop indefinitely?
 * @param {number} [volume=1.0] Value ranging from 1.0 to 0.0, default is 1.0 (i.e. 100%).
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
 * @memberof Sprite
 * @alias moveSprite
 * @param {string} spriteId The ID set for the sprite as it appears in the editor.
 * @param {string} direction The direction to push the sprite in e.g. NORTH, SOUTH, EAST, WEST.
 * @param {number} distance Number of pixels to move.
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
            if (Core.getInstance().craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
                var entity = Core.getInstance().craftyBoard.board.sprites[spriteId];
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
 * @memberof Text
 * @alias measureText
 * @param {string} text
 * @returns {Text.TextDimensions} An object containing the width and height of the text.
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
 * @memberof Character
 * @alias moveCharacter
 * @param {string} characterId The id of the character to move. (unused)
 * @param {string} direction The direction to push the character in.
 * @param {number} distance Number of pixels to move.
 */
RPGcode.prototype.moveCharacter = function (characterId, direction, distance) {
    // TODO: characterId is unused until multiple party members are supported.

    Core.getInstance().craftyCharacter.move(direction, distance);
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
 * rpgcode.moveCharacterTo(characterId, x, y, delay, function() {
 *  rpgcode.log("character has finished moving");
 * });
 * 
 * @memberof Character
 * @alias moveCharacterTo
 * @param {string} characterId The name set for the character as it appears in the editor.
 * @param {number} x A pixel coordinate on the board.
 * @param {number} y A pixel coordinate on the board.
 * @param {number} duration Time taken for the movement to complete (milliseconds).
 * @param {Callback} callback Function to invoke when the sprite has finished moving.
 */
RPGcode.prototype.moveCharacterTo = function (characterId, x, y, duration, callback) {
    Core.getInstance().craftyCharacter.trigger("TweenEnd", {});
    Core.getInstance().craftyCharacter.cancelTween({x: true, y: true});
    Core.getInstance().craftyCharacter.tweenEndCallbacks.push(callback);

    var location = rpgcode.getCharacterLocation();
    if (location.x !== x && location.y !== y) {
        Core.getInstance().craftyCharacter.tween({x: x, y: y}, duration);
    } else if (location.x !== x) {
        Core.getInstance().craftyCharacter.tween({x: x}, duration);
    } else {
        Core.getInstance().craftyCharacter.tween({y: y}, duration);
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
 * @memberof Sprite
 * @alias moveSpriteTo
 * @param {string} spriteId The ID set for the sprite as it appears in the editor.
 * @param {number} x A pixel coordinate.
 * @param {number} y A pixel coordinate.
 * @param {number} duration Time taken for the movement to complete (milliseconds).
 * @param {Callback} callback Function to invoke when the sprite has finished moving.
 */
RPGcode.prototype.moveSpriteTo = function (spriteId, x, y, duration, callback) {
    if (Core.getInstance().craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
        var entity = Core.getInstance().craftyBoard.board.sprites[spriteId];
        entity.trigger("TweenEnd", {});
        entity.cancelTween({x: true, y: true});
        entity.tweenEndCallbacks.push(callback);
        entity.tween({x: x, y: y}, duration);
    }
};

/**
 * Resets activation checks for the requested character, useful for cases where
 * you want to continually check a program activation, e.g. block pushing.
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
 * @memberof Character
 * @alias resetActivationChecks
 * @param {string} characterId The identifier associated with the character.
 */
RPGcode.prototype.resetActivationChecks = function (characterId) {
    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    Core.getInstance().craftyCharacter.activationVector.resetHitChecks();
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
 * @memberof Keyboard
 * @alias registerKeyDown
 * @param {string} key The key to listen to.
 * @param {Callback} callback The callback function to invoke when the keyDown event fires.
 * @param {boolean} [globalScope=false] Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerKeyDown = function (key, callback, globalScope) {
    if (globalScope) {
        Core.getInstance().keyDownHandlers[Crafty.keys[key]] = callback;
    } else {
        Core.getInstance().keyboardHandler.downHandlers[Crafty.keys[key]] = callback;
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
 * @memberof Keyboard
 * @alias registerKeyUp
 * @param {string} key The key to listen to.
 * @param {Callback} callback The callback function to invoke when the keyUp event fires.
 * @param {boolean} [globalScope=false] Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerKeyUp = function (key, callback, globalScope) {
    if (globalScope) {
        Core.getInstance().keyUpHandlers[Crafty.keys[key]] = callback;
    } else {
        Core.getInstance().keyboardHandler.upHandlers[Crafty.keys[key]] = callback;
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
 * @memberof Mouse
 * @alias registerMouseDown
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {boolean} [globalScope=false] Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerMouseDown = function (callback, globalScope) {
    if (globalScope) {
        Core.getInstance().mouseDownHandler = callback;
    } else {
        Core.getInstance().mouseHandler.mouseDownHandler = callback;
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
 * @memberof Mouse
 * @alias registerMouseUp
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {boolean} [globalScope=false] Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerMouseUp = function (callback, globalScope) {
    if (globalScope) {
        Core.getInstance().mouseUpHandler = callback;
    } else {
        Core.getInstance().mouseHandler.mouseUpHandler = callback;
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
 * @memberof Mouse
 * @alias registerMouseClick
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {boolean} [globalScope=false] Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerMouseClick = function (callback, globalScope) {
    if (globalScope) {
        Core.getInstance().mouseClickHandler = callback;
    } else {
        Core.getInstance().mouseHandler.mouseClickHandler = callback;
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
 * @memberof Mouse
 * @alias registerMouseDoubleClick
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {boolean} [globalScope=false] Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerMouseDoubleClick = function (callback, globalScope) {
    if (globalScope) {
        Core.getInstance().mouseDoubleClickHandler = callback;
    } else {
        Core.getInstance().mouseHandler.mouseDoubleClickHandler = callback;
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
 * @memberof Mouse
 * @alias registerMouseMove
 * @param {Callback} callback The callback function to invoke when the event fires.
 * @param {boolean} [globalScope=false] Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerMouseMove = function (callback, globalScope) {
    if (globalScope) {
        Core.getInstance().mouseMoveHandler = callback;
    } else {
        Core.getInstance().mouseHandler.mouseMoveHandler = callback;
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
 * rpgcode.removeAssets(assets); 
 * 
 * @memberof Asset
 * @alias removeAssets
 * @param {Asset.Assets} assets The object containing the assets identifiers.
 */
RPGcode.prototype.removeAssets = function (assets) {
    Crafty.removeAssets(assets);
};

/**
 * Removes a globally scoped variable from the engine.
 * 
 * @memberof Global
 * @alias removeGlobal
 * @param {string} id
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
 * @memberof Canvas
 * @alias renderNow
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to render.
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
 * Replaces a tile at the supplied (x, y, layer) position.
 * 
 * @example
 * // Places the tile at (x: 11, y: 10, layer: 0) with the 81st tile 
 * // from the tileset "tileset1.tileset".
 * rpgcode.replaceTile(11, 10, 0, "tileset1.tileset", 81);
 * 
 * @memberof Board
 * @alias replaceTile
 * @param {number} tileX The x position in tiles.
 * @param {number} tileY The y postion in tiles.
 * @param {number} layer The layer the tile is on.
 * @param {string} tileSet The name of the TileSet of the replacement tile.
 * @param {number} tileIndex The index of the tile in the replacement TileSet.
 */
RPGcode.prototype.replaceTile = function (tileX, tileY, layer, tileSet, tileIndex) {
    var tile = Core.getInstance().tilesets[tileSet].getTile(tileIndex);
    Core.getInstance().craftyBoard.board.replaceTile(tileX, tileY, layer, tile);
};

/**
 * Removes the layer image with the ID on the specified layer. If the image does
 * not exist on the layer then there is no effect.
 * 
 * @example
 * rpgcode.removeLayerImage("battle.background", 1); // Remove the image with ID "battle.background" on layer 1 
 * 
 * @memberof Image
 * @alias removeLayerImage
 * @param {string} id Unique ID of the layer image to remove.
 * @param {number} layer Layer index on the board, first layer starts at 0.
 */
RPGcode.prototype.removeLayerImage = function (id, layer) {
    if (layer < Core.getInstance().craftyBoard.board.layers.length) {
        var boardLayer = Core.getInstance().craftyBoard.board.layers[layer];
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
 * @memberof Program
 * @alias removeRunTimeProgram
 * @param {string} filename
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
 * // Removes the tile at (x: 11, y: 9, layer: 1).
 * rpgcode.removeTile(11, 9, 1); 
 * 
 * @memberof Board
 * @alias removeTile
 * @param {number} tileX The x position in tiles.
 * @param {number} tileY The y postion in tiles.
 * @param {number} layer The layer the tile is on.
 */
RPGcode.prototype.removeTile = function (tileX, tileY, layer) {
    Core.getInstance().craftyBoard.board.removeTile(tileX, tileY, layer);
};

/**
 * Restarts the game by refreshing the browser page.
 * 
 * @example
 * rpgcode.restart(); // Will refresh the browser page.
 * 
 * @memberof Util
 * @alias restart
 */
RPGcode.prototype.restart = function () {
    location.reload(); // Cheap way to implement game restart for the moment.
};

/**
 * Runs the requested program, movement is disabled for the programs duration.
 * 
 * @example
 * // Run a program at the root of the "Programs"
 * rpgcode.runProgram("MyProgram.js");
 * 
 * // Run a program in a subpath of "Programs"
 * rpgcode.runProgram("examples/MyProgram.js");
 * 
 * @memberof Program
 * @alias runProgram
 * @param {string} filename Program to run, including any subpaths.
 */
RPGcode.prototype.runProgram = function (filename) {
    Core.getInstance().runProgram(PATH_PROGRAM + filename, rpgcode, null);
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
 * @memberof Canvas
 * @alias setCanvasPosition
 * @param {number} x In pixels.
 * @param {number} y In pixels.
 * @param {string} canvasId The ID of the canvas to move.
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
 * @memberof File
 * @alias saveJSON
 * @param {Object} data JSON object containing path and data properties.
 * @param {Callback} successCallback Invoked if the file save succeeded.
 * @param {Callback} failureCallback Invoked if the file save failed.
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
 * Sends the character to a board and places them at the given (x, y, [optional] layer) position in tiles.
 * 
 * @example
 * // Use current character layer.
 * rpgcode.sendToBoard("Room1.board", 11.5, 18); 
 * 
 * // Explictly state which layer.
 * rpgcode.sendToBoard("Room1.board", 11.5, 18, 1);
 * 
 * @memberof Board
 * @alias sendToBoard
 * @param {string} boardName The board to send the character to.
 * @param {number} tileX The x position to place the character at, in tiles.
 * @param {number} tileY The y position to place the character at, in tiles.
 * @param {number} layer [Optional] The layer to place the character on, defaults to the current layer otherwise.
 */
RPGcode.prototype.sendToBoard = async function (boardName, tileX, tileY, layer) {
    if (layer === undefined || layer === null || layer < 0) {
        // Backwards compatability check.
        layer = Core.getInstance().craftyCharacter.character.layer;
    }
    await Core.getInstance().switchBoard(boardName, tileX, tileY, layer);
};

/**
 * Sets the RGBA color for all drawing operations to use. See [RGBA Colors]{@link https://www.w3schools.com/css/css_colors_rgb.asp}
 * 
 * @example
 * // Set the color to red.
 * rpgcode.setColor(255, 0, 0, 1.0);
 * 
 * @memberof Draw2D
 * @alias setColor
 * @param {number} r 0 to 255
 * @param {number} g 0 to 255
 * @param {number} b 0 to 255
 * @param {number} a 0 to 1.0
 */
RPGcode.prototype.setColor = function (r, g, b, a) {
    rpgcode.rgba = {r: r, g: g, b: b, a: a};
};

/**
 * Sets the engine's font to the specified size, and font family. Custom fonts
 * need to be installed on the user's system to work. For a list of builtin fonts
 * see: [CSS Websafe Fonts]{@link https://www.w3schools.com/cssref/css_websafe_fonts.asp}
 *
 * @example
 * // Set the global font to 8px Lucida Console
 * rpgcode.setFont(8, "Lucida Console");
 *   
 * @memberof Text
 * @alias setFont
 * @param {number} size in pixels
 * @param {string} family E.g. Arial, Comic Sans, etc.
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
 * @memberof Global
 * @alias setGlobal
 * @param {string} id The ID to use for this global.
 * @param {Object} value The value this global holds.
 */
RPGcode.prototype.setGlobal = function (id, value) {
    rpgcode.globals[id] = value;
};

/**
 * Sets the global drawing alpha for all subsequent canvas drawing operation. 
 * Useful for drawing transparent elements to a canvas.
 * 
 * For more details see: [Canvas Global Alpha]{@link https://www.w3schools.com/Tags/canvas_globalalpha.asp}
 *  
 * @example
 * // All drawing operations will now be semi-transparent
 * rpgcode.setGlobalAlpha(0.5);
 * 
 * @memberof Draw2D
 * @alias setGlobalAlpha
 * @param {number} alpha
 */
RPGcode.prototype.setGlobalAlpha = function (alpha) {
    rpgcode.globalAlpha = alpha;
};

/**
 * @memberof Image
 * @alias setImage
 * @deprecated since 1.7.0, use "drawImage" instead.
 */
RPGcode.prototype.setImage = function (fileName, x, y, width, height, canvasId) {
    rpgcode.drawImage(fileName, x, y, width, height, 0, canvasId);
};

/**
 * @memberof Image
 * @alias setImagePart
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
 * @memberof Draw2D
 * @alias setPixel
 * @param {number} x In pixels.
 * @param {number} y In pixels.
 * @param {string} [canvasId=renderNowCanvas] The ID of the canvas to draw on.
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
 * @memberof Util
 * @alias setDialogDimensions
 * @deprecated
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
 * @memberof Util
 * @alias setDialogGraphics
 * @deprecated
 * @param {string} profileImage The relative path to the profile image.
 * @param {string} backgroundImage The relative path to the background image.
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
 * @memberof Util
 * @alias setDialogPadding
 * @deprecated
 * @param {Object} padding An object containing x and/or y padding values in pixels.
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
 * @memberof Util
 * @alias setDialogPosition
 * @deprecated
 * @param {string} position Either NORTH (top of screen) or SOUTH (bottom of screen).
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
 * @memberof Sprite
 * @alias setSpriteLocation
 * @param {string} spriteId The ID set for the sprite as it appears in the editor.
 * @param {number} x In pixels by default.
 * @param {number} y In pixels by default.
 * @param {number} layer Target layer to put the sprite on.
 * @param {boolean} inTiles Is (x, y) in tile coordinates, defaults to pixels.
 */
RPGcode.prototype.setSpriteLocation = function (spriteId, x, y, layer, inTiles) {
    if (inTiles) {
        x *= Core.getInstance().craftyBoard.board.tileWidth;
        y *= Core.getInstance().craftyBoard.board.tileHeight;
    }

    var entity = Core.getInstance().craftyBoard.board.sprites[spriteId];
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
 * @memberof Sprite
 * @alias setSpriteStance
 * @param {string} spriteId The ID set for the sprite as it appears in the editor.
 * @param {string} stanceId The stanceId (animationId) to use.
 */
RPGcode.prototype.setSpriteStance = function (spriteId, stanceId) {
    if (Core.getInstance().craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
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
 * @memberof Character
 * @alias setCharacterLocation
 * @param {string} characterId The identifier associated with character to move.
 * @param {number} x In pixels by default.
 * @param {number} y In pixels by default.
 * @param {number} layer Target layer to put the character on.
 * @param {boolean} isTiles Is (x, y) in tile coordinates, defaults to pixels.
 */
RPGcode.prototype.setCharacterLocation = function (characterId, x, y, layer, isTiles) {
    if (isTiles) {
        x *= Core.getInstance().craftyBoard.board.tileWidth;
        y *= Core.getInstance().craftyBoard.board.tileHeight;
    }

    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    Core.getInstance().craftyCharacter.x = x;
    Core.getInstance().craftyCharacter.y = y;
    Core.getInstance().craftyCharacter.character.layer = layer;
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
 * @memberof Character
 * @alias setCharacterSpeed
 * @param {string} characterId The index of the character on the board.
 * @param {string} change Factor to change the character speed by.
 */
RPGcode.prototype.setCharacterSpeed = function (characterId, change) {
    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    if (change === 0) {
        return;
    } else if (change > 0) {
        Core.getInstance().craftyCharacter._speed *= change;
        Core.getInstance().craftyCharacter._diagonalSpeed *= change;
    } else if (change < 0) {
        Core.getInstance().craftyCharacter._speed /= -change;
        Core.getInstance().craftyCharacter._diagonalSpeed /= -change;
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
 * @memberof Character
 * @alias setCharacterStance
 * @param {string} characterId The index of the character on the board.
 * @param {string} stanceId The stanceId (animationId) to use.
 */
RPGcode.prototype.setCharacterStance = function (characterId, stanceId) {
    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    Core.getInstance().craftyCharacter.character.changeGraphics(stanceId);
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
 * @memberof Util
 * @alias showDialog
 * @deprecated
 * @param {string} dialog The dialog to output.
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
 * @memberof Sound
 * @alias stopSound
 * @param {string} file The relative path of the sound file to stop.
 */
RPGcode.prototype.stopSound = function (file) {
    if (file) {
        Crafty.audio.stop(file);
    } else {
        Crafty.audio.stop();
    }
};

/**
 * Takes the item from the character's inventory.
 * 
 * @memberof Item
 * @alias takeItem
 * @param {string} filename The filename of the item.
 * @param {string} characterId The character to remove it from.
 */
RPGcode.prototype.takeItem = function (filename, characterId) {
    var inventory = Core.getInstance().craftyCharacter.character.inventory;
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
 * @memberof Keyboard
 * @alias unregisterKeyDown
 * @param {string} key The key associated with the listener.
 * @param {boolean} [globalScope=false] Is this a global scope key down handler.
 */
RPGcode.prototype.unregisterKeyDown = function (key, globalScope) {
    if (globalScope) {
        delete Core.getInstance().keyDownHandlers[Crafty.keys[key]];
    } else {
        delete Core.getInstance().keyboardHandler.downHandlers[Crafty.keys[key]];
    }
};

/**
 * Removes a previously registered KeyUp listener.
 * 
 * @example
 * rpgcode.unregisterKeyUp("ENTER");
 * 
 * @memberof Keyboard
 * @alias unregisterKeyUp
 * @param {string} key The key associated with the listener.
 * @param {boolean} [globalScope=false] Is this a global scope key up handler.
 */
RPGcode.prototype.unregisterKeyUp = function (key, globalScope) {
    if (globalScope) {
        delete Core.getInstance().keyUpHandlers[Crafty.keys[key]];
    } else {
        delete Core.getInstance().keyboardHandler.upHandlers[Crafty.keys[key]];
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
 * @memberof Mouse
 * @alias unregisterMouseDown
 * @param {boolean} [globalScope=false] Is this a global scope mouse handler.
 */
RPGcode.prototype.unregisterMouseDown = function (globalScope) {
    if (globalScope) {
        Core.getInstance().mouseDownHandler = null;
    } else {
        Core.getInstance().mouseHandler.mouseDownHandler = null;
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
 * @memberof Mouse
 * @alias unregisterMouseUp
 * @param {boolean} [globalScope=false] Is this a global scope mouse handler.
 */
RPGcode.prototype.unregisterMouseUp = function (globalScope) {
    if (globalScope) {
        Core.getInstance().mouseUpHandler = null;
    } else {
        Core.getInstance().mouseHandler.mouseUpHandler = null;
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
 * @memberof Mouse
 * @alias unregisterMouseClick
 * @param {boolean} [globalScope=false] Is this a global scope mouse handler.
 */
RPGcode.prototype.unregisterMouseClick = function (globalScope) {
    if (globalScope) {
        Core.getInstance().mouseClickHandler = null;
    } else {
        Core.getInstance().mouseHandler.mouseClickHandler = null;
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
 * @memberof Mouse
 * @alias unregisterMouseDoubleClick
 * @param {boolean} [globalScope=false] Is this a global scope mouse handler.
 */
RPGcode.prototype.unregisterMouseDoubleClick = function (globalScope) {
    if (globalScope) {
        Core.getInstance().mouseDoubleClickHandler = null;
    } else {
        Core.getInstance().mouseHandler.mouseDoubleClickHandler = null;
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
 * @memberof Mouse
 * @alias unregisterMouseMove
 * @param {boolean} [globalScope=false] Is this a global scope mouse handler.
 */
RPGcode.prototype.unregisterMouseMove = function (globalScope) {
    if (globalScope) {
        Core.getInstance().mouseMoveHandler = null;
    } else {
        Core.getInstance().mouseHandler.mouseMoveHandler = null;
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
 * @memberof Image
 * @alias updateLayerImage
 * @param {Object} image The layer image to update on the board.
 * @param {number} layer Layer index on the board, first layer starts at 0.
 */
RPGcode.prototype.updateLayerImage = function (image, layer) {
    if (layer < Core.getInstance().craftyBoard.board.layers.length) {
        var boardLayer = Core.getInstance().craftyBoard.board.layers[layer];
        var length = boardLayer.images.length;
        for (var i = 0; i < length; i++) {
            if (boardLayer.images[i].id === image.id) {
                boardLayer.images[i] = image;
                break;
            }
        }
    }
};

//
// Typedefs for complex API objects
//
/**
 * @memberOf Geometry
 * @typedef {Object} Location
 * @property {number} x
 * @property {number} y
 * @property {number} layer
 */

/**
 * @memberof Geometry
 * @typedef {Object} RaycastResult
 * @property {Asset.CharacterAsset[]} characters
 * @property {Asset.EnemyAsset[]} enemies
 * @property {Asset.NpcAsset[]} npcs
 * @property {Geometry.Solid[]} solids
 */

/**
 * @memberof Geometry
 * @typedef {Object} Solid
 * @property {number} distance
 * @property {number} x
 * @property {number} y
 */

/**
 * @memberof Geometry
 * @typedef {Object} Point
 * @property {number} x
 * @property {number} y
 */

/**
 * @memberOf Text
 * @typedef {Object} TextDimensions
 * @property {number} width
 * @property {number} height
 */

/**
 * @memberOf Util
 * @typedef {Object} Viewport
 * @property {number} x
 * @property {number} y
 * @property {number} width
 * @property {number} height
 * @property {number} offsetX
 * @property {number} offsetY
 */

/**
 * @memberof Util
 * @typedef {Object} Delay
 * @property {string} key
 * @property {string} filename
 */

/**
 * @memberof Board
 * @typedef {Object} TileData
 * @property {string} type
 * @property {number} defence
 * @property {string} custom
 */

/**
 * @memberof Program
 * @typedef {Object} RunningProgram
 * @property {boolean} inProgram
 * @property {string} currentProgram
 */

/**
 * @memberof Image
 * @typedef {Object} ImageInfo
 * @property {number} width
 * @property {number} height
 */

/**
 * @memberof Draw2D
 * @typedef {Object} ImageData
 * @property {number[]} data One-dimensional array containing the data in the RGBA order, with integer values between 0 and 255 (inclusive).
 * @property {number} width In pixels.
 * @property {number} height In pixels.
 */

/**
 * @memberof Asset
 * @typedef {Object} Assets
 * @property {string[]} images
 * @property {Asset.KeyValue[]} audio
 * @property {string[]} programs
 */

/**
 * @memberof Asset
 * @typedef {Object} KeyValue
 * @property {string} key
 * @property {string} value
 */

/**
 * @memberof Asset
 * @typedef {Object} Item
 * @property {string} name
 * @property {string} description
 * @property {string} type
 * @property {string} icon
 * @property {number} price
 * @property {Asset.ItemEffects} effects
 * @property {numbers} version
 * 
 * @example
 {
    "description": "",
    "effects": {
        "attack": 0,
        "defence": 0,
        "health": 1,
        "magic": 0
    },
    "icon": "Icons/Item__64.png",
    "name": "Apple",
    "price": 0,
    "type": "battle",
    "version": 1.7
}
 */

/**
 * @memberof Asset
 * @typedef {Object} ItemEffects
 * @property {number} attack
 * @property {number} defence
 * @property {number} health
 * @property {number} magic
 */

/**
 * @memberof Asset
 * @typedef {Object} Vector
 * @property {Asset.Event[]} events
 * @property {Geometry.Point[]} points
 */

/**
 * @memberof Asset
 * @typedef {Object} Event
 * @property {string} program
 * @property {string} type
 * @property {string} key
 */

/**
 * @memberof Asset
 * @typedef {Object} Sprite
 * @property {string} name
 * @property {string} description
 * @property {Asset.Vector} activationVector
 * @property {Geometry.Point} activationOffset
 * @property {boolean} activationVectorDisabled
 * @property {Asset.KeyValue} animations
 * @property {Asset.Vector} baseVector
 * @property {Geometry.Point} baseVectorOffset
 * @property {boolean} baseVectorDisabled
 * @property {number} frameRate
 * @property {Asset.KeyValue} graphics
 * @property {string} version
 */

/**
 * @memberof Asset
 * @typedef {Asset.Sprite} NpcAsset
 * 
 * @example
{
    "activationOffset": {
        "x": -5,
        "y": -5
    },
    "activationVector": {
        "events": [{
                "program": "",
                "type": "overlap"
            }
        ],
        "points": [{
                "x": 0,
                "y": 0
            }
        ]
    },
    "activationVectorDisabled": false,
    "animations": {
        "ATTACK": ""
    },
    "baseVector": {
        "events": [{
                "program": "",
                "type": "overlap"
            }
        ],
        "points": [{
                "x": 0,
                "y": 0
            }
        ]
    },
    "baseVectorDisabled": false,
    "baseVectorOffset": {
        "x": -5,
        "y": -5
    },
    "description": "Undefined",
    "frameRate": 0,
    "graphics": {
        "PROFILE": ""
    },
    "name": "Untitled",
    "version": 1.7
}
 */

/**
 * @memberof Asset
 * @typedef {Asset.Sprite} EnemyAsset
 * @property {number} level
 * @property {number} health
 * @property {number} attack
 * @property {number} defence
 * @property {number} magic
 * @property {number} experienceReward
 * @property {number} goldReward
 * 
 * @example
{
    "activationOffset": {
        "x": -20,
        "y": -5
    },
    "activationVector": {
        "events": [{
                "program": "",
                "type": "overlap"
            }
        ],
        "points": [{
                "x": 0,
                "y": 0
            }
        ]
    },
    "activationVectorDisabled": false,
    "animations": {
        "ATTACK": "Evil Eye/attack.animation"
    },
    "attack": 1,
    "baseVector": {
        "events": [{
                "program": "",
                "type": "overlap"
            }
        ],
        "points": [{
                "x": 0,
                "y": 0
            }
        ]
    },
    "baseVectorDisabled": false,
    "baseVectorOffset": {
        "x": -15,
        "y": 0
    },
    "defence": 0,
    "experienceReward": 0,
    "frameRate": 0,
    "goldReward": 0,
    "graphics": {
        "PROFILE": ""
    },
    "health": 5,
    "level": 0,
    "magic": 0,
    "name": "Evil Eye",
    "version": 1.7
}
 */

/**
 * @memberof Asset
 * @typedef {Asset.Sprite} CharacterAsset
 * @property {number} level
 * @property {number} maxLevel
 * @property {number} health
 * @property {number} maxHealth
 * @property {number} attack
 * @property {number} maxAttack
 * @property {number} defence
 * @property {number} maxDefence
 * @property {number} magic
 * @property {number} maxMagic
 * @property {number} experience
 * @property {Asset.KeyValue} inventory
 * @property {Asset.KeyValue} equipment
 * 
 * @example
{
    "maxLevel": 10,
    "magic": 0,
    "maxDefence": 0,
    "baseVectorOffset": {
        "x": -15,
        "y": 8
    },
    "defence": 0,
    "graphics": {
        "PROFILE": "sword_profile_1_small.png"
    },
    "experience": 0,
    "inventory": {
        "TODO": "TODO"
    },
    "gold": 0,
    "frameRate": 0,
    "attack": 1,
    "animations": {
        "DEFEND": "Hero/south_hurt.animation"
    },
    "maxHealth": 5,
    "activationOffset": {
        "x": -20,
        "y": 1
    },
    "level": 0,
    "maxMagic": 0,
    "maxAttack": 1,
    "health": 5,
    "equipment": {
        "head": "",
        "accessory-2": "",
        "accessory-1": "",
        "chest": "",
        "gloves": "",
        "right-hand": "",
        "boots": "",
        "left-hand": ""
    },
    "maxExperience": 0,
    "version": 1.7,
    "baseVector": {
        "events": [{
                "program": "",
                "type": "overlap"
            }
        ],
        "points": [{
                "x": 0,
                "y": 0
            }
        ]
    },
    "activationVector": {
        "events": [{
                "program": "",
                "type": "overlap"
            }
        ],
        "points": [{
                "x": 0,
                "y": 0
            }
        ]
    },
    "baseVectorDisabled": false,
    "name": "Hero",
    "activationVectorDisabled": false
}
 */

/**
 * @memberof Asset
 * @typedef {Object} BoardAsset
 * @property {string} name
 * @property {string} backgroundMusic
 * @property {string} firstRunProgram
 * @property {number} width 
 * @property {number} height 
 * @property {number} tileWidth 
 * @property {number} tileHeight
 * @property {string[]} tileSets 
 * @property {Asset.BoardLayer[]} layers
 * @property {Asset.BoardSprite[]} sprites
 * @property {Geometry.Location} startingPosition
 * 
 * @example 
 {
    "backgroundMusic": "Tower.ogg",
    "firstRunProgram": "entry.js",
    "height": 10,
    "layers": [{
            "images": [{
                    "id": "image-1",
                    "src": "background.png",
                    "x": 0,
                    "y": 0
                }
            ],
            "name": "Base",
            "tiles": ["0:11"],
            "vectors": [{
                    "events": [{
                            "key": "E",
                            "program": "auto_generated/1586278226170_d49fe8d2-171c-4b7c-b625-bda20c6eacbb.js",
                            "type": "keypress"
                        }
                    ],
                    "id": "",
                    "isClosed": true,
                    "points": [{
                            "x": 361,
                            "y": 64
                        }
                    ],
                    "type": "PASSABLE"
                }
            ]
        }
    ],
    "name": "demo.board",
    "sprites": [{
            "name": "Torch.npc",
            "id": "b596ab34-903c-4856-9d1d-90f6465c075a",
            "thread": "Idle.js",
            "startingPosition": {
                "x": 112,
                "y": 80,
                "layer": 0
            },
            "events": []
        }
    ],
    "startingPosition": {
        "layer": 0,
        "x": 242,
        "y": 178
    },
    "tileHeight": 32,
    "tileSets": ["tower.tileset"],
    "tileWidth": 32,
    "version": 1.7,
    "width": 15
} 
 */

/**
 * @memberof Asset
 * @typedef {Object} BoardLayer
 * @property {string} name
 * @property {string[]} tiles 
 * @property {Asset.LayerImage[]} images
 * @property {Asset.LayerVector[]} vectors
 */

/**
 * @memberof Asset
 * @typedef {Object} BoardSprite
 * @property {string} name
 * @property {string} id
 * @property {string} thread
 * @property {Asset.Event[]} events
 * @property {Geometry.Location} startingPosition
 */

/**
 * @memberof Asset
 * @typedef {Object} LayerImage
 * @property {string} id
 * @property {string} src
 * @property {number} x
 * @property {number} y
 */

/**
 * @memberof Asset
 * @typedef {Object} LayerVector
 * @property {string} id
 * @property {string} type
 * @property {string} isClosed
 * @property {string} points
 * @property {Asset.Event[]} events
 */

