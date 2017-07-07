/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global Sprite */

Character.prototype = Object.create(Sprite.prototype);
Character.prototype.constructor = Character;

function Character(filename) {
    console.info("Loading Character filename=[%s]", filename);
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
        sprite.checkCollisions(hit, entity);
    });
};

Character.prototype.hitOffCollision = function (hitData, entity) {
    // Not used yet.
};

Character.prototype.hitOnActivation = function (hitData, entity) {
    var sprite = this;
    hitData.forEach(function(hit) {
        sprite.checkActivations(hit, entity);
    });
};

Character.prototype.hitOffActivation = function (hitData, entity) {
    // Not used yet.
};

Character.prototype.checkCollisions = function (collision, entity) {
    console.debug("Checking collisions for Character name=[%s]", this.name);

    var object = collision.obj;
    if (object.layer !== this.layer) {
        return;
    }

    switch (object.vectorType) {
        case "ENEMY":
        case "NPC":
        case "SOLID":
            entity.x -= collision.overlap * collision.normal.x;
            entity.y -= collision.overlap * collision.normal.y;
            entity.resetHitChecks();
            break;
        case "PASSABLE":
            break;
    }
};