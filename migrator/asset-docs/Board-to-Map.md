# Board To Map

# Structure Example
```json
{
    "name": "Tower",
    "width": 12,
    "height": 20,
    "tileWidth": 32,
    "tileHeight": 32,
    "music": "Tower.ogg",
    "tilesets": ["tower.tileset"],
    "entryScript": "some-script.js",
    "startLocation": {
        "x": 191,
        "y": 558,
        "layer": 0
    },
    "layers": [{
            "id": "Floor",
            "tiles": ["-1:-1", "0:1", "0:2"],
            "colliders": {
                "collider-1": {
                    "points": [{
                            "x": 64,
                            "y": 352
                        }, {
                            "x": 320,
                            "y": 352
                        }
                    ]
                },
                "collider-2": {
                    "points": [{
                            "x": 160,
                            "y": 128
                        }, {
                            "x": 160,
                            "y": 160
                        }, {
                            "x": 224,
                            "y": 160
                        }, {
                            "x": 224,
                            "y": 128
                        }
                    ]
                }
            },
            "triggers": {
                "trigger-1": {
                    "events": [{
                            "script": "GameOver.js",
                            "type": "overlap"
                        }
                    ],
                    "points": [{
                            "x": 160,
                            "y": 128
                        }, {
                            "x": 160,
                            "y": 160
                        }, {
                            "x": 224,
                            "y": 160
                        }, {
                            "x": 224,
                            "y": 128
                        }
                    ]
                }
            },
            "sprites": {
                "sprite-1": {
                    "asset": "Torch.npc",
                    "thread": "Idle.js",
                    "startLocation": {
                        "x": 112,
                        "y": 80,
                        "layer": 0
                    },
                    "events": [{
                            "script": "turn-off.js",
                            "type": "keypress"
                        }
                    ]
                }
            },
            "images": {
                "image-1": {
                    "image": "rooms/above.png",
                    "x": 120,
                    "y": 12
                }
            }
        }
    ],
    "version": "2.0.0"
}
```

## Top Level Fields
| Old Field        | New Field     | Comment                                    |
|------------------|---------------|--------------------------------------------|
| name             | name          |                                            |
| backgroundMusic  | music         | Drop "background" from field start         |
| firstRunProgram  | entryScript   | Move away from "program" naming            |
| width            | width         | _NO CHANGE_                                |
| height           | height        | _NO CHANGE_                                |
| tileWidth        | tileWidth     | _NO CHANGE_                                |
| tileHeight       | tileHeight    | _NO CHANGE_                                |
| tileSets         | tilesets      | Lowercase "S", tileset is one word now     |
| layers           | layers        | _NO CHANGE_                                |
| sprites          | MOVED         | Within layers now                          |
| startingPosition | startLocation | Standardise on "location" term             |
| version          | version       | _NO CHANGE_                                |

## Subtypes

### BoardLayer to MapLayer
| Old Field        | New Field   | Comment                                      |
|------------------|-------------|----------------------------------------------|
| name             | id          | Standardise on "id" term                     |
| tiles            | tiles       | _NO CHANGE_                                  |
| images           | images      | _NO CHANGE_                                  |
| vectors          | REMOVED     | Split into colliders and triggers            |

### BoardSprite to MapSprite
| Old Field        | New Field     | Comment                                    |
|------------------|---------------|--------------------------------------------|
| id               | REMOVED       | Keyed by ID instead                        |
| name             | asset         | Represents backing asset                   |
| thread           | thread        | _NO CHANGE_                                |
| events           | events        | _NO CHANGE_                                |
| startingPosition | startLocation | Standardise on "location" term             |

### LayerImage to LayerImage (no name change)
| Old Field        | New Field   | Comment                                      |
|------------------|-------------|----------------------------------------------|
| id               | REMOVED     | Keyed by ID instead                          |
| src              | image       | More consistent with other assets            |
| x                | x           | _NO CHANGE_                                  |
| y                | y           | _NO CHANGE_                                  |

### Base Vector to Collider
| Old Field        | New Field   | Comment                                      |
|------------------|-------------|----------------------------------------------|
| id               | REMOVED     | Keyed by ID instead                          |
| points           | points      | _NO CHANGE_                                  |

### Activation Vector to Trigger
| Old Field        | New Field   | Comment                                      |
|------------------|-------------|----------------------------------------------|
| id               | REMOVED     | Keyed by ID instead                          |
| points           | points      | _NO CHANGE_                                  |
| events           | events      | _NO CHANGE_                                  |
