// Setup

// Drawing Function
rpgcode.setFont(20, "Lucida Console");

rpgcode.drawText(60, 320, rpgcode.getCharacter().name);
rpgcode.drawText(240, 320, rpgcode.getCharacter().level);
rpgcode.drawText(300, 320, rpgcode.getCharacter().health);
rpgcode.drawText(380, 320, rpgcode.getCharacter().magic);
rpgcode.drawText(529, 320, rpgcode.getCharacter().experience);

rpgcode.drawText(60, 395, rpgcode.getCharacter().name);

rpgcode.drawText(280, 400, "FIGHT");
rpgcode.drawText(470, 400, "RUN");

rpgcode.renderNow();

// Input Function
rpgcode.registerKeyDown("LEFT_ARROW", function () {
   var image = {"src": "cursor.png", "x": 260, "y": 380, "id": "cursor"};
   rpgcode.updateLayerImage(image, 1);
}, false);

rpgcode.registerKeyDown("RIGHT_ARROW", function () {
   var image = {"src": "cursor.png", "x": 450, "y": 380, "id": "cursor"};
   rpgcode.updateLayerImage(image, 1);
}, false);

// State Function

