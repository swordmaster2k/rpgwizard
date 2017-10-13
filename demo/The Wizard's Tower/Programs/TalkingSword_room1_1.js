/* global rpgcode */

rpgcode.clearDialog();

var swordActive = rpgcode.getGlobal("swordActive");
if (swordActive) {
  rpgcode.sendToBoard("Room2.board", 5.5, 17);
} else {
  rpgcode.showDialog("Hey where are you going. Come back here.");
}

rpgcode.endProgram();
