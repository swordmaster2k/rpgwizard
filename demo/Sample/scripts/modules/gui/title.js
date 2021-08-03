export const state = {};

const backgroundCanvas = "title.background";
const textCanvas = "title.text";

const music = "title.music";
const startKey = "SPACE";
const flashingText = `PRESS ${startKey}`;

/**
 *  Shows the default title screen system based the supplied config.
 * 
 * @example
 * let config = {
 *  "backgroundImage": "startscreen.png", 
 *  "titleMusic": "intro.ogg"
 * };
 * await title.show(config); 
 * 
 * @param {Object} config
 * @returns {undefined}
 */
export async function show(config) {
   await _loadAssets(config);
   _setup(config);
   rpg.playAudio(music, true, 1.0);

   return await state.promise;
}

function _end() {
   rpg.stopAudio(music);
   rpg.unregisterKeyDown(startKey);
   rpg.clear(backgroundCanvas);
   rpg.clear(textCanvas);
   clearInterval(state.interval);
   state.resolve();
}

function _setup(config) {
   // State variables
   Object.assign(state, config); // Copy everything from config
   
   state.processingInput = false;
   state.promise = new Promise((resolve, reject) => {
      state.resolve = resolve;
      state.reject = reject;
   });

   const viewportWidth = rpg.getViewport(false).width;
   const viewportHeight = rpg.getViewport(false).height;

   // Background Canvas
   rpg.createCanvas(backgroundCanvas, viewportWidth, viewportHeight);
   rpg.setColor(255, 255, 255, 1.0);
   rpg.fillRect(backgroundCanvas, 0, 0, viewportWidth, viewportHeight);
   rpg.render(backgroundCanvas);

   // Text Canvas
   rpg.setFont(state.font.size, state.font.family);
   
   const textWidth = rpg.measureText(flashingText).width;
   const textHeight = rpg.measureText(flashingText).height;
   const textX = 0;
   const textY = textHeight;

   const textCanvasWidth = textWidth;
   const textCanvasHeight = textHeight;
   
   rpg.createCanvas(textCanvas, textCanvasWidth, textCanvasHeight);
   rpg.setColor(state.font.color);
   rpg.drawText(textCanvas, textX, textY, flashingText);
   rpg.setCanvasPosition(textCanvas, (viewportWidth / 2) - (textCanvasWidth / 2), (viewportHeight / 2) - (textCanvasHeight / 2));
   rpg.render(textCanvas);

   // Setup a simple interval to flash the text every 1/2 second
   state.flash = true;
   state.interval = setInterval(
      function() { 
         state.flash = !state.flash;
         if (state.flash) {
            rpg.render(textCanvas);
         } else {
            rpg.hide(textCanvas);
         }
      }, 500
   );

   // Setup the start key
   rpg.registerKeyDown(startKey, _startGame, false);
}

async function _loadAssets(config, callback) {
   let assets = {
      "images": [config.backgroundImage],
      "audio": {
         "title.music": config.titleMusic
      }
   };
   await rpg.loadAssets(assets);
}

function _startGame() {
   if (state.processingInput) {
      return;
   }
   state.processingInput = true;
   _end();
}
