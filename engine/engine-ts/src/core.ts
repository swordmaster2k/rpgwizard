/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/* global PATH_BITMAP, PATH_MEDIA, PATH_PROGRAM, PATH_BOARD, PATH_TILESET, engineUtil, Promise, requirejs, PATH_SPRITE */

import { Cache } from "./asset/asset-cache.js";
import * as Factory from "./asset/asset-factory.js";

import { Game } from "./asset/game.js";

import { ScreenRenderer } from "./renderers/screenRenderer.js";

import { RPGcode } from "./rpgcode/rpgcode.js";
import { ScriptVM } from "./client-api/script-vm.js";

import { Keyboard } from "./io/keyboard.js";
import { Mouse } from "./io/mouse.js";
import { MapController } from "./map-controller.js";
import { Framework } from "./framework.js";

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
    private static _instance: Core;

    // Composites
    private _mapController: MapController;
    private _scriptVM: ScriptVM;

    // REFACTOR: Move this
    private dt: number; // Craftyjs time step since last frame.

    public screen: any; // REFACTOR: visibility
    private _game: Game;
    private assetsToLoad: any;
    private waitingEntities: Array<any>;

    // REFACTOR: Move this
    private craftyBoard: any;
    private craftyCharacter: any;
    private craftyRPGcodeScreen: any;

    private tileSize: number;

    // Asset cache
    private _cache: Cache;
    private animations: any;

    // REFACTOR: Remove this
    private activePrograms: number;
    private endProgramCallback: any;

    // REFACTOR: Move this
    private keyboardHandler: any;
    private keyDownHandlers: any;
    private keyUpHandlers: any;

    // REFACTOR: Move this
    public mouseHandler: any; // REFACTOR: visibility
    public mouseDownHandler: any; // REFACTOR: visibility
    public mouseUpHandler: any; // REFACTOR: visibility
    public mouseClickHandler: any; // REFACTOR: visibility
    public mouseDoubleClickHandler: any; // REFACTOR: visibility
    public mouseMoveHandler: any; // REFACTOR: visibility

    // REFACTOR: Move this
    private controlEnabled: boolean;
    private movementSpeed: number;

    private firstScene: boolean;

    // REFACTOR: Move this
    public inProgram: boolean; // REFACTOR: visibility
    private currentProgram: any;
    private haveRunStartup: boolean;

    // REFACTOR: Move this
    private lastBackgroundMusic: string;

    // REFACTOR: Update this
    private showVectors: boolean;
    public debugEnabled: boolean;

    private constructor() {
        // Setup composites
        this._mapController = new MapController();
        this._scriptVM = new ScriptVM();
        window.rpgcode = new RPGcode();

        this.dt = 0; // Craftyjs time step since last frame.
        this.screen = {};

        // Game project file.
        this._game = null;

        // Assets to load.
        this.assetsToLoad = { images: [], audio: {} };
        this.waitingEntities = []; // The entities to setReady at assets are loaded.

        // Game entities.
        this.craftyCharacter = {};
        this.craftyRPGcodeScreen = {};

        // Default tile size
        this.tileSize = 32;

        // In-Memory asset Cache
        this._cache = new Cache();
        this.animations = {};
        // this.layerCache = {};

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
    }

    public static getInstance(): Core {
        if (!Core._instance) {
            Core._instance = new Core();
        }

        return Core._instance;
    }

    get cache(): Cache {
        return this._cache;
    }

    // REFACTOR
    get map(): any {
        return this._mapController.map;
    }

    get scriptVM(): ScriptVM {
        return this._scriptVM;
    }

    public async setup(filename: string) {
        if (this.debugEnabled) {
            console.debug("Starting engine with filename=[%s]", filename);
        }

        this._game = await Factory.build(filename) as Game;

        // Check if we should draw debugging vectors
        this.showVectors = this._game.debug.showColliders;

        // Get underlying frameworks ready
        Framework.bootstrap(this._game);

        // Setup run time keys.
        this.keyboardHandler = new Keyboard();

        // Setup the mouse handler.
        this.mouseHandler = new Mouse();

        // Setup the drawing canvas (game screen).
        this.screen = new ScreenRenderer();

        // Disable controls until everything is ready.
        this.controlEnabled = false;

        // Create the UI layer for RPGcode.
        Framework.createUI(this);

        // Run game's startup script
        try {
            console.info("Starting to run startup script...");
            await this._scriptVM.run("../../game/scripts/new-startup.js", this);
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
                if (Core._instance.debugEnabled) {
                    console.debug(JSON.stringify(e));
                }
            } else {
                console.error(JSON.stringify(e));
            }
        } else {
            engineUtil.hideProgress();

            // REFACTOR: Update this
            // Run the startup program before the game logic loop.
            // if (Core._instance.project.startupProgram && Core._instance.firstScene && !Core._instance.haveRunStartup) {
            //     await this.scriptVM.run(PATH_PROGRAM + this.project.startupProgram, Core._instance);
            //     Core._instance.haveRunStartup = true;
            //     Core._instance.inProgram = false;
            //     Core._instance.currentProgram = null;
            //     Core._instance.controlEnabled = true;

            //     await Core._instance.startScene();
            // } else {
                await Core._instance.startScene();
            // }
        }
    }

    // REFACTOR: Move this
    public async startScene() {
        if (this.firstScene) {
            this.firstScene = false;
        }

        Framework.setViewport(0, 0);
        // var width = Math.floor((this._mapController.map.width * this._mapController.map.map.tileWidth) * Crafty.viewport._scale);
        // var height = Math.floor((this._mapController.map.height * this._mapController.map.map.tileHeight) * Crafty.viewport._scale);
        // if (width > Crafty.viewport._width || height > Crafty.viewport._height) {
        //     Crafty.viewport.follow(this.craftyCharacter, 0, 0);
        // }

        this._mapController.map.show = true;
        if (this._mapController.map.map.backgroundMusic !== this.lastBackgroundMusic) {
            Framework.stopAudio();
            Framework.playAudio(this._mapController.map.map.backgroundMusic, -1);
        }
        Framework.trigger("Invalidate");

        if (this._mapController.map.map.firstRunProgram) {
            await this._scriptVM.run(PATH_PROGRAM + this._mapController.map.map.firstRunProgram, Core._instance);
        } else {
            this.controlEnabled = true;
            Framework.trigger("EnterFrame", {});
        }
    }

    // REFACTOR: Move this
    public async switchMap(boardName: string, tileX: number, tileY: number, layer: number) {
        await this._mapController.switchMap(boardName, tileX, tileY, layer);
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

}
