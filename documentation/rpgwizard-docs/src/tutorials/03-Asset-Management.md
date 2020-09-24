# What are Assets?
In RPGWizard an "asset" **refers to any sound, image, or program file** that you wish to use in your game at runtime. Before you can play audio, draw images, or call functions from other programs, you must load them into the engine.

Assets are loaded **asynchronously** meaning they cannot be used until the engine has loaded them. Once an asset has been loaded it will be available in the engine to programs until it is explictly removed, this means you can load up common assets on game startup and access them anytime without needing to wait for them again.

These files must be available in the **Sounds**, **Graphics**, or **Programs** folder of your project which is where the engine looks for them by default.

# Loading Assets
To load assets into the engine you will be using a special function provided by the RPGcode API, [rpgcode.loadAssets](images/rpgcode_api_reference/RPGcode.html#loadAssets). This function accepts an object containing the different types of assets that you wish to load into the engine, you also need to provide it with a **callback function** which it will call as soon as the assets are ready for use. Below you will find examples for loading each of the asset types into the engine.

## Images

```javascript
// An example of an assets object
var assets = {
    // Images are stored in an array
    "images": ["block.png", "startscreen.png"]
};

// Load up the assets we need.
rpgcode.loadAssets(assets, function() {
   // They are ready, so do some stuff here...
});
```

### Supported Formats
* png
* jpeg

## Audio

```javascript
// An example of an assets object
var assets = {
    // Audio is stored in an object
    "audio": {"intro": "intro.mp3"}
};

// Load up the assets we need.
rpgcode.loadAssets(assets, function() {
   // They are ready, so do some stuff here...
});
```

### Supported Formats
* wav
* mp3
* ogg

## Programs
> IMPORTANT: Only a special type of program called a "module" can be loaded into the engine this way. For example all of the default systems are defined as modules so they can be easily be reused. Writing custom modules will be covered at a later date.

```javascript
var assets = {
  // Programs are stored in an array
  "programs": [
      // Default systems.
      "defaults/gui.js",
      "defaults/dialog.js"
  ]
};

// Load up the assets we need.
rpgcode.loadAssets(assets, function() {
   // They are ready, so do some stuff here...
});
```

## Everything Together
You are not limited to loading 1 type of asset at time, you can combine multiple asset types that you want to load in a single statement, and wait for them all to load:

```javascript
var assets = {
  "images": ["block.png", "startscreen.png"],
  "audio": {"intro": "intro.mp3"},
  "programs": ["defaults/gui.js", "defaults/dialog.js"]
};

// Load up the assets we need.
rpgcode.loadAssets(assets, function() {
   // They are ready, so do some stuff here...
});
```

# Removing Assets
> IMPORTANT: The RPGWizard does not manage the amount of memory your assets can consume. It is good practice to remove assets from the engine if they will no longer. This can be done by calling [rpgcode.removeAssets](images/rpgcode_api_reference/RPGcode.html#removeAssets).

```javascript
var assets = {
  "images": ["block.png", "startscreen.png"],
  "audio": {"intro": "intro.mp3"},
  "programs": ["defaults/gui.js", "defaults/dialog.js"]
};

// Remove some assets after use
rpgcode.removeAssets(assets);
```
