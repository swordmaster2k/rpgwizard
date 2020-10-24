/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import { Framework } from "../framework.js";

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
        await Framework.loadAssets(assets);
        sprite.spriteGraphics.active = sprite.spriteGraphics.south;
        sprite.getActiveFrame();

        return sprite;

    } else if (file.endsWith(".tileset")) {

        const dto: Asset.Tileset = <Asset.Tileset>json;
        await Framework.loadAssets({ images: [dto.image], audio: {} });
        return new Tileset(dto);

    }

    throw new Error(`Unknown asset type=[${file}]!`);
}
