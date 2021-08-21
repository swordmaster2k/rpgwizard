import * as action from "../modules/action-bs/action.js";
import * as dialog from "../modules/gui/dialog.js";

export default async function (e) {
   const chest = e.target;
   if (chest.data.open) {
      return;
   }

   rpg.setGlobal("pause.input", true);
   
   const rewardSpriteId = await action.openChest(chest);

   const config = rpg.getGlobal("dialog.config");
   config.text = 
   `
   You have obtained a sword. Use it by pressing SPACE.
   `;
   config.showProfile = false;
   await dialog.show(config);

   rpg.removeSprite(rewardSpriteId);

   rpg.setGlobal("player.hasSword", true);

   rpg.setGlobal("pause.input", false);
}
