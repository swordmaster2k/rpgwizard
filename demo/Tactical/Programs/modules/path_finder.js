/**
 * This module is responsible for finding paths to a requested
 * goal on the battlefield grid. It can also set the walkable
 * state of points.
 * 
 * It is a high-level wrapper over lib/PathingFinding.js file.
 */
let PathFinder = function() {

   let grid = null;
   let finder = null;

   function init(matrix) {
      grid = new PF.Grid(matrix);
      finder = new PF.AStarFinder({
         allowDiagonal: false,
         dontCrossCorners: true
      });
   }

   function printGrid() {
      console.log(grid);
   }

   function isWalkableAt(x, y) {
      return grid.isWalkableAt(x, y);
   }

   function setWalkable(x, y, walkable) {
      grid.setWalkableAt(x, y, walkable);
   }

   function findPath(start, goal) {
      const walkable = isWalkableAt(Math.floor(goal.x), Math.floor(goal.y));
      if (!walkable) {
         setWalkable(Math.floor(goal.x), Math.floor(goal.y), true);
      }
      let path = finder.findPath(
         Math.floor(start.x), Math.floor(start.y),
         Math.floor(goal.x), Math.floor(goal.y),
         grid.clone()
      );
      if (!walkable) {
         setWalkable(Math.floor(goal.x), Math.floor(goal.y), false);
      }
      return path;
   }

   return {
      name: "PathFinder",
      init: init,
      printGrid: printGrid,
      isWalkableAt: isWalkableAt,
      setWalkable: setWalkable,
      findPath: findPath
   };

}();