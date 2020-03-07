// A basic sword slash program that should be triggered by a key press or mouse click.

// Some basic sword properites.
var attackPushTime = 250;  // Amount of time in milliseconds the hit entity (if any) is pushed for.
var attackVelocity = 32;   // Distance to push the entity we hit (if any) in px.
var attackRangePx = 22;    // Range in pixels of the attack.
var attackDamage = 1;      // Amount of damage the sword deals.

// Only slash the sword if we aren't in a hit state.
if (!rpgcode.getCharacter("Hero").isHit) {
   ARPG.slashSword(attackPushTime, attackVelocity, attackRangePx, attackDamage, hitEnemy, hitNpc);
} else {
   rpgcode.endProgram();
}

// The function that is called if an enemy is hit.
function hitEnemy(sprite, direction, defendAnimationId) {
   var details = {
      "spriteId": sprite.id,
      "attackDamage": attackDamage,
      "direction": direction, 
      "attackVelocity": attackVelocity, 
      "attackPushTime": attackPushTime,
      "defendAnimationId": defendAnimationId,
      "attackSound": "hurtEnemy"
   };
   ARPG.attackEnemy(details, function(details, result) {
      if (result.dead) {
         ARPG.dropItem("heart.npc", result.location);
      }
   });
   rpgcode.endProgram();
}

// The function that is called if an npc is hit.
function hitNpc(sprite) {
   if (sprite.name === "bush.npc") {
      rpgcode.destroySprite(sprite.id);
   }
   rpgcode.endProgram();
}
