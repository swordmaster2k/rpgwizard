export async function wander(sprite, distance, direction, time) {
   if (sprite.data.isHit) {
      return;
   }
   
   switch (direction) {
      case "NORTH":
         return rpg.moveSprite(sprite.id, sprite.x, sprite.y - distance, time);
      case "SOUTH":
         return rpg.moveSprite(sprite.id, sprite.x, sprite.y + distance, time);
      case "EAST":
         return rpg.moveSprite(sprite.id, sprite.x + distance, sprite.y, time);
      default:
         return rpg.moveSprite(sprite.id, sprite.x - distance, sprite.y, time);
   }
}

export async function chase(sprite, targetId) {
   if (sprite.data.isHit) {
      return;
   }
   
   const approachDistance = 256;   // Max distance the character can be away from us in px.
   const movementVelocity = 1.0;   // Amount of movement per cycle.
   const movementTime = 40;        // Time it takes to move once in milliseconds.

   const target = rpg.getSprite(targetId);
   if (sprite.layer === target.layer) {
      const distance = rpg.getDistanceBetweenPoints(sprite.x, sprite.y, target.x, target.y);
      if (distance < approachDistance) {
         await _moveSpriteTowardsPoint(sprite, target.location, movementVelocity, movementTime);
      }
   }
}

export async function _moveSpriteTowardsPoint(sprite, point, distance, time) {
   const angle = rpg.getAngleBetweenPoints(point.x, point.y, sprite.x, sprite.y);
   const direction = sprite.direction;
   const velocityX = distance * Math.cos(angle);
   const velocityY = distance * Math.sin(angle);

   return rpg.moveSprite(sprite.id, sprite.x + velocityX, sprite.y + velocityY, time);
}
