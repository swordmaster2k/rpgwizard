import * as action from "../../modules/action-bs/action.js";

export default async function (e) {

   await action.wander(e.source, 15, 1000);

}
