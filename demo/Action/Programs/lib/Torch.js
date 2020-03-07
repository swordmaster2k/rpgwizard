if (rpgcode.canvases[Lighting.canvasId]) {
   var location = rpgcode.getCharacterLocation(false, true);
   var oldLocation = rpgcode.getGlobal(Lighting.canvasId);
   if (Lighting.needRedraw || !oldLocation || oldLocation.x !=  location.x || oldLocation.y != location.y) {
      rpgcode.setGlobal(Lighting.canvasId, location);
      var ctx = rpgcode.canvases[Lighting.canvasId].canvas.getContext('2d');
      var scale = rpgcode.getScale();
      Lighting.drawCutOutLight(ctx, location.x * scale, location.y * scale, 75 * scale);
   }
}
