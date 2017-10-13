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