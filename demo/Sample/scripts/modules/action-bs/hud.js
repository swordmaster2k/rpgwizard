export function setup() {
   const heartCanvasId = "heartCanvas";
   rpg.createCanvas(heartCanvasId, rpg.getViewport().width, rpg.getViewport().height);

   setInterval(function() {
      const player = rpg.getSprite("player");
      if (!player) {
         return;
      }
      
      const hearts = player.data.health;
      if (hearts < 1) {
         rpg.restart();
      }

      rpg.clear(heartCanvasId);
      for (let i = 0; i < hearts; i++) {
         rpg.drawImagePart(heartCanvasId, "objects.png", 64, 0, 16, 16, 8 + (16 * i), 8, 16, 16, 0);
      }
      rpg.render(heartCanvasId);
   }, 50);
}
