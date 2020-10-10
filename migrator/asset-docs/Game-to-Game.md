# TileSet to Tileset

# Structure Example
```json
{
    "name": "The Wizard's Tower",
    "viewport": {
      "width": 640,
      "height": 480,
      "fullScreen": false
    },
    "debug": {
      "showColliders": false,
      "showTriggers": false
    },
    "version": "2.0.0"
}
```

# Top Level Fields
| Old Field                | New Field     | Comment                                    |
|--------------------------|---------------|--------------------------------------------|
| name                     | name          | _NO CHANGE_                                |
| initialCharacter         | REMOVED       | Look for file with name instead            |
| initialBoard             | REMOVED       | Look for file with name instead            |
| startupProgram           | REMOVED       | Look for file with name instead            |
| gameOverProgram          | REMOVED       | Look for file with name instead            |
| projectIcon              | REMOVED       | Look for file with name instead            |
| showVectors              | debug         | Moved under debug                          |
| isFullScreen             | viewport      | Moved under viewport                       |
| resolutionWidth          | viewport      | Moved under viewport                       |
| resolutionHeight         | viewport      | Moved under viewport                       |
| version                  | version       | _NO CHANGE_                                |
