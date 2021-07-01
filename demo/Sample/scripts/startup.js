import * as action from "./modules/action-bs/action.js";

export default async function (origin) {

   const assets = {
      "audio": {
         "sword": "sword.wav",
         "hurt-enemy": "hurt_enemy.wav",
         "item": "item.wav"
      }   
   };
   await rpg.loadAssets(assets);

   await rpg.loadMap("sample.map");
   rpg.attachController("player");

   rpg.registerKeyDown("SPACE", async function() {
      await action.slashSword();
   }, true);
   
}