import * as action from "../modules/action-bs/action.js";

export default async function (e) {

   await action.pickupItem(e.source, e.target);

}
