let assets = {
  "programs": [
      // Default systems.
      "defaults/gui.js",
      "defaults/hud.js",
      "defaults/battle.js",
      "defaults/dialog.js",
      "defaults/weather.js",
      "defaults/inventory.js",
      "defaults/titleScreen.js"
  ]
};

// Set the game globals.
rpgcode.setGlobal("swordactive", false);

rpgcode.loadAssets(assets, function() {
   rpgcode.runProgram("TalkingSword_room1_1.js");
});
