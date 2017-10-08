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
        console.info("Loading Enemy filename=[%s]", filename);
    }
    
    Sprite.call(this);

    // TODO: Make the changes here that chrome suggests.
    var req = new XMLHttpRequest();
    req.open("GET", filename, false);
    req.overrideMimeType("text/plain; charset=x-user-defined");
    req.send(null);

    var npc = JSON.parse(req.responseText);
    for (var property in npc) {
        this[property] = npc[property];
    }

    this.calculateCollisionPoints();
    this.calculateActivationPoints();
}

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
        case "CHARACTER":
        case "ENEMY":
        case "PASSABLE":
            break;
    }
};

Enemy.prototype.checkActivations = function (collisions, entity) {
    // Not used yet.
};