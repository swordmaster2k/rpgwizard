// // Behavior for a passive enemy that wanders randomly around the board.

 var wanderDistancePx = 80;   // px
 var wanderTime = 2500;       // ms
 var entity = this;

 // Setup this enemies destroy callback, this will be called when the enemy is defeated.
 if (!this.destroyCallback) {
    this.destroyCallback = function() {
       rpgcode.destroySprite(entity.id);
    };
 }

 function doPassive() {
    if (!entity.sprite.enemy.isHit) {
       // We aren't hit keep wandering.
       ARPG.wander(entity, wanderDistancePx, wanderTime, doPassive);
    } else {
       // Stop wandering.
       entity.wandering = false;
    }
 }

 // Check we aren't already wandering and aren't in a hit state.
 if (!this.wandering && !this.sprite.enemy.isHit) {
    this.wandering = true; // We are now wandering until hit.
    doPassive(); // Continously call this function until we are hit.
 }
