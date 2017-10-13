/*
 * This program is loaded on game startup, it ensures that the basic assets
 * for the dialog system are ready. 
 * 
 * It also plays the intro to the game and registers the key handler for
 * the menu system.
 */
// Game assets used in this program.
var assets = {
   "audio": {
      "intro": "intro.ogg"
   },
   "images": [
      "block.png",
      "mwin_small.png",
      "sword_profile_1_small.png",
      "startscreen.png"
   ]
};

// Canvas IDs
var canvas = "flashingText";

// A message stack used for cycling through the intro text:
//
//    https://en.wikibooks.org/wiki/Data_Structures/Stacks_and_Queues
var messageStack = new Array();
messageStack.push("Without delay he grabs his armour and heads to the tower.....");
messageStack.push("sister of a knight.");
messageStack.push("And this time is no different, apart from the fact that the girl that is missing is the");
messageStack.push("And the hundred years is up.");
messageStack.push("Every hundred years somebody always goes missing from the village.");
messageStack.push("grey plains. A reminder to not leave the village after dark.");
messageStack.push("For as long as the villagers can remember the wizard's tower has stood upon the");

// Current message is stored in this variable.
var message = "";

var x = 20;
var y = 20;

var alpha = 0.0;

var flash = true;
var isShowingIntro = false;

// Load up the assets we need
rpgcode.loadAssets(assets, function() {
   rpgcode.setGlobal("swordactive", false);
   rpgcode.setDialogGraphics("sword_profile_1_small.png", "mwin_small.png");

   rpgcode.setImage("startscreen.png", 0, 0, 640, 480);
   rpgcode.setColor(0, 0, 0, 1.0);
   rpgcode.renderNow();

   rpgcode.createCanvas(640, 480, canvas);
   rpgcode.delay(500, flashText);

   rpgcode.playSound("intro", true);

   rpgcode.registerKeyDown("ENTER", showIntro);
});

function flashText() {
   rpgcode.log("Running flashText");

   if (flash) {
      rpgcode.drawText(270, 300, "PRESS ENTER", canvas);
      rpgcode.renderNow(canvas);
   } else {
      rpgcode.clearCanvas(canvas);
   }

   flash = !flash;

   if (!isShowingIntro) {
      rpgcode.delay(500, flashText);
   }
}

function showIntro() {
   rpgcode.log("Running showIntro");

   rpgcode.unregisterKeyDown("ENTER");

   isShowingIntro = true;
   rpgcode.clearCanvas();
   rpgcode.clearCanvas(canvas);
   rpgcode.destroyCanvas(canvas);

   // Fill the screen with a black background.
   rpgcode.setColor(0, 0, 0, 1.0);
   rpgcode.fillRect(0, 0, 640, 480);
   rpgcode.renderNow();

   // Get the first message and start
   // calling the fadeIn function 
   // every 150 milliseconds.
   message = messageStack.pop();
   rpgcode.delay(150, fadeIn);
}

function fadeIn() {
   rpgcode.log("Running fadeIn");

   rpgcode.setColor(255, 255, 255, alpha);
   rpgcode.drawText(x, y, message);
   rpgcode.renderNow();

   // Slowly fade in the text using the 
   // alpha channel of RGBA.
   alpha += 0.05;
   if (alpha >= 0.5) {
      if (messageStack.length > 0) {
         y += 20;
         alpha = 0.0;
         // Move on to the next message in
         // the intro text.
         message = messageStack.pop();
         rpgcode.delay(150, fadeIn);
      } else {
         finish();
      }
   } else {
      rpgcode.delay(150, fadeIn);
   }
}

function finish() {
   rpgcode.log("Running finish");

   rpgcode.stopSound("intro");
   rpgcode.clearCanvas();

   // Remove some assets that won't ever be used,
   // this will save memory espcially on useful
   // when running on a mobile device.
   rpgcode.removeAssets({
      "audio": {
         "intro": "intro.ogg"
      },
      "images": [
         "startscreen.png"
      ]
   });

   // Reset the text alpha.
   rpgcode.setColor(255, 255, 255, 1.0);

   // Register the menu key program
   rpgcode.registerKeyDown("ENTER", function() {
      rpgcode.runProgram("MenuSystem.js");
   }, true);

   rpgcode.endProgram();
}