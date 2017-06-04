/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Example of how to create a custom canvas and draw text to it. 
 */

var customCanvas = "mycanvas";

// Create the canvas to draw onto.
rpgcode.createCanvas(640, 480, customCanvas);

// Move to (270, 300) and draw the text "PRESS SPACE" to the screen
rpgcode.drawText(270, 300, "PRESS SPACE", customCanvas);
rpgcode.renderNow(customCanvas);

// End the program when the spacebar is pressed
rpgcode.registerKeyDown("SPACE", function () {
   rpgcode.clearCanvas(customCanvas);
   rpgcode.endProgram();
}, false); // False here means the keyhandler should not live after this program has ended