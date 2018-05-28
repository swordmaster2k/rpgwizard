/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/* global rpgcode */

var gui = new GUI();

function GUI() {
    this.borderWidth = 3;
    this.cornerRadius = 15;
    this.colors = {
        background: {r: 255, g: 255, b: 255, a: 1.0},
        border: {r: 255, g: 255, b: 255, a: 1.0},
        text: {r: 255, g: 255, b: 255, a: 1.0},
        statBar: {r: 255, g: 0, b: 0, a: 1.0},
        selection: {r: 255, g: 255, b: 255, a: 0.3}
    };
    this.padding = {
        x: 15,
        y: 35,
        line: 10
    };
}

GUI.prototype.getBackgroundGradient = function () {
    return ["rgba(74, 121, 90, 1)", "rgba(0, 0, 0, 1)"];
};

GUI.prototype.getFontSize = function () {
    return 20;
};

GUI.prototype.getFontFamily = function () {
    return "Lucida Console";
};

GUI.prototype.getFont = function () {
    return this.getFontSize() + "px " + this.getFontFamily();
};

GUI.prototype.setColor = function (color) {
    rpgcode.setColor(color.r, color.g, color.b, color.a);
};

GUI.prototype.setGradient = function (x1, y1, x2, y2, canvasId) {
    var instance = rpgcode.canvases[canvasId];
    var context = instance.canvas.getContext("2d");
    var gradient = context.createLinearGradient(x1, y1, x2, y2);
    var colorStops = gui.getBackgroundGradient();
    for (var i = 0; i < colorStops.length; i++) {
        gradient.addColorStop(i, colorStops[i]);
    }
    rpgcode.gradient = gradient;
};

GUI.prototype.removeGradient = function () {
    rpgcode.gradient = null;
};

GUI.prototype.createFrame = function (config) {
    var frame = (function () {
        var _visible = false;
        var _menu = null;
        var _image = null;

        var id = config.id;
        var width = config.width;
        var height = config.height;
        var x = config.x;
        var y = config.y;

        var init = function () {
            // Create the canvas element.
            rpgcode.createCanvas(width, height, id);
            rpgcode.setCanvasPosition(x, y, id);
        };

        var destroy = function () {
            if (_menu) {
                _menu.destroy();
            }
            rpgcode.clearCanvas(id);
            rpgcode.destroyCanvas(id);
        };

        var setVisible = function (visible) {
            if (_visible !== visible) {
                draw();
                rpgcode.canvases[id].render = _visible = visible;
                Crafty.trigger("Invalidate");
            }
        };
        
        var setImage = function (image) {
            _image = image;
            draw();
        };

        var getMenu = function () {
            return _menu;
        };

        var setMenu = function (menu) {
            _menu = (function () {
                var _selectedIndex = 0;
                var items = menu.items;
                var _flashMenuSelection = true;
                var _flashInterval = setInterval(function () {
                    _flashMenuSelection = !_flashMenuSelection;
                    draw();
                }.bind(this), 500);
                
                var destroy = function() {
                    clearInterval(_flashInterval);
                };

                var getSelectedIndex = function () {
                    return _selectedIndex;
                };

                var setSelectedIndex = function (index) {
                    _selectedIndex = index;
                };
                
                var isFlashing = function() {
                    return _flashMenuSelection;
                };

                var up = function () {
                    _selectedIndex = _selectedIndex > 0 ? _selectedIndex - 1 : _selectedIndex;
                    draw();
                };

                var down = function () {
                    _selectedIndex = _selectedIndex < items.length - 1 ? _selectedIndex + 1 : _selectedIndex;
                    draw();
                };

                var executeSelectedItem = function () {
                    items[_selectedIndex].execute();
                };

                return {
                    items: items,
                    destroy: destroy,
                    getSelectedIndex: getSelectedIndex,
                    setSelectedIndex: setSelectedIndex,
                    isFlashing: isFlashing,
                    up: up,
                    down: down,
                    executeSelectedItem: executeSelectedItem
                };
            })();
            draw();
        };

        var draw = function () {
            const border = gui.borderWidth > 0 ? gui.borderWidth : 0;
            const radius = gui.cornerRadius > 0 ? gui.cornerRadius : 0;
            gui.setGradient(width / 2, 0, width / 2, height * 1.75, id);
            rpgcode.fillRoundedRect(border, border, width - (border * 2), height - (border * 2), radius, id);
            gui.removeGradient();
            gui.setColor(gui.colors.border);
            rpgcode.drawRoundedRect(border, border, width - (border * 2), height - (border * 2), border, radius, id);

            if (_image) {
                _drawImage();
            }
            if (_menu && _menu.items) {
                if (_menu.isFlashing()) {
                    _drawMenuSelection();
                }
                for (var i = 0; i < _menu.items.length; i++) {
                    _drawMenuItem(_menu.items[i], i);
                }
            }
            rpgcode.renderNow(id);
        };

        var _drawImage = function () {
            rpgcode.setImage(_image, gui.borderWidth * 3, gui.borderWidth * 3, width - (gui.borderWidth * 6), height - (gui.borderWidth * 6), id);
        };

        var _drawMenuItem = function (item, index) {
            rpgcode.font = gui.getFont();
            var x = gui.padding.x;
            var y = gui.padding.y + ((gui.getFontSize() + gui.padding.line) * index);
            gui.prepareTextColor();
            rpgcode.drawText(x, y, item.text, id);
        };

        var _drawMenuSelection = function () {
            gui.prepareSelectionColor();
            var x = gui.padding.x - (gui.padding.x / 2);
            var y = gui.padding.y + ((gui.getFontSize() + gui.padding.line) * (_menu.getSelectedIndex())) - gui.getFontSize() - (gui.padding.line / 4);
            var _width = width - gui.padding.x;
            var _height = gui.getFontSize() + (gui.padding.line);
            rpgcode.fillRect(x, y, _width, _height, id);
        };

        return {
            id: id,
            width: width,
            height: height,
            x: x,
            y: y,
            init: init,
            destroy: destroy,
            draw: draw,
            setVisible: setVisible,
            setImage: setImage,
            getMenu: getMenu,
            setMenu: setMenu
        };
    })();
    frame.init();
    return frame;
};

GUI.prototype.prepareTextColor = function () {
    rpgcode.setColor(this.colors.text.r, this.colors.text.g, this.colors.text.b, this.colors.text.a);
};

GUI.prototype.prepareStatBarColor = function () {
    rpgcode.setColor(this.colors.statBar.r, this.colors.statBar.g, this.colors.statBar.b, this.colors.statBar.a);
};

GUI.prototype.prepareSelectionColor = function () {
    rpgcode.setColor(this.colors.selection.r, this.colors.selection.g, this.colors.selection.b, this.colors.selection.a);
};