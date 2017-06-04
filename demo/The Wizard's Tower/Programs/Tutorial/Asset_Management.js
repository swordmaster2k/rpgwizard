/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Example of how to load up sound and images files for use in the engine. 
 */

// Game assets used in this program.
var assets = {
    "audio": {
        "intro": "intro.mp3"
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
         "intro": "intro.mp3"
      },
      "images": [
         "startscreen.png"
   ]});
   
   rpgcode.endProgram();
});
