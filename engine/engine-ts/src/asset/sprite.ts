/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import * as Asset from "./dto/assets.js";
import { Collider, Trigger } from "./dto/asset-subtypes.js";

export class Sprite implements Asset.Sprite {

    // Implemented
    name: string;
    description: string;
    animations: object;
    collider: Collider;
    trigger: Trigger;
    data: object;
    version: string;

    constructor(asset: Asset.Sprite) {
        // Copy over values
        this.name = asset.name;
        this.description = asset.description;
        this.animations = asset.animations;
        this.collider = asset.collider;
        this.trigger = asset.trigger;
        this.data = asset.data;
        this.version = asset.version;
    }

}
