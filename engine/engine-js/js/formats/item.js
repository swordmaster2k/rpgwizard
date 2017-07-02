/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global PATH_ITEM */

Item.prototype.constructor = Item;

function Item(filename) {
    console.info("Loading Item filename=[%s]", filename);
    
    // TODO: Make the changes here that chrome suggests.
    var req = new XMLHttpRequest();
    req.open("GET", filename, false);
    req.overrideMimeType("text/plain; charset=x-user-defined");
    req.send(null);

    var item = JSON.parse(req.responseText);
    item.fileName = filename.replace(PATH_ITEM, "");
    for (var property in item) {
        this[property] = item[property];
    }
}

Item.prototype.load = function () {
    console.info("Loading Item name=[%s]", this.name);

    // Return the assets that need to be loaded.
    return {"images": [this.icon]};
};

Item.prototype.setReady = function () {
    console.info("Setting ready Item name=[%s]", this.name);
};
