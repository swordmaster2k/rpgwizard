/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard */

export function Mouse() {
    this.mouseDownHandler = null;
    this.mouseUpHandler = null;
    this.mouseClickHandler = null;
    this.mouseDoubleClickHandler = null;
    this.mouseMoveHandler = null;
}
