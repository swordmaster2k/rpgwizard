import * as action from "./modules/action-bs/action.js";
import * as torch from "./modules/environment/torch.js";

import * as gui from "./modules/gui/gui.js";
import * as dialog from "./modules/gui/dialog.js";

export default async function (e) {

   const assets = {
      "audio": {
         "sword": "sword.wav",
         "hurt-player": "hurt_character.wav",
         "hurt-enemy": "hurt_enemy.wav",
         "item": "item.wav"
      },
      "images": [
         "objects.png"
      ]
   };
   await rpg.loadAssets(assets);

   gui.setup();

   let config = {
      position: "BOTTOM",
      advancementKey: "E",
      nextMarkerImage: "next_marker.png",
      profileImage: "tutor.jpg",
      typingSound: "typing_loop.wav",
      text: "Hello, this text will be wrote to the window like a type-writer."
   };
   await dialog.show(config);
   
//   await rpg.loadMap("sample.map");
//   await torch.setup();
//   await action.setup();

}