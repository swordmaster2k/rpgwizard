/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import { MapSprite } from "./asset/dto/asset-subtypes.js";
import { Game } from "./asset/game.js";
import { Map } from "./asset/map.js";
import { Sprite } from "./asset/sprite.js";
import { Core } from "./core.js";
import { Event } from "./rpgcode/rpgcode.js";

// REFACTOR: Find better solution
// https://stackoverflow.com/questions/31173738/typescript-getting-error-ts2304-cannot-find-name-require
declare const Crafty: any;

/**
 * Wrapper around external frameworks such as Crafty.
 *
 * Provides a layer of Abstraction over other frameworks.
 */
export namespace Framework {

    export enum EntityType {
        Collider = "Collider",
        Trigger = "Trigger",
        Map = "Map",
        MapSprite = "MapSprite"
    }

    export interface Assets {

        images: Array<String>;
        audio: any;

    }

    export function bootstrap(game: Game) {
        var scale = 1;
        if (game.viewport.fullScreen) {
            var bodyWidth = engineUtil.getBodyWidth();
            var bodyHeight = engineUtil.getBodyHeight();
            scale = parseFloat((bodyWidth / game.viewport.width).toFixed(2));
            if (game.viewport.height * scale > bodyHeight) {
                scale = parseFloat((bodyHeight / game.viewport.height).toFixed(2));
            }
        }

        var container = document.getElementById("container");
        var width = Math.floor((game.viewport.width * scale));
        var height = Math.floor((game.viewport.height * scale));
        container.style.width = width + "px";
        container.style.height = height + "px";

        Crafty.init(game.viewport.width, game.viewport.height);
        Crafty.viewport.init(width, height);
        Crafty.paths({ audio: PATH_MEDIA, images: PATH_BITMAP });
        Crafty.viewport.scale(scale);

        defineComponent(EntityType.Collider, {});
        defineComponent(EntityType.Trigger, {});
    }

    export function getViewport(): any {
        return Crafty.viewport;
    }

    export function setViewport(x: number, y: number) {
        Crafty.viewport.x = 0;
        Crafty.viewport.y = 0;
    }

    export function getImage(image: string): ImageBitmap {
        return Crafty.assets[Crafty.__paths.images + image];
    }

    export function isAssetLoaded(): boolean {
        return false;
    }

    export function destroyEntities(types: Array<string>) {
        for (const type of types) {
            Crafty(type).destroy();
        }
    }

    export function defineComponent(type: EntityType, data: any): void {
        switch (type) {
        case EntityType.Collider:
            return _defineCollider(type, data);
        case EntityType.Trigger:
            return _defineTrigger(type, data);
        case EntityType.Map:
            return _defineMap(type, data);
        case EntityType.MapSprite:
            return _defineMapSprite(type, data);
        default:
            return null; // REFACTOR: Handle this
        }
    }

    export function createEntity(type: EntityType, data: any): any {
        switch (type) {
        case EntityType.Collider:
            return _createCollider(type, data);
        case EntityType.Trigger:
            return _createTrigger(type, data);
        case EntityType.Map:
            return _createMap(type, data);
        case EntityType.MapSprite:
            return _createMapSprite(type, data);
        default:
            return null; // REFACTOR: Handle this
        }
    }

    export function createPolygon(points: Array<number>) {
        return new Crafty.polygon(points);
    }

    export function createVector2D() {
        // TODO
    }

    export async function loadAssets(assets: Assets) {
        // Remove any duplicates and already loaded images
        assets.images = assets.images.filter((it, i, ar) => ar.indexOf(it) === i);
        const images: Array<string> = [];
        assets.images.forEach(function (image: string) {
            if (!Crafty.assets[Crafty.__paths.images + image]) {
                images.push(image);
            }
        });
        assets.images = images;

        // Remove any duplicates and already loaded audio
        const audio = {};
        for (const property in assets.audio) {
            if (!Crafty.assets[Crafty.__paths.audio + property]) {
                audio[property] = assets.audio[property];
            }
        }
        assets.audio = audio;

        return new Promise<void>((resolve: any, reject: any) => {
            Crafty.load(assets,
                () => {
                    // Assets have been loaded
                    resolve();
                },
                (e: any) => {
                    // TODO: progress
                },
                (e: any) => {
                    // TODO: error
                    reject(e);
                }
            );
        });
    }

    export function removeAssets() {
        // TODO
    }

    export function trigger(event: string, data?: any) {
        if (data) {
            Crafty.trigger(event, data);
        } else {
            Crafty.trigger(event);
        }
    }

    export function raycast() {
        // TODO
    }

    export function playAudio(audio: string, loop: number) {
        Crafty.audio.play(audio, loop);
    }

    export function stopAudio() {
        Crafty.audio.stop();
    }

    // REFACTOR: decouple core?
    export function createUI(core: Core) {
        // Define a UI layer that is completely static and sits above the other layers
        Crafty.createLayer("UI", "Canvas", {
            xResponse: 0, yResponse: 0, scaleResponse: 0, z: 50
        });

        Crafty.e("2D, UI, Mouse")
            .attr({ x: 0, y: 0, w: Crafty.viewport._width, h: Crafty.viewport._height, ready: true })
            .bind("Draw", (e) => {
                if (e.ctx) {
                    core.screen.renderUI(e.ctx);
                }
            })
            .bind("MouseDown", (e) => {
                var handler = core.inProgram ? core.mouseHandler.mouseDownHandler : core.mouseDownHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("MouseUp", (e) => {
                var handler = core.inProgram ? core.mouseHandler.mouseUpHandler : core.mouseUpHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("Click", (e) => {
                var handler = core.inProgram ? core.mouseHandler.mouseClickHandler : core.mouseClickHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("DoubleClick", (e) => {
                var handler = core.inProgram ? this.mouseHandler.mouseDoubleClickHandler : core.mouseDoubleClickHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            })
            .bind("MouseMove", (e) => {
                var handler = core.inProgram ? core.mouseHandler.mouseMoveHandler : core.mouseMoveHandler;
                if (handler && typeof handler === "function") {
                    handler(e);
                }
            });
    }

}

//
// Component
//
function _defineCollider(type: Framework.EntityType, data: any) {
    Crafty.c(type, {
        Collider: function (polygon, hiton, hitoff) {
            this.requires("Collision, BASE");
            this.collision(polygon);
            this.checkHits("SOLID, BASE");
            this.bind("HitOn", hiton);
            this.bind("HitOff", hitoff);
            return this;
        }
    });
}

function _defineTrigger(type: Framework.EntityType, data: any) {
    Crafty.c(type, {
        Trigger: function(polygon, hiton, hitoff) {
            this.requires("Collision, ACTIVATION, Raycastable");
            this.collision(polygon);
            this.checkHits("PASSABLE, ACTIVATION");
            this.bind("HitOn", hiton);
            this.bind("HitOff", hitoff);
            return this;
        }
    });
}

function _defineMap(type: Framework.EntityType, data: any) {
    const width: number = data.width;
    const height: number = data.height;
    const xShift: number = data.xShift;
    const yShift: number = data.yShift;
    const map: Map = data.map;

    Crafty.c(type, {
        ready: true,
        width: width,
        height: height,
        xShift: xShift,
        yShift: yShift,
        init: function () {
            this.addComponent("2D, Canvas");
            this.attr({ x: 0, y: 0, w: width, h: height, z: 0, map: map, show: false });
            this.bind("EnterFrame", function () {
                this.trigger("Invalidate");
            });
            this.bind("Draw", function (e) {
                if (e.ctx) {
                    // REFACTOR: Fix this
                    // Excute the user specified runtime programs first.
                    // rpgcode.runTimePrograms.forEach(async function(filename) {
                    //     var program = await Core.getInstance().openProgram(PATH_PROGRAM + filename);
                    //     program();
                    // });
                    Core.getInstance().screen.renderBoard(e.ctx);
                }
            });
        }
    });
}

function _defineMapSprite(type: Framework.EntityType, data: any) {
    const sprite: Sprite = data.sprite;
    const isEnemy: boolean = data.isEnemy;
    const events: any = data.events;
    const activationVector: any = data.activationVector;
    const entity: any = data.entity;

    Crafty.c(type, {
        ready: true,
        visible: false,
        x: sprite.x,
        y: sprite.y,
        layer: sprite.layer,
        width: 150,
        height: 150,

        sprite: sprite,

        events: events,
        activationVector: activationVector,
        vectorType: isEnemy ? "ENEMY" : "NPC",

        tweenEndCallbacks: [],
        init: function () {
            this.requires(`2D, Canvas, Tween, ${Framework.EntityType.Collider}`);
            // REFACTOR: Need current (x, y) fields, decorate DTO
            this.attr({ x: sprite.x, y: sprite.y, w: 50, h: 50, show: false });
            this.bind("Move", function (from) {
                // REFACTOR: FIX ME
                // Move activation vector with us.
                // this.activationVector.x = entity.x + sprite.trigger.x;
                // this.activationVector.y = entity.y + sprite.trigger.y;

                sprite.animate(this.dt);
            });
            this.bind("EnterFrame", function (event) {
                this.dt = event.dt / 1000;
                if (sprite.thread && sprite.renderReady && Core.getInstance().mapEntity.show) {
                    // REFACTOR: FIX ME
                    // sprite.thread.default(this);
                }
            });
            this.bind("TweenEnd", function (event) {
                if (this.tweenEndCallbacks.length > 0) {
                    var callback = this.tweenEndCallbacks.shift();
                    if (callback) {
                        callback();
                    }
                }
            });
        },
        remove: function () {
            this.activationVector.destroy();
        }
    });
}

//
// Entity
//
function _createCollider(type: Framework.EntityType, data: any): any {
    const attr = {
        x: data.x,
        y: data.y,
        w: data.w,
        h: data.h,
        collider: data.collider
    };
    Crafty.e("SOLID, Collision, Raycastable")
        .attr(attr)
        .collision(data.points);
}

function _createTrigger(type: Framework.EntityType, data: any): any {
    const sprite: Sprite = data.sprite;
    const bounds: any = data.bounds;
    const entity: any = data.entity;

    return Crafty.e(`2D, Canvas, ${type}`)
        .attr({
            // REFACTOR: Need current (x, y) fields, decorate DTO
            x: sprite.x + sprite.trigger.x,
            y: sprite.y + sprite.trigger.y,
            w: bounds.width,
            h: bounds.height,
            sprite: sprite
        })
        .Trigger(
            new Crafty.polygon(sprite.activationPoints),
            function (hitData) {
                sprite.hitOnActivation(hitData, entity);
            },
            function (hitData) {
                sprite.hitOffActivation(hitData, entity);
            }
        );
}

function _createMap(type: Framework.EntityType, data: any): any {
    return Crafty.e(type);
}

function _createMapSprite(type: Framework.EntityType, data: any): any {
    const sprite: Sprite = data.sprite;
    const entity = Crafty.e(type)
        .Collider(
            new Crafty.polygon(sprite.collisionPoints),
            function (hitData) {
                sprite.hitOnCollision(hitData, entity);
            },
            function (hitData) {
                sprite.hitOffCollision(hitData, entity);
            }
        );
    return entity;
}
