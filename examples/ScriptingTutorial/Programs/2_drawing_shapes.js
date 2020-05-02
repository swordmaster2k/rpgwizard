// Drawing unfilled shapes
rpgcode.setColor(255, 0, 0, 1);
rpgcode.drawRect(25, 25, 100, 50, 2);

rpgcode.setColor(0, 255, 0, 1);
rpgcode.drawCircle(50, 125, 25);

rpgcode.setColor(0, 0, 255, 1);
rpgcode.drawLine(25, 175, 150, 175, 3);

rpgcode.setColor(255, 255, 0, 1);
rpgcode.drawRoundedRect(25, 225, 100, 50, 1, 25);

// Drawing filled shapes
rpgcode.setColor(255, 0, 0, 1);
rpgcode.fillRect(225, 25, 100, 50);

rpgcode.setColor(0, 255, 0, 1);
rpgcode.fillCircle(225, 125, 25);

rpgcode.setColor(255, 255, 0, 0.5);
rpgcode.fillRoundedRect(225, 225, 100, 50, 25);

rpgcode.renderNow();