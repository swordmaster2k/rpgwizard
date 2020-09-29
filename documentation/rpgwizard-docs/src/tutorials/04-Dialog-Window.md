## Summary
Character dialog is key part of any entertaining RPG game, and it is important to keep that dialog as interesting as possible for the reader. The RPGWizard comes bundled with an animated typewriter style dialog window, which was inspired by the Zelda games that I played in my childhood. It supports fully animated text, typing sounds, profile pictures, and a flashing next indicator, all this functionality is completely free!

![](images/default_systems/03_dialog_window/images/1.gif)

## Steps
Using the default dialog is incredibly simple, and only requires a few lines of code to get your characters talking! For example, to create the dialog above you will just need to ensure that you have loaded the default programs into the engine. Then you can supply the text, and any other images you want to display. See the below code snippet from "The Wizard's Tower" for a better idea:

```javascript
// Declare the program assets we will need to load.
var assets = {
  "programs": [
      "defaults/gui.js",
      "defaults/dialog.js"
  ]
};


// Text used of the intro.
var introText =
"For as long as the villagers can remember the wizard's tower has stood upon the " +
"grey plains. A reminder to not leave the village after dark. " +
"Every hundred years somebody always goes missing from the village. " +
"And the hundred years is up. " +
"And this time is no different, apart from the fact that the girl that is missing is the " +
"sister of a knight. " +
"Without delay he grabs his armour and heads to the tower.....";

rpgcode.loadAssets(assets, async function() {

      // Specify the config for the dialog.
      var config = {
         position: "CENTER",
         advancementKey: "E",
         nextMarkerImage: "next_marker.png",
         profileImage: rpgcode.getCharacter().graphics["PROFILE"],
         typingSound: "typing_loop.wav",
         text: introText
      };

      // Show the intro dialog.
      await dialog.show(config);

      rpgcode.endProgram();

});
```

You can specify what key you would the player to have to press to advance the dialog using them
**advancementKey** option. You can also leave this out completely from the config if you want
the player to use mouse click as the advancement input.

The dialog.show function must be passed a configuration object, and a callback function to invoke when the dialog has finished. There are only a small number of configuration options that can be set, all of them are optional excluding the text to display (for obvious reasons). You learn more about how to use and configure the dialog box from its API Reference. Below you will find a table containing a list of all the configuration options that can be set, and what they will do:

| PARAMETER       | DESCRIPTION                                                                                                                                   | REQUIRED | EXAMPLE VALUES                             | DEFAULT VALUE |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------------------------|----------|--------------------------------------------|---------------|
| text            | The actual text to show in the dialog, will be automatically word wrapped, and split across several boxes if it is too long for a single box. | Yes      | Hello world!                               | N/A           |
| position        | Specifies the general position the dialog box will appear at on the screen.                                                                   | No       | TOP, CENTER, BOTTOM                        | BOTTOM        |
| advancementKey  | If set the user must press the specified key on the keyboard to advance the dialog, otherwise it defaults to mouse click                      | No       | E, Q, X                                    | Left M. Click |
| nextMarkerImage | The image file to load and use as the dialog flashing next marker.                                                                            | No       | next_marker.png                            | N/A           |
| profileImage    | The image file to load and use as the speaker's profile image.                                                                                | No       | rpgcode.getCharacter().graphics["PROFILE"] | N/A           |
| typingSound     | The sound file to play will the text is being drawn.                                                                                          | No       | typing_loop.wav                            | N/A           |
