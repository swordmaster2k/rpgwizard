## Summary
In the last part of this tutorial series we will look at creating inventory items. An item can be placed in or removed from a character's inventory while the game is running. Certain item types can even be equipped to a character such as a sword, shield, and so on.

## Steps
To create an item go to **File > New > New Item**.

![](images/my_first_game/11_new_item/images/1.png)

<br/>

The item editor is very simple, you can set a name, an image (which can be set using a double-click), slot type, sellable price, and a few default effects to apply when equipped. There are a few basic slots that the item type can be equipped in.

### Default Item Slots
* head
* chest
* right-hand
* left-hand
* gloves
* boots

### Equipping an Item

#### Loading an Item
To use an item in the engine you need to give it to a character, which involves loading it into the engine like any other asset. The code below simply requests that an item be loaded into the engine, when it has the callback function is then called:
```javascript
// Part 1: Load the item.
var itemFile = "dragon_sword.item";
rpgcode.giveItem(itemFile, "Hero", function() { // callback
	// Code here is called once item has been loaded
});
```

<br/>

#### Equipping an Item
> IMPORTANT: Only 1 item at a time can be assigned to a slot, if you assign an item to an occupied slot then the current item will be overwritten!

If we wanted to assign the item to a slot on the character it would look something like:
```javascript
var itemFile = "dragon_sword.item";
rpgcode.giveItem(itemFile, "Hero", function() { // callback

	// Part 2: Access the character's inventory and log the number of them.
	rpcode.log("Number of itemFile=[" + itemFile + "in inventory count=[" + character.inventory[itemFile].length + "]");

	// Part 3: Equip the item, available slots are (chest, boots, gloves, head, right-hand, left-hand).
	character.equipment["right-hand"] = character.inventory[itemFile][0];

});
```

<br/>

#### Unequipping an Item
To unequip an item we must do the following things in order:

1. Get the equipped item in the slot we want to unequip
2. Move a copy of the item to the character's inventory
3. Clear the equipment after copying to the inventory.

```javascript
var item = character.equipment["right-hand"];	// Get the item
character.inventory[itemFile].push(item); 		// Copy it into the inventory
character.equipment["right-hand"] = "";			 	// Now cleanup
```

<br/>

#### Taking an Item
In some cases you might want to completely remove an item from a Character's inventory, for example they eat an Apple to regain some health. To do this you'll need to know the item's file name:

```javascript
// Removes one of requested items from character's inventory.
rpgcode.takeItem("apple.item", "Hero");
```

<br/>

This would only remove a single item from the character's inventory, if you wanted to remove multiple it would look something like:

```javascript
const numApples = character.inventory["apple.item"].length; // Get the number of apples present
for (var i = 0; i < numApples; i++) {
	rpgcode.takeItem("apple.item", "Hero"); // Keep taking until they are all gone
}
```

## Challenge
> You have completed the basic tutorial on how to make a game with the RPGWizard. The next set of tutorials will build upon what you've learned here and teach you how to bring your game to life with code.
