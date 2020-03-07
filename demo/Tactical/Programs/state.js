/**
 * This module contains the current "state" of a battle
 * between a Player and AI.
 */
let State = function() {
   ///
   /// Properties
   ///
   let _playerTurn = false;
   let _aiTurn = false;
   let _combatants = {
      player: {
         units: [],
         usedCount: 0
      },
      ai: {
         units: [],
         usedCount: 0
      }
   };
   ///
   /// Combatants
   ///
   function getUnits(combatant) {
      return _combatants[combatant].units;
   }

   function setUnits(combatant, units) {
      _combatants[combatant].units = units;
   }

   function useUnit(combatant, unit) {
      if (_combatants[combatant].units[unit.id]) {
         _combatants[combatant].units[unit.id].used = true;
         _combatants[combatant].usedCount++;
         rpgcode.setSpriteStance(unit.id, "CUSTOM_USED");
      }
      return _combatants[combatant].usedCount === Object.keys(_combatants[combatant].units).length;
   }

   function destroyUnit(combatant, unit) {
      delete _combatants[combatant].units[unit.id];
   }

   function enableUnits(combatant) {
      const units = _combatants[combatant].units;
      for (let unit in units) {
         if (Object.prototype.hasOwnProperty.call(units, unit)) {
            units[unit].used = false;
            rpgcode.setSpriteStance(units[unit].id, "EAST");
         }
      }
      _combatants[combatant].usedCount = 0;
   }

   function changeUnitsStance(combatant, stance) {
      const units = _combatants[combatant].units;
      for (let unit in units) {
         if (Object.prototype.hasOwnProperty.call(units, unit)) {
            rpgcode.setSpriteStance(units[unit].id, stance);
         }
      }
   }
   ///
   //
   // Player
   //
   function isPlayerTurn() {
      return _playerTurn;
   }

   function setPlayerTurn(playerTurn) {
      _playerTurn = playerTurn;
   }
   ///
   /// AI
   ///
   function isAiTurn() {
      return _aiTurn;
   }

   function setAiTurn(aiTurn) {
      _aiTurn = aiTurn;
   }
   ///
   /// Grid
   ///
   function isCellValid(cellX, cellY) {
      try {
         return typeof rpgcode.getGlobal("cells")[cellY][cellX] !== 'undefined';
      } catch (err) {
         return false;
      }
   }

   function getCellOccupant(cellX, cellY) {
      return rpgcode.getGlobal("cells")[cellY][cellX];
   }

   function setCellOccupant(cellX, cellY, occupant) {
      rpgcode.getGlobal("cells")[cellY][cellX] = occupant;
   }
   ///
   /// this
   ///
   return {
      name: "State",
      // Combatants
      getUnits: getUnits,
      setUnits: setUnits,
      useUnit: useUnit,
      destroyUnit: destroyUnit,
      enableUnits: enableUnits,
      changeUnitsStance: changeUnitsStance,
      // Player
      isPlayerTurn: isPlayerTurn,
      setPlayerTurn: setPlayerTurn,
      // AI
      isAiTurn: isAiTurn,
      setAiTurn: setAiTurn,
      // Grid
      isCellValid: isCellValid,
      getCellOccupant: getCellOccupant,
      setCellOccupant: setCellOccupant
   };

}();