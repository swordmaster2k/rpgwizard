/**
 * This module contains various common functions that are reused
 * across the games code.
 */
let Common = function() {

   function normalize(val, min, max) {
      return val === min && val === max ? 0 : (val - min) / (max - min);
   }

   function switchTurn(starting) {
      if (starting === Common.ID_AI) {
         State.changeUnitsStance(Common.ID_PLAYER, "EAST");
         State.enableUnits(Common.ID_AI);
         
         State.setPlayerTurn(false);
         State.setAiTurn(true);
         
         AI.execute(State.getUnits(Common.ID_AI), State.getUnits(Common.ID_PLAYER));
      } else if (starting === Common.ID_PLAYER) {
         State.changeUnitsStance(Common.ID_AI, "EAST");
         State.enableUnits(Common.ID_PLAYER);
         
         State.setPlayerTurn(true);
         State.setAiTurn(false);
         
         Player.execute(State.getUnits(Common.ID_PLAYER));
      }
   }

   async function checkEndConditions() {
      const advanceFunction = rpgcode.getGlobal("advanceFunction");
      if (typeof advanceFunction === "function") {
         // Let the defined advancement function run instead
         await advanceFunction();
      } else {
         // Check player unit count
         if (Object.entries(State.getUnits(Common.ID_PLAYER)).length === 0) {
            rpgcode.restart();
         }
         // Check AI unit count
         if (Object.entries(State.getUnits(Common.ID_AI)).length === 0) {
            rpgcode.restart();
         }
      }
   }

   function destroyUnit(id) {
      return new Promise((resolve, reject) => {
         rpgcode.setSpriteStance(id, "CUSTOM_DESTROYED");
         rpgcode.animateSprite(id, "CUSTOM_DESTROYED", 
            async function() {
               rpgcode.destroySprite(id);
               resolve();
            }
         );
      });
   }

   return {
      name: "Common",
      // Constants
      ID_PLAYER: "player",
      ID_AI: "ai",
      // Functions
      normalize: normalize, 
      switchTurn: switchTurn,
      checkEndConditions: checkEndConditions,
      destroyUnit: destroyUnit
   };

}();