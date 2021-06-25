export async function slashSword() {
   // Animate the sprite for the given direciton
   _animateAttack("player", rpg.getSpriteDirection("player"));

   rpg.playAudio("sword", false);

   const hits = _findClosetObjects(rpg.getSpriteLocation("player"), rpg.getSpriteDirection("player"), 50);
   for (const sprite of hits.sprites) {
      if (sprite.data.type === "enemy") {
         _hitEnemy(sprite, rpg.getSpriteDirection("player"));
      } else if (sprite.data.type === "npc") {
         _hitNpc(sprite, rpg.getSpriteDirection("player"));
      }
   }
}

export async function wander(sprite, distance, time) {
   switch (_randomDirection(sprite, distance)) {
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

export async function moveSpriteTowardsPoint(sprite, point, distance, time) {
   const angle = rpg.getAngleBetweenPoints(point.x, point.y, sprite.x, sprite.y);
   const direction = sprite.direction;
   const velocityX = distance * Math.cos(angle);
   const velocityY = distance * Math.sin(angle);

   return rpg.moveSprite(sprite.id, sprite.x + velocityX, sprite.y + velocityY, time);
}

export async function dropItem(spriteFile, location) {
   
}

function _randomDirection(origin, distance) {
   const directions = ["NORTH", "SOUTH", "EAST", "WEST"];
   let attempts = 0;
   let choice;
   let best = {direction: "NORTH", distance: 0};
   while (attempts < directions.length) {
      choice = Math.floor(rpg.getRandom(0, directions.length - 1));
      const objects = _findClosetObjects(origin, directions[choice], distance);
      if (objects.colliders.length < 1 || objects.colliders[0].distance > distance) {
         return directions[choice];
      }
      if (objects.colliders[0].distance > best.distance) {
         best.direction = directions[choice];
         best.distance = objects.colliders[0].distance;
      }
      attempts++;
   }

   return best.direction;
}

function _findClosetObjects(origin, direction, distance) {
   // Figure out the normalised vector (x, y) values to use based on the direction
   let vector = {};
   switch (direction) {
      case "NORTH":
         vector = { x: 0, y: -1 };
         break;
      case "SOUTH":
         vector = { x: 0, y: 1 };
         break;
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         vector = { x: 1, y: 0 };
         break;
      case "NORTH_WEST":
      case "SOUTH_WEST":
      case "WEST":
         vector = { x: -1, y: 0 };
         break;
   }

   // Fire a raycast from the character's current location, using
   // the supplied direction and range in pixels.
   return rpg.raycast({ _x: origin.x, _y: origin.y }, vector, distance);
}

function _calculatePushForce(direction, attackVelocity) {
   switch (direction) {
      case "NORTH":
         return {x: 0, y: -attackVelocity};
      case "SOUTH":
         return {x: 0, y: attackVelocity};
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         return {x: attackVelocity, y: 0};
      case "NORTH_WEST":
      case "SOUTH_WEST":
      case "WEST":
         return {x: -attackVelocity, y: 0};
   }
}

async function _hitEnemy(sprite, attackDirection) {
   const attackPushTime = 250;  // Amount of time in milliseconds the hit entity (if any) is pushed for.
   const attackVelocity = 32;   // Distance to push the entity we hit (if any) in px.
   const attackRangePx = 22;    // Range in pixels of the attack.
   const attackDamage = 1;      // Amount of damage the sword deals.
   
   const force = _calculatePushForce(attackDirection, attackVelocity);
   rpg.playAudio("hurt-enemy", false);

   await _animateDefend(sprite.id, attackDirection);
   await rpg.moveSprite(sprite.id, sprite.x + force.x, sprite.y + force.y, attackPushTime);
   
   sprite.data.health--;
   if (sprite.data.health < 1) {
      rpg.removeSprite(sprite.id);
   }
}

async function _hitNpc() {
   
}

async function _animateAttack(spriteId, direction) {
   switch(direction) {
      case "NORTH":
         await rpg.animateSprite(spriteId, "ATTACK_NORTH");
         break;
      case "SOUTH":
         await rpg.animateSprite(spriteId, "ATTACK_SOUTH");
         break;
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         await rpg.animateSprite(spriteId, "ATTACK_EAST");
         break;
      case "NORTH_WEST":
      case "SOUTH_WEST":
      case "WEST":
         await rpg.animateSprite(spriteId, "ATTACK_WEST");
   }
}

async function _animateDefend(spriteId, direction) {
    switch (direction) {
      case "NORTH":
         await rpg.animateSprite(spriteId, "DEFEND_NORTH");
         break;
      case "SOUTH":
         await rpg.animateSprite(spriteId, "DEFEND_SOUTH");
         break;
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         await rpg.animateSprite(spriteId, "DEFEND_EAST");
         break;
      default:
         await rpg.animateSprite(spriteId, "DEFEND_WEST");
   }
}
