/*
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode */

var canvas = "flashingText";

var assets = {
    "audio": {
        "intro": "intro.mp3"
    },
    "images": [
        "block.png",
        "mwin_small.png",
        "sword_profile_1_small.png",
        "startscreen.png"
    ]
};

// FYI: https://en.wikibooks.org/wiki/Data_Structures/Stacks_and_Queues
var messageStack = new Array();
messageStack.push("Without delay he grabs his armour and heads to the tower.....");
messageStack.push("sister of a knight.");
messageStack.push("And this time is no different, apart from the fact that the girl that is missing is the");
messageStack.push("And the hundred years is up.");
messageStack.push("Every hundred years somebody always goes missing from the village.");
messageStack.push("grey plains. A reminder to not leave the village after dark.");
messageStack.push("For as long as the villagers can remember the wizard's tower has stood upon the");

var message = "";

var x = 20;
var y = 20;

var alpha = 0.0;

var flash = true;
var isShowingIntro = false;

rpgcode.loadAssets(assets, function () {
    rpgcode.setGlobal("swordactive", false);
    rpgcode.setDialogGraphics("sword_profile_1_small.png", "mwin_small.png");

    rpgcode.setImage("startscreen.png", 0, 0, 640, 480);
    rpgcode.setColor(0, 0, 0, 1.0);
    rpgcode.renderNow();

    rpgcode.createCanvas(640, 480, canvas);
    rpgcode.delay(500, flashText);

    rpgcode.playSound("intro", true);

    rpgcode.registerKeyDown("ENTER", showIntro);
});

function flashText() {
    rpgcode.log("flashText");

    if (flash) {
        rpgcode.drawText(270, 300, "PRESS ENTER", canvas);
        rpgcode.renderNow(canvas);
    } else {
        rpgcode.clearCanvas(canvas);
    }

    flash = !flash;

    if (!isShowingIntro) {
        rpgcode.delay(500, flashText);
    }
}

function showIntro() {
    rpgcode.unregisterKeyDown("ENTER");

    isShowingIntro = true;
    rpgcode.clearCanvas();
    rpgcode.clearCanvas(canvas);
    rpgcode.destroyCanvas(canvas);

    rpgcode.setColor(0, 0, 0, 1.0);
    rpgcode.fillRect(0, 0, 640, 480);
    rpgcode.renderNow();

    message = messageStack.pop();
    rpgcode.delay(150, fadeIn);
}

function fadeIn() {
    rpgcode.log("fadeIn");

    rpgcode.setColor(255, 255, 255, alpha);
    rpgcode.drawText(x, y, message);
    rpgcode.renderNow();

    alpha += 0.05;
    if (alpha >= 0.9) {
        if (messageStack.length > 0) {
            y += 20;
            alpha = 0.0;
            message = messageStack.pop();
            rpgcode.delay(150, fadeIn);
        } else {
            finish();
        }
    } else {
        rpgcode.delay(150, fadeIn);
    }
}

function finish() {
    rpgcode.log("finish");

    rpgcode.stopSound("intro");
    rpgcode.clearCanvas();
    rpgcode.removeAssets({
        "audio": {
            "intro": "intro.mp3"
        },
        "images": [
            "startscreen.png"
        ]});

    // Add the HUD as a runtime program to execute each frame.
    rpgcode.addRunTimeProgram("Hud.js");

    // Register the menu key program.
    rpgcode.registerKeyDown("ENTER", function () {
        rpgcode.runProgram("MenuSystem.js");
    }, true);

    rpgcode.endProgram();
}
