/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
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
                var handler = rpgwizard.keyboardHandler.downHandlers[e.keyCode];
                if (handler) {
                    handler();
                }
            })
            .bind("KeyUp", function (e) {
                var handler = rpgwizard.keyboardHandler.upHandlers[e.keyCode];
                if (handler) {
                    handler();
                }
            });

    Crafty.c("CustomControls", {
        __move: {left: false, right: false, up: false, down: false},
        _speed: 3,

        CustomControls: function (speed) {
            if (speed) {
                this._speed = speed;
            }
            var move = this.__move;

            this.bind("EnterFrame", function () {
                if (!rpgwizard.controlEnabled) {
                    return;
                }

                // Move the player in a direction depending on the booleans
                // Only move the player in one direction at a time (up/down/left/right)
                if (move.right) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.EAST) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.EAST;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.x += this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.left) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.WEST) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.WEST;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.x -= this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.up) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.NORTH) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.NORTH;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.y -= this._speed;
                    Crafty.trigger("Moved", {});
                } else if (move.down) {
                    if (rpgwizard.craftyCharacter.character.direction !== this.character.DirectionEnum.SOUTH) {
                        rpgwizard.craftyCharacter.character.direction = this.character.DirectionEnum.SOUTH;
                        this.character.changeGraphics(this.character.direction);
                    }
                    this.y += this._speed;
                    Crafty.trigger("Moved", {});
                }
            }).bind("KeyDown", function (e) {
                // If keys are down, set the direction
                if (e.keyCode === Crafty.keys.RIGHT_ARROW || e.keyCode === Crafty.keys.D) {
                    move.right = true;
                    move.left = move.up = move.down = false;
                } else if (e.keyCode === Crafty.keys.LEFT_ARROW || e.keyCode === Crafty.keys.A) {
                    move.left = true;
                    move.right = move.up = move.down = false;
                } else if (e.keyCode === Crafty.keys.UP_ARROW || e.keyCode === Crafty.keys.W) {
                    move.up = true;
                    move.right = move.left = move.down = false;
                } else if (e.keyCode === Crafty.keys.DOWN_ARROW || e.keyCode === Crafty.keys.S) {
                    move.down = true;
                    move.right = move.left = move.up = false;
                }
            }).bind("KeyUp", function (e) {
                // If key is released, stop moving
                if (e.keyCode === Crafty.keys.RIGHT_ARROW || e.keyCode === Crafty.keys.D) {
                    move.right = false;
                } else if (e.keyCode === Crafty.keys.LEFT_ARROW || e.keyCode === Crafty.keys.A) {
                    move.left = false;
                } else if (e.keyCode === Crafty.keys.UP_ARROW || e.keyCode === Crafty.keys.W) {
                    move.up = false;
                } else if (e.keyCode === Crafty.keys.DOWN_ARROW || e.keyCode === Crafty.keys.S) {
                    move.down = false;
                }
            });

            return this;
        }
    });
}

