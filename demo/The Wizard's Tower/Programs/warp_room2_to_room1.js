/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode */

rpgcode.sendToBoard("Room1.brd.json", 12, 6);
rpgcode.replaceTile(11, 11, 0, "tileset1.tst82");
rpgcode.replaceTile(12, 11, 0, "tileset1.tst83");
rpgcode.replaceTile(11, 10, 1, "");
rpgcode.replaceTile(12, 10, 1, "");
rpgcode.clearCanvas("renderNowCanvas");
rpgcode.endProgram(); 