/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import * as Asset from "./dto/assets.js";
import { Animation } from "./animation.js";
import { Game } from "./game.js";
import { Map } from "./map.js";
import { Sprite } from "./sprite.js";
import { Tileset } from "./tileset.js";

export async function build(file: string): Promise<Asset.Base> {

    const response: Response = await fetch(file);
    const json: any = await response.json();

    if (file.endsWith(".animation")) {

        return new Animation(<Asset.Animation>json);

    } else if (file.endsWith(".game")) {

        return new Game(<Asset.Game>json);

    } else if (file.endsWith(".map")) {

        return new Map(<Asset.Map>json);

    } else if (file.endsWith(".sprite")) {

        // REFACTOR
        const dto: Asset.Sprite = <Asset.Sprite>json;
        const sprite = new Sprite(dto);
        const assets = await sprite.loadAssets();
        await loadRawAssets(assets);
        sprite.spriteGraphics.active = sprite.spriteGraphics.south;
        sprite.getActiveFrame();

        return sprite;

    } else if (file.endsWith(".tileset")) {

        const dto: Asset.Tileset = <Asset.Tileset>json;
        await loadRawAssets({ images: [dto.image] });
        return new Tileset(dto);

    }

    throw new Error(`Unknown asset type=[${file}]!`);
}

// REFACTOR
export async function loadRawAssets(assets: any): Promise<void> {

    // Remove any duplicates.
    assets.images = assets.images.filter((it, i, ar) => ar.indexOf(it) === i);

    // Remove already loaded assets.
    var images = [];
    assets.images.forEach(function (image) {
        if (!Crafty.assets[Crafty.__paths.images + image]) {
            images.push(image);
        }
    });

    var audio = {};
    for (var property in assets.audio) {
        if (!Crafty.assets[Crafty.__paths.audio + property]) {
            audio[property] = assets.audio[property];
        }
    }

    assets.images = images;
    assets.audio = audio;

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
