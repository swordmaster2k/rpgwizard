rpgcode.setColor(255, 255, 255, 1);
rpgcode.setFont(28, "Lucida Console");

const character = rpgcode.getCharacter();
rpgcode.drawText(65, 320, character.name);
rpgcode.drawText(220, 320, character.level);
rpgcode.drawText(310, 320, character.health);
rpgcode.drawText(390, 320, character.magic);
rpgcode.drawText(540, 320, character.experience);

rpgcode.drawText(65, 400, character.name);

rpgcode.drawText(270, 400, "Fight");
rpgcode.drawText(450, 400, "Run");

rpgcode.drawImage("cursor.png", 250, 383, 10, 17, 0);

rpgcode.renderNow();