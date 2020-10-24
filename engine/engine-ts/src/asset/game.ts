/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import { Debug, Viewport } from "./dto/asset-subtypes.js";
import * as Asset from "./dto/assets.js";

export class Game implements Asset.Game {

    // Implemented
    name: string;
    viewport: Viewport;
    debug: Debug;
    version: string;

    constructor(asset: Asset.Game) {
        // Copy over values
        this.name = asset.name;
        this.viewport = asset.viewport;
        this.debug = asset.debug;
        this.version = asset.version;
    }

}
