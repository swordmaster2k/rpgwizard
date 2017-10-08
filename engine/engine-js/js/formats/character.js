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
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Character filename=[%s]", filename);
    }
    
    Sprite.call(this);

    // TODO: Make the changes here that chrome suggests.
    var req = new XMLHttpRequest();
    req.open("GET", filename, false);
    req.overrideMimeType("text/plain; charset=x-user-defined");
    req.send(null);

    var character = JSON.parse(req.responseText);
    for (var property in character) {
        this[property] = character[property];
    }

    this.calculateCollisionPoints();
    this.calculateActivationPoints();
}

Character.prototype.hitOnCollision = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function(hit) {
        sprite.processCollision(hit, entity);
    });
};

Character.prototype.hitOffCollision = function (hitData, entity) {
    // Not used yet.
};

Character.prototype.hitOnActivation = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function(hit) {
        sprite.processActivation(hit, entity, true);
    });
};

Character.prototype.hitOffActivation = function (hitData, entity) {
    if (entity.previousKeyHandler) {
        rpgcode.registerKeyDown(entity.previousKeyHandler.key, entity.previousKeyHandler.callback, true);
        this.previousKeyHandler = null;
    }
};

Character.prototype.processCollision = function (collision, entity) {
    if (rpgwizard.debugEnabled) {
        console.debug("Processing collision for Character name=[%s]", this.name);
    }

    var object = collision.obj;
    if (object.layer !== this.layer) {
        return;
    }

    switch (object.vectorType) {
        case "NPC":
        case "SOLID":
            entity.cancelTween({x: true, y: true});
            entity.x -= collision.overlap * collision.normal.x;
            entity.y -= collision.overlap * collision.normal.y;
            entity.resetHitChecks();
            break;
        case "ENEMY":
        case "PASSABLE":
            break;
    }
};

Character.prototype.processActivation = function (collision, entity, entering) {
    if (rpgwizard.debugEnabled) {
        console.debug("Processing activation for Character name=[%s]", this.name);
    }

    var layer = this.layer;
    var object = collision.obj;
    var events;
    if (object.layer !== undefined && object.layer !== null) {
        if (object.layer !== layer) {
            return;
        }
        events = object.events;
    } else {
        if (object.sprite.layer !== layer) {
            return;
        }
        events = object.sprite.events;
    }
    
    events.forEach(function (event) {
        if (event.program) {
            if (event.type.toUpperCase() === "OVERLAP") {
                rpgwizard.runProgram(PATH_PROGRAM.concat(event.program), object);
            } else if (event.type.toUpperCase() === "KEYPRESS") {
                if (event.key) {
                    entity.previousKeyHandler = {
                        key: event.key, 
                        callback: rpgwizard.keyboardHandler.downHandlers[event.key]
                    };
                    var callback = function() {
                        rpgcode.runProgram(event.program, object);  
                    };
                    rpgcode.registerKeyDown(event.key, callback, true);
                }
            }
        }
    });
};