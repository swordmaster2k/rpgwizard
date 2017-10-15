/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global PATH_BITMAP, PATH_MEDIA, PATH_PROGRAM, PATH_BOARD, PATH_CHARACTER, PATH_NPC, jailed, rpgcode, PATH_TILESET, PATH_ENEMY, Crafty */

var engineUtil = new EngineUtil();

function EngineUtil() {
    
}

/**
 * Utility function for getting accurate timestamps across browsers.
 * 
 * @returns {Number}
 */
EngineUtil.prototype.timestamp = function () {
    return window.performance && window.performance.now ? window.performance.now() : new Date().getTime();
};

EngineUtil.prototype.hideProgress = function () {
    document.getElementById("progress").style.visibility = "hidden";
};

EngineUtil.prototype.showProgress = function (percentage) {
    document.getElementById("bar").style.width = percentage + '%';
    document.getElementById("progress").style.visibility = "visible";
};

// TODO: Make this a utility function. When there is a Craftyjs compiler
// it will do it instead.
EngineUtil.prototype.prependPath = function (prepend, items) {
    var len = items.length;
    for (var i = 0; i < len; i++) {
        items[i] = prepend.concat(items[i]);
    }
};

EngineUtil.prototype.getBodyWidth = function () {
    return Math.max(
            document.documentElement.clientWidth,
            document.body.scrollWidth,
            document.documentElement.scrollWidth,
            document.body.offsetWidth,
            document.documentElement.offsetWidth
            );
};

EngineUtil.prototype.getBodyHeight = function () {
    return Math.max(
            document.documentElement.clientHeight,
            document.body.scrollHeight,
            document.documentElement.scrollHeight,
            document.body.offsetHeight,
            document.documentElement.offsetHeight
            );
};
