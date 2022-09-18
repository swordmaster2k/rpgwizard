const hudCanvasId = "hudCanvas";
const redrawInterval = 50;

export function setup() {
   rpg.createCanvas(hudCanvasId, rpg.getViewport().width, rpg.getViewport().height);

   setInterval(function() {
      const player = rpg.getSprite("player");
      if (!player) {
         return;
      }
      if (rpg.getGlobal("pause.input")) {
         return;
      }

      rpg.clear(hudCanvasId);
      
      _drawHearts(player);
      _drawCoins(player);
      
      rpg.render(hudCanvasId);
      
   }, redrawInterval);
}

function _drawHearts(player) {
   const hearts = rpg.getGlobal("player.health");
   if (hearts < 1) {
      rpg.restart(); // TODO: Move this elsewhere
   }
   
   for (let i = 0; i < hearts; i++) {
      rpg.drawImagePart(hudCanvasId, "objects.png", 64, 0, 16, 16, 8 + (16 * i), 8, 16, 16, 0);
   }
}

function _drawCoins(player) {
   const coins = rpg.getGlobal("player.coins");
   rpg.drawImagePart(hudCanvasId, "objects.png", 0, 64, 16, 16, 8, 32, 16, 16, 0);
   
   const text = coins < 10 ? "00" + coins : "0" + coins;
   rpg.setFont(12, "Lucida Console");
   rpg.drawText(hudCanvasId, 28, 44, text);
}
