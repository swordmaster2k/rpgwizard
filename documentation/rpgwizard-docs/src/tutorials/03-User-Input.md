<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Keyboard Events](#keyboard-events)
	- [Local vs Global Key Listeners](#local-vs-global-key-listeners)
	- [Listening for Key Down](#listening-for-key-down)
	- [Listening for Key Up](#listening-for-key-up)
	- [Telling if a Key is Held Down](#telling-if-a-key-is-held-down)
	- [How to Stop Listening to a Key](#how-to-stop-listening-to-a-key)
- [Mouse Events](#mouse-events)
	- [Single Click](#single-click)
	- [Double Click](#double-click)
	- [Mouse Button Down](#mouse-button-down)
	- [Mouse Button Up](#mouse-button-up)
	- [Mouse Movement](#mouse-movement)
	- [How to Stop Listening to a Mouse Event](#how-to-stop-listening-to-a-mouse-event)
- [Touch Support](#touch-support)
- [List of Supported Keys](#list-of-supported-keys)

<!-- /TOC -->

# Keyboard Events
> TIP: You can listen to as many keys as you want using separate listeners, however **each key can only have a single listener attached at a time**.

## Local vs Global Key Listeners
When listening on a key events you have the option of whether or not to register the listener at a **local or global scope**.

* **local scope:** The listener only lasts as long as the program is running, useful for temporary things, e.g. navigating a menu screen.

* **global scope:** The listener will live beyond the scope of the program that created it, useful for action keys, e.g. swinging a sword in combat.

The default scope for all listeners is **local scope** unless you tell the engine to register it at a **global scope**.

## Listening for Key Down

```javascript
// Register a listener on the "ENTER" key
rpgcode.registerKeyDown("ENTER", function(e) {
  // Enter was pressed down, do something interesting
}, false); // local scope = false, global scope = true
```

## Listening for Key Up

```javascript
// Register a listener on the "ENTER" key
rpgcode.registerKeyUp("ENTER", function(e) {
  // Enter was released up, do something interesting
}, false); // local scope = false, global scope = true
```

## Telling if a Key is Held Down
Unfortunately in JavaScript there is no way to check the state of a key at a given time, instead we have to keep track of whether a key is currently held down. This can be done by simply toggling a *boolean* flag to true or false in the down and up listener for the key. For further details on the subject see this answer on [stackoverflow](https://stackoverflow.com/a/1828802).

```javascript
// Store the state of the enter key
var enterDown = false;

// Register a listener on the "ENTER" key
rpgcode.registerKeyDown("ENTER", function(e) {
  // Enter was pressed down, do something interesting
  enterDown = true;
}, false); // local scope = false, global scope = true

// Register a listener on the "ENTER" key
rpgcode.registerKeyUp("ENTER", function(e) {
  // Enter was released up, do something interesting
  enterDown = false;
}, false); // local scope = false, global scope = true
```

## How to Stop Listening to a Key
> IMPORTANT: The engine will unregister **local scope** key listeners itself when the program that created them ends.

If you want to stop listening to a key event then you can unregister the listener on it at anytime, you need to do this separately for both down and up listeners.

```javascript
// Stop listening to the "ENTER" button on down presses
rpgcode.unregisterKeyDown("ENTER", false);  // local scope = false, global scope = true

// Stop listening to the "ENTER" button on up releases
rpgcode.unregisterKeyUp("ENTER", false);  // local scope = false, global scope = true
```

# Mouse Events
> TIP: You can listen to as many mouse events as you want using separate listeners, however **each mouse event can only have a single listener attached at a time**.

When listening on a mouse events you have the option of whether or not to register the listener at a **local or global scope**.

* **local scope:** The listener only lasts as long as the program is running, useful for temporary things, e.g. navigating a menu screen.

* **global scope:** The listener will live beyond the scope of the program that created it, useful for action keys, e.g. swinging a sword in combat.

The default scope for all listeners is **local scope** unless you tell the engine to register it at a **global scope**.

## Single Click

```javascript
rpgcode.registerMouseClick(function(e) {
 // Log the x and y coordinates of the mouse.
 rpgcode.log(e.realX);
 rpgcode.log(e.realY);

 // Log the mouse button that has been clicked.
 rpgcode.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
}, false); // local scope = false, global scope = true
```

## Double Click

```javascript
rpgcode.registerMouseDoubleClick(function(e) {
 // Log the x and y coordinates of the mouse.
 rpgcode.log(e.realX);
 rpgcode.log(e.realY);

 // Log the mouse button that has been double clicked.
 rpgcode.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
}, false); // local scope = false, global scope = true
```

## Mouse Button Down

```javascript
rpgcode.registerMouseDown(function(e) {
 // Log the x and y coordinates of the mouse.
 rpgcode.log(e.realX);
 rpgcode.log(e.realY);

 // Log the mouse button that has been pressed down.
 rpgcode.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
}, false); // local scope = false, global scope = true
```

## Mouse Button Up

```javascript
rpgcode.registerMouseUp(function(e) {
 // Log the x and y coordinates of the mouse.
 rpgcode.log(e.realX);
 rpgcode.log(e.realY);

 // Log the mouse button that has been released.
 rpgcode.log(e.mouseButton); // LEFT: 0, MIDDLE: 1, RIGHT: 2
}, false); // local scope = false, global scope = true
```

## Mouse Movement

```javascript
rpgcode.registerMouseMove(function(e) {
 // Log the x and y coordinates of the mouse.
 rpgcode.log(e.realX);
 rpgcode.log(e.realY);
}, false); // local scope = false, global scope = true
```

## How to Stop Listening to a Mouse Event
> IMPORTANT: The engine will unregister **local scope** mouse listeners itself when the program that created them ends.

If you want to stop listening to a key event then you can unregister the listener on it at anytime, you need to do this separately for both down and up listeners.

```javascript
// Removes the mouse click handler
rpgcode.unregisterMouseClick(false); // local scope = false, global scope = true

// Removes the mouse double click handler
rpgcode.unregisterMouseDoubleClick(false); // local scope = false, global scope = true

// Removes the mouse down handler
rpgcode.unregisterMouseDown(false); // local scope = false, global scope = true

// Removes the mouse up handler
rpgcode.unregisterMouseUp(false); // local scope = false, global scope = true

// Removes the mouse move handler
rpgcode.unregisterMouseMove(false); // local scope = false, global scope = true
```

# Touch Support
You can achieve a limited amount of touch support using the **MouseDown** and **MouseUp** listeners which will fire on touch based devices. This makes certain touch driven games possible with RPGWizard without having to write extra code.

# List of Supported Keys

```javascript
{
BACKSPACE: 8,
TAB: 9,
ENTER: 13,
PAUSE: 19,
CAPS: 20,
ESC: 27,
SPACE: 32,
PAGE_UP: 33,
PAGE_DOWN: 34,
END: 35,
HOME: 36,
LEFT_ARROW: 37,
UP_ARROW: 38,
RIGHT_ARROW: 39,
DOWN_ARROW: 40,
INSERT: 45,
DELETE: 46,
0: 48,
1: 49,
2: 50,
3: 51,
4: 52,
5: 53,
6: 54,
7: 55,
8: 56,
9: 57,
A: 65,
B: 66,
C: 67,
D: 68,
E: 69,
F: 70,
G: 71,
H: 72,
I: 73,
J: 74,
K: 75,
L: 76,
M: 77,
N: 78,
O: 79,
P: 80,
Q: 81,
R: 82,
S: 83,
T: 84,
U: 85,
V: 86,
W: 87,
X: 88,
Y: 89,
Z: 90,
NUMPAD_0: 96,
NUMPAD_1: 97,
NUMPAD_2: 98,
NUMPAD_3: 99,
NUMPAD_4: 100,
NUMPAD_5: 101,
NUMPAD_6: 102,
NUMPAD_7: 103,
NUMPAD_8: 104,
NUMPAD_9: 105,
MULTIPLY: 106,
ADD: 107,
SUBSTRACT: 109,
DECIMAL: 110,
DIVIDE: 111,
F1: 112,
F2: 113,
F3: 114,
F4: 115,
F5: 116,
F6: 117,
F7: 118,
F8: 119,
F9: 120,
F10: 121,
F11: 122,
F12: 123,
SHIFT: 16,
CTRL: 17,
ALT: 18,
PLUS: 187,
COMMA: 188,
MINUS: 189,
PERIOD: 190,
PULT_UP: 29460,
PULT_DOWN: 29461,
PULT_LEFT: 4,
PULT_RIGHT: 5
}
```
