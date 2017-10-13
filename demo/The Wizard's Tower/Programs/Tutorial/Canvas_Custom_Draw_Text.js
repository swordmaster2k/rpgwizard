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