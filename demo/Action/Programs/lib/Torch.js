if (rpgcode.canvases[Lighting.canvasId]) {
   var loc = rpgcode.getCharacterLocation(false, true);
   var oldLoc = rpgcode.getGlobal(Lighting.canvasId);
   if (Lighting.needRedraw || !oldLoc || oldLoc.x !== loc.x || oldLoc.y !== loc.y) {
      rpgcode.setGlobal(Lighting.canvasId, loc);
      var ctx = rpgcode.canvases[Lighting.canvasId].canvas.getContext('2d');
      var scale = rpgcode.getScale();
      Lighting.drawCutOutLight(ctx, loc.x * scale, loc.y * scale, 75 * scale);
   }
}
