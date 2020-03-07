// A simple bump program to damage and push the player, can be attached to board sprites via the event program.

// Some basic dump properties.
var coolDownTime = 500;       // In milliseconds.
var attackVelocity = 32;      // Distance to push the character in px.
var attackPushTime = 250;     // Amount of time in milliseconds the character is pushed for.
var attackDamage = 1;         // Amount of damage the bump deals.
var defendAnimationId = "";   // Character animation to play when being bumped.

// Select the defend animation to use based on the character's directon.
var direction;
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

var details = {
   "characterId": "Hero",
   "attackDamage": attackDamage,
   "coolDownTime": coolDownTime,
   "direction": direction,
   "attackVelocity": attackVelocity, 
   "attackPushTime": attackPushTime,
   "defendAnimationId": defendAnimationId,
   "attackSound": "hurtCharacter"
};
ARPG.attackCharacter(details, function() {
   rpgcode.resetActivationChecks("Hero"); // So they can be bumped again if we are standing on them.
   rpgcode.endProgram();
});
