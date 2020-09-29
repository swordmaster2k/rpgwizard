## Summary
While RPGWizard has a large number of <a href="Draw2D.html" target="_blank">drawing functions</a> designed to cover the basics sometimes you might need to do more advanced drawing techniques. Fortunately the RPGWizard engine has access to all the standard browser JavaScript drawing functions listed by <a href="https://www.w3schools.com/tags/ref_canvas.asp" target="_blank">w3schools</a>, in fact the RPGCode drawing functions essentially act as wrappers around these lower level functions for you:

* <a href="https://www.w3schools.com/html/html5_canvas.asp" target="_blank">HTML Canvas Graphics</a>
* <a href="https://www.w3schools.com/tags/ref_canvas.asp" target="_blank">HTML Canvas Reference</a>

> A really good in-depth example of this is the Weather Effects that the RPGWizard includes:
> * <a href="https://github.com/swordmaster2k/rpgwizard/blob/develop/demo/The%20Wizard's%20Tower/Programs/defaults/weather.js#L160" target="_blank">Weather Effects</a>

## Steps

### Getting a 2D Context

You can gain access to the 2D context of any canvas you created simply by using:

```javascript
// Create a canvas
rpgcode.createCanvas(300, 300, "myCanvas");

// Access rpgcode.canvases by key for our canvas
const canvas = rpgcode.canvases["myCanvas"].canvas;

// Get its drawing context
const ctx = canvas.getContext("2d");
```

### Drawing a Circle

We can use any one of the <a href="https://www.w3schools.com/html/html5_canvas.asp" target="_blank">w3schools examples</a> to draw our own shapes and text to a canvas directly:

```javascript
// Use hex-decimal red
ctx.strokeStyle = "#FF0000";

// Draw a red circle
ctx.beginPath();
ctx.arc(95, 50, 40, 0, 2 * Math.PI);
ctx.stroke();
```

### Complete Example

```javascript
// Create a canvas
rpgcode.createCanvas(300, 300, "myCanvas");

// Access rpgcode.canvases by key for our canvas
const canvas = rpgcode.canvases["myCanvas"].canvas;

// Get its drawing context
const ctx = canvas.getContext("2d");

// Use hex-decimal red
ctx.strokeStyle = "#FF0000";

// Draw a red circle
ctx.beginPath();
ctx.arc(95, 50, 40, 0, 2 * Math.PI);
ctx.stroke();

// Draw it to the screen
rpgcode.renderNow("myCanvas");
```
