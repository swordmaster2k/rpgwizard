/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global PATH_ANIMATION */
import { Framework } from "../framework.js";

import * as Factory from "./asset-factory.js";
import { Animation } from "./animation.js";

import * as Asset from "./dto/assets.js";
import { Collider, Direction, StandardKeys, Trigger } from "./dto/asset-subtypes.js";
import { Core } from "../core.js";

export class Sprite implements Asset.Sprite {

    // Implemented
    readonly name: string;
    readonly description: string;
    readonly animations: Record<string, string>; // REFACTOR: Figure out typing
    readonly collider: Collider;
    readonly trigger: Trigger;
    readonly data: object;
    readonly version: string;

    // Specific
    x: number;
    y: number;
    layer: number;
    direction: Direction;

    // REFACTOR
    collisionPoints: Array<number>;
    activationPoints: Array<number>;
    spriteGraphics: any;
    hit: boolean;
    enemy: any;
    renderReady: boolean;

    constructor(asset: Asset.Sprite) {
        // Copy over values
        this.name = asset.name;
        this.description = asset.description;
        this.animations = asset.animations;
        this.collider = asset.collider;
        this.trigger = asset.trigger;
        this.data = asset.data;
        this.version = asset.version;

        // Setup
        this.x = 0;
        this.y = 0;
        this.layer = 0;
        this.direction = Direction.SOUTH;
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
            custom: {}
        };
        this.hit = false;

        // REFACTOR: Setup collider and trigger later
        // this.calculateCollisionPoints();
        // this.calculateActivationPoints();
    }

    public hitOnCollision(hitData: any, entity: any) {
        // REFACTOR: Implement
    }

    public hitOffCollision(hitData: any, entity: any) {
        // REFACTOR: Implement
    }

    public hitOnActivation(hitData: any, entity: any) {
        // REFACTOR: Implement
    }

    public hitOffActivation(hitData: any, entity: any) {
        // REFACTOR: Implement
    }

    public checkCollisions(collision: any, entity: any) {

    }

    public checkActivations(collision: any, entity: any) {

    }

    public getActiveFrame() {
        // TODO: Switch to Crafty animation.
        var index = this.spriteGraphics.frameIndex;
        var animation = this.spriteGraphics.active;
        var spriteSheet = animation.spriteSheet;

        if (!animation.spriteSheet.ctx || !spriteSheet.frames[index]) {
            // First time rendering.
            this.prepareActiveAnimation();
        }

        this.renderReady = true;

        return spriteSheet.frames[index];
    }

    public async loadAssets() {
        if (Core.getInstance().debugEnabled) {
            console.debug("Loading Sprite assets name=[%s]", this.name);
        }

        var frames = await this.loadAnimations();
        var soundEffects = this.loadSoundEffects();

        // Return the assets that need to be loaded.
        return { images: frames, audio: soundEffects };
    }

    // REFACTOR: Should be on MapSprite?
    public animate(dt: number) {
        // TODO
    }

    private async loadAnimations() {
        if (Core.getInstance().debugEnabled) {
            console.debug("Loading Sprite animations name=[%s]", this.name);
        }

        // Load up the standard animations.
        var standardKeys = StandardKeys;

        const [south, north, east, west, northEast, northWest, southEast, southWest,
            attack, defend, specialMove, die, rest, southIdle, northIdle, eastIdle,
            westIdle, northEastIdle, northWestIdle, southEastIdle, southWestIdle
        ] = await Promise.all(
            [
                this.loadAnimation(this.animations[standardKeys[0]]),
                this.loadAnimation(this.animations[standardKeys[1]]),
                this.loadAnimation(this.animations[standardKeys[2]]),
                this.loadAnimation(this.animations[standardKeys[3]]),
                this.loadAnimation(this.animations[standardKeys[4]]),
                this.loadAnimation(this.animations[standardKeys[5]]),
                this.loadAnimation(this.animations[standardKeys[6]]),
                this.loadAnimation(this.animations[standardKeys[7]]),
                this.loadAnimation(this.animations[standardKeys[8]]),
                this.loadAnimation(this.animations[standardKeys[9]]),
                this.loadAnimation(this.animations[standardKeys[10]]),
                this.loadAnimation(this.animations[standardKeys[11]]),
                this.loadAnimation(this.animations[standardKeys[12]]),
                this.loadAnimation(this.animations[standardKeys[13]]),
                this.loadAnimation(this.animations[standardKeys[14]]),
                this.loadAnimation(this.animations[standardKeys[15]]),
                this.loadAnimation(this.animations[standardKeys[16]]),
                this.loadAnimation(this.animations[standardKeys[17]]),
                this.loadAnimation(this.animations[standardKeys[18]]),
                this.loadAnimation(this.animations[standardKeys[19]]),
                this.loadAnimation(this.animations[standardKeys[20]])
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
        standardKeys.forEach(function(key) {
            delete animations[key];
        });

        // Load up the custom animations.
        var customPromises = [];
        for (animation in animations) {
            customPromises.push(this.loadAnimation(animations[animation]));
        }
        const customAnimations = await Promise.all(customPromises);
        for (var i = 0; i < customAnimations.length; i++) {
            this.spriteGraphics.custom[Object.keys(animations)[i]] = customAnimations[i];
        }

        const frames = await this.loadFrames();
        return frames;
    }

    private async loadAnimation(file: string): Promise<Animation> {
        if (Core.getInstance().debugEnabled) {
            console.debug("Loading Sprite animation name=[%s], fileName=[%s]", this.name, file);
        }

        if (file) {
            let animation: Animation = Core.getInstance().cache.get(file);
            if (animation === null) {
                animation = await Factory.build(PATH_ANIMATION + file) as Animation;
                Core.getInstance().cache.put(file, animation);
            }
            return animation;
        } else {
            return null;
        }
    }

    private async loadFrames() {
        if (Core.getInstance().debugEnabled) {
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
            const present = Object.prototype.hasOwnProperty.call(this.spriteGraphics.custom, customAnimation);
            if (present) {
                frames = frames.concat(this.spriteGraphics.custom[customAnimation].spriteSheet.image);
            }
        }

        return frames;
    }

    private async loadSoundEffects() {
        // REFACTOR: Implement
    }

    private animation(step: number) {
        // REFACTOR: Implement
    }

    private changeGraphics(direciton: Direction) {
        // REFACTOR: Implement
    }

    private prepareActiveAnimation() {
        var animation = this.spriteGraphics.active;
        var spriteSheet = animation.spriteSheet;
        var image: ImageBitmap = Framework.getImage(spriteSheet.image);

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
    }

    private onSameLayer(collision: any) {
        // REFACTOR: Implement
    }

    private isEnabled() {
        // REFACTOR: Implement
    }

    private isOtherCollidable(other: any) {
        // REFACTOR: Implement
    }

    private isOtherActivatable(other: any) {
        // REFACTOR: Implement
    }

}
