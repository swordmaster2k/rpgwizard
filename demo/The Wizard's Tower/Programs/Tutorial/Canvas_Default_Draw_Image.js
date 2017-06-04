/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Example of how to set an image on the default canvas and
 * render it to the screen.
 */

// Game assets under in this program
var assets = {
    "images": [
        "startscreen.png"
    ]
};

// Load up the assets we need
rpgcode.loadAssets(assets, function() {
   // Set the image onto the default canvas starting at (0, 0)
   // and extending up to (640, 480)
   rpgcode.setImage("startscreen.png", 0, 0, 640, 480);

   // Show the contents of the default canvas
   rpgcode.renderNow();

   // End the program when the spacebar is pressed
   rpgcode.registerKeyDown("SPACE", function () {
      rpgcode.clearCanvas();
      rpgcode.endProgram();
   }, false); // False here means the keyhandler should not live after this program has ended
});