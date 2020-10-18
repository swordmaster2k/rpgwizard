/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard */

import { Core } from "../core.js";

Animation.prototype.constructor = Animation;

export function Animation(filename) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Creating Animation filename=[%s]", filename);
    }
    this.filename = filename;
}

Animation.prototype.load = async function () {
    if (Core.getInstance().debugEnabled) {
        console.debug("Loading Animation filename=[%s]", this.filename);
    }
    
    let response = await fetch(this.filename);
    response = await response.json();
    for (var property in response) {
        this[property] = response[property];
    }
    
    if (Core.getInstance().debugEnabled) {
        console.debug("Finished loading Animation filename=[%s]", this.filename);
    }
    
    return this;
};