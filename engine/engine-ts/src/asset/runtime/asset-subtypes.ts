/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import * as Dto from "../dto/asset-subtypes";

export interface MapSprite extends Dto.MapSprite {
    // Runtime
    entity?: any;
}

export interface MapLayer {
    id: string;
    tiles: string[];
    colliders: Record<string, Dto.Collider>;
    triggers: Record<string, Dto.Trigger>;
    sprites: Record<string, MapSprite>;
    images: Record<string, Dto.MapImage>;
}
