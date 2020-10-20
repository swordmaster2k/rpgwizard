/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import * as Asset from "./dto/assets.js";
import { Animation } from "./animation.js";
import { Map } from "./map.js";
import { Sprite } from "./sprite.js";
import { Tileset } from "./tileset.js";

export async function build(file: string): Promise<Asset.Base> {

    const response: Response = await fetch(file);
    const json: any = await response.json();

    if (file.endsWith(".animation")) {

        return new Animation(<Asset.Animation>json);

    } else if (file.endsWith(".map")) {

        return new Map(<Asset.Map>json);

    } else if (file.endsWith(".sprite")) {

        return new Sprite(<Asset.Sprite>json);

    } else if (file.endsWith(".tileset")) {

        const dto: Asset.Tileset = <Asset.Tileset>json;
        await loadRawAssets({ images: [dto.image] });
        return new Tileset(dto);

    }

    throw new Error(`Unknown asset type=[${file}]!`);
}

export async function loadRawAssets(assets: any): Promise<void> {
    return new Promise<void>((resolve: any, reject: any) => {
        Crafty.load(assets,
            () => {
                // Assets have been loaded
                resolve();
            },
            (e: any) => {
                // TODO: progress
            },
            (e: any) => {
                // TODO: error
                reject(e);
            }
        );
    });
}
