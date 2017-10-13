/* global rpgcode */

rpgcode.clearDialog();

var swordActive = rpgcode.getGlobal("swordActive");
if (!swordActive) {
  rpgcode.showDialog("Pssst, Over here");
}

rpgcode.endProgram();
