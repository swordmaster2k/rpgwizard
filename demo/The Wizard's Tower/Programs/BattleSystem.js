let items = [
   "apple.item", "apple.item", "apple.item"
];
loadItems();

function loadItems() {
    if (items.length === 0) {
      start();
   } else {
      let item = items.pop();
      rpgcode.giveItem(item, "Hero", function() {
         loadItems();
      });
   }
}

function start() {
   let config = {
      enemies: ["evil-eye.enemy"],
      characters: ["Hero.character"],
      backgroundImage: "battle-background.png",
      battleMusic: "Battle.ogg",
      itemSoundEffect: "item.ogg"
   };
   hud.close();
   battle.show(config, function(result) {
      let config = {
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


