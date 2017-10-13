/* global rpgcode */

rpgcode.clearDialog();

var swordActive = rpgcode.getGlobal("swordActive");
if (!swordActive) {
  var assets = {"images": ["mwin_small.png", "sword_profile_1_small.png"]};
  rpgcode.loadAssets(assets, drawImage);
} else {
  rpgcode.endProgram();
}

function drawImage() {
  rpgcode.showDialog("You will need my power to defeat the wizard");
  rpgcode.endProgram();
}
