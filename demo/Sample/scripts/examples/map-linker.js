import * as action from "../modules/action-bs/action.js";
import * as dialog from "../modules/gui/dialog.js";

export default async function (e) {

   const triggerId = e.target.id;
   const parts = triggerId.split(",");
   if (parts.length !== 4) {
      return;
   }

   const destinationMap = parts[0];
   const x = parseFloat(parts[1]);
   const y = parseFloat(parts[2]);
   const layer = parseInt(parts[3]);

   switch(rpg.getMap().name) {
      case "start.map":
         if (!rpg.getGlobal("player.hasSword")) {
            const config = rpg.getGlobal("dialog.config");
            config.text = 
            `
            Lunk... Lunk...
            It is dangerous to go unarmed.
            Open the chest...
            `;
            config.showProfile = false;
            await dialog.show(config);
            return;
         }
   }

   const direction = rpg.getSpriteDirection("player");
   await rpg.loadMap(destinationMap);
   await action.spawnPlayer(x, y, layer);
   rpg.setSpriteAnimation("player", direction);

}
