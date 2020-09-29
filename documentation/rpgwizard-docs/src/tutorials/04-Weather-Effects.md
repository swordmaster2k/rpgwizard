## Summary
Sometimes its nice to be able to have control over the weather, well with the default weather effects it is possible to do just that. It includes 2 basic effects:

* Raining (with or without sound)
* Snowing (without sound)

> NOTE: The weather system will automatically load any sound files for you.

![](images/default_systems/05_weather_effects/images/1_raining.gif)

## Steps

### Loading the Weather Effect
The weather effects system is standalone and doesn't require any of the other default systems to be loaded in order for it to work:

```javascript
let assets = {
  "programs": [
      // Default systems.
      "defaults/weather.js"
  ]
};

rpgcode.loadAssets(assets, async function() {
   // Weather system is now loaded into the engine
});
```

### Dance for rain!
To start the rain effect you need to pass it the "rain" config parameter with optionally a sound file to play for the rain effect, and then await the rain.

```javascript
let assets = {
  "programs": [
      // Default systems.
      "defaults/weather.js"
  ]
};

rpgcode.loadAssets(assets, async function() {

   // Setup weather
   config = {
      rain: {
         sound: "rain.wav"
      }
   };
   await weather.show(config);

   rpgcode.endProgram();

});
```

<br/>

#### Result
![](images/default_systems/05_weather_effects/images/1_raining.gif)

### Let it Snow!
To start the snow effect you need to pass it the "snow" config parameter, there is no sound option for this (does snow make any sound?!?), and then await the snow.

```javascript
let assets = {
  "programs": [
      // Default systems.
      "defaults/weather.js"
  ]
};

rpgcode.loadAssets(assets, async function() {

   // Setup weather
   config = {
      snow: {}
   };
   await weather.show(config);

   rpgcode.endProgram();

});
```

<br/>

#### Result
![](images/default_systems/05_weather_effects/images/1_snowing.gif)

### Stopping the Weather Effects
To stop the current weather effect you simply need to call the "close" function at any point in your game:

```javascript
weather.close();
```

<br/>

#### Result
![](images/default_systems/05_weather_effects/images/1_stopping.gif)

### Config Parameters
| PARAMETER       | DESCRIPTION                                                                                                                                   | REQUIRED | EXAMPLE VALUES                             | DEFAULT VALUE |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------------------------|----------|--------------------------------------------|---------------|
| rain            | Configuration for rain effects.                                                                                                               | No       | {sound: "rain.wav"}                        | N/A           |
| snow            | Configuration for snow effects                                                                                                                | No       | {}                                         | N/A           |
