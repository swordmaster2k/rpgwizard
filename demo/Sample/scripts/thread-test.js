import * as action from "./modules/action.js";

export default async function (origin) {

   await action.wander(origin, 80, 2500);
//   await action.moveSpriteTowardsPoint(origin, {x: 0, y: 0}, 10, 1000);

}
