/* global rpgtoolkit */

function CanvasRenderer() {
  this.renderNowCanvas = document.createElement("canvas");
  this.renderNowCanvas.width = Crafty.viewport._width;
  this.renderNowCanvas.height = Crafty.viewport._height;
}

CanvasRenderer.prototype.render = function (context) {
  var x = -Crafty.viewport._x;
  var y = -Crafty.viewport._y;
  
  var canvases = rpgtoolkit.rpgcodeApi.canvases;
  for (var property in canvases) {
    if (canvases.hasOwnProperty(property)) {
      var element = canvases[property];
      if (element.render) {
        context.drawImage(element.canvas, x, y);
      }
    }
  }
};