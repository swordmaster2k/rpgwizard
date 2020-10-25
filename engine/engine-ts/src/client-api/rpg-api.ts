/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import { Core } from "../core.js";
import { Sprite } from "../asset/sprite.js";
import { MapController } from "../map-controller.js";

export class Rpg {

    private readonly _core: Core;
    private readonly _mapController: MapController

    constructor(core: Core) {
        this._core = core;
        this._mapController = core.mapController;
    }

    public async loadMap(map: string) {
        await this._mapController.switchMap(map, 5, 5, 1);
    }

    // REFACTOR: Stick all sprite attrs onto entity?
    public getEntity(id: string): any {
        return this._mapController.findEntity(id);
    }

    public getSprite(id: string): Sprite {
        const entity = this._mapController.findEntity(id);
        if (entity && entity.sprite) {
            return entity.sprite;
        }
        return null;
    }

    public attachControls(id: string) {
        const entity = this._mapController.findEntity(id);
        if (entity) {
            console.log(entity);
            entity.addComponent("CustomControls");
            console.log(entity);
        }
    }

}
