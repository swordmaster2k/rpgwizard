export async function openChest(chest) {
   chest.data.open = true;
   rpg.setSpriteAnimation(chest.id, "OPEN");
   return await _spawnReward("coin-chest.sprite", chest.location);
}

async function _spawnReward(spriteFile, location) {
   const spriteId = "item-" + Date.now().toString(36) + Math.random().toString(36).substr(2);
   const sprite = {
      "asset": spriteFile,
      "thread": null,
      "startLocation": {x: location.x, y: location.y - 8, layer: location.layer + 1},
      "events": null
   };
   await rpg.addSprite(spriteId, sprite);
   await rpg.moveSprite(spriteId, location.x, location.y - 16, 250);
   return spriteId;
}
