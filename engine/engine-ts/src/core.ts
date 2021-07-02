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

import { ScreenRenderer } from "./view/screen-renderer.js";

import { ScriptEvent, ScriptVM } from "./client-api/script-vm.js";

import { MapController } from "./map-controller.js";
import { Framework } from "./framework.js";
import { Rpg } from "./client-api/rpg-api.js";
import { EventType } from "./asset/dto/asset-subtypes.js";

// Allow extending window with RPG API global
declare global {
    // eslint-disable-next-line no-unused-vars
    interface Window { rpg: Rpg; }
}

export class Core {

    // Singleton reference
    private static _instance: Core;

    // Asset directory paths
    public static PATH_GAME: string = window.location.origin + "/game";
    public static PATH_ANIMATION: string = Core.PATH_GAME + "/animations/";
    public static PATH_SCRIPT: string = Core.PATH_GAME + "/scripts/";
    public static PATH_SOUND: string = Core.PATH_GAME + "/sounds/"; // REFACTOR: Name AUDIO?
    public static PATH_TEXTURE: string = Core.PATH_GAME + "/textures/";
    public static PATH_TILESET: string = Core.PATH_GAME + "/tilesets/";
    public static PATH_MAP: string = Core.PATH_GAME + "/maps/";
    public static PATH_SPRITE: string = Core.PATH_GAME + "/sprites/";

    // Debug flag for engine logging
    public debugEnabled: boolean = false;

    // Composites
    private _mapController: MapController;
    private _scriptVM: ScriptVM;

    private _screen: ScreenRenderer;
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
        this._cache = new Cache();

        // Setup in game load
        this._screen = null;

        // Game project file.
        this._game = null;

        // Used to store state when runProgram is called.
        this._keyboardHandler = {
            downHandlers: {},
            upHandlers: {}
        };
        this._keyDownHandlers = {};
        this._keyUpHandlers = {};

        // Used to store state when runProgram is called.
        this._mouseHandler = {
            mouseDownHandler: {},
            mouseUpHandler: {},
            mouseClickHandler: {},
            mouseDoubleClickHandler: {},
            mouseMoveHandler: {}
        };
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

    get screen(): ScreenRenderer {
        return this._screen;
    }

    get cache(): Cache {
        return this._cache;
    }

    get rpg(): Rpg {
        return window.rpg;
    }

    get inScript(): boolean {
        return this.scriptVM.inScript;
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

    set mouseDownHandler(handler: any) {
        this._mouseDownHandler = handler;
    }

    get mouseUpHandler(): any {
        return this._mouseUpHandler;
    }

    set mouseUpHandler(handler: any) {
        this._mouseUpHandler = handler;
    }

    get mouseClickHandler(): any {
        return this._mouseClickHandler;
    }

    set mouseClickHandler(handler: any) {
        this._mouseClickHandler = handler;
    }

    get mouseDoubleClickHandler(): any {
        return this._mouseDoubleClickHandler;
    }

    set mouseDoubleClickHandler(handler: any) {
        this._mouseDoubleClickHandler = handler;
    }

    get mouseMoveHandler(): any {
        return this._mouseMoveHandler;
    }

    set mouseMoveHandler(handler: any) {
        this._mouseMoveHandler = handler;
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
        window.rpg = new Rpg(this);

        // Run game's startup script
        try {
            console.info("Starting to run startup script...");
            const scriptEvent: ScriptEvent = new ScriptEvent(EventType.FUNCTION, this, this);
            await this._scriptVM.run("../../game/scripts/startup.js", scriptEvent);
            console.info("Finished running startup script...");
        } catch (e) {
            console.error(e);
            throw new Error("Could not run startup script!");
        }
    }

}
