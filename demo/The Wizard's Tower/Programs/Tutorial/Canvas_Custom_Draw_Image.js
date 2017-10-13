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