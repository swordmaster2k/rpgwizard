export const state = {};

export function setup() {
   state.borderWidth = 3;
   state.cornerRadius = 15;
   state.colors = {
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
         r: 0,
         g: 0,
         b: 0,
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
   state.padding = {
      x: 15,
      y: 35,
      line: 10
   };
   state.fontSize = 20;
   state.fontFamily = "Lucida Console";

   state.frames = {};
   state.buttons = {};
}

export function getBackgroundGradient() {
   return ["rgba(74, 121, 90, 1)", "rgba(0, 0, 0, 1)"];
}

export function getFontSize() {
   return state.fontSize;
}

export function setFontSize(size) {
   state.fontSize = size;
}

export function getFontFamily(family) {
   return state.fontFamily;
}

export function setFontFamily(family) {
   state.fontFamily = family;
}

export function getFont() {
   return getFontSize() + "px " + getFontFamily();
}

export function setColor(color) {
   rpg.setColor(color.r, color.g, color.b, color.a);
}

export function setGradient(x1, y1, x2, y2, canvasId) {
   let instance = rpg.canvases[canvasId];
   let context = instance.canvasElement.getContext("2d");
   let gradient = context.createLinearGradient(x1, y1, x2, y2);
   let colorStops = getBackgroundGradient();
   for (let i = 0; i < colorStops.length; i++) {
      gradient.addColorStop(i, colorStops[i]);
   }
   rpg.gradient = gradient;
}

export function removeGradient() {
   rpg.gradient = null;
}

export function scale(value) {
   return value * rpg.getScale();
}

export function descale(value) {
   return Math.round(value / rpg.getScale());
}

export function createFrame(config) {
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
         rpg.createCanvas(id, width, height);
         rpg.setCanvasPosition(id, x, y);
      };

      let destroy = function() {
         if (_menu) {
            _menu.destroy();
         }
         if (_grid) {
            _grid.destroy();
         }
         rpg.clear(id);
         rpg.removeCanvas(id);
         delete state.frames[id];
      };

      let setVisible = function(visible) {
         _visible = visible;
         if (_visible) {
            draw();
         } else {
            rpg.clear(id);
         }
      };

      let setLocation = function(newX, newY) {
         this.x = newX;
         this.y = newY;
         rpg.setCanvasPosition(id, newX, newY);
         if (_visible) {
            rpg.render(id);
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
            }, 500);

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
            }, 500);

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
         const border = state.borderWidth > 0 ? state.borderWidth : 0;
         const radius = state.cornerRadius > 0 ? state.cornerRadius : 0;
         setGradient(width / 2, 0, width / 2, height * 1.75, id);
         rpg.fillRoundedRect(id, border, border, width - (border * 2), height - (border * 2), radius);
         removeGradient();
         setColor(state.colors.border);
         rpg.drawRoundedRect(id, border, border, width - (border * 2), height - (border * 2), border, radius);

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
         rpg.render(id);
      };

      let _drawImage = function() {
         rpg.drawImage(id, _image, state.borderWidth * 3, state.borderWidth * 3, width - (state.borderWidth * 6), height - (state.borderWidth * 6), 0);
      };

      let _drawMenuItem = function(item, index) {
         rpg.setFont(getFontSize(), getFontFamily());
         let x = state.padding.x;
         let y = state.padding.y + ((getFontSize() + state.padding.line) * index);
         prepareTextColor();
         rpg.drawText(id, x, y, item.text);
      };

      let _drawMenuSelection = function() {
         prepareSelectionColor();
         let x = state.padding.x - (state.padding.x / 2);
         let y = state.padding.y + ((getFontSize() + state.padding.line) * (_menu.getSelectedIndex())) - getFontSize() - (state.padding.line / 4);
         let _width = width - state.padding.x;
         let _height = getFontSize() + (state.padding.line);
         rpg.fillRect(id, x, y, _width, _height);
      };

      let _drawGridCell = function(cell) {
         prepareGridCellColor();
         rpg.fillRect(id, cell.x, cell.y, cell.width, cell.height);
         let index = Math.floor(cell.x + _grid.columns * cell.y);
         if (cell.index < _grid.items.length) {
            let item = _grid.items[cell.index];
            if (item && item.image) {
               rpg.drawImage(id, item.image, cell.x, cell.y, cell.width, cell.height, 0);
               if (1 < item.count) {
                  let textPadding = 3;
                  let dimensions = rpg.measureText(item.count);
                  let textX = cell.x + (cell.width - dimensions.width - textPadding);
                  let textY = cell.y;
                  rpg.fillRect(id, textX, textY, dimensions.width, dimensions.height);
                  prepareTextColor();
                  rpg.drawText(
                     id,
                     textX,
                     textY + (getFontSize() / 2) + textPadding,
                     item.count
                  );
               }
            }
         }
         prepareGridBorderColor();
         rpg.drawRect(id, cell.x, cell.y, cell.width, cell.height, cell.lineWidth);
      };

      let _drawGridSelection = function() {
         prepareSelectionColor();
         let index = _grid.getSelectedIndex();
         // https://softwareengineering.stackexchange.com/a/212813
         let column = index % _grid.columns;
         let row = Math.floor(index / _grid.columns);
         let x = _grid.x + (_grid.cellWidth * column);
         let y = _grid.y + (_grid.cellHeight * row);
         let width = _grid.cellWidth;
         let height = _grid.cellHeight;
         rpg.fillRect(id, x, y, width, height);
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
   state.frames[frame.id] = frame;
   return frame;
}

export function createButton(config) {
   let button = (function() {
      let _visible = false;
      let _image = null;
      let _text = config.text;
      let _focused = false;

      let id = config.id;
      
      rpg.setFont(getFontSize(), getFontFamily());
      let width = config.width ? config.width : rpg.measureText(_text).width + (state.padding.x * 2);
      let height = config.height ? config.height : rpg.measureText(_text).height + state.padding.y;

      let x = config.x;
      let y = config.y;
      let onClick = config.onClick;

      let init = function() {
         // Create the canvas element.
         rpg.createCanvas(id, width, height);
         rpg.setCanvasPosition(id, x, y);
      };

      let destroy = function() {
         rpg.clear(id);
         rpg.removeCanvas(id);
         delete state.buttons[id];
      };

      let setVisible = function(visible) {
         _visible = visible;
         if (_visible) {
            draw();
         } else {
            rpg.clear(id);
         }
      };

      let setLocation = function(newX, newY) {
         state.x = newX;
         state.y = newY;
         rpg.setCanvasPosition(id, newX, newY);
         if (_visible) {
            rpg.render(id);
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
         const border = state.borderWidth > 0 ? state.borderWidth : 0;
         const radius = state.cornerRadius > 0 ? state.cornerRadius : 0;
         setGradient(width / 2, 0, width / 2, height * 1.75, id);
         rpg.fillRoundedRect(id, border, border, width - (border * 2), height - (border * 2), radius);
         removeGradient();
         setColor(state.colors.border);
         rpg.drawRoundedRect(id, border, border, width - (border * 2), height - (border * 2), border, radius);

         if (_focused) {
            _drawFocus(border, radius);
         }
         if (_image) {
            _drawImage();
         }
         if (_text) {
            _drawText();
         }
         rpg.render(id);
      };

      let _drawFocus = function(border, radius) {
         setColor(state.colors.border);
         rpg.setAlpha(state.colors.selection.a);
         rpg.fillRoundedRect(id, border, border, width - (border * 2), height - (border * 2), radius);
         rpg.setAlpha(1.0);
      };

      let _drawImage = function() {
         rpg.drawImage(id, _image, state.borderWidth * 3, state.borderWidth * 3, width - (state.borderWidth * 6), height - (state.borderWidth * 6), 0);
      };

      let _drawText = function() {
         rpg.setFont(getFontSize(), getFontFamily());
         let x = (width / 2) - (rpg.measureText(_text).width / 2);
         let y = state.padding.y;
         prepareTextColor();
         rpg.drawText(id, x, y, _text);
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
   state.buttons[button.id] = button;
   return button;
}

export function prepareTextColor() {
   rpg.setColor(state.colors.text.r, state.colors.text.g, state.colors.text.b, state.colors.text.a);
}

export function prepareStatBarColor() {
   rpg.setColor(state.colors.statBar.r, state.colors.statBar.g, state.colors.statBar.b, state.colors.statBar.a);
}

export function prepareSelectionColor() {
   rpg.setColor(state.colors.selection.r, state.colors.selection.g, state.colors.selection.b, state.colors.selection.a);
}

export function prepareGridCellColor() {
   rpg.setColor(state.colors.grid.cell.r, state.colors.grid.cell.g, state.colors.grid.cell.b, state.colors.grid.cell.a);
}

export function prepareGridBorderColor() {
   rpg.setColor(state.colors.grid.border.r, state.colors.grid.border.g, state.colors.grid.border.b, state.colors.grid.border.a);
}

export function listenToMouse(listen) {
   if (listen) {
      rpg.registerMouseClick(_handleMouseInput, false);
      rpg.registerMouseDoubleClick(_handleMouseInput, false);
      rpg.registerMouseDown(_handleMouseInput, false);
      rpg.registerMouseUp(_handleMouseInput, false);
      rpg.registerMouseMove(_handleMouseInput, false);
   } else {
      rpg.unregisterMouseClick(false);
      rpg.unregisterMouseDoubleClick(false);
      rpg.unregisterMouseDown(false);
      rpg.unregisterMouseUp(false);
      rpg.unregisterMouseMove(false);
   }
}

function _handleMouseInput(e, type) {
   switch (e.type) {
      case "click":
         for (const button of Object.values(state.buttons)) {
            if (state._mouseWithinBounds(e, button)) {
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
         for (const button of Object.values(state.buttons)) {
            if (state._mouseWithinBounds(e, button)) {
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
}

function _mouseWithinBounds(e, bounds) {
   // See: https://stackoverflow.com/a/33066028
   return (bounds.x <= descale(e.realX)) && (descale(e.realX) <= (bounds.x + bounds.width)) && (bounds.y <= descale(e.realY)) && (descale(e.realY) <= (bounds.y + bounds.height));
}