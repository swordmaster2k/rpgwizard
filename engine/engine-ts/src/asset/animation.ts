/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import * as Asset from "./dto/assets.js";
import { SpriteSheet } from "./dto/asset-subtypes.js";

export class Animation implements Asset.Animation {

    // Implemented
    readonly width: number;
    readonly height: number;
    readonly frameRate: number;
    readonly soundEffect: string;
    readonly spriteSheet: SpriteSheet;
    readonly version: string;

    constructor(asset: Asset.Animation) {
        // Copy over values
        this.width = asset.width;
        this.height = asset.height;
        this.frameRate = asset.frameRate;
        this.soundEffect = asset.soundEffect;
        this.spriteSheet = asset.spriteSheet;
        this.version = asset.version;
    }

}
