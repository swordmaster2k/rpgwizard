## Summary
Modules are pieces of reusable JavaScript code that are globally loaded in the engine at runtime, they are commonly used for things like libraries. All of the RPGWizard default systems are written and loaded as modules or "program assets".

Typically you write modules **if you want to reuse parts of your code across your game**, e.g. UI drawing, they are also useful for sharing parts of projects with others in a standalone way.

The RPGWizard module system is built upon <a href="https://requirejs.org/" target="_blank">requirejs</a>, here we are going to show you how you can write a simple module.

## Steps

### Module Structure

There are actually many ways to write self-contained modules in JavaScript but for this example we'll stick to <a href="https://www.w3schools.com/js/js_object_prototypes.asp" target="_blank">JavaScript Object Prototypes</a>, which have a pretty straightforward structure.

In general you simply need to ensure the following is in place:

1. Create a single instance of your module for global use, best to put this at the start.
2. Declare a suitable Constructor for your module that you want to run on load.
3. Define any number of functions that you want to be able to access.

Below is a very simple example of a module named "MyModule", which shows all the basic features of using modules:

```javascript
// 1: Actual instance of the module to be used globally
let myModule = new MyModule();

// 2: Module Constructor
function MyModule() {
   // Properties of this module
   this._lineEnding = " world!";
}

// 3: Module Function declarations
MyModule.prototype.show = function(text) {
   // Draw the text at (30, 30)
   rpgcode.drawText(30, 30, text + this._lineEnding);
   rpgcode.renderNow();
};
```

### Loading and Using a Module

Using our module is extremely simple, we just need to load it as a program asset:

> IMPORTANT: Only modules should be loaded as program assets like this!

```javascript
let assets = {
  "programs": [
      // Default systems.
      "MyModule.js"
  ]
};

rpgcode.loadAssets(assets, async function() {

  myModule.show("hello");
  rpgcode.endProgram();

});
```

### Complete Examples
For in-depth examples of modules in RPGWizard and their uses see any of the default systems:

* <a href="https://github.com/swordmaster2k/rpgwizard/tree/develop/demo/The%20Wizard's%20Tower/Programs/defaults" target="_blank">Default Systems</a>
