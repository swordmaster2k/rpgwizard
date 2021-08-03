import * as dialog from "./modules/gui/dialog.js";
import * as title from "./modules/gui/title.js";

import * as action from "./modules/action-bs/action.js";
import * as torch from "./modules/environment/torch.js";

export default async function (e) {

   // Load common assets
   const assets = {
      "audio": {
         "sword": "sword.wav",
         "hurt-player": "hurt_character.wav",
         "hurt-enemy": "hurt_enemy.wav",
         "item": "item.wav"
      }
   };
   await rpg.loadAssets(assets);

   // Show the title
   await title.show({
      backgroundImage: "Overworld.png",
      titleMusic: "music.mp3",
      font: {
         size: 20,
         family: "Lucida Console",
         color: { r: 0, g: 0, b: 0, a: 1 }
      }
   });

   // Show the intro
   const config = {
      position: "BOTTOM",
      advancementKey: "E",
      background: {
         image: "objects.png",
         leftSide: { x: 148, y: 228, w: 12, h: 40 },
         center: { x: 160, y: 228, w: 16, h: 40 },
         rightSide: { x: 176, y: 228, w: 12, h: 40 }
      },
      profile: {
         image: "objects.png",
         leftSide: { x: 148, y: 228, w: 12, h: 40 },
         center: { x: 160, y: 228, w: 16, h: 40 },
         rightSide: { x: 176, y: 228, w: 12, h: 40 }
      },
      nextMarkerImage: "next_marker.png",
      typingSound: "typing_loop.wav",
      font: {
         size: 20,
         family: "Lucida Console",
         color: { r: 0, g: 0, b: 0, a: 1 }
      },
      text: 
      `
      Hello, this text will be wrote to the window like a type-writer.
      Hello, this text will be wrote to the window like a type-writer.
      `
   };
   await dialog.show(config);

   // Load start map
   await rpg.loadMap("sample.map");
   await torch.setup();
   await action.setup();

}