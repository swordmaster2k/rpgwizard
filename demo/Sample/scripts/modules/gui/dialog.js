export const state = {};

const backgroundCanvas = "dialog.background";
const profileCanvas = "dialog.profile";
const textCanvas = "dialog.text";
const nextCanvas = "dialog.next";

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
      _playTypingSound();
      await _printLines(lines);
   }
}

async function _end() {
   rpg.unregisterKeyDown(state.advancementKey);
   rpg.unregisterMouseClick(false);
   state._blink = false;
   _clearNextMarker();
   rpg.clear(backgroundCanvas);
   if (state.showProfile) {
      rpg.clear(profileCanvas);
   }
   rpg.clear(textCanvas);
}

async function _loadAssets(config) {
   const assets = {
      "images": [
         config.background.image,
         config.profile.image,
         config.nextMarkerImage
      ],
      "audio": {
         "dialog.typingSound": config.typingSound
      }
   };
   await rpg.loadAssets(assets);
}

function _setup(config) {
   // State variables
   Object.assign(state, config); // Copy everything from config
   
   state._nextMarkerVisible = false;
   state._currentLines = 1;
   state._blink = false;
   state._defaultX = 22;
   state._defaultY = 18;
   state.characterDelay = 25;
   state.markerBlinkDelay = 500;
   state._cursorX = state._defaultX;
   state._cursorY = state._defaultY;
   state.maxLines = 3;
   state.typingSound = "dialog.typingSound";
   state.skipMode = false;
   state.padding = { x: 0, y: 0, line: 10 };

   const windowGap = 10;
   const viewportWidth = rpg.getViewport(false).width;
   const viewportHeight = rpg.getViewport(false).height;

   // Background Canvas
   state.background.width = 440;
   if (viewportWidth < 560) {
      state.background.width = viewportWidth - 160;
   }
   state.background.height = 120;

   state.background.x = (viewportWidth / 2) - (state.background.width / 2) + (windowGap / 2);
   if (state.showProfile) {
      state.background.x += (state.background.height / 2);
   }
   
   switch (state.position ? state.position : "BOTTOM") {
      case "TOP":
         state.background.y = 0;
         break;
      case "CENTER":
         state.background.y = Math.floor(viewportHeight / 2) - Math.floor(state.background.height / 2);
         break;
      case "BOTTOM":
      default:
         state.background.y = Math.floor(viewportHeight - state.background.height) - windowGap;
   }
   _setupCanvas(backgroundCanvas, state.background);

   // Profile Canvas
   if (state.showProfile) {
      state.profile.width = state.background.height;
      state.profile.height = state.background.height;
      state.profile.x = state.background.x - state.profile.width - windowGap;
      state.profile.y = state.background.y;
      _setupCanvas(profileCanvas, state.profile);
   }

   // Next Marker Canvas
   let image = rpg.getImage(state.nextMarkerImage);
   if (image && image.width > 0 && image.height > 0) {
      let widthTemp = image.width;
      let heightTemp = image.height;
      let xTemp = state.background.x + (state.background.width - (widthTemp + widthTemp / 4));
      let yTemp = state.background.y + (state.background.height - (heightTemp + heightTemp / 4));
      rpg.createCanvas(nextCanvas, widthTemp, heightTemp);
      rpg.setCanvasPosition(nextCanvas, xTemp, yTemp);
   }

   // Text Canvas
   rpg.createCanvas(textCanvas, config.background.width, config.background.height);
   rpg.setCanvasPosition(textCanvas, config.background.x, config.background.y);
}

function _setupCanvas(canvasId, config) {
   rpg.createCanvas(canvasId, config.width, config.height);
   rpg.setCanvasPosition(canvasId, config.x, config.y);
   
   const sideProportion = 0.10; // 15% per side

   const leftX = 0;
   const leftY = 0;
   const leftWidth = config.width * sideProportion;
   const leftHeight = config.height;
   rpg.drawImagePart(canvasId, config.image, config.leftSide.x, config.leftSide.y, config.leftSide.w, config.leftSide.h, leftX, leftY, leftWidth, leftHeight);

   const centerX = leftX + leftWidth;
   const centerY = leftY;
   const centerWidth = config.width - (leftWidth * 2);
   const centerHeight = config.height;
   rpg.drawImagePart(canvasId, config.image, config.center.x, config.center.y, config.center.w, config.center.h, centerX, centerY, centerWidth, centerHeight);

   const rightX = centerX + centerWidth;
   const rightY = leftY;
   const rightWidth = config.width * sideProportion;
   const rightHeight = config.height;
   rpg.drawImagePart(canvasId, config.image, config.rightSide.x, config.rightSide.y, config.rightSide.w, config.rightSide.h, rightX, rightY, rightWidth, rightHeight);
}

function _reset(lineHeight) {
   state._cursorX = state._defaultX + state.padding.x;
   state._cursorY = state._defaultY + state.padding.y + lineHeight;
   state._currentLines = 1;
   rpg.clear(textCanvas);
}

function _sortLines(text) {
   rpg.setFont(state.font.size, state.font.family);
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
      if (rpg.measureText(newLine.trim()).width < state.background.width - 45) {
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
   rpg.setCanvasPosition(backgroundCanvas, state.background.x, state.background.y);
   if (state.showProfile) {
      rpg.setCanvasPosition(profileCanvas, state.profile.x, state.profile.y);
   }

   if (state.position === "BOTTOM") {
      const originalY = state.background.y;
      state.background.y = state.profile.y = rpg.getViewport(false).height;

      const change = 5; // pixels
      do {
         state.background.y -= change;
         rpg.setCanvasPosition(backgroundCanvas, state.background.x, state.background.y);
         rpg.render(backgroundCanvas);

         if (state.showProfile) {
            state.profile.y -= change;
            rpg.setCanvasPosition(profileCanvas, state.profile.x, state.profile.y);
            rpg.render(profileCanvas);
         }

         await new Promise(r => requestAnimationFrame(r));
      } while (originalY < state.background.y);
   }

   rpg.render(backgroundCanvas);
   if (state.showProfile) {
      rpg.render(profileCanvas);
   }
}

async function _printLines(lines) {
   rpg.setFont(state.font.size, state.font.family);
   rpg.setColor(state.font.color);

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

         // Decide whether or not to use keypress or mouse.
         if (state.advancementKey) {
            rpg.registerKeyDown(state.advancementKey, async function() {
               state._blink = false;
            }, false);
         } else {
            rpg.registerMouseClick(async function() {
               state._blink = false;
            }, false);
         }

         state._blink = true;
         await _blinkNextMarker();
         await _advance(lines);
      } else {
         state._cursorX = state._defaultX + state.padding.x;
         state._cursorY += rpg.measureText(line).height + state.padding.line;
         await _printLines(lines);
      }
   } else {
      _stopTypingSound();

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
      
      state._blink = true;
      await _blinkNextMarker();
   }
}

async function _printCharacters(characters) {
   let character = characters.shift();
   rpg.drawText(textCanvas, state._cursorX, state._cursorY, character);
   rpg.render(textCanvas);
   
   state._cursorX += rpg.measureText(character).width;

   if (characters.length) {
      if (!state.skipMode) {
         await rpg.sleep(state.characterDelay);
      }
      await _printCharacters(characters);
   }
}

function _drawNextMarker() {
   let image = rpg.getImage(state.nextMarkerImage);
   if (image && image.width > 0 && image.height > 0) {
      let width = image.width;
      let height = image.height;
      rpg.drawImage(nextCanvas, state.nextMarkerImage, 0, 0, width, height, 0);
      rpg.render(nextCanvas);
      state._nextMarkerVisible = true;
   }
}

async function _blinkNextMarker() {
   while (state.nextMarkerImage && state._blink) {
      if (!state._nextMarkerVisible) {
         _drawNextMarker();
      } else {
         _clearNextMarker();
      }
      await rpg.sleep(state.markerBlinkDelay);
   }
}

function _clearNextMarker() {
   if (state.nextMarkerImage) {
      rpg.clear(nextCanvas);
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
   rpg.setFont(state.font.size, state.font.family);
   state._blink = false;
   _clearNextMarker();
   rpg.unregisterKeyDown(state.advancementKey);
   _reset(rpg.measureText(lines[0]).height);
   _playTypingSound();
   await _printLines(lines);
}