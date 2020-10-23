/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard */

import { Core } from "../core.js";

export function Board(filename) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Creating Board filename=[%s]", filename);
    }
    this.filename = filename;
    this.layerCache = [];
}

Board.prototype.load = async function (json) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Loading Board filename=[%s]", this.filename);
    }
    
    if (!json) {
        let response = await fetch(this.filename);
        json = await response.json();
        Core.getInstance().cache.put(this.filename, JSON.stringify(json));
    }
    
    for (var property in json) {
        this[property] = json[property];
    }
    
    if (Core.getInstance().debugEnabled) {
        console.debug("Finished loading Board filename=[%s]", this.filename);
    }
    
    return this;
};

Board.prototype.setReady = function () {
    if (Core.getInstance().debugEnabled) {
        console.debug("Setting ready Board name=[%s]", this.name);
    }
};

Board.prototype.addLayer = function (layer) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Adding layer dynamically to Board name=[%s]", this.name);
    }
    this._addCachedLayer(layer);
    this.layers.push(layer);
};

Board.prototype.removeLayer = function (index) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Removing layer dynamically from Board name=[%s]", this.name);
    }
    if (this.layers.length > 1) { // Can't remove the last layer
        this.layers.splice(index, 1);
        this.layerCache.splice(index, 1);
    }
};

Board.prototype.generateLayerCache = function () {
    if (Core.getInstance().debugEnabled) {
        console.debug("Generating the layer cache for Board name=[%s]", this.name);
    }
    if (Core.getInstance().layerCache[this.filename]) {
        this.layerCache = Core.getInstance().layerCache[this.filename];
    } else {
        this.layerCache = [];
        this.layers.forEach(function (layer) {
            this._addCachedLayer(layer);
        }.bind(this));
        Core.getInstance().layerCache[this.filename] = this.layerCache;
    }
};

Board.prototype._addCachedLayer = function (layer) {
    var cnvLayer = document.createElement("canvas");
    cnvLayer.width = this.width * this.tileWidth;
    cnvLayer.height = this.height * this.tileHeight;
    var context = cnvLayer.getContext("2d");
    // Render the layer tiles
    var tiles = layer.tiles.slice();
    if (tiles.length > 0) {
        for (var y = 0; y < this.height; y++) {
            for (var x = 0; x < this.width; x++) {
                var tile = tiles.shift().split(":");
                var tileSetIndex = parseInt(tile[0]);
                var tileIndex = parseInt(tile[1]);
                if (tileSetIndex === -1 || tileIndex === -1) {
                    continue; // Blank tile.
                }
                var tileset = this.tilesets[tileSetIndex]; // Render tile to board canvas
                var renderer = new TilesetRenderer(Core.getInstance().cache.get(tileset));
                renderer.renderTile(context, tileIndex, x * this.tileWidth, y * this.tileHeight);
            }
        }
    }
    this.layerCache.push(cnvLayer);
};

Board.prototype.replaceTile = function (x, y, layer, newTile) {
    var context = this.layerCache[layer].getContext("2d");
    context.putImageData(newTile, x * this.tileWidth, y * this.tileHeight);
};

Board.prototype.removeTile = function (x, y, layer) {
    var context = this.layerCache[layer].getContext("2d");
    context.clearRect(x * this.tileWidth, y * this.tileHeight, this.tileWidth, this.tileHeight);
};
  