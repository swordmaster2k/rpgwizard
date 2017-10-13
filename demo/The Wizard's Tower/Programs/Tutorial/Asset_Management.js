/*
 * Example of how to load up sound and images files for use in the engine. 
 */

// Game assets used in this program.
var assets = {
    "audio": {
        "intro": "intro.wav"
    },
    "images": [
        "block.png",
        "mwin_small.png",
        "sword_profile_1_small.png",
        "startscreen.png"
    ]
};

// Load up the assets we need.
rpgcode.loadAssets(assets, function() {
   // They are ready, so do some stuff here...

   // Now unlaod the assets we don't need to save memory.
   rpgcode.removeAssets({
      "audio": {
         "intro": "intro.wav"
      },
      "images": [
         "startscreen.png"
   ]});
   
   rpgcode.endProgram();
});
