/*
 * Copyright (c) 2017, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard, rpgcode, PATH_PROGRAM */

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

    this.canvases = {"renderNowCanvas": {
            canvas: rpgwizard.screen.renderNowCanvas,
            render: false
        }
    };

    // Global variable storage for user programs.
    this.globals = {};

    this.rgba = {r: 255, g: 255, b: 255, a: 1.0};
    this.font = "14px Arial";

    this.dialogWindow = {
        visible: false,
        profile: null,
        background: null,
        lineY: 5
    };
}

RPGcode.prototype.addRunTimeProgram = function (filename) {
    this.runTimePrograms.push(filename);
    this.runTimePrograms = Array.from(new Set(this.runTimePrograms));
};

RPGcode.prototype.removeRunTimeProgram = function (filename) {
    var index = this.runTimePrograms.indexOf(filename);

    if (index > -1) {
        this.runTimePrograms.splice(index, 1);
    }
};

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
    var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
    if (entity) {
        var npc = entity.sprite.npc;
        var resetGraphics = npc.spriteGraphics.active;
        rpgcode.setSpriteStance(spriteId, animationId);
        rpgcode._animateGeneric(npc, resetGraphics, callback);
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
    rpgcode.dialogWindow.lineY = 5;
    rpgcode.clearCanvas("renderNowCanvas");
};

RPGcode.prototype._convertCraftyId = function (craftyId) {
    return rpgwizard.craftyBoard.board.sprites.findIndex(function (entity) {
        return entity.getId() === craftyId;
    });
};

/**
 * Creates a canvas with the specified width, height, and ID. This canvas will not
 * be drawn until renderNow is called with its ID.
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
    rpgcode.canvases[canvasId] = {canvas: canvas, render: false};
};

/**
 * Delays a programs execution for a specified number of milliseconds, after which the 
 * callback function is invoked.
 * 
 * @example 
 * // Shows a dialog window after 5 seconds.
 * rpgcode.delay(5000, function(){ 
 *  rpgcode.showDialog("Hello world!");
 * });
 * 
 * @param {Number} ms Time to wait in milliseconds.
 * @param {Boolean} loop Should the call be indefinite?
 * @param {Callback} callback Function to execute after the delay.
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
        rpgwizard.craftyBoard.board.sprites[spriteId].destroy();
        delete rpgwizard.craftyBoard.board.sprites[spriteId];
        Crafty.trigger("Invalidate");
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
        var sourceCanvas = source.canvas;
        var targetContext = target.canvas.getContext("2d");
        targetContext.drawImage(sourceCanvas, x, y, width, height);
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
        var context = instance.canvas.getContext("2d");
        var rgba = rpgcode.rgba;
        context.fillStyle = "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
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
 * Fills a solid rectangle on the canvas.
 * 
 * @example
 * // Create a canvas and draw a red rectangle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 0);
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
        var context = instance.canvas.getContext("2d");
        var rgba = rpgcode.rgba;
        context.fillStyle = "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
        context.fillRect(x, y, width, height);
    }
};

/**
 * Fires a ray from its origin in the direction and report entities that intersect 
 * with it, given the parameter constraints.
 *
 * Raycasting only reports entities, that have the Collision component applied to them.
 * 
 * See: 
 *  http://craftyjs.com/api/Crafty-raycast.html
 * 
 * @param {type} origin The point of origin from which the ray will be cast. The object must contain the properties _x and _y
 * @param {type} direction The direction the ray will be cast. It must be normalized. The object must contain the properties x and y.
 * @param {type} maxDistance The maximum distance up to which intersections will be found. This is an optional parameter defaulting to Infinity. If it's Infinity find all intersections. If it's negative find only first intersection (if there is one). If it's positive find all intersections up to that distance.
 * @param {type} comp Check for intersection with entities that have this component applied to them. This is an optional parameter that is disabled by default.
 * @param {type} sort Whether to sort the returned array by increasing distance. May be disabled to slightly improve performance if sorted results are not needed. Defaults to true.
 * @returns {unresolved} an array of raycast-results that may be empty, if no intersection has been found. Otherwise, each raycast-result looks like {obj: Entity, distance: Number, x: Number, y: Number}, describing which obj entity has intersected the ray at intersection point x,y, distance px away from origin.
 */
RPGcode.prototype.fireRaycast = function (origin, direction, maxDistance, comp, sort) {
    var results;

    direction = new Crafty.math.Vector2D(direction.x, direction.y).normalize();
    if (maxDistance && comp && sort) {
        results = Crafty.raycast(origin, direction, maxDistance, comp, sort);
    } else if (maxDistance && comp) {
        results = Crafty.raycast(origin, direction, maxDistance, comp);
    } else if (maxDistance) {
        results = Crafty.raycast(origin, direction, maxDistance, "Raycastable");
    } else {
        results = Crafty.raycast(origin, direction, -1, "Raycastable");
    }

    return results;
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
            direction = "NORTH";
            break;
        case "s":
            direction = "SOUTH";
            break;
        case "e":
            direction = "EAST";
            break;
        case "w":
            direction = "WEST";
            break;
    }

    return direction;
};

/**
 * Gets the character's current location.
 * 
 * @example
 * var location = rpgcode.getCharacterLocation();
 * rpgcode.log(location);
 * 
 * @param {Boolean} inTiles Should the location be in tiles, otherwise pixels.
 * @returns {Object} An object containing the characters location in the form {x, y, z}.
 */
RPGcode.prototype.getCharacterLocation = function (inTiles) {
    var instance = rpgwizard.craftyCharacter;

    if (inTiles) {
        return {
            x: instance.x / rpgwizard.craftyBoard.board.tileWidth,
            y: instance.y / rpgwizard.craftyBoard.board.tileHeight,
            layer: instance.character.layer
        };
    } else {
        return {
            x: instance.x,
            y: instance.y,
            layer: instance.character.layer
        };
    }
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
    // If the assets already exist Crafty just ignores 
    // them but still invokes the callback.
    Crafty.load(assets, onLoad);
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
 */
RPGcode.prototype.playSound = function (file, loop) {
    var count = loop ? -1 : 1;
    Crafty.audio.play(file, count);
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
 */
RPGcode.prototype.moveSpriteTo = function (spriteId, x, y, duration) {
    switch (spriteId) {
        case "source":
            rpgcode.source.tween({x: x, y: y}, duration);
            break;
        default:
            if (rpgwizard.craftyBoard.board.sprites.hasOwnProperty(spriteId)) {
                var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
                entity.tween({x: x, y: y}, duration);
            }
    }
};

/**
 * Registers a keyDown listener for a specific key, for a list of valid key values see:
 *    http://craftyjs.com/api/Crafty-keys.html
 *    
 * The callback function will continue to be invoked for every keyDown event until it
 * is unregistered.
 * 
 * @example 
 * rpgcode.registerKeyDown("ENTER", function() {
 *  rpgcode.log("Enter key is down!");
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
 * Renders the specified canvas, if none then the "renderNowCanvas" is shown.
 * 
 * @example
 * // Draw a rectangle on the default canvas and render it.
 * rpgcode.setColor(255, 0, 0, 0);
 * rpgcode.fillRect(0, 0, 100, 100);
 * rpgcode.renderNow();
 * 
 * // Create a canvas and draw a red rectangle on it.
 * var canvas = "myCanvas";
 * rpgcode.createCanvas(640, 480, canvas);
 * rpgcode.setColor(255, 0, 0, 0);
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
 * Sends the character to a board and places them at the given (x, y) position in tiles.
 * 
 * @example
 * rpgcode.sendToBoard("Room1.board", 11.5, 18);
 * 
 * @param {String} boardName The board to send the character to.
 * @param {Number} tileX The x position to place the character at, in tiles.
 * @param {Number} tileY The y position to place the character at, in tiles.
 */
RPGcode.prototype.sendToBoard = function (boardName, tileX, tileY) {
    rpgwizard.switchBoard(boardName, tileX, tileY);
};

/**
 * Sets the RGBA color for all drawing operations to use.
 * 
 * @example
 * // Set the color to red.
 * rpgcode.setColor(255, 0, 0, 0);
 * 
 * @param {number} r
 * @param {number} g
 * @param {number} b
 * @param {number} a
 */
RPGcode.prototype.setColor = function (r, g, b, a) {
    rpgcode.rgba = {r: r, g: g, b: b, a: a};
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
 * Sets an image on the canvas specified or the default if none.
 * 
 * @example
 * // Set the image on the smaller canvas.
 * rpgcode.setImage("life.png", 0, 0, 32, 32, lifeIcon);
 * 
 * @param {String} fileName The relative path to the image.
 * @param {Number} x The start position x in pixels.
 * @param {Number} y The start position y in pixels.
 * @param {Number} width In pixels.
 * @param {Number} height In pixels.
 * @param {String} canvasId The ID of the canvas to put the image on.
 */
RPGcode.prototype.setImage = function (fileName, x, y, width, height, canvasId) {
    if (!canvasId) {
        canvasId = "renderNowCanvas";
    }

    var instance = rpgcode.canvases[canvasId];
    if (instance) {
        var image = Crafty.asset(Crafty.__paths.images + fileName);
        if (image) {
            var context = instance.canvas.getContext("2d");
            context.drawImage(image, x, y, width, height);
        }
    }
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
        x *= rpgwizard.tileSize;
        y *= rpgwizard.tileSize;
    }

    var npc = rpgwizard.craftyBoard.board.sprites[spriteId];
    if (npc) {
        npc.x = x;
        npc.y = y;
        npc.layer = layer;
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
        var entity = rpgwizard.craftyBoard.board.sprites[spriteId];
        entity.sprite.npc.changeGraphics(stanceId);
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
        x *= rpgwizard.tileSize;
        y *= rpgwizard.tileSize;
    }

    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    rpgwizard.craftyCharacter.x = x;
    rpgwizard.craftyCharacter.y = y;
    rpgwizard.craftyCharacter.character.layer = layer;
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

    if (!dialogWindow.visible) {
        rpgcode.setImage(dialogWindow.profile, 0, 0, 100, 100);
        rpgcode.setImage(dialogWindow.background, 100, 0, 540, 100);
        dialogWindow.visible = true;

    }

    dialogWindow.lineY += parseInt(rpgcode.font);
    rpgcode.drawText(105, dialogWindow.lineY, dialog);
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
 * @param {Boolean} globalScope Is this a global scope key up handler;
 */
RPGcode.prototype.unregisterKeyUp = function (key, globalScope) {
    if (globalScope) {
        delete rpgwizard.keyUpHandlers[Crafty.keys[key]];
    } else {
        delete rpgwizard.keyboardHandler.upHandlers[Crafty.keys[key]];
    }
};