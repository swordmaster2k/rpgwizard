## Summary
RPGWizard comes bundled with a simple JavaScript console that you can use for debugging, error checking, and running commands in the engine, we are briefly going to touch on its usage here.

For indepth information on the console and other tools see: [chrome-devtools](https://developers.google.com/web/tools/chrome-devtools)

> TIP: If your game isn't working as expected checking the console is probably the first place to start, check for RED error messages like this one:

![](images/getting_started/02_accessing_the_console/images/1.png)

## Steps

### Accessing the Console
With your game running **Press F12**, you might need to resize the window a bit to see everything, hopefully this will be fixed in a later version:

![](images/getting_started/02_accessing_the_console/images/2.gif)

### Hello World!
You'll notice at the bottom there are a bunch of tabs for each tool, for this we are only concerned about the "console" tab. In the console you can execute bits of JavaScript which is really useful for testing things out or querying your games state.

In the console type:
```javascript
rpgcode.log("Hello world!");
```

<br/>

You should see something similar to the below, congratulations you've just executed your first command in RPGWizard!

![](images/getting_started/02_accessing_the_console/images/3.gif)

### Hiding the Console
To hide the console **Press F12**, you might need to stop and start your game again for the window to return to its normal size.
