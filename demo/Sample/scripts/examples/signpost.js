import * as action from "../modules/action-bs/action.js";
import * as dialog from "../modules/gui/dialog.js";

export default async function (e) {

   switch (e.target.id) {
      case "sign-lunks-house":
         await showSignMessage("Lunk's House");
         break;
      case "sign-cave":
         await showSignMessage("Scary Cave! Requires the book...");
         break;
      default:
         await showSignMessage("Unknown Sign!");
   }

}

async function showSignMessage(text) {
   const config = rpg.getGlobal("dialog.config");
   config.text = text;
   config.showProfile = false;
   await dialog.show(config);
}
