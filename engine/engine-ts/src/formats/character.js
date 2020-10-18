/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global Sprite, rpgwizard, PATH_PROGRAM, rpgcode */

Character.prototype = Object.create(Sprite.prototype);
Character.prototype.constructor = Character;

function Character(filename) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Creating Character filename=[%s]", filename);
    }
    this.filename = filename;
    Sprite.call(this);
}

Character.prototype.load = async function (json) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Loading Character filename=[%s]", this.filename);
    }

    if (!json) {
        let response = await fetch(this.filename);
        json = await response.json();
        Core.getInstance().characters[this.filename] = JSON.stringify(json);
    }
    
    for (var property in json) {
        this[property] = json[property];
    }
    this.calculateCollisionPoints();
    this.calculateActivationPoints();

    if (Core.getInstance().debugEnabled) {
        console.debug("Finished loading Character filename=[%s]", this.filename);
    }

    return this;
};

Character.prototype.hitOnCollision = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function (hit) {
        sprite.processCollision(hit, entity);
    });
};

Character.prototype.hitOffCollision = function (hitData, entity) {
    entity.resetHitChecks();
};

Character.prototype.hitOnActivation = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function (hit) {
        sprite.processActivation(hit, entity, true);
    });
};

Character.prototype.hitOffActivation = function (hitData, entity) {
    if (entity.previousKeyHandler) {
        rpgcode.registerKeyDown(entity.previousKeyHandler.key, entity.previousKeyHandler.callback, true);
        this.previousKeyHandler = null;
    }
    entity.activationVector.resetHitChecks();
};

Character.prototype.processCollision = function (collision, entity) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Processing collision for Character name=[%s], collision.obj=[%s], entity=[%s]", this.name, collision.obj, entity);
    }

    if (this.onSameLayer(collision) && !this.baseVectorDisabled && this.isOtherCollidable(collision.obj)) {
        var object = collision.obj;
        switch (object.vectorType) {
            case "NPC":
            case "SOLID":
                entity.cancelTween({x: true, y: true});
                entity.x -= collision.overlap * collision.normal.x;
                entity.y -= collision.overlap * collision.normal.y;
                break;
            case "ENEMY":
            case "PASSABLE":
                break;
        }
    }
    entity.resetHitChecks();
    entity.activationVector.resetHitChecks();
};

Character.prototype.processActivation = function (collision, entity, entering) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Processing activation for Character name=[%s], collision.obj=[%s], entity=[%s], entering=[%s]", this.name, collision.obj, entity, entering);
    }
    if (!this.onSameLayer(collision) || !Core.getInstance().controlEnabled || this.activationVectorDisabled || !this.isOtherActivatable(collision.obj)) {
        return;
    }

    var events;
    var object = collision.obj;
    if (object.layer !== undefined && object.layer !== null) {
        events = object.events;
    } else {
        events = object.sprite.events;
    }

    events.forEach(function (event) {
        if (event.program) {
            if (event.type.toUpperCase() === "OVERLAP") {
                Core.getInstance().runProgram(PATH_PROGRAM.concat(event.program), object);
            } else if (event.type.toUpperCase() === "KEYPRESS") {
                if (event.key) {
                    entity.previousKeyHandler = {
                        key: event.key,
                        callback: Core.getInstance().keyboardHandler.downHandlers[event.key]
                    };
                    var callback = function () {
                        rpgcode.unregisterKeyDown(event.key, true);
                        Core.getInstance().runProgram(PATH_PROGRAM.concat(event.program), object);
                    };
                    rpgcode.registerKeyDown(event.key, callback, true);
                }
            }
        }
    });
};