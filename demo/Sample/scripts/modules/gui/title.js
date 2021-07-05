import * as gui from "./gui.js";

export const state = {};

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
   gui.setup();
   
   await _loadAssets(config);
   _setup(config);
   rpg.playAudio("title.music", true, 1.0);

   return await state.promise;
}

function _end(resolve, choice) {
   rpg.stopAudio("title.music");
   state.frame.destroy();
   rpg.clear(state.background.canvasId);
   rpg.removeCanvas(state.background.canvasId);

   resolve(choice);
}

function _setup(config) {
   state.context = {
      MENU: "MENU"
   };
   state._temp = {
      processingInput: false,
      currentContext: state.context.MENU
   };
   state.background = {
      canvasId: "title.backgroundCanvas",
      width: rpg.getViewport().width,
      height: rpg.getViewport().height,
      x: 0,
      y: 0
   };
   rpg.createCanvas(state.background.canvasId, state.background.width, state.background.height);
   rpg.setCanvasPosition(state.background.canvasId, state.background.x, state.background.y);
   rpg.drawImage(state.background.canvasId,config.backgroundImage, 0, 0, state.background.width, state.background.height, 0);
   rpg.render(state.background.canvasId);

   state.frame = gui.createFrame({
      id: "title.frameCanvas",
      width: 150,
      height: 54,
      x: (rpg.getViewport().width / 2) - 75,
      y: (rpg.getViewport().height / 1.5) - 27
   });

   state.promise = new Promise((resolve, reject) => {
      state.frame.setMenu({
         items: [{
            id: "new-game",
            text: "New Game",
            execute: function () {_newGame(resolve);}
         }]
      });
   });
   
   state.frame.setVisible(true);

   rpg.registerKeyDown("E", _handleInput, false);
   rpg.registerKeyDown("UP_ARROW", _handleInput, false);
   rpg.registerKeyDown("DOWN_ARROW", _handleInput, false);
   rpg.registerKeyDown("W", _handleInput, false);
   rpg.registerKeyDown("S", _handleInput, false);
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

function _newGame(resolve) {
   _end(resolve, "new-game");
}

//
// Input Functions
//
function _handleInput(e) {
   if (state._temp.processingInput) {
      return;
   }
   state._temp.processingInput = true;
   switch (e.key) {
      case 69:
         _handleAction();
         break;
      case 38: // UP_ARROW
      case 87: // W
         _handleUpArrow();
         break;
      case 40: // DOWN_ARROW
      case 83: // S
         _handleDownArrow();
         break;
      default:
         return;
   }
}

function _handleAction() {
   if (state._temp.currentContext === state.context.MENU) {
      state.frame.getMenu().executeSelectedItem();
   }
}

function _handleUpArrow() {
   if (state._temp.currentContext === state.context.MENU) {
      state.frame.getMenu().up();
   }
   state._temp.processingInput = false;
}

function _handleDownArrow() {
   if (state._temp.currentContext === state.context.MENU) {
      state.frame.getMenu().down();
   }
   state._temp.processingInput = false;
}