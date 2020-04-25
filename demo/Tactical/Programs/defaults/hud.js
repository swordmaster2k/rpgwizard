/* global rpgcode, gui */
let hud = new HUD();

/**
 * The builtin HUD system.
 * 
 * @class
 * @constructor
 * 
 * @returns {HUD}
 */
function HUD() {
   
}

/**
 *  Shows the default title screen system based the supplied config.
 * 
 * @example
 * let config = {
 *    life: {
 *       image: "life.png",
 *       width: 32,
 *       height: 32
 *    }
 * };
 * hud.show(config, function() {rpgcode.log("hud closed";});
 * 
 * @param {Object} config
 * @param {Callback} callback
 * @returns {undefined}
 */
HUD.prototype.show = function(config, callback) {
   this._loadAssets(config, function() {
      this._callback = callback;
      this._setup(config);
   }.bind(this));
};

HUD.prototype.close = function() {
   this._end();
};

HUD.prototype._end = function() {
   clearInterval(this._updateInterval);
   rpgcode.clearCanvas(this.background.canvasId);
   rpgcode.destroyCanvas(this.background.canvasId);
};

HUD.prototype._setup = function(config) {
   this._config = config;
   this._state = {};
   this.background = {
      canvasId: "HUD.backgroundCanvas",
      width: rpgcode.getViewport().width,
      height: rpgcode.getViewport().height,
      x: 0,
      y: 0
   };
   rpgcode.createCanvas(this.background.width, this.background.height, this.background.canvasId);
   rpgcode.setCanvasPosition(this.background.x, this.background.y, this.background.canvasId);
   this._updateInterval = setInterval(this._update.bind(this), 50); // Update the HUD every 50ms
};

HUD.prototype._loadAssets = function(config, callback) {
   let assets = {
      "images": [config.life.image]
   };
   rpgcode.loadAssets(assets, callback.bind(this));
};

HUD.prototype._update = function() {
   let hearts = rpgcode.getCharacter().health;
   if (hearts < 1) {
      rpgcode.restart();
   }

   let width = this._config.life.width;
   let height = this._config.life.height;
   rpgcode.clearCanvas(this.background.canvasId);
   for (let i = 0; i < hearts; i++) {
      rpgcode.setImage(this._config.life.image, (width / 2) + (width * i), 0, width, height, this.background.canvasId);
   }
   rpgcode.renderNow(this.background.canvasId);
};