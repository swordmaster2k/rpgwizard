# RPGCode to RPG API

> Primary module will be shortened from "rpgcode" to simply "rpg".

## Functions to Remove
| Function                 | Comment                                    |
|--------------------------|--------------------------------------------|
| getBoardName             | Prefer getMap().name                       |
| animateCharacter         | REPLACED use sprite equivalent instead     |
| changeCharacterGraphics  | REPLACED use sprite equivalent instead     |
| getCharacter             | REPLACED use sprite equivalent instead     |
| getCharacterDirection    | REPLACED use sprite equivalent instead     |
| getCharacterLocation     | REPLACED use sprite equivalent instead     |
| moveCharacter            | REPLACED use sprite equivalent instead     |
| moveCharacterTo          | REPLACED use sprite equivalent instead     |
| setCharacterLocation     | REPLACED use sprite equivalent instead     |
| setCharacterSpeed        | REPLACED use sprite equivalent instead     |
| setCharacterStance       | REPLACED use sprite equivalent instead     |
| setImage                 | DEPRECATED                                 |
| setImagePart             | DEPRECATED                                 |
| getItem                  | REMOVED                                    |
| giveItem                 | REMOVED                                    |
| takeItem                 | REMOVED                                    |
| endProgram               | REMOVED                                    |
| clearDialog              | REMOVED                                    |
| hitCharacter             | REMOVED                                    |
| hitEnemy                 | REMOVED                                    |
| log                      | Just use console.log                       |
| setDialogDimensions      | REMOVED                                    |
| setDialogGraphics        | REMOVED                                    |
| setDialogPadding         | REMOVED                                    |
| setDialogPosition        | REMOVED                                    |
| showDialog               | REMOVED                                    |
| isControlEnabled         | REMOVED                                    |
| runProgram               | REMOVED                                    |

## Functions to Rename
| Old Name                 | New Name      | Comment                                    |
|--------------------------|---------------|--------------------------------------------|
| isAssetLoaded            | isLoaded      | Shorter                                    |
| loadAssets               | load          | Shorter                                    |
| removeAssets             | remove        | Shorter                                    |
| getBoard                 | getMap        |                                            |
| getTileData              | getTile       | Shorter                                    |
| sendToBoard              | loadMap       | Shorter                                    |
| renderNow                | render        | Shorter                                    |
| setGlobalAlpha           | setAlpha      | Shorter                                    |
| runProgram               | run           | Shorter                                    |
| delay                    | sleep         |                                            |
| loadJSON                 | loadJson      |                                            |
| saveJSON                 | saveJson      |                                            |

## Needs attention

resetActivationChecks

drawImagePart

getRunningProgram

addRunTimeProgram
removeRunTimeProgram
