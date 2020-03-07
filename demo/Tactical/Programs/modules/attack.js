/**
 * This module is responsible for handling unit-to-unit attacks,
 * and can determine if either unit has been destroyed as part
 * of an attack.
 * 
 * Damage dealt, is based on the unit's effectiveness against the
 * defending unit type, and the attacking unit's health.
 */
let Attack = function() {

   async function attack(u, t) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`attack: u=[${JSON.stringify(u)}], t=[${JSON.stringify(t)}]`);
      }

      await _damageUnit(t.id);
      t.hp -= _calculateDamage(u, t);
      if (t.hp < 1 || Math.floor(t.hp) === 1) {
         let loc = rpgcode.getSpriteLocation(t.id, true, false);
         await Common.destroyUnit(t.id);
         return { target: t, x: loc.x, y: loc.y };
      }
      
      await _damageUnit(u.id);
      u.hp -= _calculateDamage(t, u);
      if (u.hp < 1) {
         let loc = rpgcode.getSpriteLocation(u.id, true, false);
         await Common.destroyUnit(u.id);
         return { target: u, x: loc.x, y: loc.y };
      }
      
      return null;
   }

   function _calculateDamage(attacker, defender) {
      const effectiveness = rpgcode.getGlobal("effectiveness")[attacker.type][defender.type];
      const hpNorm = Common.normalize(attacker.hp, rpgcode.getGlobal("minHp"), rpgcode.getGlobal("maxHp"));
      return Math.round((effectiveness * hpNorm) * 10);
   }

   function _damageUnit(id) {
      return new Promise((resolve, reject) => {
         rpgcode.setSpriteStance(id, "CUSTOM_DAMAGED");
         rpgcode.animateSprite(id, "CUSTOM_DAMAGED", 
            async function() {
               rpgcode.setSpriteStance(id, "EAST");
               resolve();
            }
         );
      });
   }

   return {
      name: "Attack",
      attack: attack
   };

}();