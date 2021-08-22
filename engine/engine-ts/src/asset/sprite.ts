/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
import { Framework } from "../framework.js";

import * as Factory from "./asset-factory.js";
import { Animation } from "./animation.js";

import * as Asset from "./dto/assets.js";
import { Collider, Direction, Event, EventType, Location, StandardKeys, Trigger } from "./dto/asset-subtypes.js";
import { Core } from "../core.js";
import { ScriptEvent } from "../client-api/script-vm.js";

/**
 * REFACTOR: Break this up a bit
 */
export class Sprite implements Asset.Sprite {

    // DTO
    readonly name: string;
    readonly description: string;
    readonly animations: Record<string, string>;
    readonly collider: Collider;
    readonly trigger: Trigger;
    readonly data: object;
    readonly version: string;

    // Runtime
    _id: string;
    _x: number;
    _y: number;
    _layer: number;
    _direction: Direction;
    _thread: string;
    _collisionPoints: Array<number>;
    _triggerPoints: Array<number>;
    _triggerEntity: any;

    // REFACTOR
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
        this._x = 0;
        this._y = 0;
        this._layer = 0;
        this._direction = Direction.SOUTH;
        this._thread = null;

        this._collisionPoints = [];
        this.calculateCollisionPoints();

        this._triggerPoints = [];
        this.calculateTriggerPoints();

        this.hit = false;

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
    }

    // Getters & Setters
    get id(): string {
        return this._id;
    }

    set id(v: string) {
        this._id = v;
    }

    get x(): number {
        return this._x;
    }

    set x(v: number) {
        this._x = v;
    }

    get y(): number {
        return this._y;
    }

    set y(v: number) {
        this._y = v;
    }

    get layer(): number {
        return this._layer;
    }

    set layer(v: number) {
        this._layer = v;
    }

    get location(): Location {
        return { x: this._x, y: this._y, layer: this._layer };
    }

    get direction(): Direction {
        return this._direction;
    }

    set direction(v: Direction) {
        this._direction = v;
    }

    get thread(): string {
        return this._thread;
    }

    set thread(v: string) {
        this._thread = v;
    }

    get triggerEntity(): any {
        return this._triggerEntity;
    }

    set triggerEntity(v: any) {
        this._triggerEntity = v;
    }

    private calculateCollisionPoints() {
        for (const point of this.collider.points) {
            this._collisionPoints.push(point.x + this.collider.x);
            this._collisionPoints.push(point.y + this.collider.y);
        }
    }

    private calculateTriggerPoints() {
        for (const point of this.trigger.points) {
            this._triggerPoints.push(point.x + this.trigger.x);
            this._triggerPoints.push(point.y + this.trigger.y);
        }
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
        try {
            if (!dt || !this.renderReady || !this.spriteGraphics.active.spriteSheet.canvas) {
                return;
            }
            this.spriteGraphics.elapsed += dt;
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
    }

    public changeGraphics(direction: string|Direction) {
        this.spriteGraphics.elapsed = 0;
        this.spriteGraphics.frameIndex = 0;
        switch (direction) {
        case "NORTH":
        case Direction.NORTH:
            this.direction = Direction.NORTH;
            this.spriteGraphics.active = this.spriteGraphics.north;
            break;
        case "SOUTH":
        case Direction.SOUTH:
            this.direction = Direction.SOUTH;
            this.spriteGraphics.active = this.spriteGraphics.south;
            break;
        case "EAST":
        case Direction.EAST:
            this.direction = Direction.EAST;
            this.spriteGraphics.active = this.spriteGraphics.east;
            break;
        case "WEST":
        case Direction.WEST:
            this.direction = Direction.WEST;
            this.spriteGraphics.active = this.spriteGraphics.west;
            break;
        case "NORTH_EAST":
        case Direction.NORTH_EAST:
            this.direction = Direction.NORTH_EAST;
            this.spriteGraphics.active = this.spriteGraphics.northEast;
            break;
        case "NORTH_WEST":
        case Direction.NORTH_WEST:
            this.direction = Direction.NORTH_WEST;
            this.spriteGraphics.active = this.spriteGraphics.northWest;
            break;
        case "SOUTH_EAST":
        case Direction.SOUTH_EAST:
            this.direction = Direction.SOUTH_EAST;
            this.spriteGraphics.active = this.spriteGraphics.southEast;
            break;
        case "SOUTH_WEST":
        case Direction.SOUTH_WEST:
            this.direction = Direction.SOUTH_WEST;
            this.spriteGraphics.active = this.spriteGraphics.southWest;
            break;
        case "ATTACK":
        case Direction.ATTACK:
            this.spriteGraphics.active = this.spriteGraphics.attack;
            break;
        case "DEFEND":
        case Direction.DEFEND:
            this.spriteGraphics.active = this.spriteGraphics.defend;
            break;
        case "DIE":
        case Direction.DIE:
            this.spriteGraphics.active = this.spriteGraphics.die;
            break;
        default:
            this.spriteGraphics.active = this.spriteGraphics.custom[direction];
        }
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
                animation = await Factory.build(Core.PATH_ANIMATION + file) as Animation;
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

    public prepareActiveAnimation() {
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

    // Collision functions
    public async hitOnCollider(hitData: any, entity: any) {
        for (const hit of hitData) {
            if (entity.sprite) {
                const sprite: Sprite = entity.sprite;
                if (this.onSameLayer(hit, sprite.layer) && this.canCollide(hit, sprite.collider.enabled)) {
                    await this.processCollider(hit, entity);
                }
            } else {
                // TODO: Handle hitting regular colliders on different layers
                await this.processCollider(hit, entity);
            }
        }
    }

    public hitOffCollider(hitData: any, entity: any) {
        entity.resetHitChecks();
    }

    // REFACTOR: revisit this
    private async processCollider(hit: any, entity: any) {
        console.log(this.name);

        if (hit.obj.collider) {
            entity.cancelTween({ x: true, y: true });
            entity.x -= hit.overlap * hit.normal.x;
            entity.y -= hit.overlap * hit.normal.y;
        } else if (hit.obj.sprite) {
            // REFACTOR: Fix me
            if (hit.obj.sprite.name === "Hero") {
                return;
            }

            entity.cancelTween({ x: true, y: true });
            entity.x -= hit.overlap * hit.normal.x;
            entity.y -= hit.overlap * hit.normal.y;
        }

        entity.resetHitChecks();
    }

    // Trigger functions
    public async hitOnTrigger(hitData: any, entity: any) {
        for (const hit of hitData) {
            if (entity.sprite) {
                const sprite: Sprite = entity.sprite;
                if (this.onSameLayer(hit, sprite.layer) && this.canTrigger(hit, sprite.trigger.enabled)) {
                    // TODO: Fix this!
                    if (hit.obj.wizTrigger) {
                        await this.processTrigger(sprite, hit.obj.wizTrigger);
                    } else {
                        await this.processTrigger(hit.obj.sprite, sprite);
                    }
                }
            }
        }
    }

    public async hitOffTrigger(hitData: any, entity: any) {
        if (Core.getInstance().eventKeydownHandler) {
            Core.getInstance().keyDownHandlers[Framework.getKey(Core.getInstance().eventKeydownHandler.key)] = Core.getInstance().eventKeydownHandler.handler;
            Core.getInstance().eventKeydownHandler = null;
        }
    }

    private async processTrigger(source: any, target: any) {
        const event: Event = target.trigger ? target.trigger.events[0] : target.events[0]; // TODO: Fix this!
        if (!event || !event.script) {
            return;
        }

        if (event.type === EventType.OVERLAP) {
            try {
                const scriptEvent: ScriptEvent = new ScriptEvent(EventType.FUNCTION, source, target);
                await Core.getInstance().scriptVM.run("../../../game/scripts/" + event.script, scriptEvent);
            } catch (e) {
                console.error(e);
                throw new Error("Could not run event script!");
            }
        } else if (event.type === EventType.KEYPRESS) {
            return new Promise<void>((resolve: any, reject: any) => {
                Core.getInstance().eventKeydownHandler = { key: event.key, handler: Core.getInstance().keyDownHandlers[Framework.getKey(event.key)] };
                async function callback() {
                    try {
                        const scriptEvent: ScriptEvent = new ScriptEvent(EventType.FUNCTION, source, target);
                        await Core.getInstance().scriptVM.run("../../../game/scripts/" + event.script, scriptEvent);
                        resolve();
                    } catch (e) {
                        console.error(e);
                        reject(new Error("Could not run event script!"));
                    }
                }
                Core.getInstance().keyDownHandlers[Framework.getKey(event.key)] = callback;
            });
        }
    }

    private onSameLayer(hit: any, otherLayer: number): boolean {
        let hitLayer: number = -1;
        if (hit.obj.sprite) {
            hitLayer = hit.obj.sprite.layer;
        } else if (hit.obj.collider) {
            hitLayer = hit.obj.collider.layer;
        } else if (hit.obj.wizTrigger) {
            hitLayer = hit.obj.wizTrigger.layer;
        }
        return hitLayer === otherLayer;
    }

    private canCollide(hit: any, otherEnabled: boolean): boolean {
        let colliderEnabled: boolean = false;
        if (hit.obj.sprite) {
            colliderEnabled = hit.obj.sprite.collider.enabled;
        } else if (hit.obj.collider) {
            colliderEnabled = hit.obj.collider.enabled;
        }
        return colliderEnabled && otherEnabled;
    }

    private canTrigger(hit: any, otherEnabled: boolean): boolean {
        let triggerEnabled: boolean = false;
        if (hit.obj.sprite) {
            triggerEnabled = hit.obj.sprite.trigger.enabled;
        } else if (hit.obj.wizTrigger) {
            triggerEnabled = hit.obj.wizTrigger.enabled;
        }
        return triggerEnabled && otherEnabled;
    }

}
