/*
 * This program is loaded on game startup, it ensures that the basic assets
 * for the dialog system are ready. 
 * 
 * It also plays the intro to the game and registers the key handler for
 * the menu system.
 */

// For debugging.
let playIntro = true;

let assets = {
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
};

// Set the game globals.
rpgcode.setGlobal("swordactive", false);

// Text used of the intro.
let introText = 
"For as long as the villagers can remember the wizard's tower has stood upon the " +
"grey plains. A reminder to not leave the village after dark. " +
"Every hundred years somebody always goes missing from the village. " +
"And the hundred years is up. " +
"And this time is no different, apart from the fact that the girl that is missing is the " +
"sister of a knight. " + 
"Without delay he grabs his armour and heads to the tower.....";

let startingItems = [
   "apple.item",
   "apple.item",
   "apple.item",
   "potion.item",
   "magic_potion.item",
   "strength_potion.item"
];

function setupItems() {
   // Setup starting inventory items
   if (startingItems.length === 0) {
      // No more items to add, setup inventory key, and return
      rpgcode.registerKeyDown("Q", function() {
         rpgcode.runProgram("ToggleInventory.js");
      }, true);
      return;
   } else {
      // Keep adding items
      rpgcode.giveItem(startingItems.pop(), "Hero", setupItems);
   }
}

rpgcode.loadAssets(assets, async function() {
   // Configure and show the title screen.
   let config = {
   "backgroundImage": "startscreen.png", 
   "titleScreenMusic": "intro.ogg"
   };
   await titleScreen.show(config);
   
   if (playIntro) {
      // Show the intro when the user has passed the title screen.
      config = {
         position: "CENTER",
         nextMarkerImage: "next_marker.png",
         profileImage: rpgcode.getCharacter().graphics.PROFILE,
         typingSound: "typing_loop.wav",
         text: introText
      };
      await dialog.show(config);
   }
   
   // Register the menu key program
   rpgcode.registerKeyDown("Q", function() {
      rpgcode.runProgram("ToggleInventory.js");
   }, true);
   
   // Increase character walk speed.
   rpgcode.setCharacterSpeed("Hero", 2.0);
   
   // Setup weather
   config = {
      rain: {
         sound: "rain.wav"
      }
   };
   await weather.show(config);

   // Setup HUD after to ensure it appears above
   // any weather effects
   config = {
      life: {
         image: "life.png",
         width: 32,
         height: 32
      }
   };
   hud.show(config, function() {});
   
   // Setup inventory items
   setupItems();
   
   rpgcode.endProgram();
});
