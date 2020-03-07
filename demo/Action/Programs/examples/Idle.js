if (!this.isAnimated) {
   var id = this.sprite.id;
   this.isAnimated = true;
   var animate = function() {
      rpgcode.animateSprite(id, "SOUTH", animate);
   };
   animate();
}
