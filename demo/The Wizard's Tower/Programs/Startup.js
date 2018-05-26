/*
 * This program is loaded on game startup, it ensures that the basic assets
 * for the dialog system are ready. 
 * 
 * It also plays the intro to the game and registers the key handler for
 * the menu system.
 */

// For debugging.
var playIntro = true;

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
      profileImage: "sword_profile_1_small.png",
      typingSound: "typing_loop.wav",
      text: introText
   };
   dialog.show(config, finish);
}

function finish() {
   rpgcode.log("Running finish");

   // Register the menu key program
   rpgcode.registerKeyDown("ENTER", function() {
      rpgcode.runProgram("MenuSystem.js");
   }, true);

   // Increase character walk speed.
   rpgcode.setCharacterSpeed("Hero", 1.5);

   rpgcode.endProgram();
}