/**
 * This module contains the logic for a Player Actor, it handles
 * mouse input, moving units, attacking, etc.
 */
let Player = function() {

   let _unit = null;
   let _actionableArea = null;
   let _processingAction = false;
   let _inTargetSelection = false;

   let _actionableTypes = { 
      normal : "normal", 
      enemy: "enemy", 
      friendly: "friendly" 
   };

   function execute(units) {
      State.setUnits(Common.ID_PLAYER, units);
      State.setPlayerTurn(true);
      _processingAction = _inTargetSelection = false;
      rpgcode.registerMouseUp(_handleClick, true);
   }

   async function _handleClick(e) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`_handleClick: e=[${JSON.stringify(e)}], _processingAction=[${_processingAction}]`);
      }
      if (e.mouseButton !== 0) { // LEFT: 0
         console.debug("not a left mouse click, ignoring it!");
         return;
      }
      if ((!State.isPlayerTurn() || _processingAction) && !_inTargetSelection) {
         console.debug("player can't act at the moment!");
         return;
      }

      _processingAction = true;
      const turnOver = await _processAction(e);
      if (turnOver) {
         Common.switchTurn(Common.ID_AI);
         return;
      }
      _processingAction = false;
   }

   async function _processAction(e) {
      let allUsed = false;
      let x = e.realX / rpgcode.getScale();
      let y = e.realY / rpgcode.getScale();
      if (_withinBoard(x, y)) {
         let cell = _translateToCell(x, y);
         let actionable = _withinActionableArea(cell.x, cell.y);
         
         if (_unit && actionable && actionable.type !== _actionableTypes.friendly) {
            await _act(cell, actionable);
            if (!_inTargetSelection) {
               allUsed = State.useUnit(Common.ID_PLAYER, _unit);
               await _deselectUnit();
            }
         } else {
            if (_inTargetSelection) {
               allUsed = State.useUnit(Common.ID_PLAYER, _unit);
               _inTargetSelection = false;
            }
            if (!allUsed) {
               await _selectUnit(x, y);
            }
         }
      }

      // TEMP
      Overlay.drawHealthStats();
      
      return allUsed;
   }

   async function _act(cell, actionable) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`_act: cell=[${JSON.stringify(cell)}], actionable=[${JSON.stringify(actionable)}]`);
      }
      
      if (actionable.type === _actionableTypes.normal) {
         await _move(_unit, cell, false);
         _actionableArea = _checkForTargets(cell);
         if (_actionableArea.length) {
            console.debug(`_act: _actionableArea=[${JSON.stringify(_actionableArea)}]`);
            _actionableArea = _removeInaccessibleCells(_unit, _actionableArea);
            rpgcode.clearCanvas(Overlay.actionableAreaCanvasId);
            rpgcode.clearCanvas(Overlay.selectionCanvasId);
            rpgcode.clearCanvas(Overlay.pathCanvasId);
            Overlay.drawActionableArea(_actionableArea);
            Overlay.drawSelection(cell);
            _inTargetSelection = true;
         }
      } else if (actionable.type === _actionableTypes.enemy) {
         await _move(_unit, cell, true);
         const destroyed = await _attack(_unit, actionable.target);
         if (destroyed) {
            PathFinder.setWalkable(Math.floor(destroyed.x), Math.floor(destroyed.y), true);
            State.setCellOccupant(Math.floor(destroyed.x), Math.floor(destroyed.y), null);
            State.destroyUnit(destroyed.target.combatant, destroyed.target);
         }
         _inTargetSelection = false;
      } else if (actionable.type === _actionableTypes.friendly) {
         // Do nothing
      }

      if (!_inTargetSelection) {
         await Common.checkEndConditions();
      }
   }

   function _checkForTargets(cell) {
      let targets = [];
      // check north
      if (_getTarget(cell.x, cell.y - 1)) {
         targets.push(_getTarget(cell.x, cell.y - 1));
      }
      // check south
      if (_getTarget(cell.x, cell.y + 1)) {
         targets.push(_getTarget(cell.x, cell.y + 1));
      }
      // check east
      if (_getTarget(cell.x + 1, cell.y)) {
         targets.push(_getTarget(cell.x + 1, cell.y));
      }
      // check west
      if (_getTarget(cell.x - 1, cell.y)) {
         targets.push(_getTarget(cell.x - 1, cell.y));
      }
      return targets;
   }

   function _getTarget(x, y) {
      if (State.isCellValid(x, y)) {
         let occupant = State.getCellOccupant(x, y);
         if (occupant && occupant.id.includes(Common.ID_AI)) {
            return {x: x, y: y, type: _actionableTypes.enemy, target: State.getCellOccupant(x, y)};
         }
      }
      return null;
   }

   async function _selectUnit(x, y) {
      _deselectUnit();
      let found = _findUnit(x, y);
      if (found && !found.used) {
         _unit = found;
         _actionableArea = _getActionableArea(_unit);
         _actionableArea = _removeInaccessibleCells(_unit, _actionableArea);
         Overlay.drawActionableArea(_actionableArea);
         Overlay.drawSelection(_translateToCell(x, y));
      }
   }

   async function _deselectUnit() {
      rpgcode.clearCanvas(Overlay.actionableAreaCanvasId);
      rpgcode.clearCanvas(Overlay.selectionCanvasId);
      rpgcode.clearCanvas(Overlay.pathCanvasId);
      _unit = null;
   }

   function _translateToCell(x, y) {
      let fullCellSize = rpgcode.getGlobal("fullCellSize");
      let viewport = rpgcode.getViewport();
      x -= viewport.offsetX; 
      y -= viewport.offsetY;
      return {x: Math.floor(x / fullCellSize), y: Math.floor(y / fullCellSize)};
   }

   function _findUnit(x, y) {
      let cell = _translateToCell(x, y);
      let occupant = rpgcode.getGlobal("cells")[cell.y][cell.x];
      if (occupant && occupant.id.includes(Common.ID_PLAYER)) {
         return occupant;
      }
      return null;
   }

   function _withinBoard(x, y) {
      let board = rpgcode.getBoard();
      let viewport = rpgcode.getViewport();
      let fullCellSize = rpgcode.getGlobal("fullCellSize");
      let p = {x: x, y: y};
      let b = {
         x1: viewport.offsetX, 
         y1: viewport.offsetY, 
         x2: viewport.offsetX + (board.width * fullCellSize), 
         y2: viewport.offsetY + (board.height * fullCellSize)
      };
      return b.x1 <= p.x && p.x <= b.x2 && b.y1 <= p.y && p.y <= b.y2;
   }

   function _getActionableArea(_unit) {
      let loc = rpgcode.getSpriteLocation(_unit.id, true, false);
      let coverage = _unit.speed;

      let area = [];
      let width = 1;
      let change = 2;
      let centerX = Math.floor(loc.x);
      let centerY = Math.floor(loc.y);
      let y = Math.floor(centerY - coverage);
      let endY = Math.min(rpgcode.getBoard().height - 1, Math.floor(centerY + coverage));
      while (y <= endY) {
         for (let i = width; 0 < i; i--) {
            let x = Math.floor((centerX + i) - (width / 2));
            if (!State.isCellValid(x, y)) {
               continue; // Cell is out-of-bounds, don't process it
            }
            
            if (x !== centerX || y !== centerY) {
               if (PathFinder.isWalkableAt(x, y)) {
                  area.push({x: x, y: y, type: _actionableTypes.normal, occupant: null});
               } else {
                  let occupant = State.getCellOccupant(x, y);
                  if (occupant) {
                     if (occupant.id.includes(Common.ID_PLAYER)) {
                        area.push({x: x, y: y, type: _actionableTypes.friendly, target: occupant});
                     } else if (occupant.id.includes(Common.ID_AI)) {
                        area.push({x: x, y: y, type: _actionableTypes.enemy, target: occupant});
                     }                    
                  }
               }
            } else {
               change = -2;
            }
         }

         // Deal with attacks on bounds of area
         _addEnemyAttackBounds(Math.floor((centerX) - (width / 2)), y, area);
         _addEnemyAttackBounds(Math.ceil((centerX) + (width / 2)), y, area);
         
         width += change;
         y++;
      }
      return [...new Set(area)]; // Enforce uniqueness https://stackoverflow.com/a/9229821
   }

   function _addEnemyAttackBounds(x, y, area) {
      if (State.isCellValid(x, y) && State.getCellOccupant(x, y)) {
         let occupant = State.getCellOccupant(x, y);
         if (occupant.id.includes(Common.ID_AI)) {
            area.push({x: x, y: y, type: _actionableTypes.enemy, target: occupant});
         }
      }
   }

   function _withinActionableArea(x, y) {
      if (_actionableArea) {
         for (let i = 0; i < _actionableArea.length; i++) {
            let p = _actionableArea[i];
            if (p.x === x && p.y === y) {
               return p;
            }
         }
      }
      return null;
   }

   function _removeInaccessibleCells(_unit, _actionableArea) {
      let accessible = [];
      let coverage = (_unit.speed + _unit.range) + 1; // +1 for attacks on bounds
      let start = rpgcode.getSpriteLocation(_unit.id, true, false);
      for (let i = 0; i < _actionableArea.length; i++) {
         let p = _actionableArea[i];
         let path = PathFinder.findPath(start, p);
         if (path && path.length <= coverage) {
            accessible.push(p);
         }
      }
      return accessible;
   }

   async function _move(unit, goal, isAttack) {
      let start = rpgcode.getSpriteLocation(unit.id, true, false);
      let path = PathFinder.findPath(start, goal);
      if (isAttack) {
         path.pop(); // Remove target's actual position from end
      }
      
      if (1 < path.length) {
         if (rpgcode.getGlobal("debug")) {
            Overlay.drawPath(path);
         }

         Overlay.clearCell(rpgcode.getSpriteLocation(unit.id, true, false));
         await Movement.move(unit.id, path.shift(), path);
         
         let end = rpgcode.getSpriteLocation(unit.id, true, false);
         PathFinder.setWalkable(Math.floor(start.x), Math.floor(start.y), true);
         PathFinder.setWalkable(Math.floor(end.x), Math.floor(end.y), false);
         State.setCellOccupant(Math.floor(start.x), Math.floor(start.y), null);
         State.setCellOccupant(Math.floor(end.x), Math.floor(end.y), unit);
      }
   }

   async function _attack(unit, target) {
      if (rpgcode.getGlobal("debug")) {
         console.debug(`_attack: unit=[${JSON.stringify(unit)}], target=[${JSON.stringify(target)}]`);
      }
      return await Attack.attack(unit, target);
   }

   return {
      name: "player",
      execute: execute
   };

}();