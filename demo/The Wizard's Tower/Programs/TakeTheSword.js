/* global rpgcode */

//This is a very cheap way of doing a scene
//check the bitmap folder and see the images
//named shot1 to shot11 to see what I mean
//You will notice that I change the tiles where the sword is(for obvious reasons)
let canvas;
let assets;
let delay = 250;

let swordActive = rpgcode.getGlobal("swordActive");
if (!swordActive) {
   assets = {
      "images": [
         "shot1.png",
         "shot2.png",
         "shot3.png",
         "shot4.png",
         "shot5.png",
         "shot6.png",
         "shot7.png",
         "shot8.png",
         "shot9.png",
         "shot10.png",
         "shot11.png"
      ]
   };
   rpgcode.loadAssets(assets, async function() {
      canvas = "renderNowCanvas";
      let config = {
         position: "BOTTOM",
         advancementKey: "E",
         nextMarkerImage: "next_marker.png",
         profileImage: "sword_profile_1_small.png",
         typingSound: "typing_loop.wav",
         text: "NOW TAKE MY POWER"
      };
      await dialog.show(config);

      await takeSword();

      rpgcode.replaceTile(11, 10, 0, "tower.tileset", 49);
      rpgcode.replaceTile(12, 10, 0, "tower.tileset", 50);
      rpgcode.removeTile(11, 9, 1);
      rpgcode.removeTile(12, 9, 1);

      rpgcode.setGlobal("swordActive", true);
      config = {
         position: "BOTTOM",
         advancementKey: "E",
         nextMarkerImage: "next_marker.png",
         profileImage: "sword_profile_1_small.png",
         typingSound: "typing_loop.wav",
         text: "I have been here for so many years, cursed by that wizard to live in the form of a sword."
      };
      await dialog.show(config);

      config = {
         position: "BOTTOM",
         advancementKey: "E",
         nextMarkerImage: "next_marker.png",
         profileImage: "sword_profile_1_small.png",
         typingSound: "typing_loop.wav",
         text: "Let's go kill a wizard. I need my revenge."
      };
      await dialog.show(config);

      rpgcode.clearCanvas();
      rpgcode.removeAssets(assets); // Clean up any loaded assets!
      rpgcode.endProgram();

   });
} else {
   rpgcode.endProgram();
}

function takeSword() {
   return new Promise((resolve, reject) => {
      rpgcode.delay(delay, function() {
         animate(resolve);
      });
   });
}

function animate(resolve) {
   let image = assets.images.shift();
   rpgcode.setImage(image, 0, 0, 640, 480, canvas);
   rpgcode.renderNow(canvas);

   if (assets.images.length) {
      rpgcode.delay(delay, function() {
         animate(resolve);
      });
   } else {
      resolve();
   }
}