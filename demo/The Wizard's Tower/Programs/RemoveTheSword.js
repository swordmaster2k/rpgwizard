/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode */

// Removes the sword from the board if the player already has it
var swordActive = rpgcode.getGlobal("swordActive");
if (swordActive) {
  rpgcode.replaceTile(12, 12, 1, "tileset1.tst82");
  rpgcode.replaceTile(13, 12, 1, "tileset1.tst83");
  rpgcode.replaceTile(12, 11, 2, "");
  rpgcode.replaceTile(13, 11, 2, "");
}

rpgcode.endProgram();