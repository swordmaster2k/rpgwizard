/**
 * This module is responsible for determining whether the
 * selected target is within a units attack range.
 */
let Ranger = function() {

   function inRange(u, t) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`inRange: u=[${JSON.stringify(u)}], t=[${JSON.stringify(t)}]`);
      }

      let uloc = rpgcode.getSpriteLocation(u.id, true, false);
      let tloc = rpgcode.getSpriteLocation(t.id, true, false);
      let range = Math.ceil(
         rpgcode.getDistanceBetweenPoints(
            Math.round(uloc.x), Math.round(uloc.y), 
            Math.round(tloc.x), Math.round(tloc.y)
         )
      );
      if (rpgcode.getGlobal("debug")) {
         console.debug(`range=[${range}] to tloc=[${JSON.stringify(tloc)}] from uloc=[${JSON.stringify(uloc)}]`);
      }

      return range <= u.range;
   }

   return {
      name: "Ranger",
      inRange: inRange
   };

}();