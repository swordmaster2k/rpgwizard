/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgwizard */

import { Core } from "./core.js";

// Default error handler.
window.onerror = function(msg, url, line) {
    console.error("Uncaught error msg=[%s], url=[%s], line=[%s]", msg, url, line);
    alert("Error message: " + msg + "\n\n" + "URL: " + url + "\n\n" + "Line Number: " + line + "\n\n");
    return true;
};

// Don't start the game until the user has interacted with the window
let play = document.getElementById("play");
let playGame = async function() {
    console.info("Starting the game...");
    play.style.visibility = "hidden";

    // REFACTOR: Use module instead...
    await Core.getInstance().main("game/default.game");
};
play.addEventListener("click", playGame, {once: true});
console.info("Awaiting user input...");