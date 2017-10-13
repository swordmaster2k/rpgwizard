/* global rpgcode */

rpgcode.sendToBoard("Room1.brd.json", 12, 6);
rpgcode.replaceTile(11, 11, 0, "tileset1.tst82");
rpgcode.replaceTile(12, 11, 0, "tileset1.tst83");
rpgcode.replaceTile(11, 10, 1, "");
rpgcode.replaceTile(12, 10, 1, "");
rpgcode.clearCanvas("renderNowCanvas");
rpgcode.endProgram(); 