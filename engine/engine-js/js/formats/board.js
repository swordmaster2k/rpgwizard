/*
 * Copyright (c) 2017, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard */

function Board(filename) {
    console.info("Loading Board filename=[%s]", filename);
    
    // TODO: Make the changes here that chrome suggests.
    var req = new XMLHttpRequest();
    req.open("GET", filename, false);
    req.overrideMimeType("text/plain; charset=x-user-defined");
    req.send(null);

    var board = JSON.parse(req.responseText);
    for (var property in board) {
        this[property] = board[property];
    }

    this.layerCache = [];
}

Board.prototype.setReady = function () {
    console.info("Setting ready Board name=[%s]", this.name);
};

Board.prototype.generateLayerCache = function () {
    console.info("Generating the layer cache for Board name=[%s]", this.name);
    
    this.layerCache = [];

    // Loop through layers
    var board = this;
    this.layers.forEach(function (layer) {
        var cnvLayer = document.createElement("canvas");
        cnvLayer.width = board.width * board.tileWidth;
        cnvLayer.height = board.height * board.tileHeight;
        var context = cnvLayer.getContext("2d");

        // Render the layer tiles
        var tiles = layer.tiles.slice();
        for (var y = 0; y < board.height; y++) {
            for (var x = 0; x < board.width; x++) {
                var tile = tiles.shift().split(":");
                var tileSetIndex = parseInt(tile[0]);
                var tileIndex = parseInt(tile[1]);
                
                if (tileSetIndex === -1 || tileIndex === -1) {
                    continue; // Blank tile.
                }

                var tileSet = board.tileSets[tileSetIndex];
                var renderer = new TilesetRenderer(rpgwizard.tilesets[tileSet]);

                // Render tile to board canvas
                renderer.renderTile(
                        context, tileIndex,
                        x * board.tileWidth, y * board.tileHeight);
            }
        }

        board.layerCache.push(cnvLayer);
    });
};

Board.prototype.replaceTile = function(x, y, layer, newTile) {
    var context = this.layerCache[layer].getContext("2d");
    
    context.putImageData(newTile, x * this.tileWidth, y * this.tileHeight);
};

Board.prototype.removeTile = function(x, y, layer) {
    var context = this.layerCache[layer].getContext("2d");
    
    context.clearRect(x * this.tileWidth, y * this.tileHeight, this.tileWidth, this.tileHeight);
};
  