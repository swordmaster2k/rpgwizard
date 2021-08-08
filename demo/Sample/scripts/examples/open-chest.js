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
   config.text = `You got 1 coin.`;
   config.showProfile = false;
   await dialog.show(config);

   rpg.removeSprite(rewardSpriteId);

   rpg.setGlobal("pause.input", false);
}
