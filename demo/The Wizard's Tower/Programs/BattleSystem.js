/*
 * A simple example of a 1v1 turn based battle system that is completely driven by user input.
 */

// Assets to load up for the battle system.
var assets = {
   "audio": {
      "Battle.wav": "Battle.wav"
   },
   "images": [
      "hero_battle_profile.png",
      "sword_battle_profile.png",
      "battle_menu.png",
      "life.png",
      "emptylife.png"
   ]
};

// The keys that we will be listening to.
var keys = ["UP_ARROW", "DOWN_ARROW", "ENTER"];

// Turn variables.
var Turn = {
   CHARACTER: 1,
   SWORD: 2,
   ENEMY: 3
};
var currentTurn = Turn.CHARACTER;

// Canvas IDs.
var buffer = "buffer";
var heroMenu = "hero_menu";
var swordMenu = "sword_menu";
var battleMenu = "battle_menu";
var heroProfile = "hprofile";
var swordProfile = "sprofile";
var lifeIcon = "life_icon";
var emptyLifeIcon = "empty_life_icon";


// Menu postion.
var menuPositionX = 515;
var menuPositionY = 180;

// Menu selection status.
var Submenu = {
   DEFAULT: 1,
};
var currentSubmenu = Submenu.DEFAULT;
var menuChoice = 1;

// Enemy stats.
var enemyHp = 3;
var enemyAttackPower = 1;

// Character stats.
var character = rpgcode.getCharacter();
var characterMaxHp = character.maxHealth;
var characterHp = character.health;
var characterAttackPower = character.attack;

// Misc.
var swordDialog = [
   "You're a PUSSY",
   "STUPID MONSTER",
   "My grandma is scarier than you",
   "Come here and bleed",
   "Go #%*$ #%$* with your %*#$%*#"
];

if (rpgcode.getGlobal("haveBattled")) {
   rpgcode.endProgram();
} else {


   /* ***************************************************************************************
    * Setup Logic: Loading the required assets, setting the battled global variable.
    *************************************************************************************** */

   rpgcode.loadAssets(assets, function() {
      rpgcode.log("Assets loaded.");
      rpgcode.setGlobal("haveBattled", false);

      // Smaller canvases that make up the battle system.
      rpgcode.createCanvas(80, 90, heroMenu);
      rpgcode.createCanvas(80, 190, swordMenu);
      rpgcode.createCanvas(125, 300, battleMenu);
      rpgcode.createCanvas(54, 24, heroProfile);
      rpgcode.createCanvas(54, 24, swordProfile);
      rpgcode.createCanvas(32, 32, lifeIcon);
      rpgcode.createCanvas(32, 32, emptyLifeIcon);

      // Canvas to draw onto.
      rpgcode.createCanvas(640, 480, buffer);

      // Set the images on the smaller canvases.
      rpgcode.setImage("hero_battle_profile.png", 0, 0, 54, 24, heroProfile);
      rpgcode.setImage("sword_battle_profile.png", 0, 0, 54, 24, swordProfile);
      rpgcode.setImage("life.png", 0, 0, 32, 32, lifeIcon);
      rpgcode.setImage("emptylife.png", 0, 0, 32, 32, emptyLifeIcon);

      // Listen to these keys.
      rpgcode.registerKeyDown(keys[0], handleUpArrow);
      rpgcode.registerKeyDown(keys[1], handleDownArrow);
      rpgcode.registerKeyDown(keys[2], handleEnter);

      rpgcode.setCharacterLocation("Hero", 192, 352, 0);
      rpgcode.setCharacterStance("Hero", "ATTACK_NORTH");

      // Stop any other music and play the battle tune.
      rpgcode.stopSound();
      rpgcode.playSound("Battle.wav", true);

      update();
   });

   /* ***************************************************************************************
    * Menu Logic: Key handlers and Drawing functions.
    *************************************************************************************** */

   /**
    * Checks to ensure the current menu choice does not go out of bounds,
    * if the choise is invalid then it is set back to the last valid one.
    */
   function checkMenuChoice() {
      rpgcode.log("Calling checkMenuChoice=[" + menuChoice + "]");

      if (menuChoice < 1) {
         menuChoice = 1;
      }

      if (currentTurn === Turn.CHARACTER) {
         if (menuChoice > 1 && currentSubmenu === Submenu.DEFAULT) {
            menuChoice = 1;
         }
      } else if (currentTurn === Turn.SWORD) {
         if (menuChoice > 2) {
            menuChoice = 2;
         }
      }

      rpgcode.log("After checkMenuChoice=[" + menuChoice + "]");
   }

   /**
    * Handles UP arrow key presses in the menu.
    */
   function handleUpArrow() {
      if (currentTurn === Turn.CHARACTER) {
         menuChoice -= 1;
         checkMenuChoice();
      } else if (currentTurn === Turn.SWORD) {
         menuChoice -= 1;
         checkMenuChoice();
      }

      if (currentTurn !== Turn.ENEMY) {
         drawState();
      }
   }

   /**
    * Handles DOWN arrow key presses in the menu.
    */
   function handleDownArrow() {
      if (currentTurn === Turn.CHARACTER) {
         menuChoice += 1;
         checkMenuChoice();
      } else if (currentTurn === Turn.SWORD) {
         menuChoice += 1;
         checkMenuChoice();
      }

      if (currentTurn !== Turn.ENEMY) {
         drawState();
      }
   }

   /**
    * Handles ENTER presses in the menu, if it is either the character's or sword's turn
    * then the battle will be advanced.
    */
   function handleEnter() {
      rpgcode.log("turn=" + currentTurn + " menuChoice=" + menuChoice + " submenu=" + currentSubmenu);
      if (currentTurn === Turn.CHARACTER) {
         doCharacterTurn();
      } else if (currentTurn === Turn.SWORD) {
         doSwordTurn();
      }
   }

   /**
    * Draws the state of a menu item to the requested canvas, selected
    * items will be highlighted with a green color.
    */
   function drawMenuItem(x1, y1, x2, y2, text, canvas, isSelected) {
      rpgcode.setColor(0, 0, 0, 1);
      rpgcode.drawText(x1, y1, text, canvas);

      if (isSelected) {
         rpgcode.setColor(0, 255, 0, 1);
      } else {
         rpgcode.setColor(255, 255, 255, 1);
      }

      rpgcode.drawText(x2, y2, text, canvas);
   }

   /**
    * Draws the state of the character's health to the bottom left of the screen. Each
    * heart container is drawn individually.
    */
   function drawHealth() {
      // Loop and draw the characters HP represented as hearts to the buffer canvas.
      for (var i = 1; i < characterMaxHp + 1; i++) {
         if (i < characterHp + 1) {
            rpgcode.drawOntoCanvas(lifeIcon, i * 32, 430, 32, 32, buffer);
         }
         if (i > characterHp) {
            rpgcode.drawOntoCanvas(emptyLifeIcon, i * 32, 430, 32, 32, buffer);
         }
      }
   }

   /**
    * Draws the battle system's state to the screen which includes the menu state 
    * and the character's current health.
    */
   function drawState() {
      if (currentTurn === Turn.CHARACTER) {
         rpgcode.log("Render character turn.");
         rpgcode.drawOntoCanvas(heroProfile, menuPositionX, menuPositionY - 24, 54, 24, buffer);
         rpgcode.setImage("battle_menu.png", 0, 0, 125, 300, battleMenu);

         if (currentSubmenu === Submenu.DEFAULT) {
            rpgcode.log("Select submenu 0.");
            drawMenuItem(12, 22, 10, 20, "Attack", battleMenu, menuChoice === 1);
         }
      } else if (currentTurn === Turn.SWORD) {
         rpgcode.log("Render sword turn.");
         rpgcode.drawOntoCanvas(swordProfile, menuPositionX, menuPositionY - 24, 54, 24, buffer);
         rpgcode.setImage("battle_menu.png", 0, 0, 125, 300, battleMenu);

         drawMenuItem(12, 22, 10, 20, "Provoke", battleMenu, menuChoice === 1);
         drawMenuItem(12, 42, 10, 40, "Fire", battleMenu, menuChoice === 2);
      }

      // Render the battle system.
      drawHealth();
      rpgcode.drawOntoCanvas(battleMenu, menuPositionX, menuPositionY, 125, 300, buffer);
      rpgcode.renderNow(buffer);
   }

   /* ***************************************************************************************
    * Update Logic: Checking for end conditions, carrying out a enemy turns.
    *************************************************************************************** */

   /**
    * Checks for the following end conditions:
    *    1. Enemy HP is is less than 1
    *    2. Character HP is less than 1
    *    
    * If one of the above end conditions are met then the program ends with either the
    * enemy's defeat, or the character's defeat which leads to a gameover.
    * 
    * If neither of those conditions are met it simply updates the battle systems 
    * state on screen and checks to see if it is the enemy's turn.
    */
   function update() {
      rpgcode.log("Calling update, enemyHp=[" + enemyHp + "], characterHp=[" + characterHp + "]");

      if (enemyHp < 1) {
         // Play the enemy die animation, after it is complete end the battle.
         rpgcode.animateSprite("evil-eye-1", "DIE", function() {
            rpgcode.log("Enemy has been defeated!");
            rpgcode.setGlobal("haveBattled", true);
            rpgcode.clearCanvas(buffer);
            rpgcode.destroySprite("evil-eye-1");
            rpgcode.stopSound("Battle.wav");
            rpgcode.playSound("Tower.wav", true);
            rpgcode.setCharacterStance("Hero", "NORTH");
            rpgcode.endProgram();
         });
      } else if (characterHp < 1) {
         // The character is dead end the game.
         rpgcode.log("Game over punk...");
         rpgcode.clearCanvas(buffer);
         rpgcode.stopSound("Battle.wav");
         rpgcode.endProgram("GameOver.js"); // Pass the program we want to run next.
      } else {
         drawState();

         // Check if we need to do the enemy turn.
         if (currentTurn === Turn.ENEMY) {
            doEnemyTurn();
         }
      }
   }

   /* ***************************************************************************************
    * Turn Logic: Playing attack and defend animations, reducing health etc.
    *************************************************************************************** */

   /**
    * Carries out the character's turn by playing the character attack animation, enemy defend animation,
    * setting the next turn to the sword's. It then advances the battle with a call to update().
    */
   function doCharacterTurn() {
      rpgcode.log("Calling doCharacterTurn, enemyHp=[" + enemyHp + "], characterHp=[" + characterHp + "]");

      if (currentSubmenu === Submenu.DEFAULT) {
         if (menuChoice === 1) {
            rpgcode.log("Character attack.");
            rpgcode.animateCharacter("Hero", "ATTACK_NORTH");
            rpgcode.animateSprite("evil-eye-1", "DEFEND", function() {
               enemyHp -= characterAttackPower;
               currentTurn = Turn.SWORD;
               menuChoice = 1;

               update();
            });
         }
      }
   }

   /**
    * Carries out the sword's turn based on the selection the sword will either attempt to
    * distract the enemy or burn it. It then advances the battle with a call to update().
    */
   function doSwordTurn() {
      rpgcode.log("Calling doSwordTurn, enemyHp=[" + enemyHp + "], characterHp=[" + characterHp + "]");

      if (menuChoice === 1) {
         rpgcode.setColor(255, 255, 255, 1);

         var tmp = Math.floor(Math.random() * (4)) + 1;
         rpgcode.log("sd tmp " + tmp);
         rpgcode.showDialog(swordDialog[tmp]);

         tmp = Math.floor(Math.random() * (10)) + 1;
         if (tmp > 8) {
            currentTurn = Turn.CHARACTER;
            rpgcode.showDialog("Enemy gets angry and misses his turn.");
         } else {
            currentTurn = Turn.ENEMY;
         }

         rpgcode.delay(3000, function() {
            rpgcode.log("Clearing dialog box.");
            rpgcode.clearDialog();
            menuChoice = 1;

            update();
         });
      } else if (menuChoice === 2) {
         rpgcode.animateSprite("evil-eye-1", "BURN", function() {
            enemyHp -= 1;
            currentTurn = Turn.ENEMY;
            menuChoice = 1;

            update();
         });
      }
   }

   /**
    * Carries out the enemy's turn by playing the character enemy attack animation, the character
    * defend animation. It then advances the battle with a call to update().
    */
   function doEnemyTurn() {
      rpgcode.log("Calling doEnemyTurn, enemyHp=[" + enemyHp + "], characterHp=[" + characterHp + "]");

      rpgcode.animateSprite("evil-eye-1", "ATTACK");
      rpgcode.animateCharacter("Hero", "DEFEND", function() {
         var def = 0;
         characterHp -= enemyAttackPower;
         characterHp += def;
         currentTurn = Turn.CHARACTER;

         update();
      });
   }

}