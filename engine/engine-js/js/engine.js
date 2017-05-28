/*
 * Copyright (c) 2017, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global PATH_BITMAP, PATH_MEDIA, PATH_PROGRAM, PATH_BOARD, PATH_CHARACTER, PATH_NPC, jailed, rpgcode, PATH_TILESET */

var rpgtoolkit = new RPGToolkit();

function RPGToolkit() {
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

    // TileSets.
    this.tilesets = {};
    this.tileSize = 32;

    // Program cache, stores programs as Function objects.
    this.programCache = {};

    // Used to store state when runProgram is called.
    this.keyboardHandler = {};
    this.endProgramCallback = null;
    this.keyDownHandlers = null;
    this.keyUpHandlers = null;

    // Custom movement parameters.
    this.controlEnabled = false;
    this.movementSpeed = 1;

    // Is this the first game scene.
    this.firstScene = true;

    // Engine program states.
    this.inProgram = false;
    this.currentProgram = null;

    // Debugging options.
    this.showVectors = false;

    // Custom crafty components.
    Crafty.c("BaseVector", {
        BaseVector: function (polygon, hiton, hitoff) {
            this.requires("Collision, BASE, Raycastable");
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
RPGToolkit.prototype.setup = function (filename) {
    console.info("Starting engine with filename=[%s]", filename);

    this.project = new Project(filename);

    // Configure Crafty.
    Crafty.init(this.project.resolutionWidth, this.project.resolutionHeight);
    Crafty.viewport.init(this.project.resolutionWidth, this.project.resolutionHeight);
    Crafty.paths({audio: PATH_MEDIA, images: PATH_BITMAP});

    // Setup run time keys.
    this.keyboardHandler = new Keyboard();
    this.keyboardHandler.downHandlers[this.project.menuKey] = function () {
        rpgtoolkit.runProgram(PATH_PROGRAM + this.project.menuPlugin, {});
    };

    // Setup the drawing canvas (game screen).
    this.screen = new ScreenRenderer();

    // Setup the RPGcode rutime.
    rpgcode = new RPGcode();

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

    Crafty.viewport.follow(this.craftyCharacter, 0, 0);

    // Disable controls until everything is ready.
    this.controlEnabled = false;

    this.loadCraftyAssets(this.loadScene);
};

RPGToolkit.prototype.loadScene = function (e) {
    if (e) {
        if (e.type === "loading") {
            console.info(JSON.stringify(e));
        } else {
            console.error(JSON.stringify(e));
        }
    } else {
        // Run the startup program before the game logic loop.
        if (rpgtoolkit.project.startupProgram && rpgtoolkit.firstScene) {
            rpgtoolkit.runProgram(PATH_PROGRAM + rpgtoolkit.project.startupProgram,
                    {},
                    rpgtoolkit.startScene);
        } else {
            rpgtoolkit.startScene();
        }
    }
};

RPGToolkit.prototype.startScene = function () {
    if (rpgtoolkit.firstScene) {
        rpgtoolkit.firstScene = false;
    }

    rpgtoolkit.craftyBoard.show = true;
    if (rpgtoolkit.craftyBoard.board.backgroundMusic) {
        rpgtoolkit.playSound(rpgtoolkit.craftyBoard.board.backgroundMusic, -1);
    }
    Crafty.trigger("Invalidate");

    if (rpgtoolkit.craftyBoard.board.firstRunProgram) {
        rpgtoolkit.runProgram(
                PATH_PROGRAM + rpgtoolkit.craftyBoard.board.firstRunProgram,
                null, null);
    } else {
        rpgtoolkit.controlEnabled = true;
        Crafty.trigger("EnterFrame", {});
    }
};

RPGToolkit.prototype.queueCraftyAssets = function (assets, waitingEntity) {
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

RPGToolkit.prototype.loadCraftyAssets = function (callback) {
    var assets = this.assetsToLoad;
    console.info("Loading assets=[" + JSON.stringify(assets) + "]");

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
        rpgtoolkit.waitingEntities.forEach(function (entity) {
            entity.setReady();
        });
        // Reset asset queue.
        rpgtoolkit.assetsToLoad = {"images": [], "audio": {}};
        rpgtoolkit.waitingEntities = [];
        callback();
        return;
    }

    assets.images = images;
    assets.audio = audio;
    Crafty.load(assets,
            function () { // loaded
                console.log("Loaded assets=[%s]", JSON.stringify(assets));

                // Notifiy the entities that their assets are ready for use.
                rpgtoolkit.waitingEntities.forEach(function (entity) {
                    entity.setReady();
                });

                // Reset asset queue.
                rpgtoolkit.assetsToLoad = {"images": [], "audio": {}};
                rpgtoolkit.waitingEntities = [];

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

RPGToolkit.prototype.createCraftyBoard = function (board) {
    console.debug("Creating Crafty board=[%s]", JSON.stringify(board));

    var width = board.width * board.tileWidth;
    var height = board.height * board.tileHeight;
    Crafty.c("Board", {
        ready: true,
        width: width,
        height: height,
        init: function () {
            this.addComponent("2D, Canvas");
            this.attr({x: 0, y: 0, w: width, h: height, board: board, show: false});
            this.bind("EnterFrame", function () {
                this.trigger("Invalidate");
            });
            this.bind("Draw", function (e) {
                if (e.ctx) {
                    // Excute the user specified runtime programs first.
                    rpgcode.runTimePrograms.forEach(function (filename) {
                        var program = rpgtoolkit.openProgram(PATH_PROGRAM + filename);
                        program();
                    });

                    rpgtoolkit.screen.render(e.ctx);
                }
            });
        }
    });

    this.craftyBoard = Crafty.e("Board");
    return this.craftyBoard;
};

RPGToolkit.prototype.loadBoard = function (board) {
    console.info("Loading board=[%s]", JSON.stringify(board));

    var craftyBoard = this.createCraftyBoard(board);
    var assets = {"images": [], "audio": {}};

    craftyBoard.board.tileSets.forEach(function (file) {
        var tileSet = new TileSet(PATH_TILESET + file);
        rpgtoolkit.tilesets[tileSet.name] = tileSet;
        rpgtoolkit.queueCraftyAssets({"images": tileSet.images}, tileSet);
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
            for (var i = 0; i < len - 1; i++) {
                this.createVector(points[i].x, points[i].y,
                        points[i + 1].x, points[i + 1].y,
                        layer, type, events);
            }
            if (vector.isClosed) {
                this.createVector(
                        points[0].x, points[0].y,
                        points[len - 1].x, points[len - 1].y,
                        layer, type, events);
            }
        }, this);
    }

    /*
     * Setup board sprites.
     */
    var sprites = {};
    var len = board.sprites.length;
    for (var i = 0; i < len; i++) {
        var sprite = board.sprites[i];
        sprite.npc = new NPC(PATH_NPC + sprite.name);
        sprite.x = sprite.startingPosition.x;
        sprite.y = sprite.startingPosition.y;
        sprite.layer = sprite.startingPosition.layer;
        sprite.collisionPoints = sprite.npc.collisionPoints;
        var boardSprite = this.loadSprite(sprite);
        sprites[sprite.id] = boardSprite;
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

RPGToolkit.prototype.switchBoard = function (boardName, tileX, tileY) {
    console.info("Switching board to boardName=[%s], tileX=[%d], tileY=[%d]",
            boardName, tileX, tileY);

    this.controlEnabled = false;

    Crafty("SOLID").destroy();
    Crafty("PASSABLE").destroy();
    Crafty("Board").destroy();
    Crafty.audio.stop();

    this.loadBoard(new Board(PATH_BOARD + boardName));

    var tileWidth = this.craftyBoard.board.tileWidth;
    var tileHeight = this.craftyBoard.board.tileHeight;
    this.craftyCharacter.x = parseInt((tileX * tileWidth) + tileWidth / 2);
    this.craftyCharacter.y = parseInt((tileY * tileHeight) + tileHeight / 2);

    console.log("Switching board player location set to x=[%d], y=[%d]",
            this.craftyCharacter.x, this.craftyCharacter.y);

    this.loadCraftyAssets(this.loadScene);
};

RPGToolkit.prototype.loadCharacter = function (character) {
    console.info("Loading character=[%s]", JSON.stringify(character));

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
                        character.hitOnActivation(hitData, rpgtoolkit.craftyCharacter);
                    },
                    function (hitData) {
                        character.hitOffActivation(hitData, rpgtoolkit.craftyCharacter);
                    }
            );
    this.craftyCharacter = Crafty.e("2D, Canvas, player, CustomControls, BaseVector")
            .attr({
                x: character.x,
                y: character.y,
                character: character,
                activationVector: activationVector
            })
            .CustomControls(1)
            .BaseVector(
                    new Crafty.polygon(character.collisionPoints),
                    function (hitData) {
                        character.hitOnCollision(hitData, rpgtoolkit.craftyCharacter);
                    },
                    function (hitData) {
                        character.hitOffCollision(hitData, rpgtoolkit.craftyCharacter);
                    }
            )
            .bind("Moved", function (from) {
                // Move activation vector with us.
                this.activationVector.x = this.x;
                this.activationVector.y = this.y;

                this.character.animate(this.dt);
            })
            .bind("EnterFrame", function (event) {
                this.dt = event.dt / 1000;
            });

    this.craftyCharacter.visible = false;
    var assets = this.craftyCharacter.character.load();
    this.queueCraftyAssets(assets, character);
};

RPGToolkit.prototype.loadSprite = function (sprite) {
    console.info("Loading sprite=[%s]", JSON.stringify(sprite));

    // TODO: width and height of npc must contain the collision polygon.
    if (sprite.thread) {
        sprite.thread = this.openProgram(PATH_PROGRAM + sprite.thread);
    }
    var entity = Crafty.e("BaseVector")
            .BaseVector(
                    new Crafty.polygon(sprite.npc.collisionPoints),
                    function (hitData) {
                        sprite.npc.hitOnCollision(hitData, entity);
                    },
                    function (hitData) {
                        sprite.npc.hitOffCollision(hitData, entity);
                    }
            );
    var activationVector = Crafty.e("2D, Canvas, ActivationVector")
            .attr({
                x: sprite.x,
                y: sprite.y,
                sprite: sprite})
            .ActivationVector(
                    new Crafty.polygon(sprite.npc.activationPoints),
                    function (hitData) {
                        sprite.npc.hitOnActivation(hitData, entity);
                    },
                    function (hitData) {
                        sprite.npc.hitOffActivation(hitData, entity);
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
        vectorType: "NPC",
        sprite: sprite,
        events: sprite.events,
        activationVector: activationVector,
        init: function () {
            this.requires("2D, Canvas, Tween, BaseVector");
            this.attr({x: sprite.x, y: sprite.y, w: 50, h: 50, show: false});
            this.bind("Move", function (from) {
                // Move activation vector with us.
                this.activationVector.x = entity.x;
                this.activationVector.y = entity.y;

                this.sprite.npc.animate(this.dt);
            });
            this.bind("EnterFrame", function (event) {
                this.dt = event.dt / 1000;

                if (this.sprite.thread && this.sprite.npc.renderReady) {
                    this.sprite.thread.apply(this);
                }
            });
        }
    });
    entity.addComponent("BoardSprite");

    var assets = sprite.npc.load();
    this.queueCraftyAssets(assets, sprite.npc);

    return entity;
};

RPGToolkit.prototype.openProgram = function (filename) {
    console.info("Opening program=[%s]", filename);

    var program = rpgtoolkit.programCache[filename];

    if (!program) {
        // TODO: Make the changes here that chrome suggests.
        var req = new XMLHttpRequest();
        req.open("GET", filename, false);
        req.overrideMimeType("text/plain; charset=x-user-defined");
        req.send(null);

        program = new Function(req.responseText);
        rpgtoolkit.programCache[filename] = program;
    }

    return program;
};

RPGToolkit.prototype.runProgram = function (filename, source, callback) {
    console.info("Running program=[%s]", filename);

    rpgtoolkit.inProgram = true;
    rpgtoolkit.currentProgram = filename;
    rpgcode.source = source; // Entity that triggered the program.

    rpgtoolkit.controlEnabled = false;

    // Store endProgram callback and runtime key handlers.
    rpgtoolkit.endProgramCallback = callback;
    rpgtoolkit.keyDownHandlers = rpgtoolkit.keyboardHandler.downHandlers;
    rpgtoolkit.keyUpHandlers = rpgtoolkit.keyboardHandler.upHandlers;
    rpgtoolkit.keyboardHandler.downHandlers = {};
    rpgtoolkit.keyboardHandler.upHandlers = {};

    var program = rpgtoolkit.openProgram(filename);
    program();
};

RPGToolkit.prototype.endProgram = function (nextProgram) {
    console.info("Ending current program, nextProgram=[%s]", nextProgram);

    if (nextProgram) {
        rpgtoolkit.runProgram(PATH_PROGRAM + nextProgram, rpgcode.source,
                rpgtoolkit.endProgramCallback);
    } else {
        if (rpgtoolkit.endProgramCallback) {
            rpgtoolkit.endProgramCallback();
            rpgtoolkit.endProgramCallback = null;
        }

        rpgtoolkit.keyboardHandler.downHandlers = rpgtoolkit.keyDownHandlers;
        rpgtoolkit.keyboardHandler.upHandlers = rpgtoolkit.keyUpHandlers;
        rpgtoolkit.inProgram = false;
        rpgtoolkit.currentProgram = null;
        rpgtoolkit.controlEnabled = true;
    }
};

RPGToolkit.prototype.createVector = function (x1, y1, x2, y2, layer, type, events) {
    var attr = this.calculateVectorPosition(x1, y1, x2, y2);
    attr.layer = layer;
    attr.vectorType = type;
    attr.events = events;

    Crafty.e(type + ", Collision, Raycastable")
            .attr(attr);
};

RPGToolkit.prototype.calculateVectorPosition = function (x1, y1, x2, y2) {
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

RPGToolkit.prototype.playSound = function (sound, loop) {
    console.info("Playing sound=[%s]", sound);

    Crafty.audio.play(sound, loop);
};

/**
 * Utility function for getting accurate timestamps across browsers.
 * 
 * @returns {Number}
 */
RPGToolkit.prototype.timestamp = function () {
    return window.performance && window.performance.now ? window.performance.now() : new Date().getTime();
};

// TODO: Make this a utility function. When there is a Craftyjs compiler
// it will do it instead.
RPGToolkit.prototype.prependPath = function (prepend, items) {
    var len = items.length;
    for (var i = 0; i < len; i++) {
        items[i] = prepend.concat(items[i]);
    }
};
