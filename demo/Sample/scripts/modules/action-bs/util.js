export function findClosetObjects(origin, direction, distance) {
   // Figure out the normalised vector (x, y) values to use based on the direction
   let vector = {};
   switch (direction) {
      case "NORTH":
         vector = { x: 0, y: -1 };
         break;
      case "SOUTH":
         vector = { x: 0, y: 1 };
         break;
      case "NORTH_EAST":
      case "SOUTH_EAST":
      case "EAST":
         vector = { x: 1, y: 0 };
         break;
      case "NORTH_WEST":
      case "SOUTH_WEST":
      case "WEST":
         vector = { x: -1, y: 0 };
         break;
   }

   // Fire a raycast from the character's current location, using
   // the supplied direction and range in pixels.
   return rpg.raycast({ _x: origin.x, _y: origin.y }, vector, distance);
}

export function randomDirection(origin, distance) {
   const directions = ["NORTH", "SOUTH", "EAST", "WEST"];
   let attempts = 0;
   let choice;
   let best = {direction: "NORTH", distance: 0};
   while (attempts < directions.length) {
      choice = Math.floor(rpg.getRandom(0, directions.length - 1));
      const objects = findClosetObjects(origin, directions[choice], distance);
      if (Object.keys(objects.colliders).length < 1 || Object.keys(objects.colliders)[0].distance > distance) {
         return directions[choice];
      }
      if (Object.keys(objects.colliders)[0].distance > best.distance) {
         best.direction = directions[choice];
         best.distance = objects.colliders[0].distance;
      }
      attempts++;
   }

   return best.direction;
}