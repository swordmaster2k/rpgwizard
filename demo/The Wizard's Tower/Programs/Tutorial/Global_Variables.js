/*
 * Example of how to retrieve and set the state of a user defined
 * global variable.
 */

// Game assets used in this program
var assets = {
    "images": [
        "mwin_small.png",
        "sword_profile_1_small.png",
    ]
};

// Reference to the global name, saves on typos!
var swordActive = "swordActive";

// Load up the assets we need for this program.
rpgcode.loadAssets(assets, function() {
   // Set the dialog box background image, and profile picture.
   rpgcode.setDialogGraphics("sword_profile_1_small.png", "mwin_small.png");

   // Set the global variable "swordActive" to the boolean value of false.
   rpgcode.setGlobal(swordActive, false);

   // Read the value of the global we just set.
   var isSwordActive = rpgcode.getGlobal("swordActive");

   // This will always be false.
   if (isSwordActive === false) {
      rpgcode.showDialog("You do not have the sword yet!");

      // Wait 3 seconds then change the value.
      rpgcode.delay(3000, function() {
         // Set the global variable "swordActive" to the boolean value of true.
         rpgcode.setGlobal(swordActive, true);

         // Read the updated value of the global.
         isSwordActive = rpgcode.getGlobal(swordActive);

         // This will always be true.
         if (isSwordActive === true) {
            rpgcode.showDialog("You have the sword now!");
         }

         rpgcode.delay(3000, function() {
            rpgcode.clearDialog();
            rpgcode.endProgram();
         });
      });
   }
});