export async function drop(spriteFile, location) {
   const spriteId = "item-" + Date.now().toString(36) + Math.random().toString(36).substr(2);
   const sprite = {
      "asset": spriteFile,
      "thread": "examples/threads/idle-sprite.js",
      "startLocation": location,
      "events": [
         {
            "script": "examples/item-pickup.js",
            "type": "overlap"
         }
      ]
   };

   await rpg.addSprite(spriteId, sprite);
}

export async function pickup(sprite) {
   rpg.playAudio("item", false);
   rpg.removeSprite(sprite.id);
   _applyItem(sprite);
}

export async function purchase(sprite, cost) {
   rpg.setGlobal("player.coins", rpg.getGlobal("player.coins") - cost);
   rpg.playAudio("item", false);
   rpg.removeSprite(sprite.id);
   _applyItem(sprite);
}

function _applyItem(sprite) {
   if (sprite.name === "heart") {
      rpg.setGlobal("player.health", rpg.getGlobal("player.health") + 1);
   } else if (sprite.name === "coin") { 
      rpg.setGlobal("player.coins", rpg.getGlobal("player.coins") + 1);
   } else if (sprite.name === "book") {
      rpg.setGlobal("player.hasBook", true);
   }
}
