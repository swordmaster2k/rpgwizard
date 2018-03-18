/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode */

var dialog = new Dialog();

function Dialog() {
    this._setup = false;
    this._defaults = {
        background: "defaults/dialog/dialog_with_profile.png",
        nextMarker: "defaults/dialog/next_marker.png",
        profile: "defaults/dialog/profile.png",
        font: {
            name: "Arial"
        },
        soundEffect: {
            name: "",
            file: ""
        }
    };
}

Dialog.prototype._defaultSetup = function (callback) {
    const assets = {
        images: [
            this._defaults.profile,
            this._defaults.background,
            this._defaults.nextMarker
        ]
    };
    rpgcode.loadAssets(assets, function () {
        dialog.setup(
                {
                    position: "BOTTOM",
                    profile: {
                        image: dialog._defaults.profile,
                        padding: {
                            x: 12,
                            y: 12
                        }
                    },
                    textArea: {
                        maxLines: 3,
                        linePadding: 10,
                        padding: {
                            x: 0,
                            y: 0
                        },
                        font: {
                            size: 14,
                            family: dialog._defaults.font.name
                        },
                        soundEffect: dialog._defaults.soundEffect.name,
                        image: dialog._defaults.background,
                        nextMarkerImage: dialog._defaults.nextMarker
                    }
                }

        );
        callback();
    });
};

Dialog.prototype.setup = function (config) {
    var scale = rpgcode.getScale();

    this._setup = true;
    this._windowCanvas = "dialog.windowCanvas";
    this._nextMarkerCanvas = "dialog.nextMarkerCanvas";
    this._nextMarkerVisible = false;
    this._currentLines = 1;
    this._blink = false;
    this.width = 350;
    this.height = 120;
    this._textAreaWidth = this.width;
    this._textAreaHeight = this.height;
    this._defaultX = 22;
    this._defaultY = 18;
    this._profileWidth = 0;
    this._profileHeight = 0;

    if (config.profile && config.textArea) {
        this._defaultX = 132;
        this._defaultY = 14;
        var profileImage = rpgcode.getImage(config.profile.image);
        this.profile = {
            image: config.profile.image,
            width: profileImage.width * scale,
            height: profileImage.height * scale,
            padding: {
                x: config.profile.padding.x * scale,
                y: config.profile.padding.y * scale
            }
        };
        this._profileWidth = 85;
        this._profileHeight = 85;
    }

    if (config.textArea) {
        this.maxLines = config.textArea.maxLines;
        this.padding = {
            x: config.textArea.padding.x * scale,
            y: config.textArea.padding.y * scale
        };
        this.linePadding = config.textArea.linePadding * scale;
        this.font = (config.textArea.font.size * scale) + "px " + config.textArea.font.family;
        this.soundEffect = config.textArea.soundEffect;
        this.backgroundImage = config.textArea.image;
        this.nextMarkerImage = config.textArea.nextMarkerImage;

        var textAreaImage = rpgcode.getImage(config.textArea.image);
        if (textAreaImage) {
            this.width = textAreaImage.width > 0 ? textAreaImage.width : this.width;
            this.height = textAreaImage.height > 0 ? textAreaImage.height : this.height;
            this._textAreaWidth = 275;
            this._textAreaHeight = 79;
        }
    }

    this.x = Math.floor((rpgcode.getViewport().width) / 2) - Math.floor(this.width / 2);
    switch (config.position ? config.position : "BOTTOM") {
        case "TOP":
            this.y = 0;
            break;
        case "CENTER":
            this.y = Math.floor((rpgcode.getViewport().height) / 2) - Math.floor(this.height / 2);
            break;
        case "BOTTOM":
        default:
            this.y = Math.floor(rpgcode.getViewport().height - this.height);
    }

    rpgcode.createCanvas(this.width, this.height, this._windowCanvas);
    rpgcode.setCanvasPosition(this.x, this.y, this._windowCanvas);

    var image = rpgcode.getImage(this.nextMarkerImage);
    if (image && image.width > 0 && image.height > 0) {
        var width = image.width * scale;
        var height = image.height * scale;
        var x = this.x + (this.width - (width + width / 4));
        var y = this.y + (this.height - (height + height / 4));
        rpgcode.createCanvas(width, height, this._nextMarkerCanvas);
        rpgcode.setCanvasPosition(x, y, this._nextMarkerCanvas);
    }

    this.color = {
        "r": 255,
        "g": 255,
        "b": 255,
        "a": 1.0
    };
    this.backgroundColor = {
        "r": 35,
        "g": 35,
        "b": 35,
        "a": this.backgroundImage ? 0 : 1.0
    };

    this.characterDelay = 25;
    this.markerBlinkDelay = 500;
    this.advancementKey = "E";
    this.timeout = 2000;
    this._cursorX = this._defaultX;
    this._cursorY = this._defaultY;
};

Dialog.prototype._reset = function (lineHeight) {
    this._cursorX = this._defaultX + this.padding.x;
    this._cursorY = this._defaultY + this.padding.y + lineHeight;
    this._currentLines = 1;
    this._draw(this._defaultX, this._defaultY, this.width, this.height);
};

Dialog.prototype._sortLines = function (text) {
    rpgcode.font = this.font;
    var words = text.split(" ");
    var lines = [];
    var line = words[0];

    for (var i = 1; i < words.length; i++) {
        var newLine = line + " " + words[i];
        if (rpgcode.measureText(newLine).width < this._textAreaWidth - this.padding.x) {
            line = newLine;
        } else {
            lines.push(line);
            line = words[i];
        }
    }
    lines.push(line);

    return lines;
};

Dialog.prototype._printCharacters = function (characters, callback) {
    rpgcode.font = this.font;
    var character = characters.shift();
    rpgcode.drawText(dialog._cursorX, dialog._cursorY, character, this._windowCanvas);
    rpgcode.renderNow(this._windowCanvas);
    dialog._cursorX += rpgcode.measureText(character).width;

    if (characters.length) {
        rpgcode.delay(dialog.characterDelay, function () {
            dialog._printCharacters(characters, callback);
        });
    } else {
        callback();
    }
};

Dialog.prototype._printLines = function (lines, callback) {
    rpgcode.font = this.font;
    rpgcode.setColor(this.color.r, this.color.g, this.color.b, this.color.a);

    var line = lines.shift();
    this._printCharacters(line.split(""), function () {
        if (lines.length) {
            dialog._currentLines++;
            if (dialog._currentLines > dialog.maxLines) {
                dialog._stopTypingSound();
                dialog._blink = true;
                dialog._blinkNextMarker();

                // Decide whether or not to use keypress or timeout.
                if (dialog.advancementKey) {
                    rpgcode.registerKeyDown(dialog.advancementKey, function () {
                        dialog._advance(lines, callback);
                    }, false);
                } else {
                    rpgcode.delay(dialog.timeout, function () {
                        dialog._advance(lines, callback);
                    });
                }
            } else {
                dialog._cursorX = dialog._defaultX + dialog.padding.x;
                dialog._cursorY += rpgcode.measureText(line).height + dialog.linePadding;
                dialog._printLines(lines, callback);
            }
        } else {
            dialog._stopTypingSound();

            // Decide whether or not to use keypress or timeout.
            if (dialog.advancementKey) {
                dialog._blink = true;
                dialog._blinkNextMarker();
                rpgcode.registerKeyDown(dialog.advancementKey, function () {
                    dialog._hide(callback);
                }, false);
            } else {
                rpgcode.delay(dialog.timeout, function () {
                    dialog._hide(callback);
                });
            }
        }
    });
};

Dialog.prototype._blinkNextMarker = function () {
    if (this.nextMarkerImage && this._blink) {
        if (!this._nextMarkerVisible) {
            this._drawNextMarker();
        } else {
            this._clearNextMarker();
        }
        rpgcode.delay(this.markerBlinkDelay, this._blinkNextMarker.bind(this));
    }
};

Dialog.prototype._clearNextMarker = function () {
    if (this.nextMarkerImage) {
        rpgcode.clearCanvas(this._nextMarkerCanvas);
        this._nextMarkerVisible = false;
    }
};

Dialog.prototype._drawNextMarker = function () {
    var image = rpgcode.getImage(this.nextMarkerImage);
    if (image && image.width > 0 && image.height > 0) {
        var scale = rpgcode.getScale();
        var width = image.width * scale;
        var height = image.height * scale;
        rpgcode.setImage(this.nextMarkerImage, 0, 0, width, height, this._nextMarkerCanvas);
        rpgcode.renderNow(this._nextMarkerCanvas);
        this._nextMarkerVisible = true;
    }
};

Dialog.prototype._draw = function (x, y, width, height) {
    rpgcode.setColor(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
    rpgcode.clearCanvas(this._windowCanvas);
    rpgcode.fillRect(x, y, width, height, this._windowCanvas);

    if (this.backgroundImage) {
        rpgcode.setImage(this.backgroundImage, 0, 0, width, height, this._windowCanvas);
    }
    if (this.profile) {
        rpgcode.setImage(this.profile.image, this.profile.padding.x, this.profile.padding.y, this._profileWidth, this._profileHeight, this._windowCanvas);
    }

    rpgcode.renderNow(this._windowCanvas);
};

Dialog.prototype._stopTypingSound = function () {
    if (this.soundEffect) {
        rpgcode.stopSound(this.soundEffect);
    }
};

Dialog.prototype._playTypingSound = function () {
    if (this.soundEffect) {
        rpgcode.playSound(this.soundEffect, true);
    }
};

Dialog.prototype._advance = function (lines, callback) {
    rpgcode.font = this.font;
    dialog._blink = false;
    dialog._clearNextMarker();
    rpgcode.unregisterKeyDown(dialog.advancementKey);
    dialog._reset(rpgcode.measureText(lines[0]).height);
    dialog._printLines(lines, callback);
    dialog._playTypingSound();
};

Dialog.prototype._hide = function (callback) {
    dialog._blink = false;
    dialog._clearNextMarker();
    rpgcode.unregisterKeyDown(dialog.advancementKey);
    rpgcode.clearCanvas(dialog._nextMarkerCanvas);
    rpgcode.clearCanvas(dialog._windowCanvas);
    callback();
};

Dialog.prototype.show = function (config, callback) {
    if (!this._setup) {
        this._defaultSetup(function () {
            dialog.show(config, callback);
        });
        return;
    }
    if (config.profile) {
        const assets = {images: [config.profile]};
        rpgcode.loadAssets(assets, function () {
            dialog.profile.image = config.profile ? config.profile : this.profile.image;
            dialog.show({text: config.text}, callback);
        });
        return;
    }

    var lines = this._sortLines(config.text);
    if (lines.length > 0) {
        this._reset(rpgcode.measureText(lines[0]).height);
        this._printLines(lines, callback);
        this._playTypingSound();
    } else {
        callback();
    }
};
