var items = [
   "apple.item", "apple.item", "apple.item"
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
      rpgcode.log("The battle has ended, result.status=" + result.status);
      rpgcode.destroySprite("evil-eye-1");
      rpgcode.endProgram();   
   });
}


