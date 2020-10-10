# Character, Enemy, NPC to Sprite

# Structure Example
```json
{
  "name": "some-sprite",
  "description": "a basic sprite",
  "animations": {
    "animation-key": "animation-asset"
  },
  "collider": {
    "enabled": true,
    "x": -15,
    "y": 0,
    "points": [{
            "x": 0,
            "y": 0
        }, {
            "x": 30,
            "y": 0
        }, {
            "x": 30,
            "y": 20
        }, {
            "x": 0,
            "y": 20
        }
    ]
  },
  "trigger" : {
    "enabled": true,
    "x": -20,
    "y": -5,
    "events": [{
            "script": "",
            "type": "overlap"
        }
    ],
    "points": [{
            "x": 0,
            "y": 0
        }, {
            "x": 40,
            "y": 0
        }, {
            "x": 40,
            "y": 30
        }, {
            "x": 0,
            "y": 30
        }
    ]
  },
  "data": {
    "key-1": "value-1"
  },
  "version": "2.0.0"
}
```

# Top Level Fields
> All additional fields that currently exist on Character and Enemy will simply
> be stored under a generic "data" key-value structure.

| Old Field                | New Field     | Comment                                    |
|--------------------------|---------------|--------------------------------------------|
| name                     | name          | _NO CHANGE_                                |
| description              | description   | _NO CHANGE_                                |
| activationVector         | trigger       |                                            |
| activationOffset         | trigger       |                                            |
| activationVectorDisabled | trigger       |                                            |
| animations               | animations    |                                            |
| baseVector               | collider      |                                            |
| baseVectorOffset         | collider      |                                            |
| baseVectorDisabled       | collider      |                                            |
| frameRate                | REMOVED       | REMOVED                                    |
| graphics                 | REMOVED       | REMOVED                                    |
| version                  | version       | _NO CHANGE_                                |
