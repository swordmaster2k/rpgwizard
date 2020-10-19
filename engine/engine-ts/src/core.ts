/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/* global PATH_BITMAP, PATH_MEDIA, PATH_PROGRAM, PATH_BOARD, PATH_CHARACTER, PATH_NPC, jailed, rpgcode, PATH_TILESET, PATH_ENEMY, Crafty, engineUtil, Promise, requirejs, PATH_SPRITE */

import { Project } from "./formats/project.js";
import { Board } from "./formats/board.js";
import { TileSet } from "./formats/tileset.js";
import { Sprite } from "./formats/sprite.js";
import { ScreenRenderer } from "./renderers/screenRenderer.js";

import { RPGcode } from "./rpgcode/rpgcode.js";
import { ScriptVM } from "./client-api/script-vm.js";

import { Keyboard } from "./io/keyboard.js";
import { Mouse } from "./io/mouse.js";

// REFACTOR: Find better solution
// https://stackoverflow.com/questions/31173738/typescript-getting-error-ts2304-cannot-find-name-require
declare const Crafty: any;

// Allow extending window with RPG API global
declare global {
    // eslint-disable-next-line no-unused-vars
    interface Window { rpgcode: any; }
}

export class Core {

    // Singleton reference
    private static instance: Core;

    // REFACTOR: Move this
    private dt: number; // Craftyjs time step since last frame.

    private screen: any;
    private project: any;
    private assetsToLoad: any;
    private waitingEntities: Array<any>;

    // REFACTOR: Move this
    private craftyBoard: any;
    private craftyCharacter: any;
    private craftyRPGcodeScreen: any;

    private tileSize: number;

    // REFACTOR: Move this
    // In-Memory asset Cache
    private boards: any;
    private tilesets: any;
    private layerCache: any;
    private animations: any;
    private enemies: any;
    private npcs: any;
    private characters: any;
    private programCache: any;

    // REFACTOR: Remove this
    private activePrograms: number;
    private endProgramCallback: any;

    // REFACTOR: Move this
    private keyboardHandler: any;
    private keyDownHandlers: any;
    private keyUpHandlers: any;

    // REFACTOR: Move this
    private mouseHandler: any;
    private mouseDownHandler: any;
    private mouseUpHandler: any;
    private mouseClickHandler: any;
    private mouseDoubleClickHandler: any;
    private mouseMoveHandler: any;

    // REFACTOR: Move this
    private controlEnabled: boolean;
    private movementSpeed: number;

    private firstScene: boolean;

    // REFACTOR: Move this
    private inProgram: boolean;
    private currentProgram: any;
    private haveRunStartup: boolean;

    // REFACTOR: Move this
    private lastBackgroundMusic: string;

    // REFACTOR: Update this
    private showVectors: boolean;
    private debugEnabled: boolean;

    private scriptVM: ScriptVM;

    private constructor() {
        this.dt = 0; // Craftyjs time step since last frame.
        this.screen = {};

        // Game project file.
        this.project = null;

        // Assets to load.
        this.assetsToLoad = { images: [], audio: {} };
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

        // REFACTOR: Remove me
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
            BaseVector: function(polygon, hiton, hitoff) {
                this.requires("Collision, BASE");
                this.collision(polygon);
                this.checkHits("SOLID, BASE");
                this.bind("HitOn", hiton);
                this.bind("HitOff", hitoff);
                return this;
            }
        });
        Crafty.c("ActivationVector", {
            ActivationVector: function(polygon, hiton, hitoff) {
                this.requires("Collision, ACTIVATION, Raycastable");
                this.collision(polygon);
                this.checkHits("PASSABLE, ACTIVATION");
                this.bind("HitOn", hiton);
                this.bind("HitOff", hitoff);
                return this;
            }
        });

        this.scriptVM = new ScriptVM();
        window.rpgcode = new RPGcode();
    }

    public static getInstance(): Core {
        if (!Core.instance) {
            Core.instance = new Core();
        }

        return Core.instance;
    }

    public async setup(filename: string) {
        if (this.debugEnabled) {
            console.debug("Starting engine with filename=[%s]", filename);
        }

        this.project = await new Project(filename).load();

        // Check if we should draw debugging vectors
        this.showVectors = this.project.showVectors;

        var scale = 1;
        if (this.project.viewport.fullScreen) {
            var bodyWidth = engineUtil.getBodyWidth();
            var bodyHeight = engineUtil.getBodyHeight();
            scale = parseFloat((bodyWidth / this.project.viewport.width).toFixed(2));
            if (this.project.viewport.height * scale > bodyHeight) {
                scale = parseFloat((bodyHeight / this.project.viewport.height).toFixed(2));
            }
        }

        var container = document.getElementById("container");
        var width = Math.floor((this.project.viewport.width * scale));
        var height = Math.floor((this.project.viewport.height * scale));
        container.style.width = width + "px";
        container.style.height = height + "px";

        // Configure Crafty.
        Crafty.init(this.project.viewport.width, this.project.viewport.height);
        Crafty.viewport.init(width, height);
        Crafty.paths({ audio: PATH_MEDIA, images: PATH_BITMAP });
        Crafty.viewport.scale(scale);

        // Setup run time keys.
        this.keyboardHandler = new Keyboard();

        // Setup the mouse handler.
        this.mouseHandler = new Mouse();

        // Setup the drawing canvas (game screen).
        this.screen = new ScreenRenderer();

        // Disable controls until everything is ready.
        this.controlEnabled = false;

        // Create the UI layer for RPGcode.
        this.createUILayer();

        // Run game's startup script
        try {
            console.info("Starting to run startup script...");
            await this.scriptVM.run("../../game/scripts/new-startup.js", this);
            // await rpgwizard.runProgram("game/scripts/startup.js", this, () => { rpgwizard.haveRunStartup = true; });
            console.info("Finished running startup script...");
        } catch (e) {
            console.error(e);
            throw new Error("Could not run startup script!");
        }
    }

    // REFACTOR: Move this
    public async loadScene(e: any) {
        if (e) {
            if (e.type === "loading") {
                engineUtil.showProgress(e.value.percent);
                if (Core.instance.debugEnabled) {
                    console.debug(JSON.stringify(e));
                }
            } else {
                console.error(JSON.stringify(e));
            }
        } else {
            engineUtil.hideProgress();

            // REFACTOR: Update this
            // Run the startup program before the game logic loop.
            if (Core.instance.project.startupProgram && Core.instance.firstScene && !Core.instance.haveRunStartup) {
                await this.scriptVM.run(PATH_PROGRAM + this.project.startupProgram, Core.instance);
                Core.instance.haveRunStartup = true;
                Core.instance.inProgram = false;
                Core.instance.currentProgram = null;
                Core.instance.controlEnabled = true;

                await Core.instance.startScene();
            } else {
                await Core.instance.startScene();
            }
        }
    }

    // REFACTOR: Move this
    public async startScene() {
        if (this.firstScene) {
            this.firstScene = false;
        }

        Crafty.viewport.x = 0;
        Crafty.viewport.y = 0;
        var width = Math.floor((this.craftyBoard.board.width * this.craftyBoard.board.tileWidth) * Crafty.viewport._scale);
        var height = Math.floor((this.craftyBoard.board.height * this.craftyBoard.board.tileHeight) * Crafty.viewport._scale);
        if (width > Crafty.viewport._width || height > Crafty.viewport._height) {
            Crafty.viewport.follow(this.craftyCharacter, 0, 0);
        }

        this.craftyBoard.show = true;
        if (this.craftyBoard.board.backgroundMusic !== this.lastBackgroundMusic) {
            Crafty.audio.stop();
            this.playSound(this.craftyBoard.board.backgroundMusic, -1);
        }
        Crafty.trigger("Invalidate");

        if (this.craftyBoard.board.firstRunProgram) {
            await this.scriptVM.run(PATH_PROGRAM + this.craftyBoard.board.firstRunProgram, Core.instance);
        } else {
            this.controlEnabled = true;
            Crafty.trigger("EnterFrame", {});
        }
    }

    // REFACTOR: Move this
    public queueCraftyAssets(assets: any, waitingEntity: any) {
        if (assets.images) {
            // Remove duplicates images.
            var seen = {};
            assets.images = assets.images.filter(function(item) {
                return Object.prototype.hasOwnProperty.call(seen, item) ? false : (seen[item] = true);
            });
            this.assetsToLoad.images = this.assetsToLoad.images.concat(assets.images);
        }
        if (assets.audio) {
            this.assetsToLoad.audio = Object.assign({}, this.assetsToLoad.audio, assets.audio);
        }
        if (waitingEntity) {
            this.waitingEntities.push(waitingEntity);
        }
    }

    // REFACTOR: Move this
    public loadCraftyAssets(callback: any) {
        var assets = this.assetsToLoad;
        if (this.debugEnabled) {
            console.debug("Loading assets=[" + JSON.stringify(assets) + "]");
        }
        // Remove any duplicates.
        assets.images = assets.images.filter((it, i, ar) => ar.indexOf(it) === i);

        // Remove already loaded assets.
        var images = [];
        assets.images.forEach(function(image) {
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
            this.waitingEntities.forEach(function(entity) {
                entity.setReady();
            });
            // Reset asset queue.
            this.assetsToLoad = { images: [], audio: {} };
            this.waitingEntities = [];
            callback();
            return;
        }

        assets.images = images;
        assets.audio = audio;
        Crafty.load(assets,
            () => { // loaded
                if (this.debugEnabled) {
                    console.debug("Loaded assets=[%s]", JSON.stringify(assets));
                }
                // Notifiy the entities that their assets are ready for use.
                this.waitingEntities.forEach(function(entity) {
                    entity.setReady();
                });
                // Reset asset queue.
                this.assetsToLoad = { images: [], audio: {} };
                this.waitingEntities = [];
                callback();
            },
            function(e) { // progress
                if (callback && callback.length) {
                    const response = { type: "loading", value: e };
                    callback(response);
                }
            },
            function(e) { // uh oh, error loading
                if (callback && callback.length) {
                    const response = { type: "error", value: e };
                    callback(response);
                }
            }
        );
    }

    // REFACTOR: Move this
    public createUILayer() {
        // Define a UI layer that is completely static and sits above the other layers
        Crafty.createLayer("UI", "Canvas", {
            xResponse: 0, yResponse: 0, scaleResponse: 0, z: 50
        });

        Crafty.e("2D, UI, Mouse")
            .attr({ x: 0, y: 0, w: Crafty.viewport._width, h: Crafty.viewport._height, ready: true })
            .bind("Draw", (e) => {
                if (e.ctx) {
                    this.screen.renderUI(e.ctx);
                }
            })
            .bind("MouseDown", (e) => {
                var handler = this.inProgram ? this.mouseHandler.mouseDownHandler : this.mouseDownHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("MouseUp", (e) => {
                var handler = this.inProgram ? this.mouseHandler.mouseUpHandler : this.mouseUpHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("Click", (e) => {
                var handler = this.inProgram ? this.mouseHandler.mouseClickHandler : this.mouseClickHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("DoubleClick", (e) => {
                var handler = this.inProgram ? this.mouseHandler.mouseDoubleClickHandler : this.mouseDoubleClickHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("MouseMove", (e) => {
                var handler = this.inProgram ? this.mouseHandler.mouseMoveHandler : this.mouseMoveHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            });
    }

    // REFACTOR: Move this
    public createCraftyBoard(board: any): any {
        if (this.debugEnabled) {
            console.debug("Creating Crafty board=[%s]", JSON.stringify(board));
        }

        var width = board.width * board.tileWidth;
        var height = board.height * board.tileHeight;
        var vWidth = Crafty.viewport._width;
        var vHeight = Crafty.viewport._height;
        var scale = Crafty.viewport._scale;
        var xShift = 0;
        var yShift = 0;
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

        if (this.debugEnabled) {
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
            init: function() {
                this.addComponent("2D, Canvas");
                this.attr({ x: 0, y: 0, w: width, h: height, z: 0, board: board, show: false });
                this.bind("EnterFrame", function() {
                    this.trigger("Invalidate");
                });
                this.bind("Draw", function(e) {
                    if (e.ctx) {
                        // REFACTOR: Fix this
                        // Excute the user specified runtime programs first.
                        // rpgcode.runTimePrograms.forEach(async function(filename) {
                        //     var program = await Core.getInstance().openProgram(PATH_PROGRAM + filename);
                        //     program();
                        // });
                        Core.getInstance().screen.renderBoard(e.ctx);
                    }
                });
            }
        });

        this.craftyBoard = Crafty.e("Board");
        return this.craftyBoard;
    }

    // REFACTOR: Move this
    public async loadBoard(board: any) {
        if (this.debugEnabled) {
            console.debug("Loading board=[%s]", JSON.stringify(board));
        }
        var craftyBoard = this.createCraftyBoard(board);
        var assets = { images: [], audio: {} };

        await Promise.all(craftyBoard.board.tilesets.map(async(file) => {
            if (!this.tilesets[file]) {
                var tileset = await new TileSet(PATH_TILESET + file).load();
                this.tilesets[file] = tileset;
                this.queueCraftyAssets({ images: [tileset.image] }, tileset);
            }
        }));

        for (var layer = 0; layer < board.layers.length; layer++) {
            var boardLayer = board.layers[layer];

            // REFACTOR: Update this
            /*
             * Setup vectors.
             */
            // boardLayer.vectors.forEach(function (vector) {
            //     var id = vector.id;
            //     var type = vector.type;
            //     var points = vector.points;
            //     var events = vector.events;

            //     var len = points.length;
            //     var collision = [];
            //     for (var i = 0; i < len; i++) {
            //         collision.push(points[i].x, points[i].y);
            //     }
            //     this.createVectorPolygon(id, collision, layer, type, events);
            // }, this);

            // REFACTOR: Update this
            /*
             * Layer images.
             */
            // boardLayer.images.forEach(function (image) {
            //     assets.images.push(image.src);
            // }, this);

            /*
            * Setup board sprites.
            */
            var sprites = {};
            for (const [key, value] of Object.entries(boardLayer.sprites)) {
                sprites[key] = await this.loadSprite(value);
            }
            boardLayer.sprites = sprites;
        }

        // REFACTOR: Update this
        /*
         * Setup board sprites.
         */
        // var sprites = {};
        // var len = board.sprites.length;
        // for (var i = 0; i < len; i++) {
        //     var sprite = board.sprites[i];
        //     sprites[sprite.id] = await this.loadSprite(sprite);
        // }

        // Change board sprites to an object set.
        // board.sprites = sprites;

        // REFACTOR: Update this
        /*
         * Play background music.
         */
        // var backgroundMusic = board.backgroundMusic;
        // if (backgroundMusic) {
        //     assets.audio[board.backgroundMusic] = board.backgroundMusic;
        // }

        this.queueCraftyAssets(assets, craftyBoard.board);
    }

    // REFACTOR: Move this
    public async switchBoard(boardName: string, tileX: number, tileY: number, layer: number) {
        if (this.debugEnabled) {
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
        if (this.craftyBoard.board) {
            this.lastBackgroundMusic = this.craftyBoard.board.backgroundMusic;
        }

        // Load in the next board.
        var json;
        var board = new Board(PATH_BOARD + boardName);
        if (this.boards[board.filename]) {
            json = JSON.parse(this.boards[board.filename]);
        }
        board = await board.load(json);

        // REFACTOR: Remove this
        // Move the character first to avoid triggering vectors on next board, in same position.
        // var tileWidth = board.tileWidth;
        // var tileHeight = board.tileHeight;
        // this.craftyCharacter.x = parseInt((tileX * tileWidth) + tileWidth / 2);
        // this.craftyCharacter.y = parseInt((tileY * tileHeight) + tileHeight / 2);
        // this.craftyCharacter.character.layer = layer;

        await this.loadBoard(board);

        if (this.debugEnabled) {
            console.debug("Switching board player location set to x=[%d], y=[%d], layer=[%d]",
                this.craftyCharacter.x, this.craftyCharacter.y, this.craftyCharacter.layer);
        }

        this.loadCraftyAssets(this.loadScene);
    }

    // REFACTOR: Remove this
    public async loadCharacter(character: any) {
        if (this.debugEnabled) {
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
                character: character
            })
            .ActivationVector(
                new Crafty.polygon(character.activationPoints),
                function(hitData) {
                    character.hitOnActivation(hitData, Core.getInstance().craftyCharacter);
                },
                function(hitData) {
                    character.hitOffActivation(hitData, Core.getInstance().craftyCharacter);
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
                function(hitData) {
                    character.hitOnCollision(hitData, Core.getInstance().craftyCharacter);
                },
                function(hitData) {
                    character.hitOffCollision(hitData, Core.getInstance().craftyCharacter);
                }
            )
            .bind("Move", function(from) {
                // Move activation vector with us.
                this.activationVector.x = this.x + this.character.activationOffset.x;
                this.activationVector.y = this.y + this.character.activationOffset.y;
                this.character.animate(this.dt);
            })
            .bind("EnterFrame", function(event) {
                this.dt = event.dt / 1000;
            })
            .bind("TweenEnd", function(event) {
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
    }

    // REFACTOR: Move this
    public async loadSprite(sprite: any) {
        if (this.debugEnabled) {
            console.debug("Loading sprite=[%s]", JSON.stringify(sprite));
        }

        var asset;
        if (sprite.asset.endsWith(".sprite")) {
            var json;
            const newSprite = new Sprite(PATH_SPRITE + sprite.asset);
            if (this.enemies[newSprite.filename]) {
                json = JSON.parse(this.enemies[newSprite.filename]);
            }
            asset = sprite.enemy = await newSprite.load(json);
            sprite.collisionPoints = sprite.enemy.collisionPoints;
        }

        sprite.x = sprite.startLocation.x;
        sprite.y = sprite.startLocation.y;
        sprite.layer = sprite.startLocation.layer;

        // REFACTOR: Update this
        var isEnemy = sprite.enemy !== undefined;

        // TODO: width and height of npc must contain the collision polygon.
        if (sprite.thread) {
            sprite.thread = await this.scriptVM.open(PATH_PROGRAM + sprite.thread);
        }

        var entity;
        const bounds = engineUtil.getPolygonBounds(asset.activationPoints);
        var activationVector = Crafty.e("2D, Canvas, ActivationVector")
            .attr({
                x: sprite.x + asset.trigger.x,
                y: sprite.y + asset.trigger.y,
                w: bounds.width,
                h: bounds.height,
                sprite: sprite
            })
            .ActivationVector(
                new Crafty.polygon(asset.activationPoints),
                function(hitData) {
                    asset.hitOnActivation(hitData, entity);
                },
                function(hitData) {
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
            init: function() {
                this.requires("2D, Canvas, Tween, BaseVector");
                this.attr({ x: sprite.x, y: sprite.y, w: 50, h: 50, show: false });
                this.bind("Move", function(from) {
                    // Move activation vector with us.
                    this.activationVector.x = entity.x + asset.activationOffset.x;
                    this.activationVector.y = entity.y + asset.activationOffset.y;
                    asset.animate(this.dt);
                });
                this.bind("EnterFrame", function(event) {
                    this.dt = event.dt / 1000;
                    if (sprite.thread && asset.renderReady && Core.getInstance().craftyBoard.show) {
                        // REFACTOR: FIX ME
                        // sprite.thread.default(this);
                    }
                });
                this.bind("TweenEnd", function(event) {
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
                function(hitData) {
                    asset.hitOnCollision(hitData, entity);
                },
                function(hitData) {
                    asset.hitOffCollision(hitData, entity);
                }
            );

        const assets = await asset.loadAssets();
        this.queueCraftyAssets(assets, asset);

        return entity;
    }

    // REFACTOR: Move this
    public createVectorPolygon(id: string, points: Array<any>, layer: number, type: string, events: any) {
        const bounds = engineUtil.getPolygonBounds(points);
        var attr = {
            x: bounds.x,
            y: bounds.y,
            w: bounds.width,
            h: bounds.height,
            vectorId: id,
            layer: layer,
            vectorType: type,
            events: events
        };

        if (points[0] === points[points.length - 2] && points[1] === points[points.length - 1]) {
            // Start and end points are the same, Crafty does not like that.
            points.pop(); // Remove last y.
            points.pop(); // Remove last x.
        }

        Crafty.e(type + ", Collision, Raycastable")
            .attr(attr)
            .collision(points);
    }

    // REFACTOR: Move this
    public calculateVectorPosition(x1: number, y1: number, x2: number, y2: number) {
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

        return { x: x1, y: y1, w: width, h: height };
    }

    // REFACTOR: Move this
    public playSound(sound: string, loop: number) {
        if (this.debugEnabled) {
            console.debug("Playing sound=[%s]", sound);
        }
        Crafty.audio.play(sound, loop);
    }

}
