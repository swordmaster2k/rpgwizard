var assets = {
  "programs": [
      "lib/ARPG.js",
      "lib/Lighting.js"
  ],
   "audio": {
      "item": "item.wav",
      "sword": "sword.wav",
      "hurtCharacter": "hurt_character.wav",
      "hurtEnemy": "hurt_enemy.wav"
   },
   "images": ["objects.png"]
};

rpgcode.loadAssets(assets, function() {
   // Setup the Lighting library
   Lighting.setup();
   rpgcode.addRunTimeProgram("lib/Torch.js");

   // Register a key down listener for the sword slash program.
   rpgcode.registerKeyDown("SPACE", function() {
      rpgcode.runProgram("examples/SwordSlash.js"); 
   }, true);

   // Increase the character's movement speed by 25% times.
   rpgcode.setCharacterSpeed("Hero", 1.25);

   // Create a canvas for the hearts
   var heartCanvasId = "heartCanvas";
   rpgcode.createCanvas(rpgcode.getViewport().width, rpgcode.getViewport().height, heartCanvasId);

   // Call this code every 50 milliseconds.
   setInterval(function() {
      var hearts = rpgcode.getCharacter().health;
      if (hearts < 1) {
         rpgcode.restart();
      }
      
      rpgcode.clearCanvas(heartCanvasId);
      for (var i = 0; i < hearts; i++) {
         rpgcode.setImagePart("objects.png", 64, 0, 16, 16, 8 + (16 * i), 8, 16, 16, heartCanvasId);
      }
      rpgcode.renderNow(heartCanvasId);
   }, 50);

   rpgcode.font = "18px Arial";

   rpgcode.endProgram();
});
 