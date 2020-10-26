/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

export namespace EngineUtil {

    export function timestamp() {
        return window.performance && window.performance.now ? window.performance.now() : new Date().getTime();
    }

    export function hideProgress() {
        document.getElementById("progress").style.visibility = "hidden";
    }

    export function showProgress(percentage: number) {
        document.getElementById("bar").style.width = percentage + "%";
        document.getElementById("progress").style.visibility = "visible";
    }

    // TODO: Make this a utility function. When there is a Craftyjs compiler
    export function prependPath(prepend: string, items: string[]) {
        const len = items.length;
        for (let i = 0; i < len; i++) {
            items[i] = prepend.concat(items[i]);
        }
    }

    export function getBodyWidth() {
        return Math.max(
            document.documentElement.clientWidth,
            document.body.scrollWidth,
            document.documentElement.scrollWidth,
            document.body.offsetWidth,
            document.documentElement.offsetWidth
        );
    }

    export function getBodyHeight() {
        return Math.max(
            document.documentElement.clientHeight,
            document.body.scrollHeight,
            document.documentElement.scrollHeight,
            document.body.offsetHeight,
            document.documentElement.offsetHeight
        );
    }

    export function getPolygonBounds(points: Array<number>) {
        let minX: number = points[0];
        let maxX: number = minX;
        let minY: number = points[1];
        let maxY: number = minY;
        let currentX: number;
        let currentY: number;
        const len: number = points.length;
        for (let i = 2; i < len; i += 2) {
            currentX = points[i];
            currentY = points[i + 1];
            if (currentX < minX) {
                minX = currentX;
            } else if (currentX > maxX) {
                maxX = currentX;
            }
            if (currentY < minY) {
                minY = currentY;
            } else if (currentY > maxY) {
                maxY = currentY;
            }
        }
        for (let i = 0; i < len; i += 2) {
            points[i] -= minX;
            points[i + 1] -= minY;
        }
        return {
            x: minX,
            y: minY,
            width: Math.abs(maxX - minX) + 1,
            height: Math.abs(maxY - minY) + 1
        };
    }

    export function calculateVectorPosition(x1: number, y1: number, x2: number, y2: number) {
        let width: number;
        let height: number;
        const xDiff = x2 - x1;
        const yDiff = y2 - y1;
        const distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        if (x1 !== x2) {
            width = distance;
            height = 2;
            if (xDiff < 0) {
                x1 = x2;
            }
        } else {
            width = 2;
            height = distance;
            if (yDiff < 0) {
                y1 = y2;
            }
        }

        return { x: x1, y: y1, w: width, h: height };
    }

}
