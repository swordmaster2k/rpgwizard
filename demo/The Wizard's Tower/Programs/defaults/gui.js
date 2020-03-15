/* global rpgcode */
let gui = new GUI();

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
   let instance = rpgcode.canvases[canvasId];
   let context = instance.canvas.getContext("2d");
   let gradient = context.createLinearGradient(x1, y1, x2, y2);
   let colorStops = gui.getBackgroundGradient();
   for (let i = 0; i < colorStops.length; i++) {
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
   let frame = (function() {
      let _visible = false;
      let _menu = null;
      let _image = null;
      let _grid = null;

      let id = config.id;
      let width = config.width;
      let height = config.height;
      let x = config.x;
      let y = config.y;

      let init = function() {
         // Create the canvas element.
         rpgcode.createCanvas(width, height, id);
         rpgcode.setCanvasPosition(x, y, id);
      };

      let destroy = function() {
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

      let setVisible = function(visible) {
         _visible = visible;
         if (_visible) {
            draw();
         } else {
            rpgcode.clearCanvas(id);
         }
      };

      let setLocation = function(newX, newY) {
         this.x = newX;
         this.y = newY;
         rpgcode.setCanvasPosition(newX, newY, id);
         if (_visible) {
            rpgcode.renderNow(id);
         }
      };

      let setImage = function(image) {
         _image = image;
         draw();
      };

      let getMenu = function() {
         return _menu;
      };

      let setMenu = function(menu) {
         _menu = (function() {
            let _selectedIndex = 0;
            let items = menu.items;
            let _flashMenuSelection = true;
            let _flashInterval = setInterval(function() {
               _flashMenuSelection = !_flashMenuSelection;
               draw();
            }.bind(this), 500);

            let destroy = function() {
               clearInterval(_flashInterval);
            };

            let getSelectedIndex = function() {
               return _selectedIndex;
            };

            let setSelectedIndex = function(index) {
               _selectedIndex = index;
            };

            let isFlashing = function() {
               return _flashMenuSelection;
            };

            let up = function() {
               _selectedIndex = _selectedIndex > 0 ? _selectedIndex - 1 : _selectedIndex;
               draw();
            };

            let down = function() {
               _selectedIndex = _selectedIndex < items.length - 1 ? _selectedIndex + 1 : _selectedIndex;
               draw();
            };

            let executeSelectedItem = function() {
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

      let getGrid = function() {
         return _grid;
      };

      let setGrid = function(grid) {
         _grid = (function() {
            let x = grid.x;
            let y = grid.y;
            let rows = grid.rows;
            let columns = grid.columns;
            let cellWidth = grid.cellWidth;
            let cellHeight = grid.cellHeight;
            let cellLineWidth = grid.cellLineWidth;

            let _selectedIndex = 0;
            let items = grid.items;
            let _flashMenuSelection = true;
            let _flashInterval = setInterval(function() {
               _flashMenuSelection = !_flashMenuSelection;
               draw();
            }.bind(this), 500);

            let destroy = function() {
               clearInterval(_flashInterval);
            };

            let getSelectedIndex = function() {
               return _selectedIndex;
            };

            let setSelectedIndex = function(index) {
               _selectedIndex = index;
            };

            let isFlashing = function() {
               return _flashMenuSelection;
            };

            let up = function() {
               _selectedIndex = Math.max(_selectedIndex - columns, 0);
               draw();
            };

            let down = function() {
               _selectedIndex = Math.min(_selectedIndex + columns, (columns * rows) - 1);
               draw();
            };

            let left = function() {
               _selectedIndex = _selectedIndex > 0 ? _selectedIndex - 1 : _selectedIndex;
               draw();
            };

            let right = function() {
               _selectedIndex = _selectedIndex < (columns * rows) - 1 ? _selectedIndex + 1 : _selectedIndex;
               draw();
            };

            let executeSelectedItem = function() {
               if (items[_selectedIndex]) {
                  items[_selectedIndex].execute();
               }
            };

            let removeSelectedItem = function() {
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

      let draw = function() {
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
            for (let row = 0; row < _grid.rows; row++) {
               for (let column = 0; column < _grid.columns; column++) {
                  let cell = {
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
            for (let i = 0; i < _menu.items.length; i++) {
               _drawMenuItem(_menu.items[i], i);
            }
         }
         rpgcode.renderNow(id);
      };

      let _drawImage = function() {
         rpgcode.setImage(_image, gui.borderWidth * 3, gui.borderWidth * 3, width - (gui.borderWidth * 6), height - (gui.borderWidth * 6), id);
      };

      let _drawMenuItem = function(item, index) {
         rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
         let x = gui.padding.x;
         let y = gui.padding.y + ((gui.getFontSize() + gui.padding.line) * index);
         gui.prepareTextColor();
         rpgcode.drawText(x, y, item.text, id);
      };

      let _drawMenuSelection = function() {
         gui.prepareSelectionColor();
         let x = gui.padding.x - (gui.padding.x / 2);
         let y = gui.padding.y + ((gui.getFontSize() + gui.padding.line) * (_menu.getSelectedIndex())) - gui.getFontSize() - (gui.padding.line / 4);
         let _width = width - gui.padding.x;
         let _height = gui.getFontSize() + (gui.padding.line);
         rpgcode.fillRect(x, y, _width, _height, id);
      };

      let _drawGridCell = function(cell) {
         gui.prepareGridCellColor();
         rpgcode.fillRect(cell.x, cell.y, cell.width, cell.height, id);
         let index = Math.floor(cell.x + _grid.columns * cell.y);
         if (cell.index < _grid.items.length) {
            let item = _grid.items[cell.index];
            if (item && item.image) {
               rpgcode.setImage(item.image, cell.x, cell.y, cell.width, cell.height, id);
               if (1 < item.count) {
                  let textPadding = 3;
                  let dimensions = rpgcode.measureText(item.count);
                  let textX = cell.x + (cell.width - dimensions.width - textPadding);
                  let textY = cell.y;
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

      let _drawGridSelection = function() {
         gui.prepareSelectionColor();
         let index = _grid.getSelectedIndex();
         // https://softwareengineering.stackexchange.com/a/212813
         let column = index % _grid.columns;
         let row = Math.floor(index / _grid.columns);
         let x = _grid.x + (_grid.cellWidth * column);
         let y = _grid.y + (_grid.cellHeight * row);
         let width = _grid.cellWidth;
         let height = _grid.cellHeight;
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
   let button = (function() {
      let _visible = false;
      let _image = null;
      let _text = config.text;
      let _focused = false;

      let id = config.id;
      
      rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
      let width = config.width ? config.width : rpgcode.measureText(_text).width + (gui.padding.x * 2);
      let height = config.height ? config.height : rpgcode.measureText(_text).height + gui.padding.y;

      let x = config.x;
      let y = config.y;
      let onClick = config.onClick;

      let init = function() {
         // Create the canvas element.
         rpgcode.createCanvas(width, height, id);
         rpgcode.setCanvasPosition(x, y, id);
      };

      let destroy = function() {
         rpgcode.clearCanvas(id);
         rpgcode.destroyCanvas(id);
         delete gui.buttons[id];
      };

      let setVisible = function(visible) {
         _visible = visible;
         if (_visible) {
            draw();
         } else {
            rpgcode.clearCanvas(id);
         }
      };

      let setLocation = function(newX, newY) {
         this.x = newX;
         this.y = newY;
         rpgcode.setCanvasPosition(newX, newY, id);
         if (_visible) {
            rpgcode.renderNow(id);
         }
      };

      let setImage = function(image) {
         _image = image;
         draw();
      };

      let isFocused = function() {
         return _focused;
      };

      let onEnter = function() {
         _focused = true;
         draw();
      };

      let onExit = function() {
         _focused = false;
         draw();
      };

      let draw = function() {
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

      let _drawFocus = function(border, radius) {
         gui.setColor(gui.colors.border);
         rpgcode.setGlobalAlpha(gui.colors.selection.a);
         rpgcode.fillRoundedRect(border, border, width - (border * 2), height - (border * 2), radius, id);
         rpgcode.setGlobalAlpha(1.0);
      };

      let _drawImage = function() {
         rpgcode.setImage(_image, gui.borderWidth * 3, gui.borderWidth * 3, width - (gui.borderWidth * 6), height - (gui.borderWidth * 6), id);
      };

      let _drawText = function() {
         rpgcode.setFont(gui.getFontSize(), gui.getFontFamily());
         let x = (width / 2) - (rpgcode.measureText(_text).width / 2);
         let y = gui.padding.y;
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
