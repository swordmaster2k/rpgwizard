import * as action from "./modules/action-bs/action.js";

export default async function (origin) {

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
   await rpg.loadMap("sample.map");
   await action.setup();

}