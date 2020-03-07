// ARPG.js depends on this program, do not move or remove it.
rpgcode.playSound("item", false);

// Do something based on the type of item we are picking up.
switch (this.sprite.name) {
   case "heart.npc":
      rpgcode.getCharacter("Hero").health++;
      break;
   default:
	   // Do nothing.
}

rpgcode.destroySprite(this.sprite.id);
rpgcode.endProgram()