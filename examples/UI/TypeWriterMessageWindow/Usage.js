var assets = {
   "programs": [
      "TypeWriterMessageWindow.js"
   ],
   "images": [
      // Provide your own image here.
      //"arrow_18.png"
   ],
   "audio": {
      // Provide your own sound here.
      //"typing": "typing_loop.wav",
   }
};

rpgcode.loadAssets(assets, function() {
   // Pixel settings.
   var width = 250;
   var height = 57;
   var padding = 15;
   var linePadding = 10;

   // Font, typing sound to play, image for the next marker.
   var fontSize = 20 * rpgcode.getScale(); // Proivdes a scalable 20px font size.
   var fontFamily = fontSize + " Arial";

   // Assign the image and sound files if required.
   var typingSound = ""; //"typing";
   var nextMarker = ""; //"arrow_18.png";

   // Only need to call setup once, unless you want to change it again later on.
   TypeWriter.setup(width, height, padding, linePadding, fontFamily, typingSound, nextMarker);
   
   // Other settings not included in setup. 
   TypeWriter.color = {
     "r": 255,
     "g": 255,
     "b": 255,
     "a": 1.0 
   };
   TypeWriter.backgroundColor = {
     "r": 255,
     "g": 0,
     "b": 0,
     "a": 1.0 
   };
   TypeWriter.advancementKey = "E";
   TypeWriter.maxLines = 2;
   TypeWriter.characterDelay = 50;
   TypeWriter.markerBlinkDelay = 500;
   
   TypeWriter.showDialog("This text will appear like a typewriter, and wrap across multiple lines.", 
      function() {
         rpgcode.log("The message window has been closed.");
         rpgcode.endProgram() 
      }
   );
});

