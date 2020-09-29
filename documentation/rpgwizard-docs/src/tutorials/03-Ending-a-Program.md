## Summary

## How to use rpgcode.endProgram

> IMPORTANT: Run Time Programs and Threads should never call rpgcode.endProgram, as they are constantly being invoked by the engine!

The function <a href="Program.html#endProgram" target="_blank">rpgcode.endProgram</a> is probably one of the trickier things about coding in the RPGWizard, it is a necessary evil. In RPGWizard **all normally running programs**, e.g. activations & board entry, **must tell the engine when they have finished.** Without this the engine will essentially wait forever, and it is very easy for your program to become stuck if you've forgotten to put it in somewhere.

Basically where ever you want your program to finish up you'll need to put an rpgcode.endProgram, there can be multiple ways out of your program, e.g. a menu system, so you'll have to consider this.

_If your program gets stuck because you forgot one then simply open the console and type rpgcode.endProgram() so the engine will continue running again._

### Simple (No Callbacks)
In a simple piece of JavaScript code there is no callbacks, the code is easy to follow, each statement is written and executes one after the other.

For cases like this **rpgcode.endProgram** can be placed simply at the end of your code.

```javascript
var location = rpgcode.getCharacterLocation();
rpgcode.log(location.x);
rpgcode.log(location.y);
rpgcode.log(location.layer);

rpgcode.endProgram();
```

### With Callbacks
Things aren't always that simple with JavaScript and you are typically dealing with code that involves <a href="https://developer.mozilla.org/en-US/docs/Glossary/Callback_function" target="_blank">callbacks</a>, this typically involves finding the place in the code where you actually want your program to end.

In the below example we use the <a href="Character.html#moveCharacterTo" target="_blank">rpgcode.moveCharacterTo</a> function that moves the character and then invokes a callback function. Our program shouldn't actually end until the callback has been called so we need to **put rpgcode.endProgram inside of the callback** for it to work as expected:

```javascript
rpgcode.log("Moving character...");

// Call moveCharacterTo which uses a callback
rpgcode.moveCharacterTo("Hero", 150, 175, 5000, function() {
  // Program isn't done until this callback is called
  rpgcode.log("character has finished moving");

  rpgcode.endProgram(); // At end of callback
});
```

### With Async/Await
If you are familiar with <a href="https://developer.mozilla.org/en-US/docs/Learn/JavaScript/Asynchronous/Async_await" target="_blank">async & await</a> then the flow is fairly simple, just call **rpgcode.endProgram** after you are finished using your await code:

```javascript
await foo();

rpgcode.endProgram();
```
