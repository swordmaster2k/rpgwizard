/*
 * Copyright (c) 2017, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
// Game assets used in this program
var assets = {
  "images": ["GameOver.png"]
};

// Canvas IDs
var gameOverCanvas = "gameOverCanvas";

// Load up the assets we need
rpgcode.loadAssets(assets, function() {
  // Assets are ready show game over
  rpgcode.createCanvas(640, 480, gameOverCanvas);
  rpgcode.setImage("GameOver.png", 0, 0, 640, 480, gameOverCanvas);
  rpgcode.renderNow(gameOverCanvas);

  rpgcode.delay(5000, function() {
    rpgcode.restart();
  });
}); 