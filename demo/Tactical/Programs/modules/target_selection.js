/**
 * This module is responsible for calculating the priority of
 * a target for an AI actor. Which will then select the best
 * target from all of the priorties.
 */
let TargetSelection = function() {

   function priority(u, t, path) {
     let priority = 1;
     priority -= Common.normalize(range(u, t, path), rpgcode.getGlobal("minRange"), rpgcode.getGlobal("maxRange"));
     priority -= Common.normalize(t.cover, rpgcode.getGlobal("minCover"), rpgcode.getGlobal("maxCover"));
     priority -= damage(t, u, damage(u, t, 0));
     priority = Math.round(priority * 100) / 100;
     return Math.min(1, Math.max(0, priority));
   }

   function range(u, t, path) {
     if (!path) {
         return rpgcode.getGlobal("maxRange");
     }
     return path;
   }

   function damage(u, t, delta) {
     const effectiveness = rpgcode.getGlobal("effectiveness")[u.type][t.type];
     const hpDelta = Common.normalize(u.hp, rpgcode.getGlobal("minHp"), rpgcode.getGlobal("maxHp")) - delta;
     return effectiveness * hpDelta;
   }

   return {
     name: "TargetSelection",
     priority: priority
   };

}();
