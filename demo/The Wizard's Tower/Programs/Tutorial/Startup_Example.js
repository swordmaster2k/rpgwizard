// Add the HUD as a runtime program to execute each frame
rpgcode.addRunTimeProgram("Hud.js");

// Register the menu key program
rpgcode.registerKeyDown("ENTER", function () {
   rpgcode.runProgram("MenuSystem.js");
}, true);

rpgcode.endProgram();