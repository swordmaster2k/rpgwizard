if (inventory.visible) {
   // Close the inventory if it is visible
   inventory.close();
   rpgcode.endProgram();
} else {
   // Configure and show the inventory.
   var config = {
      // Set these if you want them
//      "backgroundImage": "startscreen.png", 
//      "inventoryMusic": "intro.ogg"
   };
   await (inventory.show(config));
   rpgcode.endProgram();
}