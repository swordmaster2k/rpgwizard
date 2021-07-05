import * as dialog from "./modules/gui/dialog.js";
import * as title from "./modules/gui/title.js";

import * as action from "./modules/action-bs/action.js";
import * as torch from "./modules/environment/torch.js";

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

   const choice = await title.show({
      "backgroundImage": "Overworld.png",
      "titleMusic": "music.mp3"
   });
   console.log(choice);

   let config = {
      position: "BOTTOM",
      advancementKey: "E",
      nextMarkerImage: "next_marker.png",
      profileImage: "tutor.jpg",
      typingSound: "typing_loop.wav",
      text: 
      `
      Hello, this text will be wrote to the window like a type-writer.
      Hello, this text will be wrote to the window like a type-writer.
      `
   };
   await dialog.show(config);
   
   await rpg.loadMap("sample.map");
   await torch.setup();
   await action.setup();

}