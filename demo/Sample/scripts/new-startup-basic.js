export default async function (origin) {
    await rpg.loadMap("sample.map");
    const sprite = rpg.getSprite("player");
    if (sprite) {
        setupControls(sprite);
        rpg.attachControls("player");
    }

    // console.log("sleeping");
    // await rpg.sleep(3000);
    // console.log("awoke");

    // console.log("moving");
    // await rpg.moveSprite("5f53ef6f-9742-456b-9c93-9a96c8b81b8e", 100, 100, 3000);
    // console.log("moved");

    // console.log("animating");
    // await rpg.animateSprite("5f53ef6f-9742-456b-9c93-9a96c8b81b8e", "SOUTH");
    // console.log("animated");

    rpg.registerKeyDown("E", async function() {
        console.log("E");
        console.log("sleeping");
        await rpg.sleep(3000);
        console.log("awoke");
    }, true);
    rpg.registerKeyDown("Q", async function() {
        console.log("Q");
        console.log("sleeping");
        await rpg.sleep(3000);
        console.log("awoke");
    }, true);

    rpg.registerMouseDown(async function() {
        console.log("mouse down");
    }, true);

    rpg.registerMouseUp(async function() {
        console.log("mouse up");
    }, true);

    rpg.registerMouseMove(async function() {
        console.log("mouse move");
    }, true);

    console.log("sleeping");
    await rpg.sleep(30000);
    console.log("awoke");
}

function setupControls(sprite) {
    Crafty.c("CustomControls", {
        __move: {
            west: false,
            east: false,
            north: false,
            south: false
        },
        _speed: 1,
        _diagonalSpeed: 0.8,

        init: function (speed, diagonalSpeed) {
            if (speed) {
                this._speed = speed;
            }
            if (diagonalSpeed) {
                this._diagonalSpeed = diagonalSpeed;
            }
            var move = this.__move;

            this.bind("EnterFrame", function () {
                // Move the player in a direction depending on the booleans.
                if (move.south && move.west) {
                    if (sprite.direction !== "sw") {
                        sprite.direction = "sw";
                        sprite.changeGraphics(sprite.direction);
                    }
                    this.x -= this._diagonalSpeed;
                    this.y += this._diagonalSpeed;
                    Crafty.trigger("Moved", {});
                } else if (move.south && move.east) {
                    if (sprite.direction !== "se") {
                        sprite.direction = "se";
                        sprite.changeGraphics(sprite.direction);
                    }
                    this.x += this._diagonalSpeed;
                    this.y += this._diagonalSpeed;
                    Crafty.trigger("Moved", {});
                } else if (move.north && move.west) {
                    if (sprite.direction !== "nw") {
                        sprite.direction = "nw";
                        sprite.changeGraphics(sprite.direction);
                    }
                    this.x -= this._diagonalSpeed;
                    this.y -= this._diagonalSpeed;
                    Crafty.trigger("Moved", {});
                } else if (move.north && move.east) {
                    if (sprite.direction !== "ne") {
                        sprite.direction = "ne";
                        sprite.changeGraphics(sprite.direction);
                    }
                    this.x += this._diagonalSpeed;
                    this.y -= this._diagonalSpeed;
                    Crafty.trigger("Moved", {});
                } else if (move.east) {
                    if (sprite.direction !== "e") {
                        sprite.direction = "e";
                        sprite.changeGraphics(sprite.direction);
                    }
                    this.x += this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.west) {
                    if (sprite.direction !== "w") {
                        sprite.direction = "w";
                        sprite.changeGraphics(sprite.direction);
                    }
                    this.x -= this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.north) {
                    if (sprite.direction !== "n") {
                        sprite.direction = "n";
                        sprite.changeGraphics(sprite.direction);
                    }
                    this.y -= this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.south) {
                    if (sprite.direction !== "s") {
                        sprite.direction = "s";
                        sprite.changeGraphics(sprite.direction);
                    }
                    this.y += this._speed;
                    Crafty.trigger("Moved", {});
                }
            }).bind("KeyDown", function (e) {
                // If keys are south, set the direction
                if (e.key === Crafty.keys.RIGHT_ARROW || e.key === Crafty.keys.D) {
                    move.east = true;
                    move.west = false;
                } else if (e.key === Crafty.keys.LEFT_ARROW || e.key === Crafty.keys.A) {
                    move.west = true;
                    move.east = false;
                } else if (e.key === Crafty.keys.UP_ARROW || e.key === Crafty.keys.W) {
                    move.north = true;
                    move.south = false;
                } else if (e.key === Crafty.keys.DOWN_ARROW || e.key === Crafty.keys.S) {
                    move.south = true;
                    move.north = false;
                }
            }).bind("KeyUp", function (e) {
                // If key is released, stop moving
                if (e.key === Crafty.keys.RIGHT_ARROW || e.key === Crafty.keys.D) {
                    move.east = false;
                } else if (e.key === Crafty.keys.LEFT_ARROW || e.key === Crafty.keys.A) {
                    move.west = false;
                } else if (e.key === Crafty.keys.UP_ARROW || e.key === Crafty.keys.W) {
                    move.north = false;
                } else if (e.key === Crafty.keys.DOWN_ARROW || e.key === Crafty.keys.S) {
                    move.south = false;
                }
            });

            return this;
        }
    });
}
