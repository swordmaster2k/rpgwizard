/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard, PATH_ANIMATION, PATH_PROGRAM, Promise */

function Sprite() {
    this.x = 0;
    this.y = 0;
    this.layer = 0;
    this.direction = this.DirectionEnum.SOUTH;
    this.collisionPoints = [];
    this.activationPoints = [];
    this.spriteGraphics = {
        elapsed: 0,
        frameIndex: 0,
        active: {},
        south: null,
        north: null,
        east: null,
        west: null,
        southEast: null,
        southWest: null,
        northEast: null,
        northWest: null,
        southIdle: null,
        northIdle: null,
        eastIdle: null,
        westIdle: null,
        southEastIdle: null,
        southWestIdle: null,
        northEastIdle: null,
        northWestIdle: null,
        attack: null,
        defend: null,
        specialMove: null,
        die: null,
        rest: null,
        custom: {

        }
    };
    this.isHit = false;
}

Sprite.prototype.DirectionEnum = {
    NORTH: "n",
    SOUTH: "s",
    EAST: "e",
    WEST: "w",
    NORTH_EAST: "ne",
    NORTH_WEST: "nw",
    SOUTH_EAST: "se",
    SOUTH_WEST: "sw",
    ATTACK: "ATTACK",
    DEFEND: "DEFEND",
    DIE: "DIE"
};

Sprite.prototype.StandardKeys = [
    "SOUTH", "NORTH", "EAST", "WEST", "NORTH_EAST", "NORTH_WEST",
    "SOUTH_EAST", "SOUTH_WEST", "ATTACK", "DEFEND", "SPECIAL_MOVE", "DIE",
    "REST", "SOUTH_IDLE", "NORTH_IDLE", "EAST_IDLE", "WEST_IDLE",
    "NORTH_EAST_IDLE", "NORTH_WEST_IDLE", "SOUTH_EAST_IDLE", "SOUTH_WEST_IDLE"
];

Sprite.prototype.calculateCollisionPoints = function () {
    // Build the collision polygon.
    var xOffset = this.baseVectorOffset.x;
    var yOffset = this.baseVectorOffset.y;
    var points = [];
    this.baseVector.points.forEach(function (point) {
        points.push(point.x += xOffset);
        points.push(point.y += yOffset);
    });
    this.collisionPoints = points;
};

Sprite.prototype.calculateActivationPoints = function () {
    // Build the collision polygon.
    var xOffset = this.activationOffset.x;
    var yOffset = this.activationOffset.y;
    var points = [];
    this.activationVector.points.forEach(function (point) {
        points.push(point.x += xOffset);
        points.push(point.y += yOffset);
    });
    this.activationPoints = points;
};

Sprite.prototype.loadAssets = async function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Sprite assets name=[%s]", this.name);
    }

    var frames = await this.loadAnimations();
    var soundEffects = this.loadSoundEffects();

    // Return the assets that need to be loaded.
    return {"images": frames, "audio": soundEffects};
};

Sprite.prototype.loadAnimations = async function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Sprite animations name=[%s]", this.name);
    }

    // Load up the standard animations.
    var standardKeys = this.StandardKeys;

    const [south, north, east, west, northEast, northWest, southEast, southWest,
        attack, defend, specialMove, die, rest, southIdle, northIdle, eastIdle,
        westIdle, northEastIdle, northWestIdle, southEastIdle, southWestIdle
    ] = await Promise.all(
            [
                this._loadAnimation(this.animations[standardKeys[0]]),
                this._loadAnimation(this.animations[standardKeys[1]]),
                this._loadAnimation(this.animations[standardKeys[2]]),
                this._loadAnimation(this.animations[standardKeys[3]]),
                this._loadAnimation(this.animations[standardKeys[4]]),
                this._loadAnimation(this.animations[standardKeys[5]]),
                this._loadAnimation(this.animations[standardKeys[6]]),
                this._loadAnimation(this.animations[standardKeys[7]]),
                this._loadAnimation(this.animations[standardKeys[8]]),
                this._loadAnimation(this.animations[standardKeys[9]]),
                this._loadAnimation(this.animations[standardKeys[10]]),
                this._loadAnimation(this.animations[standardKeys[11]]),
                this._loadAnimation(this.animations[standardKeys[12]]),
                this._loadAnimation(this.animations[standardKeys[13]]),
                this._loadAnimation(this.animations[standardKeys[14]]),
                this._loadAnimation(this.animations[standardKeys[15]]),
                this._loadAnimation(this.animations[standardKeys[16]]),
                this._loadAnimation(this.animations[standardKeys[17]]),
                this._loadAnimation(this.animations[standardKeys[18]]),
                this._loadAnimation(this.animations[standardKeys[19]]),
                this._loadAnimation(this.animations[standardKeys[20]])
            ]
            );

    this.spriteGraphics.south = south;
    this.spriteGraphics.north = north;
    this.spriteGraphics.east = east;
    this.spriteGraphics.west = west;
    this.spriteGraphics.northEast = northEast;
    this.spriteGraphics.northWest = northWest;
    this.spriteGraphics.southEast = southEast;
    this.spriteGraphics.southWest = southWest;
    this.spriteGraphics.attack = attack;
    this.spriteGraphics.defend = defend;
    this.spriteGraphics.specialMove = specialMove;
    this.spriteGraphics.die = die;
    this.spriteGraphics.rest = rest;
    this.spriteGraphics.southIdle = southIdle;
    this.spriteGraphics.northIdle = northIdle;
    this.spriteGraphics.eastIdle = eastIdle;
    this.spriteGraphics.westIdle = westIdle;
    this.spriteGraphics.northEastIdle = northEastIdle;
    this.spriteGraphics.northWestIdle = northWestIdle;
    this.spriteGraphics.southEastIdle = southEastIdle;
    this.spriteGraphics.southWestIdle = southWestIdle;

    if (this.spriteGraphics.northEast === null) {
        this.spriteGraphics.northEast = this.spriteGraphics.east;
    }
    if (this.spriteGraphics.northWest === null) {
        this.spriteGraphics.northWest = this.spriteGraphics.west;
    }
    if (this.spriteGraphics.southEast === null) {
        this.spriteGraphics.southEast = this.spriteGraphics.east;
    }
    if (this.spriteGraphics.southWest === null) {
        this.spriteGraphics.southWest = this.spriteGraphics.west;
    }

    // Get a copy of the animations for the next step;
    var animations = {};
    for (var animation in this.animations) {
        animations[animation] = this.animations[animation];
    }

    // Clear out the standard animations to get the custom ones.
    standardKeys.forEach(function (key) {
        delete animations[key];
    });

    // Load up the custom animations.
    var customPromises = [];
    for (animation in animations) {
        customPromises.push(this._loadAnimation(animations[animation]));
    }
    const customAnimations = await Promise.all(customPromises);
    for (var i = 0; i < customAnimations.length; i++) {
        this.spriteGraphics.custom[Object.keys(animations)[i]] = customAnimations[i];
    }
    
    const frames = await this.loadFrames();
    return frames;
};

Sprite.prototype._loadAnimation = async function (fileName) {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Sprite animation name=[%s], fileName=[%s]", this.name, fileName);
    }

    if (fileName) {
        if (rpgwizard.animations[fileName]) {
            return rpgwizard.animations[fileName];
        } else {
            var animation = new Animation(PATH_ANIMATION + fileName);
            var result = await animation.load();
            rpgwizard.animations[fileName] = result;
            return result;
        }
    } else {
        return null;
    }
};

Sprite.prototype.loadFrames = async function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Sprite frames name=[%s]", this.name);
    }

    var frames = [];
    // TODO: create a standard graphics collection in the place of this hack!
    for (var property in this.spriteGraphics) {
        if (this.spriteGraphics[property]) {
            if (this.spriteGraphics[property].spriteSheet) {
                frames = frames.concat(this.spriteGraphics[property].spriteSheet.image);
            }
        }
    }

    for (var customAnimation in this.spriteGraphics.custom) {
        if (this.spriteGraphics.custom.hasOwnProperty(customAnimation)) {
            frames = frames.concat(this.spriteGraphics.custom[customAnimation].spriteSheet.image);
        }
    }

    return frames;
};

Sprite.prototype.loadSoundEffects = function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Loading Sprite sound effects name=[%s]", this.name);
    }

    var soundEffects = {};
    soundEffects[this.spriteGraphics.north.soundEffect] = this.spriteGraphics.north.soundEffect;
    soundEffects[this.spriteGraphics.south.soundEffect] = this.spriteGraphics.south.soundEffect;
    soundEffects[this.spriteGraphics.east.soundEffect] = this.spriteGraphics.east.soundEffect;
    soundEffects[this.spriteGraphics.west.soundEffect] = this.spriteGraphics.west.soundEffect;

    for (var customAnimation in this.spriteGraphics.custom) {
        if (this.spriteGraphics.custom.hasOwnProperty(customAnimation)) {
            soundEffects[this.spriteGraphics.custom[customAnimation].soundEffect] = this.spriteGraphics.custom[customAnimation].soundEffect;
        }
    }

    delete soundEffects[""]; // TODO: need to make sure this can't happen.

    return soundEffects;
};

Sprite.prototype.setReady = function () {
    if (rpgwizard.debugEnabled) {
        console.debug("Setting ready Sprite name=[%s]", this.name);
    }

    this.spriteGraphics.active = this.spriteGraphics.south;
    this.getActiveFrame();
    this.renderReady = true;
};

Sprite.prototype.animate = function (step) {
    try {
        if (!step || !this.renderReady || !this.spriteGraphics.active.spriteSheet.canvas) {
            return;
        }
        this.spriteGraphics.elapsed += step;
        var delay = (1.0 / this.spriteGraphics.active.frameRate);
        if (this.spriteGraphics.elapsed >= delay) {
            this.spriteGraphics.elapsed -= delay;
            var frame = this.spriteGraphics.frameIndex + 1;
            if (frame < this.spriteGraphics.active.spriteSheet.width / this.spriteGraphics.active.width) {
                this.spriteGraphics.frameIndex = frame;
            } else {
                this.spriteGraphics.frameIndex = 0;
            }
        }
    } catch (err) {
        console.error(err);
    }
};

Sprite.prototype.changeGraphics = function (direction) {
    this.spriteGraphics.elapsed = 0;
    this.spriteGraphics.frameIndex = 0;

    switch (direction) {
        case "NORTH":
        case this.DirectionEnum.NORTH:
            this.direction = this.DirectionEnum.NORTH;
            this.spriteGraphics.active = this.spriteGraphics.north;
            break;
        case "SOUTH":
        case this.DirectionEnum.SOUTH:
            this.direction = this.DirectionEnum.SOUTH;
            this.spriteGraphics.active = this.spriteGraphics.south;
            break;
        case "EAST":
        case this.DirectionEnum.EAST:
            this.direction = this.DirectionEnum.EAST;
            this.spriteGraphics.active = this.spriteGraphics.east;
            break;
        case "WEST":
        case this.DirectionEnum.WEST:
            this.direction = this.DirectionEnum.WEST;
            this.spriteGraphics.active = this.spriteGraphics.west;
            break;
        case "NORTH_EAST":
        case this.DirectionEnum.NORTH_EAST:
            this.direction = this.DirectionEnum.NORTH_EAST;
            this.spriteGraphics.active = this.spriteGraphics.northEast;
            break;
        case "NORTH_WEST":
        case this.DirectionEnum.NORTH_WEST:
            this.direction = this.DirectionEnum.NORTH_WEST;
            this.spriteGraphics.active = this.spriteGraphics.northWest;
            break;
        case "SOUTH_EAST":
        case this.DirectionEnum.SOUTH_EAST:
            this.direction = this.DirectionEnum.SOUTH_EAST;
            this.spriteGraphics.active = this.spriteGraphics.southEast;
            break;
        case "SOUTH_WEST":
        case this.DirectionEnum.SOUTH_WEST:
            this.direction = this.DirectionEnum.SOUTH_WEST;
            this.spriteGraphics.active = this.spriteGraphics.southWest;
            break;
        case this.DirectionEnum.ATTACK:
            this.spriteGraphics.active = this.spriteGraphics.attack;
            break;
        case this.DirectionEnum.DEFEND:
            this.spriteGraphics.active = this.spriteGraphics.defend;
            break;
        case this.DirectionEnum.DIE:
            this.spriteGraphics.active = this.spriteGraphics.die;
            break;
        default:
            this.spriteGraphics.active = this.spriteGraphics.custom[direction];
    }
};

Sprite.prototype.prepareActiveAnimation = function () {
    var animation = this.spriteGraphics.active;
    var spriteSheet = animation.spriteSheet;
    var image = Crafty.assets[Crafty.__paths.images + spriteSheet.image];

    var canvas = document.createElement("canvas");
    canvas.width = image.width;
    canvas.height = image.height;
    spriteSheet.canvas = canvas;
    spriteSheet.ctx = canvas.getContext("2d");
    spriteSheet.ctx.drawImage(image, 0, 0);
    spriteSheet.frames = [];

    var columns = Math.round(spriteSheet.width / animation.width);
    var rows = Math.round(spriteSheet.height / animation.height);
    var frames = columns * rows;
    for (var index = 0; index < frames; index++) {
        // Converted 1D index to 2D cooridnates.
        var x = index % columns;
        var y = Math.floor(index / columns);

        var imageData = spriteSheet.ctx.getImageData(
                (x * animation.width) + spriteSheet.x, 
                (y * animation.height) + spriteSheet.y,
                animation.width, 
                animation.height
        );

        var frame = document.createElement("canvas");
        frame.width = animation.width;
        frame.height = animation.height;
        var frameCtx = frame.getContext("2d");
        frameCtx.putImageData(imageData, 0, 0);

        spriteSheet.frames.push(frame);
    }
};

Sprite.prototype.getActiveFrame = function () {
    // TODO: Switch to Crafty animation.
    var index = this.spriteGraphics.frameIndex;
    var animation = this.spriteGraphics.active;
    var spriteSheet = animation.spriteSheet;

    if (!animation.spriteSheet.ctx || !spriteSheet.frames[index]) {
        // First time rendering.
        this.prepareActiveAnimation();
    }

    return spriteSheet.frames[index];
};

Sprite.prototype.onSameLayer = function (collision) {
    if (collision.obj.layer !== undefined && collision.obj.layer !== null) {
        return collision.obj.layer === this.layer;
    } else if (collision.obj.sprite !== undefined && collision.obj.sprite !== null) {
        return collision.obj.sprite.layer === this.layer;
    } else if (collision.obj.character !== undefined && collision.obj.character !== null) {
        return collision.obj.character.layer === this.layer;
    }
    return false;
};

Sprite.prototype.isEnabled = function() {
    return !(this.baseVectorDisabled || this.activationVectorDisabled);
};

Sprite.prototype.checkCollisions = function (collision, entity) {
    // Not used yet.
};

Sprite.prototype.checkActivations = function (collision, entity) {
    // Not used yet.
};