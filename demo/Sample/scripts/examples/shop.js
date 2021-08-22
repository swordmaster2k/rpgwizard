import * as dialog from "../modules/gui/dialog.js";

export default async function (e) {
   const itemId = e.target.id.replace("trigger", "item");
   const sprite = rpg.getSprite(itemId);
   if (!sprite) {
      return;
   }

   const player = rpg.getSprite("player");
   const coins = player.data.coins;
   if (coins < 1) {
      await showShopMessage(`You have no coins.`);
   } else if (itemId === "heart-item" && coins < 50) {
      await showShopMessage(`This item requires 5 coins.`);
   } else if (itemId === "book-item" && coins < 50) {
      await showShopMessage(`This item requires 10 coins.`);
   }

}

async function showShopMessage(text) {
   const config = rpg.getGlobal("dialog.config");
   config.text = text;
   config.showProfile = false;
   await dialog.show(config);
}
