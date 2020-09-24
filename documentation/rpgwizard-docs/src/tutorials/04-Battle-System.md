# Summary
The RPGWizard comes packaged with a simple turn based battle system, similar in style to the original Final Fantasy series of games. Using this battle system it is possible to create multi-character vs multi-enemy encounters, against custom backdrops, and looping music.

![](images/default_systems/04_battle_system/images/1.gif)

# Steps
Starting a battle with the default battle system is very straightforward, as it deals with loading all the assets needed in background for you. You can supply it with a list of enemies to battle against, and the list of characters you wish to fight with. The default game included with the RPGWizard, "The Wizard's Tower", comes with a complete example of how to start a battle:

```javascript
// Load up some food items for use in battle.
var items = [
   "apple.item", "apple.item", "apple.item"
];
loadItems();

function loadItems() {
    if (items.length === 0) {
      // When all are loaded start the battle.
      start();
   } else {
      var item = items.pop();
      rpgcode.giveItem(item, "Hero", function() {
         loadItems();
      });
   }
}

function start() {
   // Configure this battle setting, and show it.
   var config = {
      enemies: ["evil-eye.enemy"],
      characters: ["Hero.character"],
      backgroundImage: "battle-background.png",
      battleMusic: "Battle.ogg",
      itemSoundEffect: "item.ogg"
   }
   battle.show(config, function(result) {
      rpgcode.log("The battle has ended, result.status=" + result.status);
      rpgcode.destroySprite("evil-eye-1");
      rpgcode.endProgram();   
   });
}
```

The default battle system will look for items in your character's inventory with the type "battle". When creating items in the editor you must ensure that they have this type tag in order for them to appear in the battle system's item menu. The default battle system can show at most 5 types of items at a time from the inventory.

![](images/default_systems/04_battle_system/images/2.jpg)

The default battle system will respond to a total of 5 input keys. It is possible to navigate the menus using either the arrow keys, or by using the WASD standard.

| KEY            | WHAT IT TRIGGERS                                                        |
|----------------|-------------------------------------------------------------------------|
| W / Up Arrow   | Navigate up in the menu, and enemy selection stages.                    |
| S / Down Arrow | Navigate down in the menu, and enemy selection stages.                  |
| Enter          | Used to select a menu item, or an enemy in the character's battle step. |

The battle.show function must be passed a configuration object, and a callback function to invoke when the battle has finished. There are only a small number of configuration options that can be set, all of them are required You learn more about how to use and configure the battle system from its API Reference. Below you will find a table containing a list of all the configuration options that can be set, and what they will do:

| PARAMETER       | DESCRIPTION                                                               | REQUIRED | EXAMPLE VALUES                                                           | DEFAULT VALUE |
|-----------------|---------------------------------------------------------------------------|----------|--------------------------------------------------------------------------|---------------|
| enemies         | List of enemies (Max 4) to use for the battle. Can contain duplicates.    | Yes      | ["evil-eye.enemy", "evil-eye.enemy", "evil-eye.enemy", "evil-eye.enemy"] | N/A           |
| characters      | List of characters (Max 4) to use for the battle. Can contain duplicates. | Yes      | ["Hero.character", "Hero.character"]                                     | N/A           |
| backgroundImage | The background image to use for the battle stage.                         | Yes      | next_marker.png                                                          | N/A           |
| battleMusic     | The battle background music to continously loop.                          | Yes      | rpgcode.getCharacter().graphics["PROFILE"]                               | N/A           |
| itemSoundEffect | The sound file to play when an item has been consumed by a character.     | Yes      | typing_loop.wav                                                          | N/A           |
