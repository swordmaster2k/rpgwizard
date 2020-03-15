if (!this.isAnimated) {
   let id = this.sprite.id;
   this.isAnimated = true;
   let animate = function() {
      rpgcode.animateSprite(id, "SOUTH", animate);
   };
   animate();
}
