/**
 * This program contains a sample mission made up of 3 parts:
 *  1. Intro
 *  2. Victory
 *  3. Defeat
 */
if (rpgcode.getGlobal("isVictory")) {
   await (victory());
} else if (rpgcode.getGlobal("isDefeat")) {
   await (defeat());
} else {
   await (init());
   rpgcode.setGlobal("advanceFunction", advance);
}
rpgcode.endProgram();

async function init() {
   MissionBootstrap.bootstrap();
   await intro();
}

async function intro() {
   var config = {
      position: "BOTTOM",
      nextMarkerImage: "next_marker.png",
      typingSound: "typing_loop.wav",
   };

   config.profileImage = "avatars/tutor.jpg";
   config.text = `
   Welcome to the Tactical sample, where everything is mouse driven.
   I will now highlight the enemy units that you can destroy.
   `;
   await dialog.show(config);

   await Overlay.highlightTargets(Object.values(State.getUnits(Common.ID_AI)));

   config.text = `You must protect this unit from dying.`;
   await dialog.show(config);

   await Overlay.highlightTargets([{id: "player-protect"}]);

   config.profileImage = "avatars/tutor.jpg";
   config.text = 
   `
   Here are a few other units that you can use.
   `;
   await dialog.show(config);

   await Overlay.highlightTargets([{id: "player-1"}, {id: "player-2"}, {id: "player-3"}]);

   config.text = 
   `
   Now try moving your units, and destroying the enemy ones. Enjoy!
   `;
   await dialog.show(config);
   
   rpgcode.playSound("mission_0", true, 0.4);
}

async function advance() {
   // Check if Nebula is dead
   if (!rpgcode.getSprite("player-protect")) {
      rpgcode.setGlobal("isDefeat", true);
      rpgcode.runProgram("missions/mission_0.js");
      throw "defeat";
   }
   
   // Check AI unit count
   if (Object.entries(State.getUnits(Common.ID_AI)).length === 1) {
      rpgcode.setGlobal("isVictory", true);
      rpgcode.runProgram("missions/mission_0.js");
      throw "victory";
   }
}

async function victory() {   
   var config = {
      position: "BOTTOM",
      nextMarkerImage: "next_marker.png",
      typingSound: "typing_loop.wav",
   };
   config.profileImage = "avatars/enemy.jpg";
   config.text = 
   `
   Wait!!! 
   I surrender... Please spare my life!
   `;
   await dialog.show(config);

   config.profileImage = "avatars/tutor.jpg";
   config.text = 
   `
   You'll now be asked on what action to take.
   `;
   await dialog.show(config);

   const choices = ["Take the enemy prisoner", "Kill the enemy, no mercy"];
   const result = await UI.presentChoice(choices[0], choices[1]);

   config.profileImage = "avatars/tutor.jpg";
   if (result === choices[0]) {
      config.profileImage = "avatars/tutor.jpg";
      config.text = 
      `
      You have chosen to take the enemy prisoner.
      The game will now restart.
      `;
      await dialog.show(config);

      rpgcode.restart();
   } else {
      config.profileImage = "avatars/tutor.jpg";
      config.text = 
      `
      NO! NO! Pleas--
      ARRRRGH!!!
      `;
      await dialog.show(config);

      for (const unit of Object.values(State.getUnits(Common.ID_AI))) {
         await Common.destroyUnit(unit.id);
      }

      config.profileImage = "avatars/tutor.jpg";
      config.text = 
      `
      You chose to kill the enemy.
      The game will now restart.
      `;
      await dialog.show(config);
      
      rpgcode.restart();
   }
}

async function defeat() {
   var config = {
      position: "BOTTOM",
      nextMarkerImage: "next_marker.png",
      typingSound: "typing_loop.wav",
   };
   config.profileImage = "avatars/tutor.jpg";
   config.text = 
   `
   You failed to protect my unit!
   The game will now restart.
   `;
   await dialog.show(config);
   
   rpgcode.restart();
}