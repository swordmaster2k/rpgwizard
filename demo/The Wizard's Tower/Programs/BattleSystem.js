/*
 * Copyright (c) 2017, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode */

// Assets to load up for the battle system.
var assets = {
    "audio": {
        "Battle.mp3": "Battle.mp3"
    },
    "images": [
        "hero_battle_profile.png",
        "sword_battle_profile.png",
        "life.png",
        "manasphere.png",
        "emptylife.png",
        "emptymana.png",
        "battle_menu.png"
    ]
};

// The keys that we will be listening to.
var keys = ["UP_ARROW", "DOWN_ARROW", "ENTER"];

// Turn variables.
var Turn = {
    PLAYER: 1,
    SWORD: 2,
    ENEMY: 3
};
var currentTurn = Turn.PLAYER;

// Canvas IDs.
var buffer = "buffer";
var heroMenu = "hero_menu";
var swordMenu = "sword_menu";
var battleMenu = "battle_menu";
var heroProfile = "hprofile";
var swordProfile = "sprofile";
var lifeIcon = "life_icon";
var manaIcon = "mana_icon";
var emptyLifeIcon = "empty_life_icon";
var emptyManaIcon = "empty_mana_icon";

// Menu postion.
var menuPositionX = 515;
var menuPositionY = 180;

// Menu selection status.
var Submenu = {
    DEFAULT: 1,
    ITEMS: 2
};
var currentSubmenu = Submenu.DEFAULT;
var menuChoice = 1;

// Enemy stats.
var enemyHp = 3;
var enemyAttackPower = 1;

// Player stats.
var playerMaxHp = 5;
var playerHp = 4;
var playerMaxMp = 5;
var playerMp = 3;
var playerAttackPower = 1;

// Inventory stats.
var healthPotions = 0;
var manaPotions = 0;
var powerPotions = 0;
var fireOrb = true;

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

    rpgcode.setGlobal("haveBattled", false);

    rpgcode.loadAssets(assets, function () {
        console.log("Assets loaded.");

        // Smaller canvases that make up the battle system.
        rpgcode.createCanvas(80, 90, heroMenu);
        rpgcode.createCanvas(80, 190, swordMenu);
        rpgcode.createCanvas(125, 300, battleMenu);
        rpgcode.createCanvas(54, 24, heroProfile);
        rpgcode.createCanvas(54, 24, swordProfile);
        rpgcode.createCanvas(32, 32, lifeIcon);
        rpgcode.createCanvas(32, 32, manaIcon);
        rpgcode.createCanvas(32, 32, emptyLifeIcon);
        rpgcode.createCanvas(32, 32, emptyManaIcon);

        // Canvas to draw onto.
        rpgcode.createCanvas(640, 480, buffer);

        // Set the images on the smaller canvases.
        rpgcode.setImage("hero_battle_profile.png", 0, 0, 54, 24, heroProfile);
        rpgcode.setImage("sword_battle_profile.png", 0, 0, 54, 24, swordProfile);
        rpgcode.setImage("life.png", 0, 0, 32, 32, lifeIcon);
        rpgcode.setImage("manasphere.png", 0, 0, 32, 32, manaIcon);
        rpgcode.setImage("emptylife.png", 0, 0, 32, 32, emptyLifeIcon);
        rpgcode.setImage("emptymana.png", 0, 0, 32, 32, emptyManaIcon);

        // Listen to these keys.
        rpgcode.registerKeyDown(keys[0], handleUpArrow);
        rpgcode.registerKeyDown(keys[1], handleDownArrow);
        rpgcode.registerKeyDown(keys[2], handleEnter);

        rpgcode.setPlayerLocation("Hero", 192, 352, 0);
        rpgcode.setPlayerStance("Hero", "ATTACK_NORTH");

        rpgcode.stopSound();
        rpgcode.playSound("Battle.mp3", true);

        update();
    });

    function checkEndConditions() {
        console.log("enemyHp=[" + enemyHp + "], playerHp=[" + playerHp + "].");

        if (enemyHp < 1) {
            console.log("Enemy has been defeated!");
            rpgcode.animateItem(0, "DIE", function () {
                rpgcode.setGlobal("haveBattled", true);
                rpgcode.clearCanvas(buffer);
                rpgcode.destroyItem(0);
                rpgcode.stopSound("Battle.mp3");
                rpgcode.playSound("Tower.mp3", true);
                rpgcode.endProgram();
            });
        } else if (playerHp < 1) {
            console.log("Game over punk...");
            rpgcode.clearCanvas(buffer);
            rpgcode.stopSound("Battle.mp3");
            rpgcode.endProgram("GameOver.js"); // Pass the program we want to run next.
        }
    }

    function drawHealth() {
        for (var i = 1; i < playerMaxHp + 1; i++) {
            if (i < playerHp + 1) {
                rpgcode.drawOntoCanvas(lifeIcon, i * 32, 395, 32, 32, buffer);
            }
            if (i > playerHp) {
                rpgcode.drawOntoCanvas(emptyLifeIcon, i * 32, 395, 32, 32, buffer);
            }
        }
    }

    function drawMana() {
        for (var i = 1; i < playerMaxMp + 1; i++) {
            if (i < playerMp + 1) {
                rpgcode.drawOntoCanvas(manaIcon, i * 32, 430, 32, 32, buffer);
            }
            if (i > playerMp) {
                rpgcode.drawOntoCanvas(emptyManaIcon, i * 32, 430, 32, 32, buffer);
            }
        }
    }

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

    function update() {
        rpgcode.log("update");

        drawHealth();
        drawMana();

        if (currentTurn === Turn.PLAYER) {
            rpgcode.log("Render player turn.");
            rpgcode.drawOntoCanvas(heroProfile, menuPositionX, menuPositionY - 24, 54, 24, buffer);
            rpgcode.setImage("battle_menu.png", 0, 0, 125, 300, battleMenu);

            if (currentSubmenu === Submenu.DEFAULT) {
                rpgcode.log("Select submenu 0.");
                drawMenuItem(12, 22, 10, 20, "Attack", battleMenu, menuChoice === 1);
                drawMenuItem(12, 42, 10, 40, "Item", battleMenu, menuChoice === 2);
            } else if (currentSubmenu === Submenu.ITEMS) {
                rpgcode.log("Select submenu 2.");
                drawMenuItem(12, 22, 10, 20, "Health Potion " + healthPotions, battleMenu, menuChoice === 1);
                drawMenuItem(12, 42, 10, 40, "Mana Potion " + manaPotions, battleMenu, menuChoice === 2);
                drawMenuItem(12, 62, 10, 60, "Elixer " + powerPotions, battleMenu, menuChoice === 3);
                drawMenuItem(12, 82, 10, 80, "Return", battleMenu, menuChoice === 4);
            }
        } else if (currentTurn === Turn.SWORD) {
            rpgcode.log("Render sword turn.");
            rpgcode.drawOntoCanvas(swordProfile, menuPositionX, menuPositionY - 24, 54, 24, buffer);
            rpgcode.setImage("battle_menu.png", 0, 0, 125, 300, battleMenu);

            drawMenuItem(12, 22, 10, 20, "Provoke", battleMenu, menuChoice === 1);
            drawMenuItem(12, 42, 10, 40, "Fire", battleMenu, menuChoice === 2);
        }

        // Render the battle system.
        rpgcode.drawOntoCanvas(battleMenu, menuPositionX, menuPositionY, 125, 300, buffer);
        rpgcode.renderNow(buffer);

        checkEndConditions();
    }

    function checkMenuChoice() {
        console.log("Before check menuChoice=[" + menuChoice + "]");

        if (menuChoice < 1) {
            menuChoice = 1;
        }

        if (currentTurn === Turn.PLAYER) {
            if (menuChoice > 2 && currentSubmenu === Submenu.DEFAULT) {
                menuChoice = 2;
            }
            if (menuChoice > 4 && currentSubmenu === Submenu.ITEMS) {
                menuChoice = 4;
            }
        } else if (currentTurn === Turn.PLAYER) {
            if (menuChoice > 2) {
                menuChoice = 2;
            }
        }

        console.log("After check menuChoice=[" + menuChoice + "]");
    }

    function handleUpArrow() {
        if (currentTurn === Turn.PLAYER) {
            menuChoice -= 1;
            checkMenuChoice();
        } else if (currentTurn === Turn.SWORD) {
            menuChoice -= 1;
            checkMenuChoice();
        }

        if (currentTurn !== Turn.ENEMY) {
            update();
        }

        rpgcode.registerKeyDown("UP_ARROW", handleUpArrow);
    }

    function handleDownArrow() {
        if (currentTurn === Turn.PLAYER) {
            menuChoice += 1;
            checkMenuChoice();
        } else if (currentTurn === Turn.SWORD) {
            menuChoice += 1;
            checkMenuChoice();
        }

        if (currentTurn !== Turn.ENEMY) {
            update();
        }

        rpgcode.registerKeyDown("DOWN_ARROW", handleDownArrow);
    }

    function handleEnter() {
        rpgcode.log("turn=" + currentTurn + " menuChoice=" + menuChoice + " submenu=" + currentSubmenu);
        if (currentTurn === Turn.PLAYER) {
            doPlayerTurn();
        } else if (currentTurn === Turn.SWORD) {
            doSwordTurn();
        }

        if (currentTurn !== Turn.ENEMY) {
            update();
        }

        rpgcode.registerKeyDown("ENTER", handleEnter);
    }

    function doPlayerTurn() {
        console.log("doPlayerTurn");

        if (currentSubmenu === Submenu.DEFAULT) {
            if (menuChoice === 1) {
                rpgcode.log("Player attack.");
                rpgcode.animatePlayer("Hero", "ATTACK_NORTH");

                rpgcode.animateItem(0, "DEFEND");
                enemyHp -= playerAttackPower;
                currentTurn = Turn.SWORD;
            }

            if (menuChoice === 2) {
                rpgcode.log("Choose item menu.");
                currentSubmenu = Submenu.ITEMS;
            }
        } else if (currentSubmenu === Submenu.ITEMS) {
            if (menuChoice === 1) {
                rpgcode.log("Use healing potion.");
                if (healthPotions > 0) {
                    rpgcode.animatePlayer("Hero", "CURE");
                    playerHp += 3;

                    if (playerHp > playerMaxHp) {
                        playerHp = playerMaxHp;
                    }

                    healthPotions -= 1;
                    currentTurn = Turn.SWORD;
                }
            }

            if (menuChoice === 2) {
                rpgcode.log("Use mana potion.");
                if (manaPotions > 0) {
                    rpgcode.animatePlayer("Hero", "ETHER");
                    playerMp += 3;

                    if (playerMp > playerMaxMp) {
                        playerMp = playerMaxMp;
                    }

                    manaPotions -= 1;
                    currentTurn = Turn.SWORD;
                }
            }

            if (menuChoice === 3) {
                rpgcode.log("Use elixer.");
                if (powerPotions > 0) {
                    rpgcode.animatePlayer("Hero", "ELIXER");
                    playerHp = playerMaxHp;
                    playerMp = playerMaxMp;
                    powerPotions -= 1;
                    currentTurn = Turn.SWORD;
                }
            }

            if (menuChoice === 4) {
                rpgcode.log("Return from item menu.");
                currentSubmenu = Submenu.DEFAULT;
            }
        }

        menuChoice = 1;
    }

    function doSwordTurn() {
        console.log("doSwordTurn");

        if (menuChoice === 1) {
            rpgcode.setColor(255, 255, 255, 1);

            var tmp = Math.floor(Math.random() * (4)) + 1;
            console.log("sd tmp " + tmp);
            rpgcode.showDialog(swordDialog[tmp]);

            tmp = Math.floor(Math.random() * (10)) + 1;
            if (tmp > 8) {
                currentTurn = Turn.PLAYER;
                rpgcode.showDialog("Enemy gets angry and misses his turn.");
            } else {
                currentTurn = Turn.ENEMY;
            }
        } else if (menuChoice === 2) {
            rpgcode.animateItem(0, "BURN");
            enemyHp -= 1;
            currentTurn = Turn.ENEMY;
        }

        rpgcode.delay(3000, function () {
            console.log("Clearing dialog box.");
            rpgcode.clearDialog();

            if (currentTurn === Turn.ENEMY) {
                doEnemyTurn();
            }
        });
    }

    function doEnemyTurn() {
        console.log("doEnemyTurn");

        rpgcode.animateItem(0, "ATTACK");
        rpgcode.animatePlayer("Hero", "DEFEND");

        var def = 0;
        playerHp -= enemyAttackPower;
        playerHp += def;

        currentTurn = Turn.PLAYER;
        update();
    }
}
