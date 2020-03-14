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
   };
   hud.close();
   battle.show(config, function(result) {
      var config = {
         life: {
            image: "life.png",
            width: 32,
            height: 32
         }
      };
      hud.show(config, function() {});
      rpgcode.log("The battle has ended, result.status=" + result.status);
      rpgcode.destroySprite("evil-eye-1");
      rpgcode.endProgram();   
   });
}


