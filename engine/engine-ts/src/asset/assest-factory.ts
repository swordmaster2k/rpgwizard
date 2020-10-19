/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import * as Asset from "./assets";

export async function build(file: string): Promise<Asset.Base> {

    const response: Response = await fetch(file);
    const json: any = await response.json();

    if (file.endsWith(".animation")) {
        return <Asset.Animation>json;
    } else if (file.endsWith(".map")) {
        return <Asset.Map>json;
    } else if (file.endsWith(".sprite")) {
        return <Asset.Sprite>json;
    } else if (file.endsWith(".tileset")) {
        return <Asset.Tileset>json;
    }

    throw new Error(`Unknown asset type=[${file}]!`);
}
