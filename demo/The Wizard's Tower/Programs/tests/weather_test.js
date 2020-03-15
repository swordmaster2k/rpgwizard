let assets = {
  "programs": [
      // Default systems.
      "defaults/weather.js"
  ]
};

rpgcode.loadAssets(assets, function() {
   let config = {
      rain: {
         sound: "rain.wav"
      }
   };
   weather.show(config, function() {console.log("weather stopped");});
   rpgcode.endProgram();
});

