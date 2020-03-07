/**
 * This module contains the core drawing functions for
 * highlighting targets, paths, walkable areas etc.
 */
let Overlay = function() {

   let selectionCanvasId = "selection";
   let _selectionCanvas = null;
   let _selectionLineWidth = 2;

   let actionableAreaCanvasId = "actionableArea";
   let _actionableAreaCanvas = null;

   let pathCanvasId = "path";
   let _pathCanvas = null;

   let statsCanvasId = "stats";
   let _statsCanvas = null;

   let notificationCanvasId = "notification";
   let _notificationCanvas = null;

   let debugCanvasId = "debug";
   let _debugCanvas = null;

   function init() {
      const screenWidth = rpgcode.getGlobal("screenWidth");
      const screenHeight = rpgcode.getGlobal("screenHeight");
      const fullCellSize = rpgcode.getGlobal("fullCellSize");

      rpgcode.createCanvas(fullCellSize + 8, fullCellSize + 8, selectionCanvasId);
      rpgcode.createCanvas(screenWidth, screenHeight, actionableAreaCanvasId);
      rpgcode.createCanvas(screenWidth, screenHeight, pathCanvasId);
      rpgcode.createCanvas(screenWidth, screenHeight, statsCanvasId);
      rpgcode.createCanvas(screenWidth, screenHeight, notificationCanvasId);
      rpgcode.createCanvas(screenWidth, screenHeight, debugCanvasId);
   }

   function drawInfo(target, priority) {
      var tloc = rpgcode.getSpriteLocation(target.id, false, true);
      rpgcode.setColor(255, 255, 255, 1);
      rpgcode.setFont(8, "Lucida Console");
      var info = target.hp + "/" + target.cover + "/" + priority;
      var width = rpgcode.measureText(info).width;

      rpgcode.drawText((tloc.x - (width / 4)), (tloc.y - 12), info, debugCanvasId);
      rpgcode.renderNow(debugCanvasId);
   }

   async function highlightTargets(targets) {
      const start = new Date();
      do {
         drawTargets(targets);
         await new Promise(r => setTimeout(r, 300));
         clearTargets();
         await new Promise(r => setTimeout(r, 300));
      } while (new Date() - start < 1500);
      await new Promise(r => setTimeout(r, 500));
   }

   function drawTargets(targets) {
      for (const target of targets) {
         drawTarget(target);
      }
   }

   function drawTarget(target) {
      var tloc = rpgcode.getSpriteLocation(target.id, false, true);
      rpgcode.setColor(255, 255, 0, 1.0);
      rpgcode.drawCircle(tloc.x, tloc.y, 14, notificationCanvasId);
      rpgcode.renderNow(notificationCanvasId);
   }

   function clearTargets() {
      rpgcode.clearCanvas(notificationCanvasId);
   }

   function drawPath(path) {
      rpgcode.setColor(0, 255, 0, 1.0);
      var viewport = rpgcode.getViewport();
      var fullCellSize = rpgcode.getGlobal("fullCellSize");
      var halfCellSize = fullCellSize / 2;
      for (var i = 1; i < path.length; i++) {
         var x = (path[i][0] * fullCellSize) + viewport.offsetX;
         var y = (path[i][1] * fullCellSize) + viewport.offsetY;
         rpgcode.drawCircle(x + halfCellSize, y + halfCellSize, 5, pathCanvasId);
      }
      rpgcode.renderNow(pathCanvasId);
   }

   function drawSelection(cell) {
      var lineWidth = 2;
      var viewport = rpgcode.getViewport();
      var fullCellSize = rpgcode.getGlobal("fullCellSize");
      x = cell.x * fullCellSize + viewport.offsetX;
      y = cell.y * fullCellSize + viewport.offsetY;
      
      rpgcode.setColor(255, 255, 255, 1.0);
      rpgcode.setCanvasPosition(x- _selectionLineWidth, y - _selectionLineWidth, selectionCanvasId);
      rpgcode.drawRect(_selectionLineWidth, _selectionLineWidth, fullCellSize, fullCellSize, lineWidth, selectionCanvasId);
      
      rpgcode.renderNow(selectionCanvasId);
   }

   function drawActionableArea(area) {
      rpgcode.clearCanvas(actionableAreaCanvasId);
      
      var fullCellSize = rpgcode.getGlobal("fullCellSize");
      var viewport = rpgcode.getViewport();
      for (var i = 0; i < area.length; i++) {
         var p = area[i];
         if (p.type === "normal") {
            rpgcode.setColor(0, 255, 0, 0.25);
         } else if (p.type === "friendly") {
            rpgcode.setColor(0, 0, 255, 0.25);
         } else if (p.type === "enemy") {
            rpgcode.setColor(255, 0, 0, 0.25);
         }
         var drawX = (p.x * fullCellSize) + viewport.offsetX;
         var drawY = (p.y * fullCellSize) + viewport.offsetY;
         rpgcode.fillRect(drawX, drawY, fullCellSize, fullCellSize, actionableAreaCanvasId);
      }

      rpgcode.renderNow(actionableAreaCanvasId);
   }

   function drawHealthStats() {
      rpgcode.clearCanvas(statsCanvasId);
      
      let units = State.getUnits(Common.ID_PLAYER);
      for (let unit in units) {
         if (Object.prototype.hasOwnProperty.call(units, unit)) {
            drawHealth(units[unit]);
         }
      }

      units = State.getUnits(Common.ID_AI);
      for (let unit in units) {
         if (Object.prototype.hasOwnProperty.call(units, unit)) {
            drawHealth(units[unit]);
         }
      }
   }

   function drawHealth(unit) {
      if (9 < unit.hp) {
         return;
      }
      
      rpgcode.setColor(255, 255, 255, 1);
      rpgcode.setFont(10, "Lucida Console");
      var text = Math.floor(unit.hp);
      var loc = rpgcode.getSpriteLocation(unit.id, false, true);

      // TODO: Add stroke text to rpgcode API
      let canvasId = statsCanvasId;
      let instance = rpgcode.canvases[canvasId];
      let x = (loc.x * rpgcode.getScale()) + (6 * rpgcode.getScale());
      let y = (loc.y * rpgcode.getScale()) + (10 * rpgcode.getScale());
      let context = instance.canvas.getContext("2d");
      context.imageSmoothingEnabled = rpgcode.imageSmoothingEnabled;
      let rgba = rpgcode.rgba;
      context.font = rpgcode.font;
      context.strokeStyle = "black";
      context.lineWidth = 4;
      context.strokeText(text, x, y);      
      context.globalAlpha = rpgcode.globalAlpha;
      context.fillStyle = rpgcode.gradient ? rpgcode.gradient : "rgba(" + rgba.r + "," + rgba.g + "," + rgba.b + "," + rgba.a + ")";
      context.fillText(text, x, y);
      
      rpgcode.renderNow(statsCanvasId);
   }

   function drawObjectiveMarker(cell) {
      var lineWidth = 2;
      var viewport = rpgcode.getViewport();
      var fullCellSize = rpgcode.getGlobal("fullCellSize");
      x = cell.x * fullCellSize + viewport.offsetX;
      y = cell.y * fullCellSize + viewport.offsetY;
      
      rpgcode.setColor(0, 255, 0, 1.0);
      rpgcode.setCanvasPosition(x- _selectionLineWidth, y - _selectionLineWidth, selectionCanvasId);
      rpgcode.drawRect(_selectionLineWidth, _selectionLineWidth, fullCellSize, fullCellSize, lineWidth, selectionCanvasId);
      
      rpgcode.renderNow(selectionCanvasId);
   }

   function clearCell(cell) {
      var lineWidth = 2;
      var fullCellSize = rpgcode.getGlobal("fullCellSize");
      var viewport = rpgcode.getViewport();
      var x = Math.round((Math.floor(cell.x) * fullCellSize)) + viewport.offsetX;
      var y = Math.round((Math.floor(cell.y) * fullCellSize)) + viewport.offsetY;
      x -= lineWidth;
      y -= lineWidth;
      var width = fullCellSize + (lineWidth * 2);
      var height = width;
     
      // TODO: add clearRect to rpgcode API
      let canvasId = statsCanvasId;
      let instance = rpgcode.canvases[canvasId];
      var ctx = instance.canvas.getContext("2d");
      ctx.fillStyle = "red";
      ctx.clearRect(x * rpgcode.getScale(), y * rpgcode.getScale(), width * rpgcode.getScale(), height * rpgcode.getScale());

      rpgcode.renderNow(statsCanvasId);
   }

   return {
      name: "Overlay",
      // properties
      selectionCanvasId: selectionCanvasId,
      actionableAreaCanvasId: actionableAreaCanvasId,
      pathCanvasId: pathCanvasId,
      statsCanvasId: statsCanvasId,
      notificationCanvasId: notificationCanvasId,
      debugCanvasId: debugCanvasId,
      // functions
      init: init,
      drawInfo: drawInfo,
      highlightTargets: highlightTargets,
      drawTargets: drawTargets,
      drawTarget: drawTarget,
      clearTargets: clearTargets,
      drawPath: drawPath,
      drawSelection: drawSelection,
      drawActionableArea: drawActionableArea,
      drawHealthStats: drawHealthStats,
      drawHealth: drawHealth,
      drawObjectiveMarker: drawObjectiveMarker,
      clearCell: clearCell
   };

}();