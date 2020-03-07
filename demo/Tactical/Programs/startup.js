/**
 * Initial startup program which declares all game wide globals, and ensures
 * that the required assets are loaded up before the game starts.
 */

// Controls whether debugging information should be displayed
rpgcode.setGlobal("debug", true);

rpgcode.setGlobal("screenWidth", rpgcode.getViewport().width);
rpgcode.setGlobal("screenHeight", rpgcode.getViewport().height);
rpgcode.setGlobal("fullCellSize", 24);
rpgcode.setGlobal("pointTravelTime", 150);
rpgcode.setGlobal("minRange", Infinity);
rpgcode.setGlobal("maxRange", -Infinity);
rpgcode.setGlobal("minCover", -1);
rpgcode.setGlobal("maxCover", 3);
rpgcode.setGlobal("minHp", 1);
rpgcode.setGlobal("maxHp", 10);

// How effective are units against each other
rpgcode.setGlobal("effectiveness", 
   {
      "infantry": {
         "infantry": 0.6
      }
   }
);

// Assets required
let assets = {
   "audio": {
      "mission_0": "mission_0.mp3"
   },
   "images": [
      "avatars/tutor.jpg",
      "avatars/enemy.jpg"
   ],
   "programs": [
      // Default systems
      "defaults/gui.js",
      "defaults/hud.js",
      "defaults/battle.js",
      "defaults/dialog.js",
      "defaults/weather.js",
      "defaults/inventory.js",
      "defaults/titleScreen.js",
      // Custom systems
      "lib/PathFinding.js",
      "modules/target_selection.js",
      "modules/path_finder.js",
      "modules/movement.js",
      "modules/ranger.js",
      "modules/attack.js",
      "common.js",
      "player.js",
      "state.js",
      "ai.js",
      "ui.js",
      "overlay.js",
      // Mission systems
      "missions/mission_bootstrap.js"
   ]
};

rpgcode.loadAssets(assets, async function() {
   rpgcode.log("Loaded assets");
   rpgcode.endProgram();
});