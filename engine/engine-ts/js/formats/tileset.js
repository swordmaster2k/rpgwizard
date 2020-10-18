/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global PATH_BITMAP, rpgwizard, Crafty */

function TileSet(filename) {
    if (rpgwizard.debugEnabled) {
        console.debug("Creating Tileset filename=[%s]", filename);
    }
    this.filename = filename;
}

TileSet.prototype.load = async function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Tileset filename=[%s]", this.filename);
    }

    let response = await fetch(this.filename);
    response = await response.json();
    for (var property in response) {
        this[property] = response[property];
    }

    if (rpgwizard.debugEnabled) {
        console.debug("Finished loading Tileset filename=[%s]", this.filename);
    }

    return this;
};

TileSet.prototype.setReady = function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Setting ready TileSet name=[%s]", this.name);
    }
    
    this.img = Crafty.assets[Crafty.__paths.images + this.image];
    this.tileRows = Math.floor(this.img.height / this.tileHeight);
    this.tileColumns = Math.floor(this.img.width / this.tileWidth);
    this.count = this.tileRows * this.tileColumns;
    
    this._prepareTiles();
};

TileSet.prototype._prepareTiles = function () {
    const canvas = document.createElement("canvas");
    canvas.width = this.img.width;
    canvas.height = this.img.height;
    const ctx = canvas.getContext("2d");
    ctx.drawImage(this.img, 0, 0);
    
    this.tiles = [];
    for (var i = 0; i < this.count; i++) {
        // Converted 1D index to 2D cooridnates.
        var x = i % this.tileColumns;
        var y = Math.floor(i / this.tileColumns);
        var tile = ctx.getImageData(
                x * this.tileWidth, y * this.tileHeight,
                this.tileWidth, this.tileHeight);
        this.tiles.push(tile);
    }
};

TileSet.prototype.getTile = function (index) {
    return this.tiles[index];
};