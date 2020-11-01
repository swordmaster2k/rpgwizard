/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import { Tileset } from "../asset/tileset";

export class TilesetRenderer {

    private _tileset: Tileset
    private _size: any;

    constructor(tileset: Tileset) {
        this._tileset = tileset;

        this._size = {
            x: this._tileset.count * this._tileset.tileWidth,
            y: this._tileset.tileHeight
        };
    }

    public render(cnv: HTMLCanvasElement): HTMLCanvasElement {
        cnv = cnv || document.createElement("canvas");
        cnv.width = this._size.x;
        cnv.height = this._size.y;

        const context: CanvasRenderingContext2D = cnv.getContext("2d");

        const offset = {
            x: 0,
            y: 0
        };

        // render each tile
        for (let i = 0; i < this._tileset.count; i++) {
            this.renderTile(context, i, offset.x, offset.y);
            offset.x += this._tileset.tileWidth;
        }

        return cnv;
    }

    public renderTile(ctx: CanvasRenderingContext2D, tileIndex: number, offsetX: number, offsetY: number) {
        const tile: ImageData = this._tileset.getTile(tileIndex);
        if (tile) {
            ctx.putImageData(tile, offsetX, offsetY);
        }
    }

}
