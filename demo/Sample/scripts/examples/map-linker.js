import * as action from "../modules/action-bs/action.js";
import * as dialog from "../modules/gui/dialog.js";

export default async function (origin) {

   const map = rpg.getMap();
   if (!map) {
      return;
   }

   switch(map.name) {
      case "start.map":
         if (rpg.getGlobal("player.hasSword")) {
            await rpg.loadMap("outside.map");
            await action.spawnPlayer(10, 10, 1);
         } else {
            const config = rpg.getGlobal("dialog.config");
            config.text = 
            `
            Lunk... Lunk...
            It is dangerous to go unarmed.
            Open the chest...
            `;
            config.showProfile = false;
            await dialog.show(config);
         }
         break;
   }

}
