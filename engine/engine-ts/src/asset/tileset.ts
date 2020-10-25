/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import { Framework } from "../framework.js";

import * as Asset from "./dto/assets.js";

export class Tileset implements Asset.Tileset {

    // Implemented
    readonly tileWidth: number;
    readonly tileHeight: number;
    readonly image: string;
    readonly tileData: object;
    readonly version: string;

    // Specific
    readonly imageBitmap: ImageBitmap;
    readonly rows: number;
    readonly columns: number;
    readonly count: number;
    readonly tiles: Array<ImageData>;

    constructor(asset: Asset.Tileset) {
        // Copy over values
        this.tileWidth = asset.tileWidth;
        this.tileHeight = asset.tileHeight;
        this.image = asset.image;
        this.tileData = asset.tileData;
        this.version = asset.version;

        this.imageBitmap = Framework.getImage(this.image);
        this.rows = Math.floor(this.imageBitmap.height / this.tileHeight);
        this.columns = Math.floor(this.imageBitmap.width / this.tileWidth);
        this.count = this.rows * this.columns;
        this.tiles = [];

        this.prepareTiles();
    }

    public getTile(index: number): ImageData {
        return this.tiles[index];
    }

    private prepareTiles(): void {
        const canvas: HTMLCanvasElement = document.createElement("canvas");
        canvas.width = this.imageBitmap.width;
        canvas.height = this.imageBitmap.height;

        const ctx: CanvasRenderingContext2D = canvas.getContext("2d");
        ctx.drawImage(this.imageBitmap, 0, 0);

        for (let i: number = 0; i < this.count; i++) {
            // Converted 1D index to 2D cooridnates.
            const x: number = i % this.columns;
            const y: number = Math.floor(i / this.columns);
            const tile: ImageData = ctx.getImageData(
                x * this.tileWidth,
                y * this.tileHeight,
                this.tileWidth,
                this.tileHeight
            );
            this.tiles.push(tile);
        }
    }

}
