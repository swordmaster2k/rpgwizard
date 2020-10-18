/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard */

function CanvasRenderer() {
  this.renderNowCanvas = document.createElement("canvas");
  this.renderNowCanvas.width = Crafty.viewport._width;
  this.renderNowCanvas.height = Crafty.viewport._height;
}

CanvasRenderer.prototype.render = function (context) {
  var x = -Crafty.viewport._x;
  var y = -Crafty.viewport._y;
  
  var canvases = Core.getInstance().rpgcodeApi.canvases;
  for (var property in canvases) {
    if (canvases.hasOwnProperty(property)) {
      var element = canvases[property];
      if (element.render) {
        context.drawImage(element.canvas, x, y);
      }
    }
  }
};