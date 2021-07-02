export async function slashSword(hits) {
   rpg.setGlobal("pause.input", true);
   
   await _animateAttack("player", rpg.getSpriteDirection("player"));
   rpg.playAudio("sword", false);
   
   rpg.setGlobal("pause.input", false);

   for (const spriteId in hits.sprites) {
      if (hits.sprites.hasOwnProperty(spriteId)) {
         const sprite = hits.sprites[spriteId];
         if (sprite.data.type === "enemy") {
            return await _hitEnemy(sprite, rpg.getSpriteDirection("player"));
         } else if (sprite.data.type === "object") {
            return await _hitObject(sprite, rpg.getSpriteDirection("player"));
         }
      }
   }
}

export async function bumpPlayer(sprite) {
   rpg.setGlobal("pause.input", true);

   const coolDownTime = 500;       // In milliseconds.
 
   const player = rpg.getSprite("player");
   const cooledDown = player.data.lastHit ? (Date.now() - player.data.lastHit) > coolDownTime : true;

   if (player.data.isHit || !cooledDown) {
      return;
   }

   const dead = await _hitPlayer(player, rpg.getSpriteDirection("player"));
   if (!dead) {
      rpg.setGlobal("pause.input", false);
   }
}

async function _hitPlayer(player, attackDirection) {
   const attackVelocity = 32;      // Distance to push the character in px.
   const attackPushTime = 250;     // Amount of time in milliseconds the character is pushed for.
   const attackDamage = 1;         // Amount of damage the bump deals.

   const bump = _calculateBump(attackDirection);
   const force = _calculatePushForce(bump.direction, attackVelocity);

   rpg.playAudio("hurt-player", false);
   rpg.getSprite("player").data.isHit = true;
   
   await rpg.moveSprite("player", player.x + force.x, player.y + force.y, attackPushTime);
   await _animateDefend("player", attackDirection);
   
   rpg.getSprite("player").data.lastHit = Date.now();
   rpg.getSprite("player").data.isHit = false;

   player.data.health--;
   if (player.data.health < 1) {
      return true;
   }
   return false;
}

async function _hitEnemy(sprite, attackDirection) {
   const attackPushTime = 250;  // Amount of time in milliseconds the hit entity (if any) is pushed for.
   const attackVelocity = 32;   // Distance to push the entity we hit (if any) in px.
   const attackRangePx = 22;    // Range in pixels of the attack.
   const attackDamage = 1;      // Amount of damage the sword deals.

   rpg.getSprite(sprite.id).data.isHit = true;
   
   const force = _calculatePushForce(attackDirection, attackVelocity);
   rpg.playAudio("hurt-enemy", false);

   await rpg.moveSprite(sprite.id, sprite.x + force.x, sprite.y + force.y, attackPushTime);
   await _animateDefend(sprite.id, attackDirection);

   rpg.getSprite(sprite.id).data.isHit = false;
   
   sprite.data.health--;
   if (sprite.data.health < 1) {
      rpg.removeSprite(sprite.id);
      return { item: "heart.sprite", location: sprite.location };
   }
}

async function _hitObject(sprite, attackDirection) {
   rpg.removeSprite(sprite.id);
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

function _calculateBump(direction) {
   switch (direction) {
      case "NORTH":
         return { direction: "SOUTH", defendAnimationId: "DEFEND_NORTH" };
      case "SOUTH":
         return { direction: "NORTH", defendAnimationId: "DEFEND_SOUTH" };
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         return { direction: "WEST", defendAnimationId: "DEFEND_EAST" };
      default:
         return { direction: "EAST", defendAnimationId: "DEFEND_WEST" };
   }
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