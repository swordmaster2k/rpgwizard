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
   await rpg.loadMap("sample.map");
   
   await torch.setup();
   await action.setup();

}