import * as action from "../modules/action-bs/action.js";
import * as dialog from "../modules/gui/dialog.js";

export default async function (e) {

   const itemId = e.target.id.replace("trigger", "item");
   const sprite = rpg.getSprite(itemId);
   if (!sprite) {
      return;
   }

   const player = rpg.getSprite("player");
   if (rpg.getGlobal("player.coins") < 1) {
      await showShopMessage(`You have no coins.`);
      return;
   }

   const cost = 5;
   if (rpg.getGlobal("player.coins") < cost) {
      await showShopMessage(`This item requires ${cost} coins.`);
      return;
   }

   action.purchaseItem(sprite, cost);
   await showShopMessage(`You purchased a ${sprite.name} for ${cost} coins.`);

}

async function showShopMessage(text) {
   const config = rpg.getGlobal("dialog.config");
   config.text = text;
   config.showProfile = false;
   await dialog.show(config);
}
