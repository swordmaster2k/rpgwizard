/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import { Cache } from "./asset/asset-cache.js";
import * as Factory from "./asset/asset-factory.js";

import { Game } from "./asset/game.js";

import { ScreenRenderer } from "./renderers/screen-renderer.js";

import { ScriptVM } from "./client-api/script-vm.js";

import { MapController } from "./map-controller.js";
import { Framework } from "./framework.js";
import { Rpg } from "./client-api/rpg-api.js";

// Allow extending window with RPG API global
declare global {
    // eslint-disable-next-line no-unused-vars
    interface Window { rpg: any; }
}

export class Core {

    // Singleton reference
    private static _instance: Core;

    // Asset directory paths
    public static PATH_PROJECT: string = window.location.origin + "/game";
    public static PATH_ANIMATION: string = Core.PATH_PROJECT + "/animations/";
    public static PATH_PROGRAM: string = Core.PATH_PROJECT + "/scripts/";
    public static PATH_MEDIA: string = Core.PATH_PROJECT + "/sounds/";
    public static PATH_BITMAP: string = Core.PATH_PROJECT + "/textures/";
    public static PATH_TILESET: string = Core.PATH_PROJECT + "/tilesets/";
    public static PATH_BOARD: string = Core.PATH_PROJECT + "/maps/";
    public static PATH_SPRITE: string = Core.PATH_PROJECT + "/sprites/";

    // Debug flag for engine logging
    public debugEnabled: boolean = false;

    // Composites
    private _mapController: MapController;
    private _scriptVM: ScriptVM;

    private _screen: any;
    private _game: Game;

    // Asset cache
    private _cache: Cache;

    // Keyboard handlers
    private _keyboardHandler: any;
    private _keyDownHandlers: any;
    private _keyUpHandlers: any;

    // Mouse handlers
    private _mouseHandler: any;
    private _mouseDownHandler: any;
    private _mouseUpHandler: any;
    private _mouseClickHandler: any;
    private _mouseDoubleClickHandler: any;
    private _mouseMoveHandler: any;

    public _inProgram: boolean;

    private constructor() {
        // Setup composites
        this._mapController = new MapController();
        this._scriptVM = new ScriptVM();
        window.rpg = new Rpg(this);
        this._cache = new Cache();

        this._screen = {};

        // Game project file.
        this._game = null;

        // Used to store state when runProgram is called.
        this._keyboardHandler = {};
        this._keyDownHandlers = {};
        this._keyUpHandlers = {};

        // Used to store state when runProgram is called.
        this._mouseHandler = {};
        this._mouseDownHandler = {};
        this._mouseUpHandler = {};
        this._mouseClickHandler = {};
        this._mouseDoubleClickHandler = {};
        this._mouseMoveHandler = {};

        // Engine program states.
        this._inProgram = false;
    }

    public static getInstance(): Core {
        if (!Core._instance) {
            Core._instance = new Core();
        }

        return Core._instance;
    }

    get game(): Game {
        return this._game;
    }

    get screen(): any {
        return this._screen;
    }

    get cache(): Cache {
        return this._cache;
    }

    get inProgram(): boolean {
        return this._inProgram;
    }

    get mapEntity(): any {
        return this._mapController.mapEntity;
    }

    get scriptVM(): ScriptVM {
        return this._scriptVM;
    }

    get mapController(): MapController {
        return this._mapController;
    }

    get keyboardHandler(): any {
        return this._keyboardHandler;
    }

    get keyDownHandlers(): any {
        return this._keyDownHandlers;
    }

    get keyUpHandlers(): any {
        return this._keyUpHandlers;
    }

    get mouseHandler(): any {
        return this._mouseHandler;
    }

    get mouseDownHandler(): any {
        return this._mouseDownHandler;
    }

    get mouseUpHandler(): any {
        return this._mouseUpHandler;
    }

    get mouseClickHandler(): any {
        return this._mouseClickHandler;
    }

    get mouseDoubleClickHandler(): any {
        return this._mouseDoubleClickHandler;
    }

    get mouseMoveHandler(): any {
        return this._mouseMoveHandler;
    }

    public async main(filename: string) {
        if (this.debugEnabled) {
            console.debug("Starting engine with filename=[%s]", filename);
        }

        this._game = await Factory.build(filename) as Game;

        // Get underlying frameworks ready
        Framework.bootstrap(this._game);

        // Setup IO & UI
        this._screen = new ScreenRenderer();
        Framework.createUI(this);

        // Run game's startup script
        try {
            console.info("Starting to run startup script...");
            await this._scriptVM.run("../../game/scripts/new-startup.js", this);
            console.info("Finished running startup script...");
        } catch (e) {
            console.error(e);
            throw new Error("Could not run startup script!");
        }
    }

}
