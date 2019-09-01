var assets = {
  "programs": [
      // Default systems.
      "defaults/gui.js",
      "defaults/inventory.js"
  ]
}

var startingItems = [
   "apple.item",
   "apple.item",
   "apple.item",
   "potion.item",
   "magic_potion.item",
   "strength_potion.item"
];

rpgcode.loadAssets(assets, function() {
   setupItems();
});

function setupItems() {
   if (startingItems.length === 0) {
      rpgcode.registerKeyDown("Q", function() {
         rpgcode.runProgram("ToggleInventory.js");
      }, true);
      rpgcode.endProgram();
   } else {
      rpgcode.giveItem(startingItems.pop(), "Hero", setupItems);
   }
}