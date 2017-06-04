/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * An example of how to show a basic menu system on the screen using
 * images on canvases and sub-canvases. The player can navigate the
 * menu system using the registered arrow keys.
 */
var assets = {
   "audio": {
      "item": "item.wav"
   },
   "images": [
      "menu_bg.png",
      "item_box.png",
      "fire_orb.png",
      "ice_orb.png",
      "lightning_orb.png",
      "time_orb.png",
      "earth_orb.png",
      "cure_orb.png",
      "pure_orb.png",
      "strength_orb.png",
      "health_potion.png",
      "mana_potion.png",
      "pure_potion.png",
      "life.png",
      "manasphere.png",
      "emptylife.png",
      "emptymana.png",
      "herostance.png",
      "item_box_selection.png",
      "hpotion_descr_box.png",
      "mpotion_descr_box.png",
      "epotion_descr_box.png",
      "sword_descr_box.png",
      "fireorb_descr_box.png",
      "iceorb_descr_box.png",
      "litorb_descr_box.png",
      "timeorb_descr_box.png",
      "cureorb_descr_box.png",
      "pureorb_descr_box.png",
      "strengthorb_descr_box.png",
      "defenceorb_descr_box.png",
      "sword_item_box.png"
   ]
};

// The keys that we will be listening to.
var keys = ["LEFT_ARROW", "RIGHT_ARROW", "UP_ARROW", "DOWN_ARROW", "ENTER", "ESC"];

// Controlling variables.
var swordactive = true;
var cho = 1;
var Hpotion = 1;
var Mpotion = 1;
var Ppotion = 1;
var fireorb = true;
var iceorb = true;
var lightningorb = true;
var timeorb = true;
var cureorb = true;
var pureorb = true;
var strengthorb = true;
var earthorb = true;
var maxhp = 5;
var hp = 4;
var maxmp = 5;
var mp = 3;

rpgcode.loadAssets(assets, function() {
   // Smaller canvases that make up the menu system.
   rpgcode.createCanvas(640, 480, "menu_bg");
   rpgcode.createCanvas(100, 100, "empty_tile");
   rpgcode.createCanvas(100, 100, "ice_orb");
   rpgcode.createCanvas(100, 100, "fire_orb");
   rpgcode.createCanvas(100, 100, "lightning_orb");
   rpgcode.createCanvas(100, 100, "time_orb");
   rpgcode.createCanvas(100, 100, "earth_orb");
   rpgcode.createCanvas(100, 100, "strength_orb");
   rpgcode.createCanvas(100, 100, "cure_orb");
   rpgcode.createCanvas(100, 100, "pure_orb");
   rpgcode.createCanvas(100, 100, "health_potion");
   rpgcode.createCanvas(100, 100, "mana_potion");
   rpgcode.createCanvas(100, 100, "pure_potion");
   rpgcode.createCanvas(32, 32, "life_icon");
   rpgcode.createCanvas(32, 32, "mana_icon");
   rpgcode.createCanvas(32, 32, "emptylife_icon");
   rpgcode.createCanvas(32, 32, "emptymana_icon");
   rpgcode.createCanvas(100, 100, "sword_icon");

   rpgcode.createCanvas(223, 374, "herostance");
   rpgcode.createCanvas(100, 100, "selected_box");
   rpgcode.createCanvas(210, 100, "hpotion_description");
   rpgcode.createCanvas(210, 100, "mpotion_description");
   rpgcode.createCanvas(210, 100, "epotion_description");
   rpgcode.createCanvas(210, 100, "sword_description");
   rpgcode.createCanvas(210, 100, "fireorb_description");
   rpgcode.createCanvas(210, 100, "iceorb_description");
   rpgcode.createCanvas(210, 100, "litorb_description");
   rpgcode.createCanvas(210, 100, "timeorb_description");
   rpgcode.createCanvas(210, 100, "cureorb_description");
   rpgcode.createCanvas(210, 100, "pureorb_description");
   rpgcode.createCanvas(210, 100, "strengthorb_description");
   rpgcode.createCanvas(210, 100, "defenceorb_description");

   // Canvas to draw onto.
   rpgcode.createCanvas(640, 480, "buffer");

   // Set the images on the smaller canvases.
   rpgcode.setImage("menu_bg.png", 0, 0, 640, 480, "menu_bg");
   rpgcode.setImage("item_box.png", 0, 0, 100, 100, "empty_tile");
   rpgcode.setImage("fire_orb.png", 0, 0, 100, 100, "fire_orb");
   rpgcode.setImage("ice_orb.png", 0, 0, 100, 100, "ice_orb");
   rpgcode.setImage("lightning_orb.png", 0, 0, 100, 100, "lightning_orb");
   rpgcode.setImage("time_orb.png", 0, 0, 100, 100, "time_orb");
   rpgcode.setImage("earth_orb.png", 0, 0, 100, 100, "earth_orb");
   rpgcode.setImage("cure_orb.png", 0, 0, 100, 100, "cure_orb");
   rpgcode.setImage("pure_orb.png", 0, 0, 100, 100, "pure_orb");
   rpgcode.setImage("strength_orb.png", 0, 0, 100, 100, "strength_orb");
   rpgcode.setImage("health_potion.png", 0, 0, 100, 100, "health_potion");
   rpgcode.setImage("mana_potion.png", 0, 0, 100, 100, "mana_potion");
   rpgcode.setImage("pure_potion.png", 0, 0, 100, 100, "pure_potion");
   rpgcode.setImage("life.png", 0, 0, 32, 32, "life_icon");
   rpgcode.setImage("manasphere.png", 0, 0, 32, 32, "mana_icon");
   rpgcode.setImage("emptylife.png", 0, 0, 32, 32, "emptylife_icon");
   rpgcode.setImage("emptymana.png", 0, 0, 32, 32, "emptymana_icon");
   rpgcode.setImage("herostance.png", 0, 0, 223, 374, "herostance");
   rpgcode.setImage("item_box_selection.png", 0, 0, 100, 100, "selected_box");
   rpgcode.setImage("hpotion_descr_box.png", 0, 0, 210, 100, "hpotion_description");
   rpgcode.setImage("mpotion_descr_box.png", 0, 0, 210, 100, "mpotion_description");
   rpgcode.setImage("epotion_descr_box.png", 0, 0, 210, 100, "epotion_description");
   rpgcode.setImage("sword_descr_box.png", 0, 0, 210, 100, "sword_description");
   rpgcode.setImage("fireorb_descr_box.png", 0, 0, 210, 100, "fireorb_description");
   rpgcode.setImage("iceorb_descr_box.png", 0, 0, 210, 100, "iceorb_description");
   rpgcode.setImage("litorb_descr_box.png", 0, 0, 210, 100, "litorb_description");
   rpgcode.setImage("timeorb_descr_box.png", 0, 0, 210, 100, "timeorb_description");
   rpgcode.setImage("cureorb_descr_box.png", 0, 0, 210, 100, "cureorb_description");
   rpgcode.setImage("pureorb_descr_box.png", 0, 0, 210, 100, "pureorb_description");
   rpgcode.setImage("strengthorb_descr_box.png", 0, 0, 210, 100, "strengthorb_description");
   rpgcode.setImage("defenceorb_descr_box.png", 0, 0, 210, 100, "defenceorb_description");
   rpgcode.setImage("sword_item_box.png", 0, 0, 100, 100, "sword_icon");

   // Listen to these keys.
   rpgcode.registerKeyDown(keys[0], handleLeftArrow);
   rpgcode.registerKeyDown(keys[1], handleRightArrow);
   rpgcode.registerKeyDown(keys[2], handleUpArrow);
   rpgcode.registerKeyDown(keys[3], handleDownArrow);
   rpgcode.registerKeyDown(keys[4], handleEnter);
   rpgcode.registerKeyDown(keys[5], handleEsc);

   render();
});

function render() {
   // Draw the menu background.
   rpgcode.drawOntoCanvas("menu_bg", 0, 0, 640, 480, "buffer");

   //--sword_slot
   rpgcode.drawOntoCanvas("empty_tile", 25, 25, 100, 100, "buffer");
   if (swordactive === true) {
      rpgcode.drawOntoCanvas("sword_icon", 25, 25, 100, 100, "buffer");
   }
   if (cho === 1) {
      rpgcode.drawOntoCanvas("selected_box", 25, 25, 100, 100, "buffer");
   }

   //--health potion slot
   if (Hpotion === 0) {
      rpgcode.drawOntoCanvas("empty_tile", 25, 135, 100, 100, "buffer");
   }
   if (Hpotion > 0) {
      rpgcode.drawOntoCanvas("health_potion", 25, 135, 100, 100, "buffer");
   }
   if (cho === 4) {
      rpgcode.drawOntoCanvas("selected_box", 25, 135, 100, 100, "buffer");
   }
   if (Hpotion > 0) {
      rpgcode.drawText(100, 145, Hpotion, "buffer");
   }

   //--mana potion slot
   if (Mpotion === 0) {
      rpgcode.drawOntoCanvas("empty_tile", 25, 245, 100, 100, "buffer");
   }
   if (Mpotion > 0) {
      rpgcode.drawOntoCanvas("mana_potion", 25, 245, 100, 100, "buffer");
   }
   if (cho === 7) {
      rpgcode.drawOntoCanvas("selected_box", 25, 245, 100, 100, "buffer");
   }
   if (Mpotion > 0) {
      rpgcode.drawText(100, 255, Mpotion, "buffer");
   }

   //--pure potion slot
   if (Ppotion === 0) {
      rpgcode.drawOntoCanvas("empty_tile", 25, 355, 100, 100, "buffer");
   }
   if (Ppotion > 0) {
      rpgcode.drawOntoCanvas("pure_potion", 25, 355, 100, 100, "buffer");
   }
   if (cho === 10) {
      rpgcode.drawOntoCanvas("selected_box", 25, 355, 100, 100, "buffer");
   }
   if (Ppotion > 0) {
      rpgcode.drawText(100, 365, Ppotion, "buffer");
   }

   //--fire orb slot
   if (!fireorb) {
      rpgcode.drawOntoCanvas("empty_tile", 160, 25, 100, 100, "buffer");
   }
   if (fireorb) {
      rpgcode.drawOntoCanvas("fire_orb", 160, 25, 100, 100, "buffer");
   }
   if (cho === 2) {
      rpgcode.drawOntoCanvas("selected_box", 160, 25, 100, 100, "buffer");
   }

   //--ice orb slot
   if (!iceorb) {
      rpgcode.drawOntoCanvas("empty_tile", 270, 25, 100, 100, "buffer");
   }
   if (iceorb) {
      rpgcode.drawOntoCanvas("ice_orb", 270, 25, 100, 100, "buffer");
   }
   if (cho === 3) {
      rpgcode.drawOntoCanvas("selected_box", 270, 25, 100, 100, "buffer");
   }

   //--lightning orb slot
   if (!lightningorb) {
      rpgcode.drawOntoCanvas("empty_tile", 160, 135, 100, 100, "buffer");
   }
   if (lightningorb) {
      rpgcode.drawOntoCanvas("lightning_orb", 160, 135, 100, 100, "buffer");
   }
   if (cho === 5) {
      rpgcode.drawOntoCanvas("selected_box", 160, 135, 100, 100, "buffer");
   }

   //--time orb slot
   if (!timeorb) {
      rpgcode.drawOntoCanvas("empty_tile", 270, 135, 100, 100, "buffer");
   }
   if (timeorb) {
      rpgcode.drawOntoCanvas("time_orb", 270, 135, 100, 100, "buffer");
   }
   if (cho === 6) {
      rpgcode.drawOntoCanvas("selected_box", 270, 135, 100, 100, "buffer");
   }

   //--cure orb slot
   if (!cureorb) {
      rpgcode.drawOntoCanvas("empty_tile", 160, 245, 100, 100, "buffer");
   }
   if (cureorb) {
      rpgcode.drawOntoCanvas("cure_orb", 160, 245, 100, 100, "buffer");
   }
   if (cho === 8) {
      rpgcode.drawOntoCanvas("selected_box", 160, 245, 100, 100, "buffer");
   }

   //--pure orb slot
   if (!pureorb) {
      rpgcode.drawOntoCanvas("empty_tile", 270, 245, 100, 100, "buffer");
   }
   if (pureorb) {
      rpgcode.drawOntoCanvas("pure_orb", 270, 245, 100, 100, "buffer");
   }
   if (cho === 9) {
      rpgcode.drawOntoCanvas("selected_box", 270, 245, 100, 100, "buffer");
   }

   //--strength orb slot
   if (!strengthorb) {
      rpgcode.drawOntoCanvas("empty_tile", 160, 355, 100, 100, "buffer");
   }
   if (strengthorb) {
      rpgcode.drawOntoCanvas("strength_orb", 160, 355, 100, 100, "buffer");
   }
   if (cho === 11) {
      rpgcode.drawOntoCanvas("selected_box", 160, 355, 100, 100, "buffer");
   }

   //--earth orb slot
   if (!earthorb) {
      rpgcode.drawOntoCanvas("empty_tile", 270, 355, 100, 100, "buffer");
   }
   if (earthorb) {
      rpgcode.drawOntoCanvas("earth_orb", 270, 355, 100, 100, "buffer");
   }
   if (cho === 12) {
      rpgcode.drawOntoCanvas("selected_box", 270, 355, 100, 100, "buffer");
   }

   //--profile pic
   rpgcode.drawOntoCanvas("herostance", 402, 22, 223, 374, "buffer");

   //--health icons
   for (var tmp = 1; tmp < maxhp + 1; tmp++) {
      if (tmp < hp + 1) {
         rpgcode.drawOntoCanvas("life_icon", 375 + tmp * 32, 395, 32, 32, "buffer");
      }
      if (tmp > hp) {
         rpgcode.drawOntoCanvas("emptylife_icon", 375 + tmp * 32, 395, 32, 32, "buffer");
      }
   }
   //--mana icons
   for (tmp = 1; tmp < maxmp + 1; tmp++) {
      if (tmp < mp + 1) {
         rpgcode.drawOntoCanvas("mana_icon", 375 + tmp * 32, 430, 32, 32, "buffer");
      }
      if (tmp > mp) {
         rpgcode.drawOntoCanvas("emptymana_icon", 375 + tmp * 32, 430, 32, 32, "buffer");
      }
   }

   //--Description boxes
   if (cho === 1 && swordactive === true) {
      rpgcode.drawOntoCanvas("sword_description", 125, 25, 210, 100, "buffer");
   }
   if (cho === 2 && fireorb === true) {
      rpgcode.drawOntoCanvas("fireorb_description", 260, 25, 210, 100, "buffer");
   }
   if (cho === 3 && iceorb === true) {
      rpgcode.drawOntoCanvas("iceorb_description", 370, 25, 210, 100, "buffer");
   }
   if (cho === 4 && Hpotion > 0) {
      rpgcode.drawOntoCanvas("hpotion_description", 125, 135, 210, 100, "buffer");
   }
   if (cho === 5 && lightningorb === true) {
      rpgcode.drawOntoCanvas("litorb_description", 260, 135, 210, 100, "buffer");
   }
   if (cho === 6 && timeorb === true) {
      rpgcode.drawOntoCanvas("timeorb_description", 370, 135, 210, 100, "buffer");
   }
   if (cho === 7 && Mpotion > 0) {
      rpgcode.drawOntoCanvas("mpotion_description", 125, 245, 210, 100, "buffer");
   }
   if (cho === 8 && cureorb === true) {
      rpgcode.drawOntoCanvas("cureorb_description", 260, 245, 210, 100, "buffer");
   }
   if (cho === 9 && pureorb === true) {
      rpgcode.drawOntoCanvas("pureorb_description", 370, 245, 210, 100, "buffer");
   }
   if (cho === 10 && Ppotion > 0) {
      rpgcode.drawOntoCanvas("epotion_description", 125, 355, 210, 100, "buffer");
   }
   if (cho === 11 && strengthorb === true) {
      rpgcode.drawOntoCanvas("strengthorb_description", 260, 355, 210, 100, "buffer");
   }
   if (cho === 12 && earthorb === true) {
      rpgcode.drawOntoCanvas("defenceorb_description", 370, 355, 210, 100, "buffer");
   }

   // Render the menu.
   rpgcode.renderNow("buffer");
}

function checkChoice() {
   if (cho < 1) {
      cho += 3;
   }
   if (cho > 12) {
      cho -= 3;
   }
}

function handleUpArrow() {
   cho -= 3;
   checkChoice();
   render();

   rpgcode.registerKeyDown("UP_ARROW", handleUpArrow);
}

function handleDownArrow() {
   cho += 3;
   checkChoice();
   render();

   rpgcode.registerKeyDown("DOWN_ARROW", handleDownArrow);
}

function handleRightArrow() {
   cho += 1;
   checkChoice();
   render();

   rpgcode.registerKeyDown("RIGHT_ARROW", handleRightArrow);
}

function handleLeftArrow() {
   cho -= 1;
   checkChoice();
   render();

   rpgcode.registerKeyDown("LEFT_ARROW", handleLeftArrow);
}

function handleEnter() {
   if (cho === 4 && Hpotion > 0 && hp < maxhp) {
      Hpotion = Hpotion - 1;
      hp = hp + 3;
      rpgcode.playSound("item", false);
   }
   if (cho === 7 && Mpotion > 0 && mp < maxmp) {
      Mpotion = Mpotion - 1;
      mp = mp + 3;
      rpgcode.playSound("item", false);
   }
   if (cho === 8 && mp > 0 && hp < maxhp && cureorb === true) {
      mp = mp - 1;
      hp = hp + 4;
      rpgcode.playSound("item", false);
   }
   if (cho === 10 && Ppotion > 0) {
      if (mp < maxmp || hp < maxhp) {
         Ppotion = Ppotion - 1;
         hp = hp + 3;
         mp = mp + 3;
         rpgcode.playSound("item", false);
      }
   }
   if (hp > maxhp) {
      hp = maxhp;
   }
   if (mp > maxmp) {
      mp = maxmp;
   }

   render();

   rpgcode.registerKeyDown("ENTER", handleEnter);
}

function handleEsc() {
   // Clear it but leave the canvases in memory.
   rpgcode.clearCanvas("buffer");

   // Stop listening to menu keys.
   keys.forEach(function(key) {
      rpgcode.unregisterKeyDown(key);
   });

   rpgcode.removeAssets(assets);

   // Let the HUD know it needs to redraw itself.
   rpgcode.setGlobal("redrawHud", true);

   rpgcode.endProgram();
}