import * as lighting from "./lighting.js";

export async function setup() {
   await lighting.setup();
   
   setInterval(function() {

      if (!rpg.getSprite("player")) {
         return;
      }

      const loc = rpg.getSpriteLocation("player");
      const oldLoc = rpg.getGlobal("torch.oldLoc");
      if (lighting.state.needRedraw || !oldLoc || oldLoc.x !== loc.x || oldLoc.y !== loc.y) {
         rpg.setGlobal("torch.oldLoc", loc);
         const ctx = rpg.canvases[lighting.state.canvasId].canvasElement.getContext("2d");
         const scale = rpg.getScale();
         lighting.drawCutOutLight(ctx, loc.x * scale, loc.y * scale, 75 * scale);
      }
      
   }, 50);
}
