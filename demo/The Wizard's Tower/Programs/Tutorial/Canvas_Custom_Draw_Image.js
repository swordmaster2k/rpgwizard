/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Example of how to create a canvas and draw an image to it.
 */

// Game assets under in this program
var assets = {
    "images": [
        "startscreen.png"
    ]
};

var customCanvas = "mycanvas";

// Load up the assets we need
rpgcode.loadAssets(assets, function() {
   // Create a new custom canvas with dimensions 640x480px
   // with the ID stored in customCanvas.
   rpgcode.createCanvas(640, 480, customCanvas);
   
   // Set the image onto the custom canvas starting at (0, 0)
   // and extending up to (640, 480)
   rpgcode.setImage("startscreen.png", 0, 0, 640, 480, customCanvas);

   // Show the contents of the custom canvas
   rpgcode.renderNow(customCanvas);

   // End the program when the spacebar is pressed
   rpgcode.registerKeyDown("SPACE", function () {
      rpgcode.clearCanvas(customCanvas);
      rpgcode.endProgram();
   }, false); // False here means the keyhandler should not live after this program has ended
});