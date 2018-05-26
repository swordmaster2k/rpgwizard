/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode, gui */

var titleScreen = new TitleScreen();

function TitleScreen() {
    this.context = {
        MENU: "MENU"
    };
}

//
// General Functions
//

TitleScreen.prototype.show = function (config, callback) {
    this._loadAssets(config, function () {
        this._callback = callback;
        this._setup(config);
        rpgcode.playSound("titleScreen.music", true, 1.0);
    }.bind(this));
};

TitleScreen.prototype._end = function (config, callback) {
    rpgcode.stopSound("titleScreen.music");
    this.frame.destroy();
    rpgcode.clearCanvas(this.background.canvasId);
    rpgcode.destroyCanvas(this.background.canvasId);
};

TitleScreen.prototype._setup = function (config) {
    this._state = {
        processingInput: false,
        currentContext: this.context.MENU
    };
    this.background = {
        canvasId: "TitleScreen.backgroundCanvas",
        width: rpgcode.getViewport().width, height: rpgcode.getViewport().height,
        x: 0, y: 0
    };
    rpgcode.createCanvas(this.background.width, this.background.height, this.background.canvasId);
    rpgcode.setCanvasPosition(this.background.x, this.background.y, this.background.canvasId);
    rpgcode.setImage(config.backgroundImage, 0, 0, this.background.width, this.background.height, this.background.canvasId);
    rpgcode.renderNow(this.background.canvasId);

    this.frame = gui.createFrame({
        id: "TitleScreen.frameCanvas",
        width: 150,
        height: 54,
        x: (rpgcode.getViewport().width / 2) - 75,
        y: (rpgcode.getViewport().height / 1.5) - 27
    });
    this.frame.setMenu({
        items: [
            {id: "new-game", text: "New Game", execute: this._newGame.bind(this)}
        ]
    });
    this.frame.setVisible(true);

    rpgcode.registerKeyDown("ENTER", this._handleInput.bind(this), false);
    rpgcode.registerKeyDown("UP_ARROW", this._handleInput.bind(this), false);
    rpgcode.registerKeyDown("DOWN_ARROW", this._handleInput.bind(this), false);
    rpgcode.registerKeyDown("W", this._handleInput.bind(this), false);
    rpgcode.registerKeyDown("S", this._handleInput.bind(this), false);
};

TitleScreen.prototype._loadAssets = function (config, callback) {
    var assets = {
        "images": [config.backgroundImage],
        "audio": {"titleScreen.music": config.titleScreenMusic}
    };
    rpgcode.loadAssets(assets, callback.bind(this));
};

TitleScreen.prototype._newGame = function () {
    this._end();
    this._callback();
};

//
// Input Functions
//

TitleScreen.prototype._handleInput = function (e) {
    if (this._state.processingInput) {
        return;
    }
    this._state.processingInput = true;
    switch (e.key) {
        case 13:
            this._handleEnter();
            break;
        case 38: // UP_ARROW
        case 87: // W
            this._handleUpArrow();
            break;
        case 40: // DOWN_ARROW
        case 83: // S
            this._handleDownArrow();
            break;
        default:
            return;
    }
};

TitleScreen.prototype._handleEnter = function () {
    if (this._state.currentContext === this.context.MENU) {
        this.frame.getMenu().executeSelectedItem();
    }
};

TitleScreen.prototype._handleUpArrow = function () {
    if (this._state.currentContext === this.context.MENU) {
        this.frame.getMenu().up();
    }
    this._state.processingInput = false;
};

TitleScreen.prototype._handleDownArrow = function () {
    if (this._state.currentContext === this.context.MENU) {
        this.frame.getMenu().down();
    }
    this._state.processingInput = false;
};