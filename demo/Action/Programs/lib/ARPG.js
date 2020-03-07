var ARPG = new arpg();

function arpg() {
  
}

arpg.prototype._getPushForce = function(direction, attackVelocity) {
   switch (direction) {
      case "NORTH":
         return {x: 0, y: -attackVelocity};
      case "SOUTH":
         return {x: 0, y: attackVelocity};
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         return {x: attackVelocity, y: 0};
      case "NORTH_WEST":
      case "SOUTH_WEST":
      case "WEST":
         return {x: -attackVelocity, y: 0};
   }
};

arpg.prototype._getRandomDirection = function(origin, distance) {
   const directions = ["NORTH", "SOUTH", "EAST", "WEST"];
   var attempts = 0;
   var choice;
   var best = {direction: "NORTH", distance: 0};
   while (attempts < directions.length) {
      choice = Math.floor(rpgcode.getRandom(0, directions.length - 1));
      var objects = this.getClosestObjects(origin, directions[choice], distance);
      if (objects["solids"].length < 1 || objects["solids"][0].distance > distance) {
         return directions[choice];
      }
      if (objects["solids"][0].distance > best.distance) {
         best.direction = directions[choice];
         best.distance = objects["solids"][0].distance;
      }
      attempts++;
   }

   return best.direction;
};

arpg.prototype.getClosestObjects = function(origin, direction, distance) {
   // Figure out the normalised vector (x, y) values to use based on the direction
   var vector = {};
   switch (direction) {
      case "NORTH":
         vector = {
            x: 0,
            y: -1
         }
         break;
      case "SOUTH":
         vector = {
            x: 0,
            y: 1
         }
         break;
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         vector = {
            x: 1,
            y: 0
         }
         break;
      case "NORTH_WEST":
      case "SOUTH_WEST":
      case "WEST":
         vector = {
            x: -1,
            y: 0
         }
         break;
   }

   // Fire a raycast from the character's current location, using
   // the supplied direction and range in pixels.
   return rpgcode.fireRaycast({
      _x: origin.x,
      _y: origin.y
   }, vector, distance);
};

arpg.prototype.generateProjectile = function(file, position, thread, program) {
   return {  
      "name": file,
      "id": this.generateUUID(),
      "thread": thread,
      "startingPosition":{  
         "x": position.x,
         "y": position.y,
         "layer": position.layer
      },
      "events":[  
         {  
         "program": program,
         "type": "overlap",
         "key": ""
         }
      ]
   };
};

// Sourced from: https://jsfiddle.net/briguy37/2MVFd/
arpg.prototype.generateUUID = function() {
   var d = new Date().getTime();
   var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = (d + Math.random()*16)%16 | 0;
      d = Math.floor(d/16);
      return (c=='x' ? r : (r&0x3|0x8)).toString(16);
   });
   return uuid;
};

arpg.prototype.attackCharacter = function(details, callback) {
   try {
   	var character = rpgcode.getCharacter(details.characterId);
   	var cooledDown = character.lastHit ? (Date.now() - character.lastHit) > details.coolDownTime : true;
   	
   	if (character.isHit || !cooledDown) {
         if (callback) {
               callback();
         }
   		return;
   	}
   
      var force = ARPG._getPushForce(details.direction, details.attackVelocity);

      rpgcode.playSound(details.attackSound, false);
   	rpgcode.getCharacter(details.characterId).isHit = true;
   	var loc = rpgcode.getCharacterLocation(false);
   	rpgcode.moveCharacterTo(details.characterId, loc.x + force.x, loc.y + force.y, details.attackPushTime, function() {
   		rpgcode.hitCharacter(details.characterId, details.attackDamage, details.defendAnimationId, function() {
   			character.lastHit = Date.now();
            if (callback) {
               callback();
            }
   		});
   	});
   } catch (err) {
      if (callback) {
         callback();
      }
   }
};

arpg.prototype.attackEnemy = function(details, callback) {
   try {
      var sprite = rpgcode.getSprite(details.spriteId).sprite;
      var result = {
         "dead": false,
         "location": null
      };
   
      if (!sprite.enemy.isHit && !sprite.enemy.isInvulnerable) {
         var force = ARPG._getPushForce(details.direction, details.attackVelocity);

         rpgcode.playSound(details.attackSound, false);
         sprite.enemy.isHit = true; // Do this in advance of hitEnemy so we can push them back.
         var spriteLocation = rpgcode.getSpriteLocation(sprite.id, false, false);
         rpgcode.moveSpriteTo(sprite.id, spriteLocation.x + force.x, spriteLocation.y + force.y, details.attackPushTime, function() {
            rpgcode.hitEnemy(sprite.id, details.attackDamage, details.defendAnimationId, function() {
               if (sprite.enemy.health < 1) {
                  result.dead = true;
                  result.location = rpgcode.getSpriteLocation(sprite.id, false, false);

                  if (sprite.id.destroyCallback) {
                     sprite.destroyCallback();
                  } else {
                     rpgcode.destroySprite(sprite.id);
                  }
               }
               
               if (callback) {
                  callback(details, result);
               }
            });
         });
      } else {
         if (callback) {
            callback(details, result);
         }
      }
   } catch (err) {
      if (callback) {
         callback(details, result);
      }  
   }
};

arpg.prototype.dropItem = function (npcId, location, callback) {
   if (!callback) {
      callback = function() {};
   }
   rpgcode.addSprite(
      {  
         "name":npcId,
         "id": Math.floor(Date.now()),
         "thread":"examples/Idle.js",
         "startingPosition":{  
            "x": location.x,
            "y": location.y,
            "layer": location.layer
         },
         "events":[  
            {  
            "program":"lib/ItemPickup.js",
            "type":"overlap",
            "key":""
            }
         ]
      }, 
      callback
   );
};

arpg.prototype.fireProjectile = function (sprite, direction, distance, pixelsPerSecond, callback) {
   var timeTaken = (distance / pixelsPerSecond) * 1000;
   var point;
   switch (direction) {
      case "NORTH":
         point = {"x": sprite.startingPosition.x, "y": sprite.startingPosition.y - distance};
         break;
      case "SOUTH":
         point = {"x": sprite.startingPosition.x, "y": sprite.startingPosition.y + distance};
         break;
      case "EAST":
         point = {"x": sprite.startingPosition.x + distance, "y": sprite.startingPosition.y};
         break;
      case "WEST":
         point = {"x": sprite.startingPosition.x - distance, "y": sprite.startingPosition.y};
         break;
      default:
          point = {"x": sprite.startingPosition.x - distance, "y": sprite.startingPosition.y};
   }
   rpgcode.setGlobal(sprite.id, {"point": point, "direction": direction, "distance": distance, "timeTaken": timeTaken});
   rpgcode.addSprite(sprite, function() {
      if (callback) {
         callback();
      }      
   });
};

arpg.prototype.moveSpriteTowardsPoint = function(entity, point, distance, timeTaken, animationId, callback) {
   try {
   	var angle = rpgcode.getAngleBetweenPoints(point.x, point.y, entity.x, entity.y);
      var direction = rpgcode.getSpriteDirection(entity.sprite.id);
   	var velocityX = distance * Math.cos(angle);
   	var velocityY = distance * Math.sin(angle);
      var id = entity.sprite.id;

      if (animationId) {
          rpgcode.setSpriteStance(id, animationId);
      } else {
         var fullAngle = angle + 3.14;
         if (0.785 < fullAngle && fullAngle < 2.355) {
            if (direction !== "NORTH")
               rpgcode.setSpriteStance(id, "NORTH");
         } else if (3.925 < fullAngle && fullAngle < 5.495) {
            if (direction !== "SOUTH")
               rpgcode.setSpriteStance(id, "SOUTH");
         } else if (2.355 < fullAngle && fullAngle < 3.925) {
            if (direction !== "EAST")
               rpgcode.setSpriteStance(id, "EAST");
         } else if (direction !== "WEST") {
               rpgcode.setSpriteStance(id, "WEST");
         }
      }
   	
   	rpgcode.moveSpriteTo(id, entity.x + velocityX, entity.y + velocityY, timeTaken, callback);
   } catch (err) {
      if (callback) {
         callback();
      }
   }
};

arpg.prototype.selectDefendAnimation = function(direction) {
   var defendAnimationId;
   switch (rpgcode.getCharacterDirection("Hero")) {
      case "NORTH":
         direction = "SOUTH";
         defendAnimationId = "DEFEND_NORTH";
         break;
      case "SOUTH":
         direction = "NORTH";
         defendAnimationId = "DEFEND_SOUTH";
         break;
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         direction = "WEST";
         defendAnimationId = "DEFEND_EAST";
         break;
      default:
         direction = "EAST";
         defendAnimationId = "DEFEND_WEST";
   }

   return {"direction": direction, "defendAnimationId": defendAnimationId};
};

arpg.prototype.slashSword = function(attackPushTime, attackVelocity, attackRangePx, attackDamage, hitEnemy, hitNpc) {
   var direction = rpgcode.getCharacterDirection();
   var location = rpgcode.getCharacterLocation();
   var vector = {};
   
   var getHits = function() {
      // Check to see if we hit any enemies.
      var hits = ARPG.getClosestObjects(location, direction, attackRangePx);
      if (hits["enemies"].length > 0) {
         // Simply attack the first enemy.
         var sprite = hits["enemies"][0]
         var spriteDirection = rpgcode.getSpriteDirection(sprite.id);
         var defendAnimationId;
         switch (spriteDirection) {
            case "NORTH":
               defendAnimationId = "DEFEND_NORTH";
            break;
            case "SOUTH":
               defendAnimationId = "DEFEND_SOUTH";
            break;
            case "NORTH_EAST":
            case "SOUTH_EAST":
            case "EAST":
               defendAnimationId = "DEFEND_EAST";
            break;
            case "NORTH_WEST":
            case "SOUTH_WEST":
            case "WEST":
               defendAnimationId = "DEFEND_WEST";
            break;
         }
         if (hitEnemy) {
            hitEnemy(sprite, direction, defendAnimationId);
         } else {
            rpgcode.endProgram();
         }
      } else if (hits["npcs"].length > 0) {
         if (hitNpc) {
            hitNpc(hits["npcs"][0]);
         } else {
            rpgcode.endProgram();
         }
      } else {
         rpgcode.endProgram();
      }
   };
   
   // Animate the character for the given direciton.
   switch (direction) {
      case "NORTH":
         rpgcode.animateCharacter("Hero", "ATTACK_NORTH", getHits);
      break;
      case "SOUTH":
         rpgcode.animateCharacter("Hero", "ATTACK_SOUTH", getHits);
      break;
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         rpgcode.animateCharacter("Hero", "ATTACK_EAST", getHits);
      break;
      case "NORTH_WEST":
      case "SOUTH_WEST":
      case "WEST":
         rpgcode.animateCharacter("Hero", "ATTACK_WEST", getHits);
      break;
   }
   
   rpgcode.playSound("sword", false); 
};

arpg.prototype.wander = function(entity, distance, timeTaken, callback) {
   try {
   	var id = entity.sprite.id;
   	var choice = ARPG._getRandomDirection(entity, distance);
   	if (choice === "EAST") {
   		velocityX = distance;
   		velocityY = 0;
   		rpgcode.setSpriteStance(id, "EAST");
   	} else if (choice === "WEST") {
   		velocityX = -distance;
   		velocityY = 0;
   		rpgcode.setSpriteStance(id, "WEST");
   	} else if (choice === "SOUTH") {
   		velocityY = distance;
   		velocityX = 0;
   		rpgcode.setSpriteStance(id, "SOUTH");
   	} else {
   		velocityY = -distance;
   		velocityX = 0;
   		rpgcode.setSpriteStance(id, "NORTH");
   	}
   
   	rpgcode.moveSpriteTo(id, entity.x + velocityX, entity.y + velocityY, timeTaken, callback);
   } catch (err) {
      if (callback) {
         callback();
      }
   }
};
