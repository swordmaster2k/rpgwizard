# Character, Enemy, NPC to Sprite

# Structure Example
```json
{
  "animations": {
    "animation-key": "animation-asset"
  },
  "collider": {
    "enabled": true
  },
  "trigger" : {
    "enabled": true
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
| name                     | ???           | **What does this actually do?**            |
| description              | ???           | **What does this actually do?**            |
| activationVector         | ???           |                                            |
| activationOffset         | ???           |                                            |
| activationVectorDisabled | ???           |                                            |
| animations               | ???           |                                            |
| baseVector               | ???           |                                            |
| baseVectorOffset         | ???           |                                            |
| baseVectorDisabled       | ???           |                                            |
| frameRate                | REMOVED       | REMOVED                                    |
| graphics                 | ???           |                                            |
| version                  | version       | _NO CHANGE_                                |
