export async function drop(spriteFile, location) {
   const spriteId = "item-" + Date.now().toString(36) + Math.random().toString(36).substr(2);
   const sprite = {
      "asset": spriteFile,
      "thread": "",
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

   console.log(sprite);

   rpg.removeSprite(sprite.id);
}
