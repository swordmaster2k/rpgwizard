/**
 * This module contains common functions used to setup missions.
 */
let MissionBootstrap = function() {

   function bootstrap() {
      setupPathFinding();
   
      const sprites = Object.values(rpgcode.getBoard().sprites);
      let units = setupNpcs(sprites, Common.ID_PLAYER);
      let targets = setupNpcs(sprites, Common.ID_AI);
   
      // TEMP
      Overlay.init();
      Overlay.drawHealthStats();
   
      State.setUnits(Common.ID_AI, targets);
      Player.execute(units);
   }

   function setupPathFinding() {
      let layout = "";
      const board = rpgcode.getBoard();
      for (let y = 0; y < board.height; y++) {
         let line = "";
         for (let x = 0; x < board.width; x++) {
            let data = null;
            for (let z = 0; z < board.layers.length; z++) {
               const newData = rpgcode.getTileData(x, y, z);
               if (data === null && newData !== null) {
                  data = newData;
               } else if (newData !== null && newData.type === "wall") {
                  data = newData;
               }
            }
            line += (data === null || data.type === "wall" ? "#" : " "); 
         }
         layout += line + "\n";
      }

      console.log(layout);

      let matrix = [];
      const lines = layout.split("\n");
      for (let i = 0; i < lines.length; i++) {
         let row = [];
         const characters = lines[i].split("");
         for (let j = 0; j < characters.length; j++) {
            const character = characters[j];
            if (character === "#") {
               row.push(1);
            } else {
               row.push(0);
            }
         }
         matrix.push(row);
      }
   
      PathFinder.init(matrix);
   
      let cells = new Array(matrix.length).fill(null).map(() => new Array(matrix[0].length).fill(null));
      rpgcode.setGlobal("cells", cells);
   }

   function setupNpcs(sprites, combatant) {
      let npcs = {};
      for (let v of sprites) {
         if (v.sprite.id.includes(combatant)) {
            npcs[v.sprite.id] = setupGeneric(v.sprite, combatant);
         }
      }
      return npcs;
   }
   
   function setupGeneric(sprite, combatant) {
      let target = {
         id: sprite.id,
         combatant: combatant,
         type: "infantry",
         cover: 0,
         hp: 10,
         range: 1,
         speed: 3
      };
   
      let fullCellSize = rpgcode.getGlobal("fullCellSize");
      let cellX = Math.floor(sprite.x / fullCellSize);
      let cellY = Math.floor(sprite.y / fullCellSize);
      PathFinder.setWalkable(cellX, cellY, false);
      rpgcode.getGlobal("cells")[cellY][cellX] = target;
   
      return target;
   }

   return {
      name: "MissionBootstrap",
      bootstrap: bootstrap
   };

}();