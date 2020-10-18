/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global Sprite, rpgwizard */

NPC.prototype = Object.create(Sprite.prototype);
NPC.prototype.constructor = NPC;

function NPC(filename) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Creating NPC filename=[%s]", filename);
    }
    this.filename = filename;
    Sprite.call(this);
}

NPC.prototype.load = async function (json) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Loading NPC filename=[%s]", this.filename);
    }

    if (!json) {
        let response = await fetch(this.filename);
        json = await response.json();
        Core.getInstance().npcs[this.filename] = JSON.stringify(json);
    }
    
    for (var property in json) {
        this[property] = json[property];
    }
    this.calculateCollisionPoints();
    this.calculateActivationPoints();

    if (Core.getInstance().debugEnabled) {
        console.debug("Finished loading NPC filename=[%s]", this.filename);
    }

    return this;
};

NPC.prototype.hitOnCollision = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function(hit) {
        sprite.checkCollisions(hit, entity);
    });
};

NPC.prototype.hitOffCollision = function (hitData, entity) {
    // Not used yet.
};

NPC.prototype.hitOnActivation = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function(hit) {
        sprite.checkActivations(hit, entity);
    });
};

NPC.prototype.hitOffActivation = function (hitData, entity) {
    // Not used yet.
};

NPC.prototype.checkCollisions = function (collision, entity) {
    if (Core.getInstance().debugEnabled) {
        console.debug("Checking collisions for NPC name=[%s], collision.obj=[%s], entity=[%s]", this.name, collision.obj, entity);
    }
    
    if (!this.onSameLayer(collision) || this.baseVectorDisabled || !this.isOtherCollidable(collision.obj)) {
        entity.resetHitChecks();
        return;
    }

    var object = collision.obj;
    switch (object.vectorType) {
        case "CHARACTER":
        case "SOLID":
            entity.cancelTween({x: true, y: true});
            entity.x += collision.normal.x;
            entity.y += collision.normal.y;
            break;
       case "ENEMY":
           break;
    }
    
    entity.resetHitChecks();
};

NPC.prototype.checkActivations = function (collisions, entity) {
    // Not used yet.
};