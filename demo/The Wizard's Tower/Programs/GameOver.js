// Game assets used in this program
let assets = {
  "images": ["GameOver.png"]
};

// Canvas IDs
let gameOverCanvas = "gameOverCanvas";

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