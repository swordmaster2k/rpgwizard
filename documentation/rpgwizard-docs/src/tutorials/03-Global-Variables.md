## What are Global Variables
A global variable is any piece of information that we wish to be able to share across programs in the engine. By default any variable that you define in a program cannot exist outside of it, for example if you define **myvar1** in a program and try to access it in another it will be **undefined** as it doesn't exist in the other programs **scope**.

In order to access variables across programs you need to store them globally via the RPGcode API which provides functions for storing and removing global variables.

Global variables are very useful for storing **state**, e.g. has a player reached a certain stage in the game, what name did the player choose, etc. Anything that you need to remember longterm in your game could live in a global variable.

## Using Global Variables
Before we can read a global variable we must first set its value using <a href="Global_.html#setGlobal" target="_blank">rpgcode.setGlobal</a>, which accepts a unique ID for the variable, and its value. A global variable can be any JavaScript type, e.g. a number, boolean, string, object, etc. the engine does not restrict what the value is.

Once a global variable has been set you can access its value using <a href="Global_.html#getGlobal" target="_blank">rpgcode.getGlobal</a>, if the global variable does not exist then **undefined** will be returned. To update a global variable simply call **setGlobal** again with the new value.

```javascript
// Reference to the global name, saves on typos!
var swordActive = "swordActive";

// Set the global variable "swordActive" to the boolean value of false.
rpgcode.setGlobal(swordActive, false);

// Read the value of the global we just set.
var isSwordActive = rpgcode.getGlobal("swordActive");

// Set the global variable "swordActive" to the boolean value of true.
rpgcode.setGlobal(swordActive, true);
```

## Removing Global Variables
> IMPORTANT: Once you are done with a global variable it is good practice to remove it from the engine in order to save space in memory. The RPGWizard will not remove global variables itself as it cannot determine how important they may be.

To remove a global variable from the engine you simply need to call <a href="Global_.html#removeGlobal" target="_blank">rpgcode.removeGlobal</a>.

```javascript
// Removes the global variable from the engine.
rpgcode.removeGlobal("swordActive");
```
