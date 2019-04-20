/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global Sprite, rpgwizard */

Enemy.prototype = Object.create(Sprite.prototype);
Enemy.prototype.constructor = Enemy;

function Enemy(filename) {
    if (rpgwizard.debugEnabled) {
        console.info("Creating Enemy filename=[%s]", filename);
    }
    this.filename = filename;
    Sprite.call(this);
}

Enemy.prototype.load = async function (json) {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Enemy filename=[%s]", this.filename);
    }

    if (!json) {
        let response = await fetch(this.filename);
        json = await response.json();
        rpgwizard.enemies[this.filename] = JSON.stringify(json);
    }
    
    for (var property in json) {
        this[property] = json[property];
    }
    this.calculateCollisionPoints();
    this.calculateActivationPoints();

    if (rpgwizard.debugEnabled) {
        console.debug("Finished loading Enemy filename=[%s]", this.filename);
    }

    return this;
};

Enemy.prototype.hitOnCollision = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function(hit) {
        sprite.checkCollisions(hit, entity);
    });
};

Enemy.prototype.hitOffCollision = function (hitData, entity) {
    // Not used yet.
};

Enemy.prototype.hitOnActivation = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function(hit) {
        sprite.checkActivations(hit, entity);
    });
};

Enemy.prototype.hitOffActivation = function (hitData, entity) {
    // Not used yet.
};

Enemy.prototype.checkCollisions = function (collision, entity) {
    if (rpgwizard.debugEnabled) {
        console.debug("Checking collisions for Enemy name=[%s]", this.name);
    }

    if (!this.onSameLayer(collision)) {
        entity.resetHitChecks();
        return;
    }
    
    var object = collision.obj;
    switch (object.vectorType) {
        case "NPC":
        case "SOLID":
            entity.cancelTween({x: true, y: true});
            entity.x -= collision.overlap * collision.normal.x;
            entity.y -= collision.overlap * collision.normal.y;
            break;
        case "CHARACTER":
        case "ENEMY":
        case "PASSABLE":
            break;
    }
    
    entity.resetHitChecks();
};

Enemy.prototype.checkActivations = function (collisions, entity) {
    // Not used yet.
};