var Lighting = new lighting();

function lighting() {   
   this.canvasId = "Lighting.canvas";
   
   // Default values.
   this.change = 0.001;
   this.roomAlpha = 0.9;
   this.innerCircleAlpha = 0.9;
   this.outerCircleAlpha = 0.45;

   this.needRedraw = false;
}

lighting.prototype.setup = function () {
   rpgcode.createCanvas(rpgcode.getViewport().width, rpgcode.getViewport().height, this.canvasId);
};

lighting.prototype.fill = function() {
   var ctx = rpgcode.canvases[Lighting.canvasId].canvas.getContext('2d');
   ctx.globalCompositeOperation = 'source-over';
   ctx.globalAlpha = 1.0;
   ctx.fillStyle = "black";
      
   ctx.beginPath();
   ctx.fillRect(0, 0, rpgcode.getViewport().width, rpgcode.getViewport().height);
   ctx.fill();
};

lighting.prototype.clear = function() {
   rpgcode.clearCanvas(Lighting.canvasId);
};

lighting.prototype.darken = function () {
   this.needRedraw = false;
   if (this.roomAlpha + this.change <= 0.9) {
      this.roomAlpha += this.change;
      this.needRedraw = true;
   }
   if (this.innerCircleAlpha + this.change <= 0.9) {
      this.innerCircleAlpha += this.change;
      this.needRedraw = true;
   }
   if (this.outerCircleAlpha + (this.change / 2) <= 0.45) {
      this.outerCircleAlpha += (this.change / 2);
      this.needRedraw = true;
   }

   return this.needRedraw;
};

lighting.prototype.lighten = function () {
   this.needRedraw = false;
   if (this.roomAlpha - this.change >= 0) {
      this.roomAlpha -= this.change;
      this.needRedraw = true;
   }
   if (this.innerCircleAlpha - this.change >= 0) {
      this.innerCircleAlpha -= this.change;
      this.needRedraw = true;
   }
   if (this.outerCircleAlpha - (this.change / 2) >= 0) {
      this.outerCircleAlpha -= (this.change / 2);
      this.needRedraw = true;
   }

   return this.needRedraw;
};

lighting.prototype.drawCutOutLight = function (ctx, x, y, radius) {
   if (this.needRedraw) {
      this.needRedraw = false;
   }
   rpgcode.clearCanvas(this.canvasId);
   
   roomAlpha = this.roomAlpha;
   innerCircleAlpha = this.innerCircleAlpha;
   outerCircleAlpha = this.outerCircleAlpha;
   var backgroundAlpha;
   ctx.globalCompositeOperation = 'source-over';
   ctx.globalAlpha = roomAlpha;
   ctx.fillStyle = "black";
   
   ctx.beginPath();
   ctx.fillRect(0, 0, rpgcode.getViewport().width, rpgcode.getViewport().height);
   ctx.fill();

   ctx.globalCompositeOperation = 'destination-out';
   ctx.beginPath();
   ctx.globalAlpha = innerCircleAlpha;
   ctx.arc(x, y, radius / 1.5, 0, Math.PI * 2);
   ctx.fill();
   
   ctx.beginPath();
   ctx.globalAlpha = outerCircleAlpha;
   ctx.arc(x, y, radius, 0, Math.PI * 2);
   ctx.fill();
   rpgcode.renderNow(this.canvasId);
};
