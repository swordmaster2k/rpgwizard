/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Example of how to set the dialog box graphics, display a message, and close the message 
 * only after the player presses the spacebar on their keyboard.
 */

// Game assets used in this program
var assets = {
    "images": [
        "mwin_small.png",
        "sword_profile_1_small.png",
    ]
};

// Load up the assets we need for this program
rpgcode.loadAssets(assets, function() {
   // Set the dialog box background image, and profile picture.
   rpgcode.setDialogGraphics("sword_profile_1_small.png", "mwin_small.png");

   // Show the message in the dialog box.
   rpgcode.showDialog("This message will only disapper when the spacebar has been pressed!");

   // Listen for the SPACE key, when it is pressed close the message window.
   rpgcode.registerKeyDown("SPACE", function () {
      rpgcode.clearDialog();
      rpgcode.endProgram();
   }, false); // False here means the keyhandler should not live after this program has ended.
});