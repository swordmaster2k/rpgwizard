# Release 1.4.0 - (06/06/2018)

## New

* Added a "Project" panel to the editor for viewing and opening project files.
* New default dialog, battle, and titlescreen systems included with the demo project see:
  * Programs/defaults/dialog.js
  * Programs/defaults/battle.js
  * Programs/defaults/titleScreen.js


## Improvements

* Code completion added to the code editor for the default libraries.
* Find, Replace, Cut, Copy, Paste, etc. now available for code editor only.
* Updated the code editor UI to a dark theme.

## Fixes

* #16 Adding duplicate points to a BoardVector breaks collisions
  * No longer possible to add duplicate points to a vector sequentially.
* #12 BoardSprites in the BoardEditor are being drawn above everything
  * Sprites are now drawn correctly on a per-layer basis.
