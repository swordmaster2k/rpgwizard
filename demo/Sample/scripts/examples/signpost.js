import * as action from "../modules/action-bs/action.js";
import * as dialog from "../modules/gui/dialog.js";

export default async function (origin) {

   const map = rpg.getMap();
   if (!map) {
      return;
   }

   switch(map.name) {
      case "outside.map":
         const config = rpg.getGlobal("dialog.config");
         config.text = 
         `
         Lunk's House
         `;
         config.showProfile = false;
         await dialog.show(config);
         break;
   }

}
