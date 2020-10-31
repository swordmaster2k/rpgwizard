/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import { Core } from "../core.js"; // REFACTOR: Decouple this?
import * as Asset from "./dto/assets.js";
import * as Dto from "./dto/asset-subtypes.js";
import * as Runtime from "./runtime/asset-subtypes.js";
import { TilesetRenderer } from "../renderers/tileset-renderer.js";

export class Map implements Asset.Map {

    // DTO
    readonly name: string;
    readonly width: number;
    readonly height: number;
    readonly tileWidth: number;
    readonly tileHeight: number;
    readonly music: string;
    readonly tilesets: string[];
    readonly entryScript: string;
    readonly startLocation: Dto.Location;
    readonly layers: Runtime.MapLayer[];
    readonly version: string;

    // Runtime
    readonly layerCache: HTMLCanvasElement[];

    constructor(asset: Asset.Map) {
        // Copy over values
        this.name = asset.name;
        this.width = asset.width;
        this.height = asset.height;
        this.tileWidth = asset.tileWidth;
        this.tileHeight = asset.tileHeight;
        this.music = asset.music;
        this.tilesets = asset.tilesets;
        this.entryScript = asset.entryScript;
        this.startLocation = asset.startLocation;
        this.layers = asset.layers;
        this.version = asset.version;

        this.layerCache = [];
    }

    public replaceTile(x: number, y: number, layer: number, newTile: ImageData) {
        const ctx: CanvasRenderingContext2D = this.layerCache[layer].getContext("2d");
        ctx.putImageData(newTile, x * this.tileWidth, y * this.tileHeight);
    }

    public removeTile(x: number, y: number, layer: number) {
        const ctx: CanvasRenderingContext2D = this.layerCache[layer].getContext("2d");
        ctx.clearRect(x * this.tileWidth, y * this.tileHeight, this.tileWidth, this.tileHeight);
    }

    public generateLayerCache() {
        for (const layer of this.layers) {
            const cnv = this.generateLayer(layer);
            this.layerCache.push(cnv);
        }
    }

    private generateLayer(layer: Runtime.MapLayer): HTMLCanvasElement {
        const cnv: HTMLCanvasElement = document.createElement("canvas");
        cnv.width = this.width * this.tileWidth;
        cnv.height = this.height * this.tileHeight;
        const ctx: CanvasRenderingContext2D = cnv.getContext("2d");

        // Render the layer tiles
        const layerTiles: string[] = layer.tiles.slice();
        if (layerTiles.length > 0) {
            for (let y: number = 0; y < this.height; y++) {
                for (let x: number = 0; x < this.width; x++) {
                    const tile: string[] = layerTiles.shift().split(":");
                    const tileSetIndex: number = parseInt(tile[0]);
                    const tileIndex: number = parseInt(tile[1]);
                    if (tileSetIndex === -1 || tileIndex === -1) {
                        continue; // Blank tile.
                    }

                    const tileset: string = this.tilesets[tileSetIndex]; // Render tile to board canvas
                    const renderer: TilesetRenderer = new TilesetRenderer(Core.getInstance().cache.get(tileset));
                    renderer.renderTile(ctx, tileIndex, x * this.tileWidth, y * this.tileHeight);
                }
            }
        }

        return cnv;
    }

}
