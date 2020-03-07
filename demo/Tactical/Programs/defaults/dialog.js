/* global rpgcode, gui */
var dialog = new Dialog();

/**
 * The builtin dialog window system.
 *
 * @class
 * @constructor
 *
 * @returns {Dialog}
 */
function Dialog() {

}

/**
 * Shows the default dialog window based the supplied config.
 *
 * The dialog window can be set to appear at the "TOP", "CENTER", or "BOTTOM"
 * of the screen. It can also be supplied with an image for the blinking next
 * marker, a profile image of the speaker, and a typing sound that plays while
 * it is animating.
 *
 * Note: The current hardcoded next key is "E".
 *
 * @example
 * var config = {
 *  position: "CENTER",
 *  nextMarkerImage: "next_marker.png",
 *  profileImage: rpgcode.getCharacter().graphics["PROFILE"],
 *  typingSound: "typing_loop.wav",
 *  text: "Hello, this text will be wrote to the window like a type-writer."
 * };
 * await dialog.show(config);
 *
 * @param {Object} config
 * @returns {undefined}
 */
Dialog.prototype.show = async function(config) {
   return new Promise((resolve, reject) => {
      dialog._loadAssets(config, async function() {
         this._setup(config);
         await this._animate();
         
         var lines = this._sortLines(config.text.trim());
         if (lines.length > 0) {
            var callback = function() {
               resolve();
            };
            this._reset(rpgcode.measureText(lines[0]).height);
            this._printLines(lines, callback);
            this._playTypingSound();
         } else {
             resolve();
         }
      }.bind(dialog));
   });
};

Dialog.prototype._end = function(callback) {
   rpgcode.unregisterKeyDown(this.advancementKey);
   rpgcode.unregisterMouseClick(false);
   this._blink = false;
   this._clearNextMarker();
   rpgcode.clearCanvas(this._nextMarkerCanvas);
   this.profileFrame.setVisible(false);
   this.frame.setVisible(false);
   callback();
};

Dialog.prototype._loadAssets = function(config, callback) {
   var assets = {
      "images": [config.nextMarkerImage, config.profileImage],
      "audio": {
         "dialog.typingSound": config.typingSound
      }
   };
   rpgcode.loadAssets(assets, function() {
      callback();
   }.bind(this));
};

Dialog.prototype._setup = function(config) {
   this._nextMarkerCanvas = "dialog.nextMarkerCanvas";
   this._nextMarkerVisible = false;
   this._currentLines = 1;
   this._blink = false;
   this._defaultX = 22;
   this._defaultY = 18;
   this._position = config.position;

   this.characterDelay = 25;
   this.markerBlinkDelay = 500;
   this.advancementKey = null;
   this._cursorX = this._defaultX;
   this._cursorY = this._defaultY;
   this.maxLines = 3;
   this.nextMarkerImage = config.nextMarkerImage;
   this.typingSound = "dialog.typingSound";
   this.skipMode = false;

   this.padding = {
      x: 0,
      y: 0,
      line: 10
   };

   var width = 440;
   if (rpgcode.getViewport(false).width < 560) {
      width = rpgcode.getViewport(false).width - 120;
   }
   var height = 120;
   var profileWidth = height;
   var profileHeight = height;
   var x = Math.round((rpgcode.getViewport(false).width / 2) - (width / 2) + (profileWidth / 2));
   var y = 0;
   switch (config.position ? config.position : "BOTTOM") {
      case "TOP":
         y = 0;
         break;
      case "CENTER":
         y = Math.floor(rpgcode.getViewport(false).height / 2) - Math.floor(height / 2);
         break;
      case "BOTTOM":
      default:
         y = Math.floor(rpgcode.getViewport(false).height - height);
   }

   this.profileFrame = gui.createFrame({
      id: "Dialog.profileFrameCanvas",
      width: profileWidth,
      height: profileHeight,
      x: x - profileWidth,
      y: y
   });
   this.profileFrame.setImage(config.profileImage);
   this.profileFrame.setVisible(false);

   this.frame = gui.createFrame({
      id: "Dialog.frameCanvas",
      width: width,
      height: height,
      x: x,
      y: y
   });
   this.frame.setVisible(false);

   var image = rpgcode.getImage(this.nextMarkerImage);
   if (image && image.width > 0 && image.height > 0) {
      var widthTemp = image.width;
      var heightTemp = image.height;
      var xTemp = this.frame.x + (this.frame.width - (widthTemp + widthTemp / 4));
      var yTemp = this.frame.y + (this.frame.height - (heightTemp + heightTemp / 4));
      rpgcode.createCanvas(widthTemp, heightTemp, this._nextMarkerCanvas);
      rpgcode.setCanvasPosition(xTemp, yTemp, this._nextMarkerCanvas);
   }
};

Dialog.prototype._reset = function(lineHeight) {
   this._cursorX = this._defaultX + this.padding.x;
   this._cursorY = this._defaultY + this.padding.y + lineHeight;
   this._currentLines = 1;
   this._draw();
};

Dialog.prototype._sortLines = function(text) {
   rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
   var words = text.split(" ");
   var lines = [];
   var line = words[0];
   
   var newLine;
   for (var i = 1; i < words.length; i++) {
      if (/[\r\n]$/.test(line)) {
         lines.push(line);
         line = words[i].trim();
      }
      
      newLine = line.trim() + " " + words[i];
      if (rpgcode.measureText(newLine.trim()).width < this.frame.width - 45) {
         line = newLine;
      } else {
         lines.push(line);
         line = words[i];
      }
   }
   lines.push(line);

   return lines;
};

Dialog.prototype._animate = async function() {
   if (this._position === "BOTTOM") {
      const originalY = this.profileFrame.y;
      const newY = rpgcode.getViewport(false).height;
      this.profileFrame.setLocation(this.profileFrame.x, newY);
      this.frame.setLocation(this.frame.x, newY);
      this.profileFrame.setVisible(true);
      this.frame.setVisible(true);

      const change = 10; // pixels
      do {
         this.profileFrame.setLocation(this.profileFrame.x, this.profileFrame.y - change);
         this.frame.setLocation(this.frame.x, this.frame.y - change);
         await new Promise(r => requestAnimationFrame(r));
      } while(originalY < this.profileFrame.y);
   } else {
      this.profileFrame.setVisible(true);
      this.frame.setVisible(true);
   }
};

Dialog.prototype._printLines = function(lines, callback) {
   rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
   gui.prepareTextColor();

   if (dialog.advancementKey) {
      rpgcode.registerKeyDown(dialog.advancementKey, function () {
         dialog.skipMode = true;
      }, false);
   } else {
      rpgcode.registerMouseClick(function() {
         dialog.skipMode = true;
      }, false);
   }

   var line = lines.shift();
   if (/[\r\n]$/.test(line)) {
      var tempLine = line.split("\r\n")[0];
      if (line.replace(tempLine, "").trim()) {
         lines.unshift(line.replace(tempLine, "").trim());
      }
      dialog._currentLines = dialog.maxLines; // Force a fresh window
      line = tempLine.trim();
   }
   
   this._printCharacters(line.split(""), function() {
      if (lines.length) {
         dialog._currentLines++;
         if (dialog._currentLines > dialog.maxLines) {
            dialog.skipMode = false;
            dialog._stopTypingSound();
            dialog._blink = true;
            dialog._blinkNextMarker();

            // Decide whether or not to use keypress or mouse.
            if (dialog.advancementKey) {
               rpgcode.registerKeyDown(dialog.advancementKey, function() {
                  dialog._advance(lines, callback);
               }, false);
            } else {
               rpgcode.registerMouseClick(function() {
                  dialog._advance(lines, callback);
               }, false);
            }
         } else {
            dialog._cursorX = dialog._defaultX + dialog.padding.x;
            dialog._cursorY += rpgcode.measureText(line).height + dialog.padding.line;
            dialog._printLines(lines, callback);
         }
      } else {
         dialog._stopTypingSound();
         dialog._blink = true;
         dialog._blinkNextMarker();

         // Decide whether or not to use keypress or mouse.
         if (dialog.advancementKey) {
            rpgcode.registerKeyDown(dialog.advancementKey, function() {
               dialog._end(callback);
            }, false);
         } else {
            rpgcode.registerMouseClick(function() {
               dialog._end(callback);
            }, false);
         }
      }
   });
};

Dialog.prototype._printCharacters = function(characters, callback) {
   var character = characters.shift();
   rpgcode.drawText(dialog._cursorX, dialog._cursorY, character, this.frame.id);
   rpgcode.renderNow(this.frame.id);
   dialog._cursorX += rpgcode.measureText(character).width;

   if (characters.length) {
      if (dialog.skipMode) {
         dialog._printCharacters(characters, callback);
      } else {
         rpgcode.delay(dialog.characterDelay, function () {
            dialog._printCharacters(characters, callback);
         });
      }
   } else {
      callback();
   }
};

Dialog.prototype._blinkNextMarker = function() {
   if (this.nextMarkerImage && this._blink) {
      if (!this._nextMarkerVisible) {
         this._drawNextMarker();
      } else {
         this._clearNextMarker();
      }
      rpgcode.delay(this.markerBlinkDelay, this._blinkNextMarker.bind(this));
   }
};

Dialog.prototype._clearNextMarker = function() {
   if (this.nextMarkerImage) {
      rpgcode.clearCanvas(this._nextMarkerCanvas);
      this._nextMarkerVisible = false;
   }
};

Dialog.prototype._drawNextMarker = function() {
   var image = rpgcode.getImage(this.nextMarkerImage);
   if (image && image.width > 0 && image.height > 0) {
      var width = image.width;
      var height = image.height;
      rpgcode.setImage(this.nextMarkerImage, 0, 0, width, height, this._nextMarkerCanvas);
      rpgcode.renderNow(this._nextMarkerCanvas);
      this._nextMarkerVisible = true;
   }
};

Dialog.prototype._draw = function() {
   this.frame.draw();
};

Dialog.prototype._stopTypingSound = function() {
   if (this.typingSound) {
      rpgcode.stopSound(this.typingSound);
   }
};

Dialog.prototype._playTypingSound = function() {
   if (this.typingSound) {
      rpgcode.playSound(this.typingSound, true);
   }
};

Dialog.prototype._advance = function(lines, callback) {
   rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
   dialog._blink = false;
   dialog._clearNextMarker();
   rpgcode.unregisterKeyDown(dialog.advancementKey);
   dialog._reset(rpgcode.measureText(lines[0]).height);
   dialog._printLines(lines, callback);
   dialog._playTypingSound();
};
