// The assets we want the engine to load
let assets = {
   "images": [
     "drawing_images/herostance.png" 
   ]
};

rpgcode.loadAssets(assets, function() {
   // Call your drawing code
   rpgcode.drawImage("drawing_images/herostance.png", 150, 150, 223, 374, 1.57);
   rpgcode.drawImagePart("drawing_images/herostance.png", 0, 0, 223, 150, 10, 10, 223, 150, 1.57);
   rpgcode.renderNow();

   //rpgcode.clearCanvas();
});