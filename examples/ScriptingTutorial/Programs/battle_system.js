rpgcode.setColor(255, 255, 255, 1);
rpgcode.setFont(28, "Lucida Console");

let cursorX = 250;
let cursorY = 383;
let action = "fight";
let inAction = false;

rpgcode.registerKeyDown("LEFT_ARROW", function() {
   action = "fight";
   cursorX = 250;
   cursorY = 383;
   draw();
}, false);

rpgcode.registerKeyDown("RIGHT_ARROW", function() {
   action = "run";
   cursorX = 430;
   cursorY = 383;
   draw();
}, false);

rpgcode.registerKeyDown("ENTER", function() {
   if (inAction === true) {
      return;
   }
   
   if (action === "fight") {
      inAction = true;
      
      rpgcode.animateSprite("enemy-1", "DEFEND", function() {
         rpgcode.drawText(40, 40, action);
         rpgcode.renderNow();

         inAction = false;
      });
   }
}, false);

draw();

function draw() {
   rpgcode.clearCanvas();
   
   const character = rpgcode.getCharacter();
   rpgcode.drawText(65, 320, character.name);
   rpgcode.drawText(220, 320, character.level);
   rpgcode.drawText(310, 320, character.health);
   rpgcode.drawText(390, 320, character.magic);
   rpgcode.drawText(540, 320, character.experience);
   
   rpgcode.drawText(65, 400, character.name);
   
   rpgcode.drawText(270, 400, "Fight");
   rpgcode.drawText(450, 400, "Run");
   
   rpgcode.drawImage("cursor.png", cursorX, cursorY, 10, 17, 0);
   
   rpgcode.renderNow();
}