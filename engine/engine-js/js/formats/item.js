/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global PATH_ITEM, rpgwizard */

Item.prototype.constructor = Item;

function Item(filename) {
    if (rpgwizard.debugEnabled) {
        console.debug("Creating Item filename=[%s]", filename);
    }
    this.filename = filename;
}

Item.prototype.load = async function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Creating Item filename=[%s]", this.filename);
    }
    
    let response = await fetch(this.filename);
    response = await response.json();
    response.fileName = this.filename.replace(PATH_ITEM, "");
    for (var property in response) {
        this[property] = response[property];
    }
    
    if (rpgwizard.debugEnabled) {
        console.debug("Creating loading Item filename=[%s]", this.filename);
    }
    
    return this;
};

Item.prototype.loadAssets = function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Item name=[%s]", this.name);
    }

    // Return the assets that need to be loaded.
    return {"images": [this.icon]};
};

Item.prototype.setReady = function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Setting ready Item name=[%s]", this.name);
    }
};
