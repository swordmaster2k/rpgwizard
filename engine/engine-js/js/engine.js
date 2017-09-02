/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global PATH_BITMAP, PATH_MEDIA, PATH_PROGRAM, PATH_BOARD, PATH_CHARACTER, PATH_NPC, jailed, rpgcode, PATH_TILESET, PATH_ENEMY */

var rpgwizard = new RPGWizard();

function RPGWizard() {
    this.dt = 0; // Craftyjs time step since last frame;
    this.screen = {};

    // Game project file.
    this.project = null;

    // Assets to load.
    this.assetsToLoad = {"images": [], "audio": {}};
    this.waitingEntities = []; // The entities to setReady at assets are loaded.

    // Game entities.
    this.craftyBoard = {};
    this.craftyCharacter = {};
    this.craftyRPGcodeScreen = {};

    // TileSets.
    this.tilesets = {};
    this.tileSize = 32;

    // Program cache, stores programs as Function objects.
    this.programCache = {};
    this.activePrograms = 0;
    this.endProgramCallback = null;
    requirejs.config({
        baseUrl: PATH_PROGRAM
    });

    // Used to store state when runProgram is called.
    this.keyboardHandler = {};
    this.keyDownHandlers = {};
    this.keyUpHandlers = {};

    // Used to store state when runProgram is called.
    this.mouseHandler = {};
    this.mouseDownHandler = {};
    this.mouseUpHandler = {};
    this.mouseClickHandler = {};
    this.mouseDoubleClickHandler = {};
    this.mouseMoveHandler = {};

    // Custom movement parameters.
    this.controlEnabled = false;
    this.movementSpeed = 1;

    // Is this the first game scene.
    this.firstScene = true;

    // Engine program states.
    this.inProgram = false;
    this.currentProgram = null;
    
    // Audio states.
    this.lastBackgroundMusic = "";

    // Debugging options.
    this.showVectors = false;

    // Custom crafty components.
    Crafty.c("BaseVector", {
        BaseVector: function (polygon, hiton, hitoff) {
            this.requires("Collision, BASE");
            this.collision(polygon);
            this.checkHits("SOLID, BASE");
            this.bind("HitOn", hiton);
            this.bind("HitOff", hitoff);
            return this;
        }
    });
    Crafty.c("ActivationVector", {
        ActivationVector: function (polygon, hiton, hitoff) {
            this.requires("Collision, ACTIVATION, Raycastable");
            this.collision(polygon);
            this.checkHits("PASSABLE, ACTIVATION");
            this.bind("HitOn", hiton);
            this.bind("HitOff", hitoff);
            return this;
        }
    });
}

/**
 * Setups up the games initial state based on the configuration found in the main file.
 * 
 * @param {type} filename
 * @returns {undefined}
 */
RPGWizard.prototype.setup = function (filename) {
    console.debug("Starting engine with filename=[%s]", filename);

    this.project = new Project(filename);
    
    var scale = 1;
    if (this.project.isFullScreen) {
        var zoomLevel = 100;
        scale = document.body.scrollWidth / this.project.resolutionWidth;
        if (this.project.resolutionHeight * scale > document.body.scrollHeight) {
            scale = document.body.scrollHeight / this.project.resolutionHeight;
        }
        zoomLevel *= scale;
    }
    
    var container = document.getElementById("container");
    var width = Math.floor((this.project.resolutionWidth * scale));
    var height = Math.floor((this.project.resolutionHeight * scale));
    container.style.width =  width + "px";
    container.style.height = height + "px";

    // Configure Crafty.
    Crafty.init(this.project.resolutionWidth, this.project.resolutionHeight);
    Crafty.viewport.init(width, height);
    Crafty.paths({audio: PATH_MEDIA, images: PATH_BITMAP});
    Crafty.viewport.scale(scale);
    
    // Setup run time keys.
    this.keyboardHandler = new Keyboard();
    if (this.project.menuKey) {
        this.keyboardHandler.downHandlers[this.project.menuKey] = function () {
            rpgwizard.runProgram(PATH_PROGRAM + this.project.menuPlugin, {});
        };
    }

    // Setup the mouse handler.
    this.mouseHandler = new Mouse();

    // Setup the drawing canvas (game screen).
    this.screen = new ScreenRenderer();

    // Setup the RPGcode rutime.
    rpgcode = new RPGcode();

    // Disable controls until everything is ready.
    this.controlEnabled = false;

    // Create the UI layer for RPGcode.
    this.createUILayer();

    // Select the startup mode.
    if (this.project.initialCharacter && this.project.initialBoard) {
        // Load the initial character and board.
        this.loadCharacter(new Character(PATH_CHARACTER + this.project.initialCharacter));
        this.loadBoard(new Board(PATH_BOARD + this.project.initialBoard));

        // Setup up the Character's starting position.
        this.craftyCharacter.character.x = this.craftyBoard.board.startingPosition["x"];
        this.craftyCharacter.character.y = this.craftyBoard.board.startingPosition["y"];
        this.craftyCharacter.character.layer = this.craftyBoard.board.startingPosition["layer"];
        this.craftyCharacter.x = this.craftyCharacter.character.x;
        this.craftyCharacter.y = this.craftyCharacter.character.y;
        this.craftyCharacter.activationVector.x = this.craftyCharacter.x;
        this.craftyCharacter.activationVector.y = this.craftyCharacter.y;

        // Setup the viewport to smoothly follow the player object
        Crafty.viewport.follow(this.craftyCharacter, 0, 0);
        //Crafty.viewport.clampToEntities = false;

        this.loadCraftyAssets(this.loadScene);
    } else if (this.project.startupProgram) {
        rpgwizard.runProgram(
                PATH_PROGRAM + rpgwizard.project.startupProgram,
                {});
    } else {
        throw "No setup paramets provided, please specifiy an Initial Character" +
                " and Initial Board, or alternatively a Startup program!";
    }
};

RPGWizard.prototype.loadScene = function (e) {
    if (e) {
        if (e.type === "loading") {
            console.debug(JSON.stringify(e));
        } else {
            console.error(JSON.stringify(e));
        }
    } else {
        // Run the startup program before the game logic loop.
        if (rpgwizard.project.startupProgram && rpgwizard.firstScene) {
            rpgwizard.runProgram(
                    PATH_PROGRAM + rpgwizard.project.startupProgram,
                    {},
                    rpgwizard.startScene);
        } else {
            rpgwizard.startScene();
        }
    }
};

RPGWizard.prototype.startScene = function () {
    if (rpgwizard.firstScene) {
        rpgwizard.firstScene = false;
    }

    rpgwizard.craftyBoard.show = true;
    if (rpgwizard.craftyBoard.board.backgroundMusic !== rpgwizard.lastBackgroundMusic) {
        Crafty.audio.stop();
        rpgwizard.playSound(rpgwizard.craftyBoard.board.backgroundMusic, -1);
    }
    Crafty.trigger("Invalidate");

    if (rpgwizard.craftyBoard.board.firstRunProgram) {
        rpgwizard.runProgram(
                PATH_PROGRAM + rpgwizard.craftyBoard.board.firstRunProgram,
                null, null, true);
    } else {
        rpgwizard.controlEnabled = true;
        Crafty.trigger("EnterFrame", {});
    }
};

RPGWizard.prototype.queueCraftyAssets = function (assets, waitingEntity) {
    if (assets.images) {
        // Remove duplicates images.
        var seen = {};
        assets.images = assets.images.filter(function (item) {
            return seen.hasOwnProperty(item) ? false : (seen[item] = true);
        });
        this.assetsToLoad.images = this.assetsToLoad.images.concat(assets.images);
    }
    if (assets.audio) {
        this.assetsToLoad.audio = Object.assign({}, this.assetsToLoad.audio, assets.audio);
    }

    if (waitingEntity) {
        this.waitingEntities.push(waitingEntity);
    }
};

RPGWizard.prototype.loadCraftyAssets = function (callback) {
    var assets = this.assetsToLoad;
    console.debug("Loading assets=[" + JSON.stringify(assets) + "]");

    // Remove already loaded assets.
    var images = [];
    assets.images.forEach(function (image) {
        if (!Crafty.assets[Crafty.__paths.images + image]) {
            images.push(image);
        }
    });
    var audio = {};
    for (var property in assets.audio) {
        if (!Crafty.assets[Crafty.__paths.audio + property]) {
            audio[property] = assets.audio[property];
            ;
        }
    }
    if (images.length === 0 && Object.keys(audio).length === 0) {
        // Notifiy the entities that their assets are ready for use.
        rpgwizard.waitingEntities.forEach(function (entity) {
            entity.setReady();
        });
        // Reset asset queue.
        rpgwizard.assetsToLoad = {"images": [], "audio": {}};
        rpgwizard.waitingEntities = [];
        callback();
        return;
    }

    assets.images = images;
    assets.audio = audio;
    Crafty.load(assets,
            function () { // loaded
                console.debug("Loaded assets=[%s]", JSON.stringify(assets));

                // Notifiy the entities that their assets are ready for use.
                rpgwizard.waitingEntities.forEach(function (entity) {
                    entity.setReady();
                });

                // Reset asset queue.
                rpgwizard.assetsToLoad = {"images": [], "audio": {}};
                rpgwizard.waitingEntities = [];

                callback();
            },
            function (e) {  // progress 
                callback({"type": "loading", "value": e});
            },
            function (e) { // uh oh, error loading
                callback({"type": "error", "value": e});
            }
    );
};

RPGWizard.prototype.createUILayer = function () {
    // Define a UI layer that is completely static and sits above the other layers
    Crafty.createLayer("UI", "Canvas", {
        xResponse: 0, yResponse: 0, scaleResponse: 0, z: 50
    });

    Crafty.e("2D, UI, Mouse")
            .attr({x: 0, y: 0, w: Crafty.viewport._width, h: Crafty.viewport._height, ready: true})
            .bind("EnterFrame", function () {
                // TODO: define custom event e.g. InvalidateUI and only trigger
                // it when something has been changed via RPGcode.
                this.trigger("Invalidate");
            })
            .bind("Draw", function (e) {
                if (e.ctx) {
                    rpgwizard.screen.renderUI(e.ctx);
                }
            })
            .bind("MouseDown", function (e) {
                var handler = rpgwizard.inProgram ? rpgwizard.mouseHandler.mouseDownHandler : rpgwizard.mouseDownHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("MouseUp", function (e) {
                var handler = rpgwizard.inProgram ? rpgwizard.mouseHandler.mouseUpHandler : rpgwizard.mouseUpHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("Click", function (e) {
                var handler = rpgwizard.inProgram ? rpgwizard.mouseHandler.mouseClickHandler : rpgwizard.mouseClickHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("DoubleClick", function (e) {
                var handler = rpgwizard.inProgram ? rpgwizard.mouseHandler.mouseDoubleClickHandler : rpgwizard.mouseDoubleClickHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("MouseMove", function (e) {
                var handler = rpgwizard.inProgram ? rpgwizard.mouseHandler.mouseMoveHandler : rpgwizard.mouseMoveHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            });
};

RPGWizard.prototype.createCraftyBoard = function (board) {
    console.debug("Creating Crafty board=[%s]", JSON.stringify(board));

    var width = board.width * board.tileWidth;
    var height = board.height * board.tileHeight;
    var xShift = 0;
    var yShift = 0;
    if (width < Crafty.viewport._width) {
        xShift = ((Crafty.viewport._width - (width * Crafty.viewport._scale)) / 2) / Crafty.viewport._scale;
        width = Crafty.viewport._width;
    }
    if (height < Crafty.viewport._height) {
        yShift = ((Crafty.viewport._height - (height * Crafty.viewport._scale)) / 2) / Crafty.viewport._scale;
        height = Crafty.viewport._height;
    }

    Crafty.c("Board", {
        ready: true,
        width: width,
        height: height,
        xShift: xShift,
        yShift: yShift,
        init: function () {
            this.addComponent("2D, Canvas");
            this.attr({x: 0, y: 0, w: width, h: height, z: 0, board: board, show: false});
            this.bind("EnterFrame", function () {
                this.trigger("Invalidate");
            });
            this.bind("Draw", function (e) {
                if (e.ctx) {
                    // Excute the user specified runtime programs first.
                    rpgcode.runTimePrograms.forEach(function (filename) {
                        var program = rpgwizard.openProgram(PATH_PROGRAM + filename);
                        program();
                    });

                    rpgwizard.screen.renderBoard(e.ctx);
                }
            });
        }
    });

    this.craftyBoard = Crafty.e("Board");
    return this.craftyBoard;
};

RPGWizard.prototype.loadBoard = function (board) {
    console.debug("Loading board=[%s]", JSON.stringify(board));

    var craftyBoard = this.createCraftyBoard(board);
    var assets = {"images": [], "audio": {}};

    craftyBoard.board.tileSets.forEach(function (file) {
        var tileSet = new TileSet(PATH_TILESET + file);
        rpgwizard.tilesets[tileSet.name] = tileSet;
        rpgwizard.queueCraftyAssets({"images": [tileSet.image]}, tileSet);
    });

    for (var layer = 0; layer < board.layers.length; layer++) {
        /*
         * Setup vectors.
         */
        board.layers[layer].vectors.forEach(function (vector) {
            var type = vector.type;
            var points = vector.points;
            var events = vector.events;

            var len = points.length;
            var collision = [];
            for (var i = 0; i < len; i++) {
                collision.push(points[i].x, points[i].y);
            }
            this.createVectorPolygon(collision, layer, type, events);
        }, this);
    }

    /*
     * Setup board sprites.
     */
    var sprites = {};
    var len = board.sprites.length;
    for (var i = 0; i < len; i++) {
        var sprite = board.sprites[i];
        sprites[sprite.id] = this.loadSprite(sprite);
    }

    // Change board sprites to an object set.
    board.sprites = sprites;

    /*
     * Play background music.
     */
    var backgroundMusic = board.backgroundMusic;
    if (backgroundMusic) {
        assets.audio[board.backgroundMusic] = board.backgroundMusic;
    }

    this.queueCraftyAssets(assets, craftyBoard.board);
};

RPGWizard.prototype.switchBoard = function (boardName, tileX, tileY, layer) {
    console.debug("Switching board to boardName=[%s], tileX=[%d], tileY=[%d], layer=[%d]",
            boardName, tileX, tileY);

    this.controlEnabled = false;

    Crafty("SOLID").destroy();
    Crafty("PASSABLE").destroy();
    Crafty("Board").destroy();
    Crafty("BoardSprite").destroy();

    rpgwizard.lastBackgroundMusic = rpgwizard.craftyBoard.board.backgroundMusic;    
    this.loadBoard(new Board(PATH_BOARD + boardName));
    
    var tileWidth = this.craftyBoard.board.tileWidth;
    var tileHeight = this.craftyBoard.board.tileHeight;
    this.craftyCharacter.x = parseInt((tileX * tileWidth) + tileWidth / 2);
    this.craftyCharacter.y = parseInt((tileY * tileHeight) + tileHeight / 2);
    this.craftyCharacter.character.layer = layer;

    console.debug("Switching board player location set to x=[%d], y=[%d], layer=[%d]",
            this.craftyCharacter.x, this.craftyCharacter.y, this.craftyCharacter.layer);

    this.loadCraftyAssets(this.loadScene);
};

RPGWizard.prototype.loadCharacter = function (character) {
    console.debug("Loading character=[%s]", JSON.stringify(character));

    // Have to keep this in a separate entity, as Crafty entites can
    // only have 1 collision polygon at a time, using composition to
    // get around this limitation.
    var activationVector = Crafty.e("2D, Canvas, ActivationVector")
            .attr({
                x: character.x,
                y: character.y,
                character: character})
            .ActivationVector(
                    new Crafty.polygon(character.activationPoints),
                    function (hitData) {
                        character.hitOnActivation(hitData, rpgwizard.craftyCharacter);
                    },
                    function (hitData) {
                        character.hitOffActivation(hitData, rpgwizard.craftyCharacter);
                    }
            );
    this.craftyCharacter = Crafty.e("2D, Canvas, player, CustomControls, BaseVector, Tween")
            .attr({
                x: character.x,
                y: character.y,
                character: character,
                activationVector: activationVector,
                vectorType: "CHARACTER",
                tweenEndCallbacks: []
            })
            .CustomControls(1)
            .BaseVector(
                    new Crafty.polygon(character.collisionPoints),
                    function (hitData) {
                        character.hitOnCollision(hitData, rpgwizard.craftyCharacter);
                    },
                    function (hitData) {
                        character.hitOffCollision(hitData, rpgwizard.craftyCharacter);
                    }
            )
            .bind("Move", function (from) {
                // Move activation vector with us.
                this.activationVector.x = this.x;
                this.activationVector.y = this.y;

                this.character.animate(this.dt);
            })
            .bind("EnterFrame", function (event) {
                this.dt = event.dt / 1000;
            })
            .bind("TweenEnd", function (event) {
                if (this.tweenEndCallbacks.length > 0) {
                    var callback = this.tweenEndCallbacks.shift();
                    if (callback) {
                        callback();
                    }
                }
            });

    this.craftyCharacter.visible = false;
    var assets = this.craftyCharacter.character.load();
    this.queueCraftyAssets(assets, character);
};

RPGWizard.prototype.loadSprite = function (sprite) {
    console.debug("Loading sprite=[%s]", JSON.stringify(sprite));
    
    if (sprite.name.endsWith(".enemy")) {
        sprite.enemy = new Enemy(PATH_ENEMY + sprite.name);
        sprite.collisionPoints = sprite.enemy.collisionPoints;
    } else {
        sprite.npc = new NPC(PATH_NPC + sprite.name);
        sprite.collisionPoints = sprite.npc.collisionPoints;
    }
    sprite.x = sprite.startingPosition.x;
    sprite.y = sprite.startingPosition.y;
    sprite.layer = sprite.startingPosition.layer;

    var isEnemy = sprite.enemy !== undefined;
    var asset = isEnemy ? sprite.enemy : sprite.npc;

    // TODO: width and height of npc must contain the collision polygon.
    if (sprite.thread) {
        sprite.thread = this.openProgram(PATH_PROGRAM + sprite.thread);
    }
    var entity;
    var activationVector = Crafty.e("2D, Canvas, ActivationVector")
            .attr({
                x: sprite.x,
                y: sprite.y,
                sprite: sprite})
            .ActivationVector(
                    new Crafty.polygon(asset.activationPoints),
                    function (hitData) {
                        asset.hitOnActivation(hitData, entity);
                    },
                    function (hitData) {
                        asset.hitOffActivation(hitData, entity);
                    }
            );
    Crafty.c("BoardSprite", {
        ready: true,
        visible: false,
        x: sprite.x,
        y: sprite.y,
        layer: sprite.layer,
        width: 150,
        height: 150,
        vectorType: isEnemy ? "ENEMY" : "NPC",
        sprite: sprite,
        events: sprite.events,
        activationVector: activationVector,
        tweenEndCallbacks: [],
        init: function () {
            this.requires("2D, Canvas, Tween, BaseVector");
            this.attr({x: sprite.x, y: sprite.y, w: 50, h: 50, show: false});
            this.bind("Move", function (from) {
                // Move activation vector with us.
                this.activationVector.x = entity.x;
                this.activationVector.y = entity.y;

                asset.animate(this.dt);
            });
            this.bind("EnterFrame", function (event) {
                this.dt = event.dt / 1000;

                if (sprite.thread && asset.renderReady && rpgwizard.craftyBoard.show) {
                    sprite.thread.apply(this);
                }
            });
            this.bind("TweenEnd", function (event) {
                if (this.tweenEndCallbacks.length > 0) {
                    var callback = this.tweenEndCallbacks.shift();
                    if (callback) {
                        callback();
                    }
                }
            });
        },
        remove: function() {
            this.activationVector.destroy();
        }
    });
    entity = Crafty.e("BoardSprite")
            .BaseVector(
                    new Crafty.polygon(asset.collisionPoints),
                    function (hitData) {
                        asset.hitOnCollision(hitData, entity);
                    },
                    function (hitData) {
                        asset.hitOffCollision(hitData, entity);
                    }
            );
    
    var assets = asset.load();
    this.queueCraftyAssets(assets, asset);

    return entity;
};

RPGWizard.prototype.loadItem = function (item) {
    console.debug("Loading item=[%s]", JSON.stringify(item));
    
    var assets = item.load();
    this.queueCraftyAssets(assets, item);
};

RPGWizard.prototype.openProgram = function (filename) {
    console.debug("Opening program=[%s]", filename);

    var program = rpgwizard.programCache[filename];

    if (!program) {
        // TODO: Make the changes here that chrome suggests.
        var req = new XMLHttpRequest();
        req.open("GET", filename, false);
        req.overrideMimeType("text/plain; charset=x-user-defined");
        req.send(null);

        program = new Function(req.responseText);
        rpgwizard.programCache[filename] = program;
    }

    return program;
};

RPGWizard.prototype.runProgram = function (filename, source, callback) {
    console.debug("Running program=[%s]", filename);

    rpgwizard.activePrograms++;
    rpgwizard.inProgram = true;
    rpgwizard.currentProgram = filename;
    rpgcode.source = source; // Entity that triggered the program.

    rpgwizard.controlEnabled = false;

    // Store endProgram callback.
    rpgwizard.endProgramCallback = callback;
    
    // Wipe previous keyboard handlers.
    rpgwizard.keyboardHandler.downHandlers = {};
    rpgwizard.keyboardHandler.upHandlers = {};
    
    // Wipe previous mouse handlers.
    rpgwizard.mouseHandler.mouseDownHandler = null;
    rpgwizard.mouseHandler.mouseUpHandler = null;
    rpgwizard.mouseHandler.mouseClickHandler = null;
    rpgwizard.mouseHandler.mouseDoubleClickHandler = null;
    rpgwizard.mouseHandler.mouseMoveHandler = null;


    var program = rpgwizard.openProgram(filename);
    program.apply(source);
};

RPGWizard.prototype.endProgram = function (nextProgram) {
    console.debug("Ending current program, nextProgram=[%s]", nextProgram);
    rpgwizard.activePrograms--;

    if (nextProgram) {
        rpgwizard.runProgram(
                PATH_PROGRAM + nextProgram, 
                rpgcode.source,
                rpgwizard.endProgramCallback,
                true
        );
    } else if (rpgwizard.activePrograms === 0) {
        if (rpgwizard.endProgramCallback) {
            rpgwizard.endProgramCallback();
            rpgwizard.endProgramCallback = null;
        } else {
            rpgwizard.inProgram = false;
            rpgwizard.currentProgram = null;
            rpgwizard.controlEnabled = true;
        }
    }
};

RPGWizard.prototype.createVectorPolygon = function (points, layer, type, events) {
    var minX = maxX = points[0];
    var minY = maxY = points[1];
    var currentX, currentY; 
    var len = points.length;
    for (var i = 2; i < len; i += 2) {
        currentX = points[i];
        currentY = points[i + 1];
        if (currentX < minX) {
            minX = currentX;
        } else if (currentX > maxX) {
            maxX = currentX;
        }
        if (currentY < minY) {
            minY = currentY;
        } else if (currentY > maxY) {
            maxY = currentY;
        }
    }
    for (var i = 0; i < len; i += 2) {
        points[i] -= minX;
        points[i + 1] -= minY;
    }
    
    var width = Math.abs(maxX - minX) + 1;
    var height = Math.abs(maxY - minY) + 1;
    var attr = {x: minX, y: minY, w: width, h: height};

    attr.layer = layer;
    attr.vectorType = type;
    attr.events = events;

    if (points[0] === points[points.length - 2] && points[1] === points[points.length - 1]) {
        // Start and end points are the same, Crafty does not like that.
        points.pop(); // Remove last y.
        points.pop(); // Remove last x.
    }

    Crafty.e(type + ", Collision, Raycastable")
            .attr(attr)
            .collision(points);
};

RPGWizard.prototype.calculateVectorPosition = function (x1, y1, x2, y2) {
    var xDiff = x2 - x1;
    var yDiff = y2 - y1;

    var distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));

    var width;
    var height;

    if (x1 !== x2) {
        width = distance;
        height = 2;

        if (xDiff < 0) {
            x1 = x2;
        }
    } else {
        width = 2;
        height = distance;

        if (yDiff < 0) {
            y1 = y2;
        }
    }

    return {x: x1, y: y1, w: width, h: height};
};

RPGWizard.prototype.playSound = function (sound, loop) {
    console.debug("Playing sound=[%s]", sound);

    Crafty.audio.play(sound, loop);
};

/**
 * Utility function for getting accurate timestamps across browsers.
 * 
 * @returns {Number}
 */
RPGWizard.prototype.timestamp = function () {
    return window.performance && window.performance.now ? window.performance.now() : new Date().getTime();
};

// TODO: Make this a utility function. When there is a Craftyjs compiler
// it will do it instead.
RPGWizard.prototype.prependPath = function (prepend, items) {
    var len = items.length;
    for (var i = 0; i < len; i++) {
        items[i] = prepend.concat(items[i]);
    }
};
