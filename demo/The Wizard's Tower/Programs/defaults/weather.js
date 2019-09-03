/*
   Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:
   The above copyright notice and this permission notice shall be included in
   all copies or substantial portions of the Software.
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
   THE SOFTWARE.

   Credit given, where credit due, sourced from:
      * rain effect: https://codepen.io/ruigewaard/pen/JHDdF
      * snow effect: https://codepen.io/teetteet/pen/Jcvnf
*/
var weather = new Weather();

function Weather() {
   this._canvasId = "weather.canvas";

   // Rain config.
   this._isRaining = false;
   this._rainSound = "";

   // Snow config.
   this._isSnowing = false;

   // Audio elements.
   this._rainAudio = null;

   // Internal.
   this._rainVolume = 1.0;
   this._rainStrokeStyle = "rgba(174, 194, 224, 0.5)";
   this._rainLineWidth = 1;
   this._rainLineCap = "round";
   this._maxParts = 1000;
   this._partModifier = 0;
}

/**
 *  Shows the weather system based the supplied config.
 * 
 * @example
 *  var config = {
 *    rain: {
 *       sound: "rain.wav"
 *    }
 * };
 * weather.show(config, function() {console.log("weather stopped");});
 * rpgcode.endProgram();
 * 
 * @param {Object} config
 * @param {Callback} callback
 * @returns {undefined}
 */
Weather.prototype.show = function(config, callback) {
   this._loadAssets(config, function() {
      this._callback = callback;
      this._setup(config);
      callback();
   }.bind(this));
};

/**
 * Closes down the weather system, and destroys any canvases. 
 * Only use this if you want to free up resources.
 */
Weather.prototype.close = function() {
   if (this._isRaining) {
      this.rain(false);
   } else if (this._isSnowing) {
      this.snow(false);
   }
};

Weather.prototype._setup = function(config) {
   this._config = config;
   rpgcode.createCanvas(rpgcode.getViewport().width, rpgcode.getViewport().height, this._canvasId);
   if (this._config.rain) {
      this.rain(true);
      if (this._config.rain.sound) {
         rpgcode.playSound(this._config.rain.sound, true, 1.0);
      }
   } else if (this._config.snow) {
      this.snow(true);
   }
};

Weather.prototype._loadAssets = function(config, callback) {
   var assets = {"audio": {}};
   if (config.rain && config.rain.sound) {
      assets.audio["weather.rainSound"] = config.rain.sound;
   }
   rpgcode.loadAssets(assets, callback.bind(this));
};

Weather.prototype.setRainIntensity = function(intensity) {
   if (intensity < 0) {
      intensity = 0;
   } else if (intensity > 1.0) {
      intensity = 1.0;
   }
   this._partModifier = this._maxParts - (this._maxParts * intensity);
   this._rainVolume = intensity;
   if (this._rainAudio) {
      this._rainAudio.volume = this._rainVolume;
   }
};

Weather.prototype.rain = function(toggle) {
   this._isRaining = toggle;
   this._isSnowing = false;
   if (toggle) {
      if (this._rainSound) {
         this._rainAudio = rpgcode.playSound(this._rainSound, true, this._rainVolume);
      }
      this._drawRain();
   } else {
      if (this._rainSound) {
         rpgcode.stopSound(this._rainSound);
         this._rainAudio = null;
      }
      rpgcode.clearCanvas(weather._canvasId);
   }
};

Weather.prototype.snow = function(toggle) {
   this._isSnowing = toggle;
   this._isRaining = false;
   if (toggle) {
      this._drawSnow();
   } else {
      rpgcode.clearCanvas(weather._canvasId);
   }
};

Weather.prototype._drawRain = function() {
   var canvas = rpgcode.canvases[this._canvasId].canvas;
   var ctx = canvas.getContext("2d");
   var width = canvas.width;
   var height = canvas.height;

   ctx.strokeStyle = this._rainStrokeStyle;
   ctx.lineWidth = this._rainLineWidth;
   ctx.lineCap = this._rainLineCap;

   var init = [];
   var maxParts = this._maxParts;
   for (var a = 0; a < maxParts; a++) {
      init.push({
         x: Math.random() * width,
         y: Math.random() * height,
         l: Math.random() * 1,
         xs: -4 + Math.random() * 4 + 2,
         ys: Math.random() * 10 + 10
      });
   }

   var particles = [];
   for (var b = 0; b < maxParts; b++) {
      particles[b] = init[b];
   }

   function draw() {
      if (weather._isRaining) {
         ctx.clearRect(0, 0, width, height);

         // Chance of lighting.
         if (rpgcode.getRandom(0, 200) === 100) {
            ctx.globalAlpha = 0.2;
            ctx.fillStyle = "white";
         } else {
            ctx.globalAlpha = 0.3;
            ctx.fillStyle = "black";
         }
         ctx.beginPath();
         ctx.fillRect(0, 0, width, height);
         ctx.fill();

         // Rain particles.
         ctx.globalAlpha = 1.0;
         for (var c = 0; c < particles.length - weather._partModifier; c++) {
            var p = particles[c];
            ctx.beginPath();
            ctx.moveTo(p.x, p.y);
            ctx.lineTo(p.x + p.l * p.xs, p.y + p.l * p.ys);
            ctx.stroke();
         }
         move();
         rpgcode.renderNow(weather._canvasId);
         setTimeout(draw, 50);
      } else {
         rpgcode.clearCanvas(weather._canvasId);
      }
   }

   function move() {
      for (var b = 0; b < particles.length - weather._partModifier; b++) {
         var p = particles[b];
         p.x += p.xs;
         p.y += p.ys;
         if (p.x > width || p.y > height) {
            p.x = Math.random() * width;
            p.y = -20;
         }
      }
   }

   draw();
};
  
Weather.prototype._drawSnow = function() {
   var canvas = rpgcode.canvases[this._canvasId].canvas;
   var ctx = canvas.getContext("2d");
   var width = canvas.width;
   var height = canvas.height;
   
   var mp = 50;
   var particles = [];
   for (var i = 0; i < mp; i++) {
      particles.push({
         x: Math.random() * width,
         y: Math.random() * height,
         r: Math.random() * 4 + 1,
         d: Math.random() * mp
      })
   }

   function draw() {
      if (weather._isSnowing) {
         ctx.clearRect(0, 0, width, height);

         ctx.globalAlpha = 0.2;
         ctx.fillStyle = "white";
         ctx.beginPath();
         ctx.fillRect(0, 0, width, height);
         ctx.fill();

         ctx.globalAlpha = 1.0;
         ctx.fillStyle = "rgba(255, 255, 255, 0.8)";
         ctx.beginPath();
         for (var i = 0; i < mp; i++) {
            var p = particles[i];
            ctx.moveTo(p.x, p.y);
            ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2, true);
         }
         ctx.fill();
         update();

         rpgcode.renderNow(weather._canvasId);
         setTimeout(draw, 25);
      } else {
         rpgcode.clearCanvas(weather._canvasId);
      }
   }

   var angle = 0;

   function update() {
      angle += 0.01;
      for (var i = 0; i < mp; i++) {
         var p = particles[i];
         p.y += Math.cos(angle + p.d) + 1 + p.r / 2;
         p.x += Math.sin(angle) * 2;

         if (p.x > width + 5 || p.x < -5 || p.y > height) {
            if (i % 3 > 0) {
               particles[i] = {
                  x: Math.random() * width,
                  y: -10,
                  r: p.r,
                  d: p.d
               };
            } else {
               if (Math.sin(angle) > 0) {
                  particles[i] = {
                     x: -5,
                     y: Math.random() * height,
                     r: p.r,
                     d: p.d
                  };
               } else {
                  particles[i] = {
                     x: width + 5,
                     y: Math.random() * height,
                     r: p.r,
                     d: p.d
                  };
               }
            }
         }
      }
   }

   draw();
}