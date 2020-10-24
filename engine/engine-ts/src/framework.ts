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

// REFACTOR: Find better solution
// https://stackoverflow.com/questions/31173738/typescript-getting-error-ts2304-cannot-find-name-require
declare const Crafty: any;

/**
 * Wrapper around external frameworks such as Crafty.
 *
 * Provides a layer of Abstraction over other frameworks.
 */
export namespace Framework {

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

        defineComponent("BaseVector", {});
        defineComponent("ActivationVector", {});
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

    export function defineComponent(type: string, data: any): void {
        switch (type) {
        case "ActivationVector":
            return _defineActivationVector(type, data);
        case "BaseVector":
            return _defineBaseVector(type, data);
        case "Map":
            return _defineMap(type, data);
        case "MapSprite":
            return _defineMapSprite(type, data);
        default:
            return null; // REFACTOR: Handle this
        }
    }

    export function createEntity(type: string, data: any): any {
        switch (type) {
        case "ActivationVector":
            return _createActivationVector(type, data);
        case "BaseVector":
            return _createBaseVector(type, data);
        case "Map":
            return _createMap(type, data);
        case "MapSprite":
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
function _defineBaseVector(type: string, data: any) {
    Crafty.c(type, {
        BaseVector: function (polygon, hiton, hitoff) {
            this.requires("Collision, BASE");
            this.collision(polygon);
            this.checkHits("SOLID, BASE");
            this.bind("HitOn", hiton);
            this.bind("HitOff", hitoff);
            return this;
        }
    });
}

function _defineActivationVector(type: string, data: any) {
    Crafty.c(type, {
        ActivationVector: function(polygon, hiton, hitoff) {
            this.requires("Collision, ACTIVATION, Raycastable");
            this.collision(polygon);
            this.checkHits("PASSABLE, ACTIVATION");
            this.bind("HitOn", hiton);
            this.bind("HitOff", hitoff);
            return this;
        }
    });
}

function _defineMap(type: string, data: any) {
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

function _defineMapSprite(type: string, data: any) {
    const mapSprite: MapSprite = data.mapSprite;
    const asset: Sprite = data.asset;
    const isEnemy: boolean = data.isEnemy;
    const events: any = data.events;
    const activationVector: any = data.activationVector;
    const entity: any = data.entity;

    Crafty.c(type, {
        ready: true,
        visible: false,
        x: mapSprite.startLocation.x,
        y: mapSprite.startLocation.y,
        layer: mapSprite.startLocation.layer,
        width: 150,
        height: 150,
        vectorType: isEnemy ? "ENEMY" : "NPC",
        sprite: mapSprite,
        events: events,
        activationVector: activationVector,
        tweenEndCallbacks: [],
        init: function () {
            this.requires("2D, Canvas, Tween, BaseVector");
            // REFACTOR: Need current (x, y) fields, decorate DTO
            this.attr({ x: mapSprite.startLocation.x, y: mapSprite.startLocation.y, w: 50, h: 50, show: false });
            this.bind("Move", function (from) {
                // Move activation vector with us.
                this.activationVector.x = entity.x + asset.trigger.x;
                this.activationVector.y = entity.y + asset.trigger.y;
                asset.animate(this.dt);
            });
            this.bind("EnterFrame", function (event) {
                this.dt = event.dt / 1000;
                if (mapSprite.thread && asset.renderReady && Core.getInstance().map.show) {
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
function _createActivationVector(type: string, data: any): any {
    const mapSprite: MapSprite = data.mapSprite;
    const asset: Sprite = data.asset;
    const bounds: any = data.bounds;
    const entity: any = data.entity;

    return Crafty.e(`2D, Canvas, ${type}`)
        .attr({
            // REFACTOR: Need current (x, y) fields, decorate DTO
            x: mapSprite.startLocation.x + asset.trigger.x,
            y: mapSprite.startLocation.y + asset.trigger.y,
            w: bounds.width,
            h: bounds.height,
            sprite: mapSprite
        })
        .ActivationVector(
            new Crafty.polygon(asset.activationPoints),
            function (hitData) {
                asset.hitOnActivation(hitData, entity);
            },
            function (hitData) {
                asset.hitOffActivation(hitData, entity);
            }
        );
}

function _createBaseVector(type: string, data: any): any {
    return null;
}

function _createMap(type: string, data: any): any {
    return Crafty.e(type);
}

function _createMapSprite(type: string, data: any): any {
    const sprite: Sprite = data.sprite;
    const entity = Crafty.e(type)
        .BaseVector(
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
