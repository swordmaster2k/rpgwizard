/**
 * This module contains the logic for an AI actor which is capable
 * of commanding many units. An AI will use every unit assigned
 * to it in a turn.
 */
let AI = function() {

   /*
    * execute
    */
   async function execute(units, targets) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`act: units=[${JSON.stringify(units)}], targets=[${JSON.stringify(targets)}]`);
      }

      let unitIds = Object.keys(units);
      for (let i = 0; i < unitIds.length; i++) {
         await _act(units[unitIds[i]], targets);
      }

      Common.switchTurn(Common.ID_PLAYER);
   }

   /*
    * _act
    */
   async function _act(unit, targets) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`_act: unit=[${JSON.stringify(unit)}], targets=[${JSON.stringify(targets)}]`);
      }

      await _use(unit, targets);

      // TEMP
      rpgcode.clearCanvas(Overlay.actionableAreaCanvasId);
      rpgcode.clearCanvas(Overlay.notificationCanvasId);
      rpgcode.clearCanvas(Overlay.selectionCanvasId);
      rpgcode.clearCanvas(Overlay.debugCanvasId);
      rpgcode.clearCanvas(Overlay.pathCanvasId);
      Overlay.drawHealthStats();
      
      await Common.checkEndConditions();
   }

   /*
    * _use
    */
   async function _use(unit, targets) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`_use: unit=[${unit}], targets=[${targets}]`);
      }

      // Scale range to targets for this unit
      _scaleRange(unit, targets);

      // Get unit's current position, and foreach target calculate their priority
      const uloc = rpgcode.getSpriteLocation(unit.id, true, false);
      unit.x = uloc.x;
      unit.y = uloc.y;
      let best = {
         priority: -1,
         target: null
      };
      Object.keys(targets).forEach((id) => {
         _prioritize(unit, targets[id], best);
      });

      if (rpgcode.getGlobal("debug")) {
         Overlay.drawTarget(best.target);
      }

      // Try to advance towards the target
      let start = rpgcode.getSpriteLocation(unit.id, true, false);
      let goal = rpgcode.getSpriteLocation(best.target.id, true, false);
      let path = PathFinder.findPath(start, goal);
      if (1 < path.length) {
         await _advance(unit, best, path);
         let end = rpgcode.getSpriteLocation(unit.id, true, false);

         PathFinder.setWalkable(Math.floor(start.x), Math.floor(start.y), true);
         PathFinder.setWalkable(Math.floor(end.x), Math.floor(end.y), false);
         State.setCellOccupant(Math.floor(start.x), Math.floor(start.y), null);
         State.setCellOccupant(Math.floor(end.x), Math.floor(end.y), unit);
      }

      // Try to attack the target
      if (Ranger.inRange(unit, best.target)) {
         const destroyed = await _attack(unit, best, targets);
         if (destroyed) {
            PathFinder.setWalkable(Math.floor(destroyed.x), Math.floor(destroyed.y), true);
            State.setCellOccupant(Math.floor(destroyed.x), Math.floor(destroyed.y), null);
            State.destroyUnit(destroyed.target.combatant, destroyed.target);
         }
      }

      // Mark the unit as used if it survived
      State.useUnit(Common.ID_AI, unit);
   }

   /*
    * _scaleRange
    */
   function _scaleRange(unit, targets) {
      let [minRange, maxRange, targetIds] = [Infinity, -Infinity, Object.keys(targets)];
      let start = rpgcode.getSpriteLocation(unit.id, true, false);
      for (let i = 0; i < targetIds.length; i++) {
         let point = rpgcode.getSpriteLocation(targets[targetIds[i]].id, true, false);
         let range = PathFinder.findPath(start, point).length;
         if (range) {
            if (maxRange < range) {
               maxRange = range;
            }
            if (minRange > range) {
               minRange = range;
            }
         }
      }
      rpgcode.setGlobal("minRange", minRange);
      rpgcode.setGlobal("maxRange", maxRange);
   }

   /*
    * _prioritize
    */
   function _prioritize(unit, target, best) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`_prioritize: unit=[${JSON.stringify(unit)}], target=[${JSON.stringify(target)}], best=[${JSON.stringify(best)}]`);
      }

      // Select the target with highest priority
      let tloc = rpgcode.getSpriteLocation(target.id, true, false);
      target.x = tloc.x;
      target.y = tloc.y;
      let path = PathFinder.findPath(unit, target).length;
      let priority = TargetSelection.priority(unit, target, path);
      if (best.priority < priority) {
         best.priority = priority;
         best.target = target;
      }

      if (rpgcode.getGlobal("debug")) {
         Overlay.drawInfo(target, priority);
      }
   }

   /*
    * _advance
    */
   async function _advance(unit, best, path) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`_advance: unit=[${JSON.stringify(unit)}], best=[${JSON.stringify(best)}], path=[${JSON.stringify(path)}]`);
      }

      path.pop(); // Remove target's actual position from end
      if ((unit.speed + 1) < path.length) {
         path = path.splice(0, unit.speed + 1);
      }
      if (rpgcode.getGlobal("debug")) {
         Overlay.drawPath(path);
      }

      Overlay.clearCell(rpgcode.getSpriteLocation(unit.id, true, false));
      await Movement.move(unit.id, path.shift(), path);
   }

   /*
    * _attack
    */
   async function _attack(unit, best, targets) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`_attack: unit=[${JSON.stringify(unit)}], best=[${JSON.stringify(best)}], targets=[${JSON.stringify(targets)}]`);
      }
      return await Attack.attack(unit, best.target);
   }

   return {
      name: "ai",
      execute: execute
   };

}();