/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global PATH_BITMAP, PATH_MEDIA, PATH_PROGRAM, PATH_BOARD, PATH_CHARACTER, PATH_NPC, jailed, rpgcode, PATH_TILESET, PATH_ENEMY, Crafty, engineUtil, Promise */

var rpgwizard = new RPGWizard();
const AsyncFunction = Object.getPrototypeOf(async function () {}).constructor;

function RPGWizard() {
    this.dt = 0; // Craftyjs time step since last frame.
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

    // Default tile size
    this.tileSize = 32;

    // In-Memory asset Cache
    this.boards = {};
    this.tilesets = {};
    this.layerCache = {};
    this.animations = {};
    this.enemies = {};
    this.npcs = {};
    this.characters = {};
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
    this.haveRunStartup = false;

    // Audio states.
    this.lastBackgroundMusic = "";

    // Debugging options.
    this.showVectors = false;
    this.debugEnabled = false;

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
RPGWizard.prototype.setup = async function (filename) {
    if (rpgwizard.debugEnabled) {
        console.debug("Starting engine with filename=[%s]", filename);
    }

    this.project = await new Project(filename).load();
    
    // Check if we should draw debugging vectors
    this.showVectors = this.project.showVectors ? true : false;

    var scale = 1;
    if (this.project.isFullScreen) {
        var bodyWidth = engineUtil.getBodyWidth();
        var bodyHeight = engineUtil.getBodyHeight();
        scale = (bodyWidth / this.project.resolutionWidth).toFixed(2);
        if (this.project.resolutionHeight * scale > bodyHeight) {
            scale = (bodyHeight / this.project.resolutionHeight).toFixed(2);
        }
    }

    var container = document.getElementById("container");
    var width = Math.floor((this.project.resolutionWidth * scale));
    var height = Math.floor((this.project.resolutionHeight * scale));
    container.style.width = width + "px";
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
        var character = await new Character(PATH_CHARACTER + this.project.initialCharacter).load();
        await this.loadCharacter(character);
        
        var json;
        var board = new Board(PATH_BOARD + this.project.initialBoard);
        if (rpgwizard.boards[board.filename]) {
            json = JSON.parse(rpgwizard.boards[board.filename]);
        }
        board = await board.load(json);
        await this.loadBoard(board);

        // Setup up the Character's starting position.
        this.craftyCharacter.character.x = this.craftyBoard.board.startingPosition["x"];
        this.craftyCharacter.character.y = this.craftyBoard.board.startingPosition["y"];
        this.craftyCharacter.character.layer = this.craftyBoard.board.startingPosition["layer"];
        this.craftyCharacter.x = this.craftyCharacter.character.x;
        this.craftyCharacter.y = this.craftyCharacter.character.y;
        this.craftyCharacter.activationVector.x = this.craftyCharacter.x + this.craftyCharacter.character.activationOffset.x;
        this.craftyCharacter.activationVector.y = this.craftyCharacter.y + this.craftyCharacter.character.activationOffset.y;

        // Setup the viewport to smoothly follow the player object
        Crafty.viewport.x = 0;
        Crafty.viewport.y = 0;
        Crafty.viewport.clampToEntities = true;

        this.loadCraftyAssets(this.loadScene);
    } else if (this.project.initialCharacter && this.project.startupProgram) {
        var character = await new Character(PATH_CHARACTER + this.project.initialCharacter).load();
        await this.loadCharacter(character);
        rpgwizard.runProgram(PATH_PROGRAM + rpgwizard.project.startupProgram, {}, function() {
            rpgwizard.haveRunStartup = true;
        });
    } else if (this.project.startupProgram) {
        rpgwizard.runProgram(PATH_PROGRAM + rpgwizard.project.startupProgram, {}, function() {
            rpgwizard.haveRunStartup = true;
        });
    } else {
        throw "No setup paramets provided, please specifiy an Initial Character" +
                " and Initial Board, or alternatively a Startup program!";
    }
};

RPGWizard.prototype.loadScene = function (e) {
    if (e) {
        if (e.type === "loading") {
            engineUtil.showProgress(e.value.percent);
            if (rpgwizard.debugEnabled) {
                console.debug(JSON.stringify(e));
            }
        } else {
            console.error(JSON.stringify(e));
        }
    } else {
        engineUtil.hideProgress();
        // Run the startup program before the game logic loop.
        if (rpgwizard.project.startupProgram && rpgwizard.firstScene && !rpgwizard.haveRunStartup) {
            rpgwizard.runProgram(
                    PATH_PROGRAM + rpgwizard.project.startupProgram,
                    {},
                    function () {
                        rpgwizard.haveRunStartup = true;
                        rpgwizard.inProgram = false;
                        rpgwizard.currentProgram = null;
                        rpgwizard.controlEnabled = true;
                        rpgwizard.startScene();
                    });
        } else {
            rpgwizard.startScene();
        }
    }
};

RPGWizard.prototype.startScene = function () {
    if (rpgwizard.firstScene) {
        rpgwizard.firstScene = false;
    }

    Crafty.viewport.x = 0;
    Crafty.viewport.y = 0;
    var width = Math.floor((rpgwizard.craftyBoard.board.width * rpgwizard.craftyBoard.board.tileWidth) * Crafty.viewport._scale);
    var height = Math.floor((rpgwizard.craftyBoard.board.height * rpgwizard.craftyBoard.board.tileHeight) * Crafty.viewport._scale);
    if (width > Crafty.viewport._width || height > Crafty.viewport._height) {
        Crafty.viewport.follow(this.craftyCharacter, 0, 0);
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
    if (rpgwizard.debugEnabled) {
        console.debug("Loading assets=[" + JSON.stringify(assets) + "]");
    }
    // Remove any duplicates.
    assets.images = assets.images.filter((it, i, ar) => ar.indexOf(it) === i);

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
                if (rpgwizard.debugEnabled) {
                    console.debug("Loaded assets=[%s]", JSON.stringify(assets));
                }
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
                if (callback && callback.length) {
                    callback({"type": "loading", "value": e});
                }
            },
            function (e) { // uh oh, error loading
                if (callback && callback.length) {
                    callback({"type": "error", "value": e});
                }
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
    if (rpgwizard.debugEnabled) {
        console.debug("Creating Crafty board=[%s]", JSON.stringify(board));
    }

    var width = board.width * board.tileWidth;
    var height = board.height * board.tileHeight;
    var vWidth = Crafty.viewport._width;
    var vHeight = Crafty.viewport._height;
    var scale = Crafty.viewport._scale;
    var xShift = 0, yShift = 0;
    if (width < vWidth) {
        var sWidth = width * scale;
        xShift = Math.max(((vWidth - sWidth) / 2) / scale, 0);
        if (xShift < 1) {
            Math.max(sWidth - vWidth, 0);
        }
        width = vWidth;
    }
    if (height < vHeight) {
        var sHeight = height * scale;
        yShift = Math.max(((vHeight - sHeight) / 2) / scale, 0);
        if (yShift < 1) {
            Math.max(sHeight - vHeight, 0);
        }
        height = vHeight;
    }

    if (rpgwizard.debugEnabled) {
        console.debug("width=" + width);
        console.debug("height=" + height);
        console.debug("xShift=" + xShift);
        console.debug("yShift=" + yShift);
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
                    rpgcode.runTimePrograms.forEach(async function (filename) {
                        var program = await rpgwizard.openProgram(PATH_PROGRAM + filename);
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

RPGWizard.prototype.loadBoard = async function (board) {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading board=[%s]", JSON.stringify(board));
    }
    var craftyBoard = this.createCraftyBoard(board);
    var assets = {"images": [], "audio": {}};

    await Promise.all(craftyBoard.board.tileSets.map(async (file) => {
        if (!rpgwizard.tilesets[file]) {
            var tileSet = await new TileSet(PATH_TILESET + file).load();
            rpgwizard.tilesets[tileSet.name] = tileSet;
            rpgwizard.queueCraftyAssets({"images": [tileSet.image]}, tileSet);
        }
    }));

    for (var layer = 0; layer < board.layers.length; layer++) {
        var boardLayer = board.layers[layer];

        /*
         * Setup vectors.
         */
        boardLayer.vectors.forEach(function (vector) {
            var id = vector.id;
            var type = vector.type;
            var points = vector.points;
            var events = vector.events;

            var len = points.length;
            var collision = [];
            for (var i = 0; i < len; i++) {
                collision.push(points[i].x, points[i].y);
            }
            this.createVectorPolygon(id, collision, layer, type, events);
        }, this);

        /*
         * Layer images.
         */
        boardLayer.images.forEach(function (image) {
            assets.images.push(image.src);
        }, this);
    }

    /*
     * Setup board sprites.
     */
    var sprites = {};
    var len = board.sprites.length;
    for (var i = 0; i < len; i++) {
        var sprite = board.sprites[i];
        sprites[sprite.id] = await this.loadSprite(sprite);
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

RPGWizard.prototype.switchBoard = async function (boardName, tileX, tileY, layer) {
    if (rpgwizard.debugEnabled) {
        console.debug("Switching board to boardName=[%s], tileX=[%d], tileY=[%d], layer=[%d]",
                boardName, tileX, tileY);
    }
    this.craftyBoard.show = false;
    this.controlEnabled = false;

    Crafty("SOLID").destroy();
    Crafty("PASSABLE").destroy();
    Crafty("Board").destroy();
    Crafty("BoardSprite").destroy();

    // May not be a last board if it is being set in a startup program.
    if (rpgwizard.craftyBoard.board) {
        rpgwizard.lastBackgroundMusic = rpgwizard.craftyBoard.board.backgroundMusic;
    }
    
    // Load in the next board.
    var json;
    var board = new Board(PATH_BOARD + boardName);
    if (rpgwizard.boards[board.filename]) {
        json = JSON.parse(rpgwizard.boards[board.filename]);
    }
    board = await board.load(json);
    
    // Move the character first to avoid triggering vectors on next board, in same position.
    var tileWidth = board.tileWidth;
    var tileHeight = board.tileHeight;
    this.craftyCharacter.x = parseInt((tileX * tileWidth) + tileWidth / 2);
    this.craftyCharacter.y = parseInt((tileY * tileHeight) + tileHeight / 2);
    this.craftyCharacter.character.layer = layer;
    
    await this.loadBoard(board);

    if (rpgwizard.debugEnabled) {
        console.debug("Switching board player location set to x=[%d], y=[%d], layer=[%d]",
                this.craftyCharacter.x, this.craftyCharacter.y, this.craftyCharacter.layer);
    }

    this.loadCraftyAssets(this.loadScene);
};

RPGWizard.prototype.loadCharacter = async function (character) {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading character=[%s]", JSON.stringify(character));
    }
    // Have to keep this in a separate entity, as Crafty entites can
    // only have 1 collision polygon at a time, using composition to
    // get around this limitation.
    const bounds = engineUtil.getPolygonBounds(character.activationPoints);
    var activationVector = Crafty.e("2D, Canvas, ActivationVector")
            .attr({
                x: character.x + character.activationOffset.x,
                y: character.y + character.activationOffset.y,
                w: bounds.width,
                h: bounds.height,
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
            .CustomControls(1, 0.8)
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
                this.activationVector.x = this.x + this.character.activationOffset.x;
                this.activationVector.y = this.y + this.character.activationOffset.y;
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
    const assets = await this.craftyCharacter.character.loadAssets();
    this.queueCraftyAssets(assets, character);
};

RPGWizard.prototype.loadSprite = async function (sprite) {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading sprite=[%s]", JSON.stringify(sprite));
    }
    var asset;
    if (sprite.name.endsWith(".enemy")) {
        var json;
        const newEnemy = new Enemy(PATH_ENEMY + sprite.name);
        if (this.enemies[newEnemy.filename]) {
            json = JSON.parse(this.enemies[newEnemy.filename]);
        }
        asset = sprite.enemy = await newEnemy.load(json);
        sprite.collisionPoints = sprite.enemy.collisionPoints;
    } else if (sprite.name.endsWith(".npc")) {
        var json;
        const newNpc = new NPC(PATH_NPC + sprite.name);
        if (this.npcs[newNpc.filename]) {
            json = JSON.parse(this.npcs[newNpc.filename]);
        }
        asset = sprite.npc = await newNpc.load(json);
        sprite.collisionPoints = sprite.npc.collisionPoints;
    } else {
        var json;
        const newCharacter = new Character(PATH_CHARACTER + sprite.name);
        if (this.characters[newCharacter.filename]) {
            json = JSON.parse(this.characters[newCharacter.filename]);
        }
        asset = sprite.character = await newCharacter.load(json);
        sprite.collisionPoints = sprite.character.collisionPoints;
    }
    sprite.x = sprite.startingPosition.x;
    sprite.y = sprite.startingPosition.y;
    sprite.layer = sprite.startingPosition.layer;

    var isEnemy = sprite.enemy !== undefined;
    // TODO: width and height of npc must contain the collision polygon.
    if (sprite.thread) {
        sprite.thread = await this.openProgram(PATH_PROGRAM + sprite.thread);
    }
    var entity;
    const bounds = engineUtil.getPolygonBounds(asset.activationPoints);
    var activationVector = Crafty.e("2D, Canvas, ActivationVector")
            .attr({
                x: sprite.x + asset.activationOffset.x,
                y: sprite.y + asset.activationOffset.y,
                w: bounds.width,
                h: bounds.height,
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
                this.activationVector.x = entity.x + asset.activationOffset.x;
                this.activationVector.y = entity.y + asset.activationOffset.y;
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
        remove: function () {
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

    const assets = await asset.loadAssets();
    this.queueCraftyAssets(assets, asset);

    return entity;
};

RPGWizard.prototype.loadItem = function (item) {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading item=[%s]", JSON.stringify(item));
    }
    this.queueCraftyAssets(item.loadAssets(), item);
};

RPGWizard.prototype.openProgram = async function (filename) {
    if (rpgwizard.debugEnabled) {
        console.debug("Opening program=[%s]", filename);
    }
    var program = rpgwizard.programCache[filename];
    if (!program) {
        let response = await fetch(filename);
        response = await response.text();
        program = new AsyncFunction(response);
        rpgwizard.programCache[filename] = program;
    }

    return program;
};

RPGWizard.prototype.runProgram = async function (filename, source, callback) {
    if (rpgwizard.debugEnabled) {
        console.debug("Running program=[%s]", filename);
    }
    rpgwizard.activePrograms++;
    rpgwizard.inProgram = true;
    rpgwizard.currentProgram = filename;
    rpgcode.source = source; // Entity that triggered the program.
    rpgwizard.controlEnabled = false;
    rpgwizard.endProgramCallback = callback; // Store endProgram callback.
    rpgwizard.keyboardHandler.downHandlers = {}; // Wipe previous keyboard handlers.
    rpgwizard.keyboardHandler.upHandlers = {};
    rpgwizard.mouseHandler.mouseDownHandler = null; // Wipe previous mouse handlers.
    rpgwizard.mouseHandler.mouseUpHandler = null;
    rpgwizard.mouseHandler.mouseClickHandler = null;
    rpgwizard.mouseHandler.mouseDoubleClickHandler = null;
    rpgwizard.mouseHandler.mouseMoveHandler = null;

    var program = await rpgwizard.openProgram(filename);
    program.apply(source);
};

RPGWizard.prototype.endProgram = function (nextProgram) {
    if (rpgwizard.debugEnabled) {
        console.debug("Ending current program, nextProgram=[%s]", nextProgram);
    }

    if (rpgwizard.activePrograms > 0) {
        rpgwizard.activePrograms--;
    }
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

RPGWizard.prototype.createVectorPolygon = function (id, points, layer, type, events) {
    const bounds = engineUtil.getPolygonBounds(points);
    var attr = {x: bounds.x, y: bounds.y, w: bounds.width, h: bounds.height};
    attr.vectorId = id;
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
    var width, height;
    var xDiff = x2 - x1;
    var yDiff = y2 - y1;
    var distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
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
    if (rpgwizard.debugEnabled) {
        console.debug("Playing sound=[%s]", sound);
    }
    Crafty.audio.play(sound, loop);
};
