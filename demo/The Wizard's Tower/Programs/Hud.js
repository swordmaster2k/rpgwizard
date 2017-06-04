/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
// Canvas IDs.
var buffer = "buffer";
var lifeIcon = "life_icon";
var emptyLifeIcon = "empty_life_icon";

// Player stats.
var player = rpgcode.getCharacter();
var playerHp = player.health;
var playerMaxHp = player.maxHealth;

// Global for storing the last hudState. 
// Used to check if anything has changed
// since the last redraw.
var hudState = rpgcode.getGlobal("hudState");

// Is this the first time we've drawn the HUD?
if (hudState === undefined) {
    // Assets to load up for the HUD.
    var assets = {
        "images": [
            "life.png",
            "emptylife.png"
        ]
    };

    rpgcode.loadAssets(assets, function () {
        rpgcode.log("Assets loaded.");

        // Smaller canvases that make up the HUD.
        rpgcode.createCanvas(32, 32, lifeIcon);
        rpgcode.createCanvas(32, 32, emptyLifeIcon);

        // Canvas to draw onto.
        rpgcode.createCanvas(640, 480, buffer);

        // Set the images on the smaller canvases.
        rpgcode.setImage("life.png", 0, 0, 32, 32, lifeIcon);
        rpgcode.setImage("emptylife.png", 0, 0, 32, 32, emptyLifeIcon);

        // Set the first HUD state.
        setState(0);
        rpgcode.setGlobal("redrawHud", true);
    });
} else {
    update();
}

function setState(hp) {
    // Record the current state of the HUD.
    var hudState = {
        lastPlayerHp: hp
    };

    rpgcode.setGlobal("hudState", hudState);
}

function drawHealth() {
    // Loop and draw the players HP represented as hearts to the buffer canvas.
    for (var i = 1; i < playerMaxHp + 1; i++) {
        if (i < playerHp + 1) {
            rpgcode.drawOntoCanvas(lifeIcon, i * 32, 430, 32, 32, buffer);
        }
        if (i > playerHp) {
            rpgcode.drawOntoCanvas(emptyLifeIcon, i * 32, 430, 32, 32, buffer);
        }
    }
}

function update() {
    // Get the last HUD state.
    var redrawHud = rpgcode.getGlobal("redrawHud");

    // Has anything actually changed that requires a redraw?
    if (hudState.lastPlayerHp !== playerHp || redrawHud) {
        rpgcode.log("Redrawing the hud!");
      
        rpgcode.setGlobal("redrawHud", false);
        
        drawHealth();
        rpgcode.renderNow(buffer);
        setState(playerHp);
    }
}