/*
 * Example of how to draw text to the screen on the default canvas.
 */

// Move to (270, 300) and draw the text "PRESS SPACE" to the screen
rpgcode.drawText(270, 300, "PRESS SPACE");
rpgcode.renderNow();

// End the program when the spacebar is pressed
rpgcode.registerKeyDown("SPACE", function () {
   rpgcode.clearCanvas();
   rpgcode.endProgram();
}, false); // False here means the keyhandler should not live after this program has ended
