/*
 * Copyright (c) 2017, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode */

// Canvas IDs.
var buffer = "buffer";
var lifeIcon = "life_icon";
var emptyLifeIcon = "empty_life_icon";

// Player stats.
var player = rpgcode.getCharacter();
var playerHp = player.health;
var playerMaxHp = player.maxHealth;

var hudState = rpgcode.getGlobal("hudState");

if (hudState === undefined) {
    // Assets to load up for the HUD.
    var assets = {
        "images": [
            "life.png",
            "emptylife.png"
        ]
    };

    rpgcode.loadAssets(assets, function () {
        console.log("Assets loaded.");

        // Smaller canvases that make up the HUD.
        rpgcode.createCanvas(32, 32, lifeIcon);
        rpgcode.createCanvas(32, 32, emptyLifeIcon);

        // Canvas to draw onto.
        rpgcode.createCanvas(640, 480, buffer);

        // Set the images on the smaller canvases.
        rpgcode.setImage("life.png", 0, 0, 32, 32, lifeIcon);
        rpgcode.setImage("emptylife.png", 0, 0, 32, 32, emptyLifeIcon);

        setState(0);
    });
} else {
    update();
}

function setState(hp) {
    // Create the state object.
    var hudState = {
        lastPlayerHp: hp
    };

    rpgcode.setGlobal("hudState", hudState);
}

function drawHealth() {
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
    var redrawHud = rpgcode.getGlobal("redrawHud");
    
    if (hudState.lastPlayerHp !== playerHp || redrawHud) {
        rpgcode.setGlobal("redrawHud", false);
        
        drawHealth();
        rpgcode.renderNow(buffer);
        setState(playerHp);
    }
}