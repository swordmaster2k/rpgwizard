import * as gui from "./gui.js";

export const state = {};

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
 * let config = {
 *  position: "CENTER",
 *  advancementKey: "E",
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
export async function show(config) {
   await _loadAssets(config);
   _setup(config);

   await _animate();

   const lines = _sortLines(config.text.trim());
   if (lines.length > 0) {
      _reset(rpg.measureText(lines[0]).height);
      await _printLines(lines);
      _playTypingSound();
   }
}

async function _end() {
   rpg.unregisterKeyDown(state.advancementKey);
   rpg.unregisterMouseClick(false);
   state._blink = false;
   state._clearNextMarker();
   rpg.clear(state._nextMarkerCanvas);
   state.profileFrame.setVisible(false);
   state.frame.setVisible(false);
}

async function _loadAssets(config) {
   const assets = {
      "images": [config.nextMarkerImage, config.profileImage],
      "audio": {
         "dialog.typingSound": config.typingSound
      }
   };
   await rpg.loadAssets(assets);
}

function _setup(config) {
   state._nextMarkerCanvas = "dialog.nextMarkerCanvas";
   state._nextMarkerVisible = false;
   state._currentLines = 1;
   state._blink = false;
   state._defaultX = 22;
   state._defaultY = 18;
   state._position = config.position;

   state.characterDelay = 25;
   state.markerBlinkDelay = 500;
   state.advancementKey = config.advancementKey;
   state._cursorX = state._defaultX;
   state._cursorY = state._defaultY;
   state.maxLines = 3;
   state.nextMarkerImage = config.nextMarkerImage;
   state.typingSound = "dialog.typingSound";
   state.skipMode = false;

   state.padding = {
      x: 0,
      y: 0,
      line: 10
   };

   let width = 440;
   if (rpg.getViewport(false).width < 560) {
      width = rpg.getViewport(false).width - 120;
   }
   let height = 120;
   let profileWidth = height;
   let profileHeight = height;
   let x = Math.round((rpg.getViewport(false).width / 2) - (width / 2) + (profileWidth / 2));
   let y = 0;
   switch (config.position ? config.position : "BOTTOM") {
      case "TOP":
         y = 0;
         break;
      case "CENTER":
         y = Math.floor(rpg.getViewport(false).height / 2) - Math.floor(height / 2);
         break;
      case "BOTTOM":
      default:
         y = Math.floor(rpg.getViewport(false).height - height);
   }

   state.profileFrame = gui.createFrame({
      id: "Dialog.profileFrameCanvas",
      width: profileWidth,
      height: profileHeight,
      x: x - profileWidth,
      y: y
   });
   state.profileFrame.setImage(config.profileImage);
   state.profileFrame.setVisible(false);

   state.frame = gui.createFrame({
      id: "Dialog.frameCanvas",
      width: width,
      height: height,
      x: x,
      y: y
   });
   state.frame.setVisible(false);

   let image = rpg.getImage(state.nextMarkerImage);
   if (image && image.width > 0 && image.height > 0) {
      let widthTemp = image.width;
      let heightTemp = image.height;
      let xTemp = state.frame.x + (state.frame.width - (widthTemp + widthTemp / 4));
      let yTemp = state.frame.y + (state.frame.height - (heightTemp + heightTemp / 4));
      rpg.createCanvas(state._nextMarkerCanvas, widthTemp, heightTemp);
      rpg.setCanvasPosition(state._nextMarkerCanvas, xTemp, yTemp);
   }
}

function _reset(lineHeight) {
   state._cursorX = state._defaultX + state.padding.x;
   state._cursorY = state._defaultY + state.padding.y + lineHeight;
   state._currentLines = 1;
   _draw();
}

function _sortLines(text) {
   rpg.setFont(gui.getFontSize(), gui.getFontFamily());
   let words = text.split(" ");
   let lines = [];
   let line = words[0];

   let newLine;
   for (let i = 1; i < words.length; i++) {
      if (/[\r\n]$/.test(line)) {
         lines.push(line);
         line = words[i].trim();
      }

      newLine = line.trim() + " " + words[i];
      if (rpg.measureText(newLine.trim()).width < state.frame.width - 45) {
         line = newLine;
      } else {
         lines.push(line);
         line = words[i];
      }
   }
   lines.push(line);

   return lines;
}

async function _animate() {
   if (state._position === "BOTTOM") {
      const originalY = state.profileFrame.y;
      const newY = rpg.getViewport(false).height;
      state.profileFrame.setLocation(state.profileFrame.x, newY);
      state.frame.setLocation(state.frame.x, newY);
      state.profileFrame.setVisible(true);
      state.frame.setVisible(true);

      const change = 10; // pixels
      do {
         state.profileFrame.setLocation(state.profileFrame.x, state.profileFrame.y - change);
         state.frame.setLocation(state.frame.x, state.frame.y - change);
         await new Promise(r => requestAnimationFrame(r));
      } while (originalY < state.profileFrame.y);
   } else {
      state.profileFrame.setVisible(true);
      state.frame.setVisible(true);
   }
}

async function _printLines(lines) {
   rpg.setFont(gui.getFontSize(), gui.getFontFamily());
   gui.prepareTextColor();

   if (state.advancementKey) {
      rpg.registerKeyDown(state.advancementKey, async function() {
         state.skipMode = true;
      }, false);
   } else {
      rpg.registerMouseClick(async function() {
         state.skipMode = true;
      }, false);
   }

   let line = lines.shift();
   if (/[\r\n]$/.test(line)) {
      let tempLine = line.split("\r\n")[0];
      if (line.replace(tempLine, "").trim()) {
         lines.unshift(line.replace(tempLine, "").trim());
      }
      state._currentLines = state.maxLines; // Force a fresh window
      line = tempLine.trim();
   }

   await _printCharacters(line.split(""));

   if (lines.length) {
      state._currentLines++;
      if (state._currentLines > state.maxLines) {
         state.skipMode = false;
         _stopTypingSound();
         state._blink = true;
         await _blinkNextMarker();

         // Decide whether or not to use keypress or mouse.
         if (state.advancementKey) {
            rpg.registerKeyDown(state.advancementKey, async function() {
               await _advance(lines);
            }, false);
         } else {
            rpg.registerMouseClick(async function() {
               await _advance(lines);
            }, false);
         }
      } else {
         state._cursorX = state._defaultX + state.padding.x;
         state._cursorY += rpg.measureText(line).height + state.padding.line;
         await _printLines(lines);
      }
   } else {
      _stopTypingSound();
      state._blink = true;
      await _blinkNextMarker();

      // Decide whether or not to use keypress or mouse.
      if (state.advancementKey) {
         rpg.registerKeyDown(state.advancementKey, async function() {
            _end();
         }, false);
      } else {
         rpg.registerMouseClick(async function() {
            _end();
         }, false);
      }
   }
}

async function _printCharacters(characters) {
   let character = characters.shift();
   rpg.drawText(state.frame.id, state._cursorX, state._cursorY, character);
   rpg.render(state.frame.id);
   state._cursorX += rpg.measureText(character).width;

   if (characters.length) {
      if (!state.skipMode) {
         await rpg.sleep(state.characterDelay);
      }
      await _printCharacters(characters);
   }
}

function _draw() {
   state.frame.draw();
}

function _drawNextMarker() {
   let image = rpg.getImage(state.nextMarkerImage);
   if (image && image.width > 0 && image.height > 0) {
      let width = image.width;
      let height = image.height;
      rpg.drawImage(state._nextMarkerCanvas, state.nextMarkerImage, 0, 0, width, height, 0);
      rpg.render(state._nextMarkerCanvas);
      state._nextMarkerVisible = true;
   }
}

async function _blinkNextMarker() {
   if (state.nextMarkerImage && state._blink) {
      if (!state._nextMarkerVisible) {
         _drawNextMarker();
      } else {
         _clearNextMarker();
      }
      await rpg.sleep(state.markerBlinkDelay);
      await _blinkNextMarker();
   }
}

function _clearNextMarker() {
   if (state.nextMarkerImage) {
      rpg.clear(state._nextMarkerCanvas);
      state._nextMarkerVisible = false;
   }
}

function _playTypingSound() {
   if (state.typingSound) {
      rpg.playAudio(state.typingSound, true);
   }
}

function _stopTypingSound() {
   if (state.typingSound) {
      rpg.stopAudio(state.typingSound);
   }
}

async function _advance(lines) {
   rpg.setFont(gui.getFontSize(), gui.getFontFamily());
   state._blink = false;
   _clearNextMarker();
   rpg.unregisterKeyDown(state.advancementKey);
   _reset(rpg.measureText(lines[0]).height);
   await _printLines(lines);
   _playTypingSound();
}