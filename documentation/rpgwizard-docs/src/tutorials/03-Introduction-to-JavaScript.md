## Summary
This part of the tutorial is a bit-size introduction to the JavaScript programming language, and covers most of the basics. JavaScript is a very popular language and is used throughout the web, for example the page you reading right now uses it!

If you are completely new to programming some of the concepts here might be hard to grasp at first. So I'd highly recommend looking over some of the resources from the additional reading section.

As this tutorial series progresses we will explain any new concepts that are not covered in this light introduction. Now get ready and strap yourself in for some JavaScript programming!

## Syntax
JavaScript statements are separated by semicolons, and programs are made up of a series of statements called "instructions" that tell a computer what to do. A programming language provides us with a means of describing something to a computer, without one a computer is not much smarter than a light bulb.

```javascript
var health, potion, finalHeath;
health = 1;
potion = 4;
finalHealth = health + potion;
```

## Variables
A variable is a piece of computer memory used to store a value. In JavaScript they are declared using the var keyword and can be assigned a value using the equal sign. You can create as many variables as you have memory available. Here is a simple example of a variable being used to store a piece of text:

```
var message;
message = "Hello world!";
```

JavaScript is **case sensitive**, meaning that **message** and **Message** are seen as two separate identifiers.

## Operators
Storing values on their own isn't much use in programming languages as we normally want to change it somehow later. JavaScript provides a number of arithmetic operators (+ - * /) for manipulating values. The simple program below adds two numbers together and then multiplies them by 10 and stores the result:

```javascript
var x, y;
x = 5;
y = 6;
var result = (x + y) * 10;
```

We can even use operators to add pieces of text together, this is called **string concatenation**:

```javascript
var message = "Hello" + " world" + "!";
```

JavaScript provides a very rich operator vocabulary, far too much to cover here, for a complete list of operators available in JavaScript refer to the W3Schools wiki on [JavaScript Operators Reference](https://www.w3schools.com/jsref/jsref_operators.asp).

## Comments
Sometimes it is useful to leave notes inside a program to help human readers better understand it. Or maybe you've written something complex and you want to remind yourself later of why you did it that way. In JavaScript you can leave comments in your code, these comments are ignored when the program is executing but can be read by a human later. Below is an example of the types of comments allowed:

```javascript
// This is a single line comment.
var x = 0;

/*
* This comment spans multiple lines.
*
* It could go on forever!
*/
var y = 1;
```

## Data Types
A variable can hold many different types of data: numbers, strings, objects, etc.

```javascript
var health = 10; // Number
var name = "Hero"; // String
var helmet = { type: "Iron", defence: 10 }; // Object
```

Understanding data types and how to use them is important if we are to write meaningful programs. In JavaScript data types are **dynamic**, meaning a variable can switch between the type it holds:

```javascript
var health; // Holds undefined at this point.
health = 10; // Now it holds a number.
health = "Hello world!"; // Now it holds a string.
```

A **string** holds a series of characters and are surrounded by either double or single quotes:

```javascript
var message = "Hello world!";
```

A **number** holds either a value with or without decimals:

```javascript
var x = 10.0;
var y = 10;
```

A **boolean** holds either a true or false value, and is often used to check conditions:

```javascript
var x = true;
var y = false;
```

An **array** holds a sequence of values and are zero-based, meaning the first element is at [0], then [1], and so on. They are useful for storing collections of values:

```javascript
var weapons = ["sword", "spear", "bow"];
var sword = weapons[0];
var spear = weapons[1];
var bow = weapons[2];
```

An **object** holds properties that are defined as key and value pairs. The value of an object property can be retrieved by accessing it via its key:

```javascript
var sword = {type: "Iron", power: 10};
var type = sword["type"];
var power = sword["power"];
```

For a more on JavaScript data types refer to the W3Schools wiki on [ JavaScript Data Types](https://www.w3schools.com/js/js_datatypes.asp).

## Checking Conditions
When writing programs it is often useful that we check for some condition and take different actions based on it. These are referred to as conditional statements and JavaScript provides several types:

* if statements, used to specify if a block of code should be executed if the condition is true.
* else statements, used to specify if a block of code should be executed if the condition is false.
* else if statements, used to specify if a block of code should be executed based on a new condition.

These condition statements can be chained together, or in the case of the if exist on their own. For a more in-depth example refer to the W3Schools wiki on [JavaScript If...Else Statements](https://www.w3schools.com/js/js_if_else.asp):

```javascript
// An example of a single if condition
if (health < 1) {
    // If this condition is true execute the code
}

// Example of a simple if-else pair
if (health < 1) {
    // If this condition is true execute the code
} else {
    // Do something else instead
}

// Example of an if-else if-else chain
 if (health < 1) {
    // If this condition is true execute the code
} else if (health > 10) {
    // If the first condition wasn't true but this one was execute the code
} else {
    // Do something else instead
}
```

## Looping
Loops execute the same block of code multiple times and come in handy if you want to repeat something with a different value. The most common form of loop is the for loop which execute a block of code a number of times:

```
for (var i = 0; i < 10; i++) {
    // execute the code block 10 times
}
```

* var i = 0; sets a control variable before the loop starts.
* i < 10; defines the condition to check to run the loop.
* i++ increases the variable each time the code block is executed.

When the conditional check becomes false the loop ends, and the program continues executing the rest of its code. If the condition never becomes false it becomes an infinite loop which causes the program to hang. It is important to ensure that a loop can always end to avoid this behavior. This is just the most basic form of loop in JavaScript and W3Schools wiki provides a more in-depth summary of all the different types and their uses under [JavaScript Looping](https://www.w3schools.com/js/js_loop_for.asp).

## Functions
A function is a block of reusable code in a program that executes a particular task. If you have a piece of code that is executed many times it is usually best to put it into a function. The code they contain is only executed when something calls or invokes the function. A simple function to multiply two numbers looks something like this:

```javascript
function myFunction(x, y) {
    return x * y; // Multiply x by y and return the value
}

var result = myFunction(5, 6); // Call myFunction, set x = 5, and y = 6, then store the result
```

Functions are declared using the keyword function, followed by a name, which is followed by a set of (). Their name should be unique, and it can contain letters, digits, underscores, and dollar signs. The parentheses may include a list of parameters separated by commas that can be passed to the function. They may also optionally return a value to the callee. For a more detailed look at functions and their uses refer to the W3Schools wiki on [ JavaScript Functions](https://www.w3schools.com/js/js_functions.asp).

## Synchronous vs Asynchronous
One of the biggest differences with JavaScript when comparing it to other programming languages is its emphasis on asynchronous operations. Traditionally handling input and output (e.g. reading files) is done by a function that only returns after it has been fully read, making it synchronous. In JavaScript these kind of tasks are all asynchronous, the function does not immediately return a result it instead invokes a callback when it's done, allowing the rest of the program to continue. In a synchronous environment the requests are completed one after another, which has the drawback that the second task will only start after the first has completed. This leaves the computer idle for most of that time.

In an asynchronous environment this is not the case, as the first and second request can be processed in parallel. This is useful for lengthy operations such as retrieving a file or waiting for input on the keyboard, as it does not block the rest of the program. This can be a difficult concept to grasp and is best explained visually in a 5 minute YouTube video by **The Net Ninja**:

[![What is Asynchronous JavaScript?](http://img.youtube.com/vi/YxWMxJONp7E/0.jpg)](https://www.youtube.com/watch?v=YxWMxJONp7E "Asynchronous JavaScript #1 - What is Asynchronous JavaScript?")

## Additional Reading
The JavaScript language powers most of the web today and its uses span far beyond simple 2D RPG games. As a result of this a massive amount of learning resources can be found online today that cover the basics far beyond anything that could be captured here. If you are new to programming or even just JavaScript I highly recommend checking out Marijn Haverbeke's book [Eloquent JavaScript](http://eloquentjavascript.net/). It is personally where I started with the language and you need only read the first 5 topics to gain enough of an understanding. Another great resource that we've already linked to in parts is the excellent [ JavaScript W3Schools tutorial](https://www.w3schools.com/js/js_syntax.asp). There is also the 12 minute crash course on YouTube by Jake Wright:

[![Crash course](http://img.youtube.com/vi/Ukg_U3CnJWI/0.jpg)](https://www.youtube.com/watch?v=Ukg_U3CnJWI "Learn JavaScript in 12 Minutes")
