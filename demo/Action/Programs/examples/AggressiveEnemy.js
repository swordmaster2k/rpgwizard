// Behavior for an aggressive enemy that actively persues the player if they are within range.
// It cannot path find, it simply attempts to walk straight at the player.

// Some basic details of the enemies behavior.
var approachDistance = 256;   // Max distance the character can be away from us in px.
var movementVelocity = 1.0;   // Amount of movement per cycle.
var movementTime = 40;        // Time it takes to move once in milliseconds.

// Setup this enemies destroy callback, this will be called when the enemy is defeated.
if (!this.destroyCallback) {
   this.destroyCallback = function() {
      rpgcode.destroySprite(entity.id);
   };
}

// Check that we aren't in a hit state.
if (!this.sprite.enemy.isHit) {
   // Move towards the character if they are on the same layer and within our range.
   var loc = rpgcode.getCharacterLocation(false);
   if (loc.layer === this.layer) {
      var distance = rpgcode.getDistanceBetweenPoints(loc.x, loc.y, this.x, this.y);
      if (distance < approachDistance) {
         var entity = this;
         ARPG.moveSpriteTowardsPoint(this, loc, movementVelocity, movementTime);
      }
   }
}
