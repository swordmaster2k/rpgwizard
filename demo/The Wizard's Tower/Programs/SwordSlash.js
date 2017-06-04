/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode */
var direction = rpgcode.getCharacterDirection();
var location = rpgcode.getCharacterLocation();
var vector = {};
var range = 40;

var callback = function () {
    var hits = rpgcode.fireRaycast({_x: location.x, _y: location.y}, vector, 320);
    hits.forEach(function (hit) {
        rpgcode.destroyItem(hit.obj.getId());
    });
    rpgcode.endProgram();
};

switch (rpgcode.getCharacterDirection()) {
    case "NORTH":
        vector = {x: 0, y: -1};
        rpgcode.animateCharacter("Hero", "ATTACK_NORTH", callback);
        break;
    case "SOUTH":
        vector = {x: 0, y: 1};
        rpgcode.animateCharacter("Hero", "ATTACK_SOUTH", callback);
        break;
    case "EAST":
        vector = {x: 1, y: 0};
        range = 80; // East Animation is wider.
        rpgcode.animateCharacter("Hero", "ATTACK_EAST", callback);
        break;
    case "WEST":
        vector = {x: -1, y: 0};
        range = 80; // West Animation is wider.
        rpgcode.animateCharacter("Hero", "ATTACK_WEST", callback);
        break;
}
