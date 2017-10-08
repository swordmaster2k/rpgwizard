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
        console.debug("Loading Tileset filename=[%s]", filename);
    }
    
    // TODO: Make the changes here that chrome suggests.
    var req = new XMLHttpRequest();
    req.open("GET", filename, false);
    req.overrideMimeType("text/plain; charset=x-user-defined");
    req.send(null);

    var tileSet = JSON.parse(req.responseText);
    for (var property in tileSet) {
        this[property] = tileSet[property];
    }
}

TileSet.prototype.setReady = function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Setting ready TileSet name=[%s]", this.name);
    }
    
    this.img = Crafty.assets[Crafty.__paths.images + this.image];

    this.tileRows = Math.floor(this.img.height / this.tileHeight);
    this.tileColumns = Math.floor(this.img.width / this.tileWidth);
    this.count = this.tileRows * this.tileColumns;

    this.canvas = document.createElement("canvas");
    this.canvas.width = this.img.width;
    this.canvas.height = this.img.height;

    this.ctx = this.canvas.getContext("2d");
    this.ctx.drawImage(this.img, 0, 0);
};

TileSet.prototype.getTile = function (index) {
    // Converted 1D index to 2D cooridnates.
    var x = index % this.tileColumns;
    var y = Math.floor(index / this.tileColumns);

    var tile = this.ctx.getImageData(
            x * this.tileWidth, y * this.tileHeight,
            this.tileWidth, this.tileHeight);

    return tile;
};