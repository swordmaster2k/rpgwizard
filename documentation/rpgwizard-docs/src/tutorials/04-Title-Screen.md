# Summary
At the moment the default title screen bundled with the RPGWizard is very simple, and only supports specifying the background image, and the music to play. It has a single menu option "New Game" that can be used to delay the start of your game, in the demo project it triggers the story introduction.

![](images/default_systems/02_title_screen/images/1.gif)

# Steps
To use the default title screen in your own project you will only need a few lines of code, you simply need to load the title screen program and its dependencies. After that you can specify what background image and music you want to use, and the title screen will deal with loading and displaying them. In the table below you will find all the key inputs that the title screen listens for:

* **Enter**
  * Fires the callback function that was supplied to the title screens own show function, ending the display of the title screen.

You learn more about how to use and configure the title screen from its API Reference. An example of how the title screen above was created can be seen in the following code snippet:

```javascript
// Declare the program assets we will need to load.
var assets = {
  "programs": [
      "defaults/gui.js",
      "defaults/titleScreen.js"
  ]
};

rpgcode.loadAssets(assets, function() {
   
   // Specify the config options for the title screen to use.
   var config = {
   "backgroundImage": "startscreen.png", 
   "titleScreenMusic": "intro.ogg"
   };
   
   // Show the title screen.
   titleScreen.show(config, function() {
      // The player pressed "ENTER" ending the titlescreen.
      // Let's continue with the rest of the game.
   }); 
});
```