/* global rpgcode, gui */
let inventory = new Inventory();

/**
 * The builtin inventory system.
 * 
 * @class
 * @constructor
 * 
 * @returns {Inventory}
 */
function Inventory() {
   this.context = {
      MENU: "MENU"
   };
}

/**
 *  Shows the default inventory system based the supplied config.
 * 
 * @example
 * if (inventory.visible) {
 *    // Close the inventory if it is visible
 *    inventory.close();
 *    rpgcode.endProgram();
 * } else {
 *    // Configure and show the inventory.
 *    let config = {
 *       "backgroundImage": "startscreen.png", 
 *       "inventoryMusic": "intro.ogg"
 *    };
 *    await inventory.show(config);
 *    rpgcode.endProgram();
 * }
 * 
 * @param {Object} config
 * @returns {undefined}
 */
Inventory.prototype.show = async function(config) {
   return new Promise((resolve, reject) => {
      this.visible = true;
      this._loadAssets(config, async function() {
         this._callback = function() {
            resolve();
         };
         this._setup(config);
         if (this._config.inventoryMusic) {
            rpgcode.stopSound(rpgcode.getBoard().backgroundMusic);
            rpgcode.playSound("inventory.music", true, 1.0);
         }
      }.bind(this));
   });
};

Inventory.prototype.close = function() {
   this._end();
};

Inventory.prototype._end = function() {
   if (this._config.inventoryMusic) {
      rpgcode.stopSound("inventory.music");
      rpgcode.playSound(rpgcode.getBoard().backgroundMusic, true, 1.0);
   }
   this.frame.destroy();
   rpgcode.clearCanvas(this.background.canvasId);
   rpgcode.destroyCanvas(this.background.canvasId);
   this.visible = false;
   this._callback();
};

Inventory.prototype._setup = function(config) {
   this._config = config;
   this._state = {
      processingInput: false,
      currentContext: this.context.MENU
   };
   this.background = {
      canvasId: "Inventory.backgroundCanvas",
      width: rpgcode.getViewport().width,
      height: rpgcode.getViewport().height,
      x: 0,
      y: 0
   };
   rpgcode.createCanvas(this.background.width, this.background.height, this.background.canvasId);
   rpgcode.setCanvasPosition(this.background.x, this.background.y, this.background.canvasId);
   if (config.backgroundImage) {
      rpgcode.setImage(config.backgroundImage, 0, 0, this.background.width, this.background.height, this.background.canvasId);
   }
   rpgcode.renderNow(this.background.canvasId);

   let items = [];
   let currentInventory = rpgcode.getCharacter().inventory;
   Object.keys(currentInventory).forEach(function(key) {
      let i = currentInventory[key][0];
      items.push({
         item: i,
         count: currentInventory[key].length,
         image: i.icon,
         execute: function() {
            inventory._handleSelectedItem(i);
         }
      });
   });

   let frameWidth = 192;
   let frameHeight = 224;
   this.frame = gui.createFrame({
      id: "Inventory.frameCanvas",
      width: 192,
      height: 224,
      x: (rpgcode.getViewport().width / 2) - (frameWidth / 2),
      y: (rpgcode.getViewport().height / 2) - (frameHeight / 2)
   });
   this.frame.setGrid({
      items: items,
      x: 32,
      y: 32,
      rows: 5,
      columns: 4,
      cellWidth: 32,
      cellHeight: 32,
      cellLineWidth: 1
   });
   this.frame.setVisible(true);

   // Key bindings
   rpgcode.registerKeyDown("E", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("UP_ARROW", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("DOWN_ARROW", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("W", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("S", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("LEFT_ARROW", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("RIGHT_ARROW", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("A", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("D", this._handleInput.bind(this), false);
   rpgcode.registerKeyDown("Q", this._end.bind(this), false);
};

Inventory.prototype._loadAssets = function(config, callback) {
   let assets = {
      "images": [],
      "audio": {}
   };
   if (config.backgroundImage) {
      assets.images.push(config.backgroundImage);
   }
   if (config.inventoryMusic) {
      assets.audio["inventory.music"] = config.inventoryMusic;
   }
   rpgcode.loadAssets(assets, callback.bind(this));
};

/// 
/// Item Usage
///

Inventory.prototype._handleSelectedItem = function(item) {
   switch (item.type) {
      case "battle":
         this._applyItemEffects(item.effects);
         rpgcode.takeItem(item.fileName, "Hero");
         this.frame.getGrid().removeSelectedItem();
         break;
      default:
         break;
   }
};

Inventory.prototype._applyItemEffects = function(effects) {
   let character = rpgcode.getCharacter();
   character.health += effects.health;
   if (character.health > character.maxHealth) {
      character.health = character.maxHealth;
   }
   character.attack += effects.attack;
   if (character.attack > character.maxAttack) {
      character.attack = character.maxAttack;
   }
   character.defence += effects.defence;
   if (character.defence > character.maxDefence) {
      character.defence = character.maxDefence;
   }
   character.magic += effects.magic;
   if (character.magic > character.maxMagic) {
      character.magic = character.maxMagic;
   }
};

//
// Input Functions
//

Inventory.prototype._handleInput = function(e) {
   if (this._state.processingInput) {
      return;
   }
   this._state.processingInput = true;
   switch (e.key) {
      case 69:
         this._handleAction();
         break;
      case 38: // UP_ARROW
      case 87: // W
         this._handleUpArrow();
         break;
      case 40: // DOWN_ARROW
      case 83: // S
         this._handleDownArrow();
         break;
      case 37: // LEFT_ARROW
      case 65: // A
         this._handleLeftArrow();
         break;
      case 39: // RIGHT_ARROW
      case 68: // D
         this._handleRightArrow();
         break;
      default:
         return;
   }
};

Inventory.prototype._handleAction = function() {
   if (this._state.currentContext === this.context.MENU) {
      this.frame.getGrid().executeSelectedItem();
   }
   this._state.processingInput = false;
};

Inventory.prototype._handleUpArrow = function() {
   if (this._state.currentContext === this.context.MENU) {
      this.frame.getGrid().up();
   }
   this._state.processingInput = false;
};

Inventory.prototype._handleDownArrow = function() {
   if (this._state.currentContext === this.context.MENU) {
      this.frame.getGrid().down();
   }
   this._state.processingInput = false;
};

Inventory.prototype._handleLeftArrow = function() {
   if (this._state.currentContext === this.context.MENU) {
      this.frame.getGrid().left();
   }
   this._state.processingInput = false;
};

Inventory.prototype._handleRightArrow = function() {
   if (this._state.currentContext === this.context.MENU) {
      this.frame.getGrid().right();
   }
   this._state.processingInput = false;
};