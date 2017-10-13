// Give the character the ability to use the sword.
rpgcode.registerKeyDown("SPACE", function () {
    rpgcode.runProgram("SwordSlash.js");
}, true);


rpgcode.endProgram();
