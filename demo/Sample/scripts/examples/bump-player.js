import * as action from "../modules/action-bs/action.js";

export default async function (origin) {

   await action.bumpPlayer(origin);

}
