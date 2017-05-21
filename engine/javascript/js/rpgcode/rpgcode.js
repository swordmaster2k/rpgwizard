/* global rpgtoolkit, rpgcode, PATH_PROGRAM */

var rpgcode = null; // Setup inside of the engine.

function RPGcode() {
    // The last entity to trigger a program.
    this.source = {};

    // An array of programs that will be run each frame.
    this.runTimePrograms = [];

    this.canvases = {"renderNowCanvas": {
            canvas: rpgtoolkit.screen.renderNowCanvas,
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

RPGcode.prototype.removeRunTimeProgram = function(filename) {
    var index = this.runTimePrograms.indexOf(filename);
    
    if (index > -1) {
        this.runTimePrograms.splice(index, 1);
    }
};

/**
 * Should not be used directly, instead see animateItem and animateCharacter.
 * 
 * @param {Object} generic - The object that supports animation.
 * @param {Object} resetGraphics - The graphics set to return to after the animation has finished.
 * @param {Function} callback - Invoked on animation end if defined.
 * @returns {undefined}
 */
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
 * Animates the item using the requested animation. The animationId must be
 * available for the item.
 * 
 * @param {number} itemId - The index of the item on the board to animate.
 * @param {string} animationId - The requested animation to play for the item.
 * @param {Function} callback - If defined, the function to invoke at the end of the animation.
 */
RPGcode.prototype.animateItem = function (itemId, animationId, callback) {
    var entity = rpgtoolkit.craftyBoard.board.sprites[itemId];
    if (entity) {
        var item = entity.sprite.item;
        var resetGraphics = item.spriteGraphics.active;
        rpgcode.setItemStance(itemId, animationId);
        rpgcode._animateGeneric(item, resetGraphics, callback);
    }
};

/**
 * Animates the character using the requested animation. The animationId must be
 * available for the character.
 * 
 * @param {string} characterId - The label associated with the character. 
 * @param {string} animationId - The requested animation to character for the character.
 * @param {Function} callback - If defined, the function to invoke at the end of the animation.
 */
RPGcode.prototype.animateCharacter = function (characterId, animationId, callback) {
    // TODO: characterId will be unused until parties with multiple characters are supported.
    var character = rpgtoolkit.craftyCharacter.character;
    var resetGraphics = character.spriteGraphics.active;
    rpgcode.setCharacterStance(characterId, animationId);
    rpgcode._animateGeneric(character, resetGraphics, callback);
};

/**
 * Clears an entire canvas and triggers a redraw.
 * 
 * @param {string} canvasId - The canvas to clear, if undefined defaults to "renderNowCanas".
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
 */
RPGcode.prototype.clearDialog = function () {
    rpgcode.dialogWindow.visible = false;
    rpgcode.dialogWindow.lineY = 5;
    rpgcode.clearCanvas("renderNowCanvas");
};

/**
 * Converts an interal Crafty entity ID to the matching board sprite ID.
 * 
 * @param {number} craftyId
 * @returns {undefined}
 */
RPGcode.prototype._convertCraftyId = function (craftyId) {
    return rpgtoolkit.craftyBoard.board.sprites.findIndex(function (entity) {
        return entity.getId() === craftyId;
    });
};

/**
 * Creates a canvas with the specified width, height, and ID. This canvas will not
 * be drawn until renderNow is called with its ID.
 * 
 * @param {number} width - In pixels.
 * @param {number} height - In pixels.
 * @param {string} canvasId - The unique identifier for this canvas.
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
 * @param {number} ms - Time to wait in milliseconds.
 * @param {boolen} loop should the call be indefinite
 * @param {Function} callback - Function to execute after the delay.
 */
RPGcode.prototype.delay = function (ms, callback, loop) {
    if (loop) {
        return Crafty.e("Delay").delay(callback, ms, -1);
    } else {
        return Crafty.e("Delay").delay(callback, ms);
    }
};

/**
 * Destroys the canvas with the specified ID.
 * 
 * @param {string} canvasId - The ID for the canvas to destroy.
 */
RPGcode.prototype.destroyCanvas = function (canvasId) {
    delete rpgcode.canvases[canvasId];
};

/**
 * Destroys a particular item instance and removes it from play.
 * 
 * @param {number} itemId - The index of the item on the board to animate.
 */
RPGcode.prototype.destroyItem = function (itemId) {
    var index = rpgcode._convertCraftyId(itemId);

    if (index > -1) {
        rpgtoolkit.craftyBoard.board.sprites[index].destroy();
        rpgtoolkit.craftyBoard.board.sprites.splice(index, 1);
        Crafty.trigger("Invalidate");
    }
};

/**
 * Draws the source canvas onto the target canvas.
 * 
 * @param {string} sourceId - The ID of the source canvas.
 * @param {number} x - The start position x in pixels.
 * @param {number} y - The start position y in pixels.
 * @param {number} width - In pixels.
 * @param {number} height - In pixels.
 * @param {string} targetId - The ID of the target canvas.
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
 * @param {number} x - The start position x in pixels.
 * @param {number} y - The start postion y in pixels.
 * @param {string} text - A string of text to draw.
 * @param {string} canvasId - The ID of the canvas to draw onto, if undefined 
 *                          defaults to "renderNowCanvas".
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
 * @param {string} nextProgram - The relative path to the next program to execute.
 */
RPGcode.prototype.endProgram = function (nextProgram) {
    if (nextProgram) {
        rpgtoolkit.endProgram(nextProgram);
    } else {
        rpgtoolkit.endProgram();
    }
};

/**
 * Fills a solid rectangle on the canvas.
 * 
 * @param {number} x - The start x postion.
 * @param {number} y - The start y postion.
 * @param {number} width - In pixels.
 * @param {number} height - In pixels.
 * @param {string} canvasId - The ID of the canvas to draw on, defaults to "renderNowCanvas" 
 * if none specified.
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
 * @param {type} origin - the point of origin from which the ray will be cast. The object must contain the properties _x and _y
 * @param {type} direction - the direction the ray will be cast. It must be normalized. The object must contain the properties x and y.
 * @param {type} maxDistance - the maximum distance up to which intersections will be found. This is an optional parameter defaulting to Infinity. If it's Infinity find all intersections. If it's negative find only first intersection (if there is one). If it's positive find all intersections up to that distance.
 * @param {type} comp - check for intersection with entities that have this component applied to them. This is an optional parameter that is disabled by default.
 * @param {type} sort - whether to sort the returned array by increasing distance. May be disabled to slightly improve performance if sorted results are not needed. Defaults to true.
 * @returns {unresolved} - an array of raycast-results that may be empty, if no intersection has been found. Otherwise, each raycast-result looks like {obj: Entity, distance: Number, x: Number, y: Number}, describing which obj entity has intersected the ray at intersection point x,y, distance px away from origin.
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
 * @returns {string}
 */
RPGcode.prototype.getBoardName = function () {
    return rpgtoolkit.craftyBoard.board.name;
};

/**
 * Gets the value of global variable.
 * 
 * @param {string} id -  The ID associated with the global variable.
 * @returns {Object}
 */
RPGcode.prototype.getGlobal = function (id) {
    return rpgcode.globals[id];
};

RPGcode.prototype.getCharacter = function() {
  return rpgtoolkit.craftyCharacter.character;  
};

/**
 * Gets the character's current direction.
 * 
 * @returns {string}
 */
RPGcode.prototype.getCharacterDirection = function () {
    var direction = rpgtoolkit.craftyCharacter.character.direction;

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
 * @param {boolean} inTiles - Should the location be in tiles, otherwise pixels.
 * @returns {array[x, y, z]}
 */
RPGcode.prototype.getCharacterLocation = function (inTiles) {
    var instance = rpgtoolkit.craftyCharacter;

    if (inTiles) {
        return {
            x: instance.x / rpgtoolkit.craftyBoard.board.tileWidth,
            y: instance.y / rpgtoolkit.craftyBoard.board.tileHeight,
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
 * @param {number} min - Minimum value for the random number.
 * @param {number} max - Maximum value for the random number.
 * @returns {number}
 */
RPGcode.prototype.getRandom = function (min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
};

/**
 * 
 * 
 * @returns {Object} an object with the attributes inProgram (boolean) and the filename of the current running program, if any.
 */
RPGcode.prototype.getRunningProgram = function() {
    return { inProgram: rpgtoolkit.inProgram, currentProgram: rpgtoolkit.currentProgram };
};

/**
 * Loads the requested assets into the engine, when all of the assets have been loaded
 * the onLoad callback is invoked.
 * 
 * @param {Object} assets - Object of assets to load.
 * @param {Function} onLoad - Callback to invoke after assets are loaded.
 */
RPGcode.prototype.loadAssets = function (assets, onLoad) {
    // If the assets already exist Crafty just ignores 
    // them but still invokes the callback.
    Crafty.load(assets, onLoad);
};

/**
 * Log a message to the console.
 * 
 * @param {string} message - Message to log.
 */
RPGcode.prototype.log = function (message) {
    console.log(message);
};

/**
 * Plays the supplied sound file, up to five sound channels can be active at once. 
 * 
 * @param {string} file - Relative path to the sound file to play.
 * @param {boolean} loop - Should it loop indefinitely?
 */
RPGcode.prototype.playSound = function (file, loop) {
    var count = loop ? -1 : 1;
    Crafty.audio.play(file, count);
};

/**
 * Moves the sprite by n pixels in the given direction.
 * 
 * @param {number} spriteId - The ID of item on the board to push.
 * @param {string} direction - The direction to push the item in e.g. NORTH, SOUTH, EAST, WEST.
 * @param {number} distance - Number of pixels to move.
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
            var index = rpgcode._convertCraftyId(spriteId);
            if (index > -1) {
                var entity = rpgtoolkit.craftyBoard.board.sprites[index];
                entity.move(direction, distance);
                Crafty.trigger("Invalidate");
            }
    }
};

/**
 * Moves the character by n pixels in the given direction.
 * 
 * @param {string} characterId - the id of the character to move. (unused)
 * @param {string} direction - The direction to push the character in.
 * @param {number} distance - Number of pixels to move.
 */
RPGcode.prototype.moveCharacter = function (characterId, direction, distance) {
    // TODO: characterId is unused until multiple party members are supported.

    rpgtoolkit.craftyCharacter.move(direction, distance);
};

/**
 * Moves the sprite to the (x, y) position, the sprite will travel for the
 * supplied duration (milliseconds).
 * 
 * A short duration will result in the sprite arriving quicker and vice versa.
 * 
 * @param {number} spriteId - The ID of item on the board to push.
 * @param {number} x - pixel coordinate
 * @param {number} y - pixel coordinate
 * @param {number} duration - Time taken for the movement to complete (milliseconds)
 */
RPGcode.prototype.moveSpriteTo = function (spriteId, x, y, duration) {
    switch (spriteId) {
        case "source":
            rpgcode.source.tween({x: x, y: y}, duration);
            break;
        default:
            var index = rpgcode._convertCraftyId(spriteId);
            if (index > -1) {
                var entity = rpgtoolkit.craftyBoard.board.sprites[index];
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
 * @param {string} key - The key to listen to.
 * @param {Function} callback - The callback function to invoke when the keyDown event fires.
 * @param {boolean} globalScope - Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerKeyDown = function (key, callback, globalScope) {
    if (globalScope) {
        rpgtoolkit.keyDownHandlers[Crafty.keys[key]] = callback;
    } else {
        rpgtoolkit.keyboardHandler.downHandlers[Crafty.keys[key]] = callback;
    }
};

/**
 * Registers a keyUp listener for a specific key, for a list of valid key values see:
 *    http://craftyjs.com/api/Crafty-keys.html
 *    
 * The callback function will continue to be invoked for every keyUp event until it
 * is unregistered.
 * 
 * @param {string} key - The key to listen to.
 * @param {Function} callback - The callback function to invoke when the keyUp event fires.
 * @param {boolean} globalScope - Is this for use outside of the program itself? 
 */
RPGcode.prototype.registerKeyUp = function (key, callback, globalScope) {
    if (globalScope) {
        rpgtoolkit.keyUpHandlers[Crafty.keys[key]] = callback;
    } else {
        rpgtoolkit.keyboardHandler.upHandlers[Crafty.keys[key]] = callback;
    }
};

/**
 * Removes assets from the engine and frees up the memory allocated to them.
 * 
 * @param {Object} assets - The object containing the assets identifiers.
 */
RPGcode.prototype.removeAssets = function (assets) {
    Crafty.removeAssets(assets);
};

/**
 * Renders the specified canvas, if none then the "renderNowCanvas" is shown.
 * 
 * @param {string} canvasId - The ID of the canvas to render.
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
 * @param {number} tileX - The x position in tiles.
 * @param {number} tileY - The y postion in tiles.
 * @param {number} layer - The layer the tile is on.
 * @param {string} tileSet - The name of the TileSet of the replacement tile.
 * @param {number} tileIndex - The index of the tile in the replacement set.
 */
RPGcode.prototype.replaceTile = function (tileX, tileY, layer, tileSet, tileIndex) {
    var tile = rpgtoolkit.tilesets[tileSet].getTile(tileIndex);
    rpgtoolkit.craftyBoard.board.replaceTile(tileX, tileY, layer, tile);
};

RPGcode.prototype.removeTile = function (tileX, tileY, layer) {
    rpgtoolkit.craftyBoard.board.removeTile(tileX, tileY, layer);
};

/**
 * Restarts the game.
 */
RPGcode.prototype.restart = function () {
    location.reload(); // Cheap way to implement game restart for the moment.
};

/**
 * Runs the requested program.
 * 
 * @param {type} filename
 * @returns {undefined}
 */
RPGcode.prototype.runProgram = function(filename) {
    rpgtoolkit.runProgram(PATH_PROGRAM + filename, rpgcode, null);
};

/**
 * Sends the character to a board and places them at the given (x, y) position in tiles.
 * 
 * @param {string} boardName - The board to send the character to.
 * @param {number} tileX - The x position to place the character at, in tiles.
 * @param {number} tileY - The y position to place the character at, in tiles.
 */
RPGcode.prototype.sendToBoard = function (boardName, tileX, tileY) {
    rpgtoolkit.switchBoard(boardName, tileX, tileY);
};

/**
 * Sets the RGBA color for all drawing operations to use.
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
 * @param {string} id - The ID to use for this global.
 * @param {Object} value - The value this global holds.
 */
RPGcode.prototype.setGlobal = function (id, value) {
    rpgcode.globals[id] = value;
};

/**
 * Sets an image on the canvas.
 * 
 * @param {string} fileName - The relative path to the image.
 * @param {number} x - The start position x in pixels.
 * @param {number} y - The start position y in pixels.
 * @param {number} width - In pixels.
 * @param {number} height - In pixels.
 * @param {string} canvasId - The ID of the canvas to put the image on.
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
 * @param {string} profileImage - The relative path to the profile image.
 * @param {string} backgroundImage - The relative path to the background image.
 */
RPGcode.prototype.setDialogGraphics = function (profileImage, backgroundImage) {
    rpgcode.dialogWindow.profile = profileImage;
    rpgcode.dialogWindow.background = backgroundImage;
};

/**
 * Sets the location of the item.
 * 
 * @param {number} spriteId - The id of the sprite on the board to move.
 * @param {number} x - In pixels by default.
 * @param {number} y - In pixels by default.
 * @param {number} layer - Target layer to put the sprite on.
 * @param {boolean} inTiles - Is (x, y) in tile coordinates, defaults to pixels.
 */
RPGcode.prototype.setSpriteLocation = function (spriteId, x, y, layer, inTiles) {
    if (inTiles) {
        x *= rpgtoolkit.tileSize;
        y *= rpgtoolkit.tileSize;
    }

    var item = rpgtoolkit.craftyBoard.board.sprites[spriteId];
    if (item) {
        item.x = x;
        item.y = y;
        item.layer = layer;
        Crafty.trigger("Invalidate");
    }
};

/**
 * Sets the sprite's current stance, uses the first frame in the animation.
 * 
 * @param {number} itemId - The index of the sprite on the board.
 * @param {string} stanceId - The stanceId (animationId) to use.
 */
RPGcode.prototype.setSpriteStance = function (itemId, stanceId) {
    var entity = rpgtoolkit.craftyBoard.board.sprites[itemId];
    if (entity) {
        entity.sprite.item.changeGraphics(stanceId);
    }
};

/**
 * Sets the character's location without triggering any animation.
 * 
 * @param {string} characterId - The identifier associated with character to move.
 * @param {number} x - In pixels by default.
 * @param {number} y - In pixels by default.
 * @param {number} layer - Target layer to put the item on.
 * @param {boolean} isTiles - Is (x, y) in tile coordinates, defaults to pixels.
 */
RPGcode.prototype.setCharacterLocation = function (characterId, x, y, layer, isTiles) {
    if (isTiles) {
        x *= rpgtoolkit.tileSize;
        y *= rpgtoolkit.tileSize;
    }

    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    rpgtoolkit.craftyCharacter.x = x;
    rpgtoolkit.craftyCharacter.y = y;
    rpgtoolkit.craftyCharacter.character.layer = layer;
};

/**
 * Sets the character's current stance, uses the first frame in the animation.
 * 
 * @param {string} characterId - The index of the item on the board.
 * @param {string} stanceId - The stanceId (animationId) to use.
 */
RPGcode.prototype.setCharacterStance = function (characterId, stanceId) {
    // TODO: characterId will be unused until parties with multiple characters 
    // are supported.
    rpgtoolkit.craftyCharacter.character.changeGraphics(stanceId);
    Crafty.trigger("Invalidate");
};

/**
 * Shows the dialog window and adds the dialog to it if it is already 
 * visible the dialog is just appended to the current window.
 * 
 * Note the dialog window is drawn on the default "renderNowCanvas".
 * 
 * @param {string} dialog - The dialog to output.
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
 * Stop playing a specific sound file, if no file is set stop
 * all sounds.
 * 
 * @param {string} file - The relative path of the sound file to stop.
 */
RPGcode.prototype.stopSound = function (file) {
    if (file) {
        Crafty.audio.stop(file);
    } else {
        Crafty.audio.stop();
    }
};

/**
 * Removes a previously registered keyDown listener.
 * 
 * @param {string} key - The key associated with the listener.
 * @param {boolean} globalScope - Is this a global scope key down handler.
 */
RPGcode.prototype.unregisterKeyDown = function (key, globalScope) {
    if (globalScope) {
        delete rpgtoolkit.keyDownHandlers[Crafty.keys[key]];
    } else {
        delete rpgtoolkit.keyboardHandler.downHandlers[Crafty.keys[key]];
    }
};

/**
 * Removes a previously registered keyUp listener.
 * 
 * @param {string} key - The key associated with the listener.
 * @param {boolean} globalScope - Is this a global scope key up handler;
 */
RPGcode.prototype.unregisterKeyUp = function (key, globalScope) {
    if (globalScope) {
        delete rpgtoolkit.keyUpHandlers[Crafty.keys[key]];
    } else {
        delete rpgtoolkit.keyboardHandler.upHandlers[Crafty.keys[key]];
    }
};