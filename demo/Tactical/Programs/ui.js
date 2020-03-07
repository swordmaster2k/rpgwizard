/**
 * This module contains various custom UI functions used
 * throught the game.
 */
let UI = function() {

   var presentChoice = async function(choice1, choice2) {
      let button1, button2;
      let promise = new Promise(function(resolve, reject) {
         button1 = gui.createButton({
            id: "choice1.canvas",
            x: 0,
            y: 100,
            text: choice1,
            onClick: function() {resolve(choice1);}
         });
         button2 = gui.createButton({
            id: "choice2.canvas",
            x: 0,
            y: 150,
            width: button1.width,
            height: button1.height,
            text: choice2,
            onClick: function() {resolve(choice2);}
         });
      });
      let centerX = Math.round((rpgcode.getViewport().width / 2) - (button1.width / 2));
      let centerY = Math.round((rpgcode.getViewport().height / 2) - (button1.height / 2));
      button1.setLocation(centerX, centerY - (button1.height));
      button2.setLocation(centerX, centerY + (button1.height));
      button1.setVisible(true);
      button2.setVisible(true);
      
      gui.listenToMouse(true);
      const result = await promise;

      button1.destroy();
      button2.destroy();

      return result;
   };

   return {
      name: "UI",
      presentChoice: presentChoice
   };

}();