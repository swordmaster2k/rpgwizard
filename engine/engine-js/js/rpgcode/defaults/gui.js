/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/* global rpgcode */

var gui = new GUI();

function GUI() {
    
}

GUI.prototype.getBackgroundGradient = function() {
   return ["rgba(74, 121, 90, 1)", "rgba(0, 0, 0, 1)"];
};

GUI.prototype.getFontSize = function() {
    return 20;
};

GUI.prototype.getFontFamily = function() {
    return "Lucida Console";
};

GUI.prototype.getFont = function() {
    return this.getFontSize() + "px " + this.getFontFamily();
};
 
GUI.prototype.prepareTextColor = function() {
   rpgcode.setColor(255, 255, 255, 1.0);
};

GUI.prototype.prepareStatBarColor = function() {
   rpgcode.setColor(255, 0, 0, 1.0);
};

GUI.prototype.prepareSelectionColor = function() {
   rpgcode.setColor(255, 255, 255, 0.3);
};

