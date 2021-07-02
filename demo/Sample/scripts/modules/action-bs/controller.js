export function build() {
   return {
      direction: {
         west: false,
         east: false,
         north: false,
         south: false
      },
      speed: 1,
      diagonalSpeed: 0.8,
      update: function(e) {
         if (rpg.getGlobal("pause.input")) {
            return;
         }
         
         if (this.direction.south && this.direction.west) {
            if (e.sprite.direction !== "sw") {
               e.sprite.changeGraphics("sw");
            }
            e.x -= this.diagonalSpeed;
            e.y += this.diagonalSpeed;
         } else if (this.direction.south && this.direction.east) {
            if (e.sprite.direction !== "se") {
               e.sprite.changeGraphics("se");
            }
            e.x += this.diagonalSpeed;
            e.y += this.diagonalSpeed;
         } else if (this.direction.north && this.direction.west) {
            if (e.sprite.direction !== "nw") {
               e.sprite.changeGraphics("nw");
            }
            e.x -= this.diagonalSpeed;
            e.y -= this.diagonalSpeed;
         } else if (this.direction.north && this.direction.east) {
            if (e.sprite.direction !== "ne") {
               e.sprite.changeGraphics("ne");
            }
            e.x += this.diagonalSpeed;
            e.y -= this.diagonalSpeed;
         } else if (this.direction.east) {
            if (e.sprite.direction !== "e") {
               e.sprite.changeGraphics("e");
            }
            e.x += this.speed;
         } else if (this.direction.west) {
            if (e.sprite.direction !== "w") {
               e.sprite.changeGraphics("w");
            }
            e.x -= this.speed;
         } else if (this.direction.north) {
            if (e.sprite.direction !== "n") {
               e.sprite.changeGraphics("n");
            }
            e.y -= this.speed;
         } else if (this.direction.south) {
            if (e.sprite.direction !== "s") {
               e.sprite.changeGraphics("s");
            }
            e.y += this.speed;
         }
      },
      keyDown: function(key) {
         if (key === rpg.keys.RIGHT_ARROW || key === rpg.keys.D) {
            this.direction.east = true;
            this.direction.west = false;
         } else if (key === rpg.keys.LEFT_ARROW || key === rpg.keys.A) {
            this.direction.west = true;
            this.direction.east = false;
         } else if (key === rpg.keys.UP_ARROW || key === rpg.keys.W) {
            this.direction.north = true;
            this.direction.south = false;
         } else if (key === rpg.keys.DOWN_ARROW || key === rpg.keys.S) {
            this.direction.south = true;
            this.direction.north = false;
         }
      },
      keyUp: function(key) {
         if (key === rpg.keys.RIGHT_ARROW || key === rpg.keys.D) {
            this.direction.east = false;
         } else if (key === rpg.keys.LEFT_ARROW || key === rpg.keys.A) {
            this.direction.west = false;
         } else if (key === rpg.keys.UP_ARROW || key === rpg.keys.W) {
            this.direction.north = false;
         } else if (key === rpg.keys.DOWN_ARROW || key === rpg.keys.S) {
            this.direction.south = false;
         }
      }
   };
}
