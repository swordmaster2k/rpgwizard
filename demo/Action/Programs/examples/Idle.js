// Thread that can be attached to NPCs to idly animate their SOUTH animation
if (!this.isAnimated) {
   var id = this.sprite.id;
   this.isAnimated = true;
   var animate = function() {
      rpgcode.animateSprite(id, "SOUTH", animate);
   };
   animate();
}
