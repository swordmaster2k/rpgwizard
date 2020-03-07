/**
 * This module is responsible for moving units along
 * a provided path to an end poin.
 */
let Movement = function() {

   const fullCellSize = rpgcode.getGlobal("fullCellSize");
   const halfCellSize = fullCellSize / 2;

   function move(spriteId, point, path) {
      return new Promise((resolve, reject) => {
         rpgcode.moveSpriteTo(
            spriteId,
            translateAxis(point[0]), translateAxis(point[1]),
            rpgcode.getGlobal("pointTravelTime"),
            async function() {
               if (0 < path.length) {
                  await move(spriteId, path.shift(), path);
               }
               resolve();
            }
         );
      });
   }

   function translateAxis(axis) {
      return (axis * fullCellSize) + halfCellSize;
   }

   return {
      name: "Movement",
      move: move
   };

}();