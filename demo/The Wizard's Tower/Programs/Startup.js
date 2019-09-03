/*
 * This program is loaded on game startup, it ensures that the basic assets
 * for the dialog system are ready. 
 * 
 * It also plays the intro to the game and registers the key handler for
 * the menu system.
 */

// For debugging.
var playIntro = true;

var assets = {
  "programs": [
      // Default systems.
      "defaults/gui.js",
      "defaults/hud.js",
      "defaults/battle.js",
      "defaults/dialog.js",
      "defaults/weather.js",
      "defaults/inventory.js",
      "defaults/titleScreen.js"
  ]
}

// Set the game globals.
rpgcode.setGlobal("swordactive", false);

// Text used of the intro.
var introText = 
"For as long as the villagers can remember the wizard's tower has stood upon the " +
"grey plains. A reminder to not leave the village after dark. " +
"Every hundred years somebody always goes missing from the village. " +
"And the hundred years is up. " +
"And this time is no different, apart from the fact that the girl that is missing is the " +
"sister of a knight. " + 
"Without delay he grabs his armour and heads to the tower.....";

rpgcode.loadAssets(assets, function() {
   // Configure and show the title screen.
   var config = {
   "backgroundImage": "startscreen.png", 
   "titleScreenMusic": "intro.ogg"
   };
   titleScreen.show(config, function() {
      // Show the intro when the user has passed the title screen.
      if (playIntro) {
         showIntro();
      } else {
         finish();
      }
   }); 
   
   function showIntro() {
      // Show the intro text.
      var config = {
         position: "CENTER",
         nextMarkerImage: "next_marker.png",
         profileImage: rpgcode.getCharacter().graphics["PROFILE"],
         typingSound: "typing_loop.wav",
         text: introText
      };
      dialog.show(config, finish);
   }
   
   function finish() {
      rpgcode.log("Running finish");
   
      // Register the menu key program
      rpgcode.registerKeyDown("Q", function() {
         rpgcode.runProgram("ToggleInventory.js");
      }, true);
   
      // Increase character walk speed.
      rpgcode.setCharacterSpeed("Hero", 2.0);

      

      // Setup weather
      var config = {
         rain: {
            sound: "rain.wav"
         }
      };
      weather.show(config, function() {
         // Setup HUD after to ensure it appears above
         // any weather effects
         var config = {
            life: {
               image: "life.png",
               width: 32,
               height: 32
            }
         };
         hud.show(config, function() {});

         rpgcode.endProgram();
      });
   }
});
