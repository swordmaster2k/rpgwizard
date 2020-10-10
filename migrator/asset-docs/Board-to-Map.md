# Board To Map

# Structure Example
```json
{

}
```

## Top Level Fields
| Old Field        | New Field     | Comment                                    |
|------------------|---------------|--------------------------------------------|
| name             | REMOVED       | Doesn't have any use                       |
| backgroundMusic  | music         | Drop "background" from field start         |
| firstRunProgram  | entryScript   | Move away from "program" naming            |
| width            | width         | _NO CHANGE_                                |
| height           | height        | _NO CHANGE_                                |
| tileWidth        | tileWidth     | _NO CHANGE_                                |
| tileHeight       | tileHeight    | _NO CHANGE_                                |
| tileSets         | tilesets      | Lowercase "S", tileset is one word now     |
| layers           | layers        | _NO CHANGE_                                |
| sprites          | ???           | **Should this be within each layer?**      |
| startingPosition | startLocation | Standardise on "location" term             |
| version          | version       |                                            |

## Subtypes

### BoardLayer to MapLayer
| Old Field        | New Field   | Comment                                      |
|------------------|-------------|----------------------------------------------|
| name             | id          | Standardise on "id" term                     |
| tiles            | tiles       | _NO CHANGE_                                  |
| images           | images      | _NO CHANGE_                                  |
| vectors          | ???         |                                              |

### BoardSprite to MapSprite
| Old Field        | New Field     | Comment                                    |
|------------------|---------------|--------------------------------------------|
| id               | id            | _NO CHANGE_                                |
| name             | asset         | Represents backing asset                   |
| thread           | thread        | _NO CHANGE_                                |
| events           | events        | _NO CHANGE_                                |
| startingPosition | startLocation | Standardise on "location" term             |

### LayerImage to LayerImage (no name change)
| Old Field        | New Field   | Comment                                      |
|------------------|-------------|----------------------------------------------|
| id               | id          | _NO CHANGE_                                  |
| src              | image       | More consistent with other assets            |
| x                | x           | _NO CHANGE_                                  |
| y                | y           | _NO CHANGE_                                  |

### LayerVector to ???
| Old Field        | New Field   | Comment                                      |
|------------------|-------------|----------------------------------------------|
| id               | ???         |                                              |
| type             | ???         |                                              |
| isClosed         | ???         |                                              |
| points           | ???         |                                              |
| events           | ???         |                                              |
