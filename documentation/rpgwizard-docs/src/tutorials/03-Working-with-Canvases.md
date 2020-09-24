# What is a Canvas?
A *canvas* is a element that can be used to draw graphics via code, such as text, shapes, images, and other canvases. Up until this point every drawing operation that we have shown has used the RPGWizard's **default canvas**, which is a built-in canvas that other functions in the engine may access. Sometimes you may wish to create your own canvas for things like layering a HUD or UI in your game.

With all of the drawing functions in the RPGCode API **if you don't specify a canvasId then it defaults to the built-in canvas**. It is not always the best idea to use the built-in canvas as the engine may access it at points and clear what you have drawn. Instead you should create your own canvases if you intend to do a lot of custom drawing.

# Creating a Canvas

```javascript
// Unique id of the canvas.
var customCanvas = "mycanvas";

// Create the canvas to draw onto.
rpgcode.createCanvas(640, 480, customCanvas);
```

# Drawing onto a Canvas
```javascript
// Move to (270, 300) and draw the text "PRESS SPACE" to the screen
rpgcode.drawText(270, 300, "PRESS SPACE", customCanvas);
rpgcode.renderNow(customCanvas);
```

# Clearing a Canvas
```javascript
// Clears the canvas with ID customCanvas
rpgcode.clearCanvas(customCanvas);
```

# Combining Canvases
```javascript
var canvas1 = "canvas1";
rpgcode.createCanvas(640, 480, canvas1);

// ... Do some drawing on canvas1

var canvas2 = "canvas2";
rpgcode.createCanvas(100, 100, canvas2);
// ... Do some drawing on canvas2

// Draw the smaller canvas2 onto the larger canvas1
rpgcode.drawOntoCanvas(canvas2, 0, 0, 100, 100, canvas1);
rpgcode.renderNow(canvas1);
```

# Destroying a Canvas
> IMPORTANT: Canvases in JavaScript can use up a lot of resources, so it is good practice to clean them up once you are finished with them by telling the engine to destroy them after.

```javascript
// Unique id of the canvas.
var customCanvas = "mycanvas";

// Create the canvas to draw onto.
rpgcode.createCanvas(640, 480, customCanvas);
// ... Do some drawing on customCanvas

// Destroys the canvas that we just created
rpgcode.destroyCanvas(customCanvas);
```
