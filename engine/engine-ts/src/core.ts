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
import { Rpg } from "./client-api/rpg-api.js";

// Allow extending window with RPG API global
declare global {
    // eslint-disable-next-line no-unused-vars
    interface Window { rpg: any; }
}

export class Core {

    // Singleton reference
    private static _instance: Core;

    // Debug flag for engine logging
    public debugEnabled: boolean = false;

    // Composites
    private _mapController: MapController;
    private _scriptVM: ScriptVM;

    public screen: any; // REFACTOR: visibility
    private _game: Game;

    // Asset cache
    private _cache: Cache;

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
    public inProgram: boolean; // REFACTOR: visibility

    private constructor() {
        // Setup composites
        this._mapController = new MapController();
        this._scriptVM = new ScriptVM();
        window.rpg = new Rpg(this);
        this._cache = new Cache();

        this.screen = {};

        // Game project file.
        this._game = null;

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

        // Engine program states.
        this.inProgram = false;
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

    get cache(): Cache {
        return this._cache;
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

    public async main(filename: string) {
        if (this.debugEnabled) {
            console.debug("Starting engine with filename=[%s]", filename);
        }

        this._game = await Factory.build(filename) as Game;

        // Get underlying frameworks ready
        Framework.bootstrap(this._game);

        // Setup IO & UI
        this.keyboardHandler = new Keyboard();
        this.mouseHandler = new Mouse();
        this.screen = new ScreenRenderer();
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
