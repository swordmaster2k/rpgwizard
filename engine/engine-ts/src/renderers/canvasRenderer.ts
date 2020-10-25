/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard */

import { Core } from "../core";

export class CanvasRenderer {

    private _renderNowCanvas: HTMLCanvasElement;

    constructor() {
        this._renderNowCanvas = document.createElement("canvas");
        this._renderNowCanvas.width = Crafty.viewport._width;
        this._renderNowCanvas.height = Crafty.viewport._height;
    }

    public render(ctx: CanvasRenderingContext2D) {
        const x = -Crafty.viewport._x;
        const y = -Crafty.viewport._y;

        const canvases = Core.getInstance().rpgcodeApi.canvases;
        for (const property in canvases) {
            if (canvases.hasOwnProperty(property)) {
                const element = canvases[property];
                if (element.render) {
                    ctx.drawImage(element.canvas, x, y);
                }
            }
        }
    }

}
