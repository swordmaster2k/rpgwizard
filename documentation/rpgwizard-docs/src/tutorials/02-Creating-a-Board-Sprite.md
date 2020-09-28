## Summary
In this part of the tutorial series we will look add adding a board sprite. A board sprite is an instance of an NPC on a board. We will be using the Evil Eye NPC from the previous tutorial. By the end of this we will have an Evil Eye that follows the character around the hall board.

## Steps
Open the "hall.board" that you've created and select the board sprite tool from the main toolbar.

![](images/my_first_game/10_adding_board_sprites/images/1.png)

The board sprite tool shares the same set of mouse commands as the board vector tool. Left click to place a board sprite, right click to select it, and middle click to remove it from the board. When you place a board sprite it appears as a blank box as it has no associated NPC file.

![](images/my_first_game/10_adding_board_sprites/images/2.png)

### Setting the Sprite File
Select the newly placed board sprite and go to the "Properties" tab. Use the drop down box to select the source sprite file of our NPC. Then replace the randomly generated ID with the text "evil-eye-1", we will use this ID later to refer to the board sprite in our code.

![](images/my_first_game/10_adding_board_sprites/images/3.png)
![](images/my_first_game/10_adding_board_sprites/images/4.png)

If you play the game now and walk into the room with sprite you'll notice that is just sits there. Like activation vectors board sprites don't interact with the character unless they are told to. We are now going to add a simple program to the sprite to have it follow the character around the board. Go to **File > New -> New Program**.

![](images/my_first_game/10_adding_board_sprites/images/5.png)

### Sprite Movement
In order to bring the board sprite to life we are going to need to copy the chuck of code below into the code editor. Don't worry you don't need to understand it for this tutorial series. In simple terms it uses the characters current location to calculate how far it has to move to get closer. When you've copied this code save it to a file called "evil_eye.js".

```javascript
// Get the player's current location on the screen.
var inPixels = false;
var location = rpgcode.getCharacterLocation(inPixels);

// Are we on the same layer?
if (location.layer === this.layer) {
        // Calculate the distance to the character.
        var a = location.x - this.x;
        var b = location.y - this.y;
        var distance = Math.sqrt(a * a + b * b); // Simple Pythagora's theorem.

        // Are we less than 160 pixels away and no closer than 32 pixels?
        if (32 < distance && distance < 160) {
            // Get the angle between us and the character in degrees.
            var dx = location.x - this.x;
            var dy = location.y - this.y;
            var angle = Math.atan2(dy, dx);

            // How fast are we going to approach the character.
            var velocity = 1.25;

            // Calculate a shift in our (x, y) that will bring us closer.
            var velocityX = velocity * Math.cos(angle);
            var velocityY = velocity * Math.sin(angle);

            // Move towards the character for 50 milliseconds, this will animate the sprite.
            rpgcode.moveSpriteTo(this.sprite.id, this.x + velocityX, this.y + velocityY, 50);
        }
}
```

Now we'll attach it to the board sprite by setting its "Thread" property. The program we set here will be called every game frame and will progressively move the board sprite closer to the character if they are within about 5 tiles. If you start the game again the board sprite should slowly approach.

![](images/my_first_game/10_adding_board_sprites/images/7.png)

## Challenge
> Try experimenting with the variables in the simple character tracking program. See if you can make the NPC move faster or approach from a greater distance!
