# Summary
In the last part of this tutorial series we will look at creating inventory items. An item can be placed in or removed from a character's inventory while the game is running. Certain item types can even be equipped to a character such as a sword, shield, and so on.

# Steps
To create an item go to **File > New > New Item**.

![](images/my_first_game/11_new_item/images/1.png)

The item editor is very simple and relatively straight forward to use. You can set a name, an image (which can be set using a double-click), slot type, sellable price, and a few default effects to apply when equipped. When an item is equipped you can update the character stance to reflect this if you wish. There are a few basic slots that the item type can be set to:

* **Default Slots:** chest, boots, gloves, head, right-hand, left-hand

## Equipping an Item
To use an item in the engine you need to give it to a character, which involves loading it into the engine like any other asset. In the below code example we load up an item file and equip it to the character's right-hand slot. When we equip the item we remove it from the character's inventory, likewise when place it back into the inventory when removing it from the right-hand slot:

```javascript
// Part 1: Load the item.
var itemFile = "dragon_sword.item";
rpgcode.giveItem(itemFile, "Hero", function() {

	// Part 2: Access the character's inventory and log the number of them.
	var character = rpgcode.getCharacter("Hero");
	rpcode.log("Number of itemFile=[" + itemFile + "in inventory count=[" + character.inventory[itemFile].length + "]");

	// Part 3: Equip the item, available slots are (chest, boots, gloves, head, right-hand, left-hand).
	character.equipment["right-hand"] = character.inventory[itemFile][0];

	// Part 4: Removes one of requested items from character's inventory.
	rpgcode.takeItem(itemFile, "Hero");

	// Part 5: Unequip the item.
	var item = character.equipment["right-hand"];
	character.inventory[itemFile].push(item);
	character.equipment["right-hand"] = "";

	rpgcode.endProgram();

});
```

# Challenge
> You have completed the basic tutorial on how to make a game with the RPGWizard. The next set of tutorials will build upon what you've learned here and teach you how to bring your game to life with code.
