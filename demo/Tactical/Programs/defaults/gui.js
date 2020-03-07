/* global rpgcode */
var gui = new GUI();

function GUI() {
   this.borderWidth = 3;
   this.cornerRadius = 15;
   this.colors = {
      background: {
         r: 255,
         g: 255,
         b: 255,
         a: 1.0
      },
      border: {
         r: 255,
         g: 255,
         b: 255,
         a: 1.0
      },
      text: {
         r: 255,
         g: 255,
         b: 255,
         a: 1.0
      },
      statBar: {
         r: 255,
         g: 0,
         b: 0,
         a: 1.0
      },
      selection: {
         r: 255,
         g: 255,
         b: 255,
         a: 0.2
      },
      grid: {
         cell: {
            r: 37,
            g: 61,
            b: 45,
            a: 1.0
         },
         border: {
            r: 255,
            g: 255,
            b: 255,
            a: 1.0
         }
      }
   };
   this.padding = {
      x: 15,
      y: 35,
      line: 10
   };
   this.fontSize = 20;
   this.fontFamily = "Lucida Console";

   this.frames = {};
   this.buttons = {};
}

GUI.prototype.getBackgroundGradient = function() {
   return ["rgba(74, 121, 90, 1)", "rgba(0, 0, 0, 1)"];
};

GUI.prototype.getFontSize = function() {
   return this.fontSize;
};

GUI.prototype.setFontSize = function(size) {
   this.fontSize = size;
};

GUI.prototype.getFontFamily = function(family) {
   return this.fontFamily;
};

GUI.prototype.setFontFamily = function(family) {
   this.fontFamily = family;
};

GUI.prototype.getFont = function() {
   return this.getFontSize() + "px " + this.getFontFamily();
};

GUI.prototype.setColor = function(color) {
   rpgcode.setColor(color.r, color.g, color.b, color.a);
};

GUI.prototype.setGradient = function(x1, y1, x2, y2, canvasId) {
   var instance = rpgcode.canvases[canvasId];
   var context = instance.canvas.getContext("2d");
   var gradient = context.createLinearGradient(x1, y1, x2, y2);
   var colorStops = gui.getBackgroundGradient();
   for (var i = 0; i < colorStops.length; i++) {
      gradient.addColorStop(i, colorStops[i]);
   }
   rpgcode.gradient = gradient;
};

GUI.prototype.removeGradient = function() {
   rpgcode.gradient = null;
};

GUI.prototype.scale = function(value) {
   return value * rpgcode.getScale();
};

GUI.prototype.descale = function(value) {
   return Math.round(value / rpgcode.getScale());
};

GUI.prototype.createFrame = function(config) {
   var frame = (function() {
      var _visible = false;
      var _menu = null;
      var _image = null;
      var _grid = null;

      var id = config.id;
      var width = config.width;
      var height = config.height;
      var x = config.x;
      var y = config.y;

      var init = function() {
         // Create the canvas element.
         rpgcode.createCanvas(width, height, id);
         rpgcode.setCanvasPosition(x, y, id);
      };

      var destroy = function() {
         if (_menu) {
            _menu.destroy();
         }
         if (_grid) {
            _grid.destroy();
         }
         rpgcode.clearCanvas(id);
         rpgcode.destroyCanvas(id);
         delete gui.frames[id];
      };

      var setVisible = function(visible) {
         _visible = visible;
         if (_visible) {
            draw();
         } else {
            rpgcode.clearCanvas(id);
         }
      };

      var setLocation = function(newX, newY) {
         this.x = newX;
         this.y = newY;
         rpgcode.setCanvasPosition(newX, newY, id);
         if (_visible) {
            rpgcode.renderNow(id);
         }
      };

      var setImage = function(image) {
         _image = image;
         draw();
      };

      var getMenu = function() {
         return _menu;
      };

      var setMenu = function(menu) {
         _menu = (function() {
            var _selectedIndex = 0;
            var items = menu.items;
            var _flashMenuSelection = true;
            var _flashInterval = setInterval(function() {
               _flashMenuSelection = !_flashMenuSelection;
               draw();
            }.bind(this), 500);

            var destroy = function() {
               clearInterval(_flashInterval);
            };

            var getSelectedIndex = function() {
               return _selectedIndex;
            };

            var setSelectedIndex = function(index) {
               _selectedIndex = index;
            };

            var isFlashing = function() {
               return _flashMenuSelection;
            };

            var up = function() {
               _selectedIndex = _selectedIndex > 0 ? _selectedIndex - 1 : _selectedIndex;
               draw();
            };

            var down = function() {
               _selectedIndex = _selectedIndex < items.length - 1 ? _selectedIndex + 1 : _selectedIndex;
               draw();
            };

            var executeSelectedItem = function() {
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

      var getGrid = function() {
         return _grid;
      };

      var setGrid = function(grid) {
         _grid = (function() {
            var x = grid.x;
            var y = grid.y;
            var rows = grid.rows;
            var columns = grid.columns;
            var cellWidth = grid.cellWidth;
            var cellHeight = grid.cellHeight;
            var cellLineWidth = grid.cellLineWidth;

            var _selectedIndex = 0;
            var items = grid.items;
            var _flashMenuSelection = true;
            var _flashInterval = setInterval(function() {
               _flashMenuSelection = !_flashMenuSelection;
               draw();
            }.bind(this), 500);

            var destroy = function() {
               clearInterval(_flashInterval);
            };

            var getSelectedIndex = function() {
               return _selectedIndex;
            };

            var setSelectedIndex = function(index) {
               _selectedIndex = index;
            };

            var isFlashing = function() {
               return _flashMenuSelection;
            };

            var up = function() {
               _selectedIndex = Math.max(_selectedIndex - columns, 0);
               draw();
            };

            var down = function() {
               _selectedIndex = Math.min(_selectedIndex + columns, (columns * rows) - 1);
               draw();
            };

            var left = function() {
               _selectedIndex = _selectedIndex > 0 ? _selectedIndex - 1 : _selectedIndex;
               draw();
            };

            var right = function() {
               _selectedIndex = _selectedIndex < (columns * rows) - 1 ? _selectedIndex + 1 : _selectedIndex;
               draw();
            };

            var executeSelectedItem = function() {
               if (items[_selectedIndex]) {
                  items[_selectedIndex].execute();
               }
            };

            var removeSelectedItem = function() {
               if (items[_selectedIndex]) {
                  if (1 < items[_selectedIndex].count) {
                     items[_selectedIndex].count--;
                  } else {
                     items.splice(_selectedIndex, 1);
                  }
                  draw();
               }
            };

            return {
               x: x,
               y: y,
               rows: rows,
               columns: columns,
               cellWidth: cellWidth,
               cellHeight: cellHeight,
               cellLineWidth: cellLineWidth,
               items: items,
               destroy: destroy,
               getSelectedIndex: getSelectedIndex,
               setSelectedIndex: setSelectedIndex,
               isFlashing: isFlashing,
               up: up,
               down: down,
               left: left,
               right: right,
               executeSelectedItem: executeSelectedItem,
               removeSelectedItem: removeSelectedItem
            };
         })();
         draw();
      };

      var draw = function() {
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
         if (_grid && (0 < _grid.rows && 0 < _grid.columns)) {
            for (var row = 0; row < _grid.rows; row++) {
               for (var column = 0; column < _grid.columns; column++) {
                  var cell = {
                     index: column + _grid.columns * row,
                     x: _grid.x + (_grid.cellWidth * column),
                     y: _grid.y + (_grid.cellHeight * row),
                     width: _grid.cellWidth,
                     height: _grid.cellHeight,
                     lineWidth: _grid.cellLineWidth
                  };
                  _drawGridCell(cell);
               }
            }
            if (_grid.isFlashing()) {
               _drawGridSelection();
            }
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

      var _drawImage = function() {
         rpgcode.setImage(_image, gui.borderWidth * 3, gui.borderWidth * 3, width - (gui.borderWidth * 6), height - (gui.borderWidth * 6), id);
      };

      var _drawMenuItem = function(item, index) {
         rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
         var x = gui.padding.x;
         var y = gui.padding.y + ((gui.getFontSize() + gui.padding.line) * index);
         gui.prepareTextColor();
         rpgcode.drawText(x, y, item.text, id);
      };

      var _drawMenuSelection = function() {
         gui.prepareSelectionColor();
         var x = gui.padding.x - (gui.padding.x / 2);
         var y = gui.padding.y + ((gui.getFontSize() + gui.padding.line) * (_menu.getSelectedIndex())) - gui.getFontSize() - (gui.padding.line / 4);
         var _width = width - gui.padding.x;
         var _height = gui.getFontSize() + (gui.padding.line);
         rpgcode.fillRect(x, y, _width, _height, id);
      };

      var _drawGridCell = function(cell) {
         gui.prepareGridCellColor();
         rpgcode.fillRect(cell.x, cell.y, cell.width, cell.height, id);
         var index = Math.floor(cell.x + _grid.columns * cell.y);
         if (cell.index < _grid.items.length) {
            var item = _grid.items[cell.index];
            if (item && item.image) {
               rpgcode.setImage(item.image, cell.x, cell.y, cell.width, cell.height, id);
               if (1 < item.count) {
                  var textPadding = 3;
                  var dimensions = rpgcode.measureText(item.count);
                  var textX = cell.x + (cell.width - dimensions.width - textPadding);
                  var textY = cell.y;
                  rpgcode.fillRect(textX, textY, dimensions.width, dimensions.height, id);
                  gui.prepareTextColor();
                  rpgcode.drawText(
                     textX,
                     textY + (gui.getFontSize() / 2) + textPadding,
                     item.count, id
                  );
               }
            }
         }
         gui.prepareGridBorderColor();
         rpgcode.drawRect(cell.x, cell.y, cell.width, cell.height, cell.lineWidth, id);
      };

      var _drawGridSelection = function() {
         gui.prepareSelectionColor();
         var index = _grid.getSelectedIndex();
         // https://softwareengineering.stackexchange.com/a/212813
         var column = index % _grid.columns;
         var row = Math.floor(index / _grid.columns);
         var x = _grid.x + (_grid.cellWidth * column);
         var y = _grid.y + (_grid.cellHeight * row);
         var width = _grid.cellWidth;
         var height = _grid.cellHeight;
         rpgcode.fillRect(x, y, width, height, id);
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
         setLocation: setLocation,
         setImage: setImage,
         getMenu: getMenu,
         setMenu: setMenu,
         getGrid: getGrid,
         setGrid: setGrid
      };
   })();
   frame.init();
   this.frames[frame.id] = frame;
   return frame;
};

GUI.prototype.createButton = function(config) {
   var button = (function() {
      var _visible = false;
      var _image = null;
      var _text = config.text;
      var _focused = false;

      var id = config.id;
      
      rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
      var width = config.width ? config.width : rpgcode.measureText(_text).width + (gui.padding.x * 2);
      var height = config.height ? config.height : rpgcode.measureText(_text).height + gui.padding.y;

      var x = config.x;
      var y = config.y;
      var onClick = config.onClick;

      var init = function() {
         // Create the canvas element.
         rpgcode.createCanvas(width, height, id);
         rpgcode.setCanvasPosition(x, y, id);
      };

      var destroy = function() {
         rpgcode.clearCanvas(id);
         rpgcode.destroyCanvas(id);
         delete gui.buttons[id];
      };

      var setVisible = function(visible) {
         _visible = visible;
         if (_visible) {
            draw();
         } else {
            rpgcode.clearCanvas(id);
         }
      };

      var setLocation = function(newX, newY) {
         this.x = newX;
         this.y = newY;
         rpgcode.setCanvasPosition(newX, newY, id);
         if (_visible) {
            rpgcode.renderNow(id);
         }
      };

      var setImage = function(image) {
         _image = image;
         draw();
      };

      var isFocused = function() {
         return _focused;
      };

      var onEnter = function() {
         _focused = true;
         draw();
      };

      var onExit = function() {
         _focused = false;
         draw();
      };

      var draw = function() {
         const border = gui.borderWidth > 0 ? gui.borderWidth : 0;
         const radius = gui.cornerRadius > 0 ? gui.cornerRadius : 0;
         gui.setGradient(width / 2, 0, width / 2, height * 1.75, id);
         rpgcode.fillRoundedRect(border, border, width - (border * 2), height - (border * 2), radius, id);
         gui.removeGradient();
         gui.setColor(gui.colors.border);
         rpgcode.drawRoundedRect(border, border, width - (border * 2), height - (border * 2), border, radius, id);

         if (_focused) {
            _drawFocus(border, radius);
         }
         if (_image) {
            _drawImage();
         }
         if (_text) {
            _drawText();
         }
         rpgcode.renderNow(id);
      };

      var _drawFocus = function(border, radius) {
         gui.setColor(gui.colors.border);
         rpgcode.setGlobalAlpha(gui.colors.selection.a);
         rpgcode.fillRoundedRect(border, border, width - (border * 2), height - (border * 2), radius, id);
         rpgcode.setGlobalAlpha(1.0);
      };

      var _drawImage = function() {
         rpgcode.setImage(_image, gui.borderWidth * 3, gui.borderWidth * 3, width - (gui.borderWidth * 6), height - (gui.borderWidth * 6), id);
      };

      var _drawText = function() {
         rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
         var x = (width / 2) - (rpgcode.measureText(_text).width / 2);
         var y = gui.padding.y;
         gui.prepareTextColor();
         rpgcode.drawText(x, y, _text, id);
      };

      return {
         id: id,
         width: width,
         height: height,
         x: x,
         y: y,
         onClick: onClick,
         onEnter: onEnter,
         onExit: onExit,
         init: init,
         destroy: destroy,
         draw: draw,
         setVisible: setVisible,
         setLocation: setLocation,
         setImage: setImage,
         isFocused: isFocused
      };
   })();
   button.init();
   this.buttons[button.id] = button;
   return button;
};

GUI.prototype.prepareTextColor = function() {
   rpgcode.setColor(this.colors.text.r, this.colors.text.g, this.colors.text.b, this.colors.text.a);
};

GUI.prototype.prepareStatBarColor = function() {
   rpgcode.setColor(this.colors.statBar.r, this.colors.statBar.g, this.colors.statBar.b, this.colors.statBar.a);
};

GUI.prototype.prepareSelectionColor = function() {
   rpgcode.setColor(this.colors.selection.r, this.colors.selection.g, this.colors.selection.b, this.colors.selection.a);
};

GUI.prototype.prepareGridCellColor = function() {
   rpgcode.setColor(this.colors.grid.cell.r, this.colors.grid.cell.g, this.colors.grid.cell.b, this.colors.grid.cell.a);
};

GUI.prototype.prepareGridBorderColor = function() {
   rpgcode.setColor(this.colors.grid.border.r, this.colors.grid.border.g, this.colors.grid.border.b, this.colors.grid.border.a);
};

GUI.prototype.listenToMouse = function(listen) {
   if (listen) {
      rpgcode.registerMouseClick(this._handleMouseInput.bind(this), false);
      rpgcode.registerMouseDoubleClick(this._handleMouseInput.bind(this), false);
      rpgcode.registerMouseDown(this._handleMouseInput.bind(this), false);
      rpgcode.registerMouseUp(this._handleMouseInput.bind(this), false);
      rpgcode.registerMouseMove(this._handleMouseInput.bind(this), false);
   } else {
      rpgcode.unregisterMouseClick(false);
      rpgcode.unregisterMouseDoubleClick(false);
      rpgcode.unregisterMouseDown(false);
      rpgcode.unregisterMouseUp(false);
      rpgcode.unregisterMouseMove(false);
   }
};

GUI.prototype._handleMouseInput = function(e, type) {
   switch (e.type) {
      case "click":
         for (const button of Object.values(this.buttons)) {
            if (this._mouseWithinBounds(e, button)) {
               button.onClick();
               break;
            }
         }
         break;
      case "dblclick":
         break;
      case "mousedown":
         break;
      case "mouseup":
         break;
      case "mousemove":
         for (const button of Object.values(this.buttons)) {
            if (this._mouseWithinBounds(e, button)) {
               if (!button.isFocused()) {
                  button.onEnter();
               }
            } else if (button.isFocused()) {
               button.onExit();
            }
         }
         break;
      default:
      // Do nothing
   }
   return;
};

GUI.prototype._mouseWithinBounds = function(e, bounds) {
   // See: https://stackoverflow.com/a/33066028
   return (bounds.x <= gui.descale(e.realX)) && (gui.descale(e.realX) <= (bounds.x + bounds.width)) && (bounds.y <= gui.descale(e.realY)) && (gui.descale(e.realY) <= (bounds.y + bounds.height));
};
