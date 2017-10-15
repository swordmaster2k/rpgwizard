/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * Copyeast (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard */

function Keyboard() {
    this.downHandlers = {};
    this.upHandlers = {};
    this.entity = Crafty.e()
            .bind("KeyDown", function (e) {
                var handler = rpgwizard.inProgram ? rpgwizard.keyboardHandler.downHandlers[e.key] : rpgwizard.keyDownHandlers[e.key];
                
                if (handler) {
                    handler();
                }
            })
            .bind("KeyUp", function (e) {
                var handler = rpgwizard.inProgram ? rpgwizard.keyboardHandler.upHandlers[e.key] : rpgwizard.keyUpHandlers[e.key];
                
                if (handler) {
                    handler();
                }
            });

    Crafty.c("CustomControls", {
        __move: {
            west: false, 
            east: false, 
            north: false, 
            south: false
        },
        _speed: 1,
        _diagonalSpeed: 0.8,

        CustomControls: function (speed, diagonalSpeed) {
            if (speed) {
                this._speed = speed;
            }
            if (diagonalSpeed) {
                this._diagonalSpeed = diagonalSpeed;
            }
            var move = this.__move;

            this.bind("EnterFrame", function () {
                if (!rpgwizard.controlEnabled) {
                    return;
                }

                // Move the player in a direction depending on the booleans.
                if (move.south && move.west) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.SOUTH_WEST) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.SOUTH_WEST;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.x -= this._diagonal_speed;
                    this.y += this._diagonal_speed;
                    Crafty.trigger("Moved", {});
                } else if (move.south && move.east) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.SOUTH_EAST) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.SOUTH_EAST;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.x += this._diagonal_speed;
                    this.y += this._diagonal_speed;
                    Crafty.trigger("Moved", {});
                } else if (move.north && move.west) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.NORTH_WEST) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.NORTH_WEST;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.x -= this._diagonal_speed;
                    this.y -= this._diagonal_speed;
                    Crafty.trigger("Moved", {});
                } else if (move.north && move.east) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.NORTH_EAST) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.NORTH_EAST;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.x += this._diagonal_speed;
                    this.y -= this._diagonal_speed;
                    Crafty.trigger("Moved", {});
                } else if (move.east) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.EAST) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.EAST;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.x += this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.west) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.WEST) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.WEST;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.x -= this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.north) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.NORTH) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.NORTH;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.y -= this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.south) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.SOUTH) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.SOUTH;
                        this.character.changeGraphics(this.character.direction);
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

