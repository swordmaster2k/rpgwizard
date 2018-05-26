var items = [
   "apple.item", "apple.item", "apple.item",
   "potion.item", "potion.item", "potion.item", "potion.item", "potion.item",
   "potion.item", "potion.item", "potion.item", "potion.item", "potion.item", 
   "potion.item", "potion.item", "potion.item", "potion.item", "potion.item",
   "strength_potion.item", "magic_potion.item", "stamina_potion.item"
];
loadItems();

function loadItems() {
    if (items.length === 0) {
      start();
   } else {
      var item = items.pop();
      rpgcode.giveItem(item, "Hero", function() {
         loadItems();
      });
   }
}

function start() {
   var config = {
      enemies: ["evil-eye.enemy"],
      characters: ["Hero.character"],
      backgroundImage: "battle-background.png",
      battleMusic: "Battle.ogg",
      itemSoundEffect: "item.ogg"
   }
   battle.show(config, function(result) {
      rpgcode.log("The battle has ended!");
      rpgcode.destroySprite("evil-eye-1");
      rpgcode.endProgram();   
   });
}


