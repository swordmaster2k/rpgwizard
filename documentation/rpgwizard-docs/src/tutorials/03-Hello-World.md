## Summary
We are going to start with the absolute basics here and look at writing a simple <a href="https://en.wikipedia.org/wiki/%22Hello,_World!%22_program" target="_blank">"Hello world!" program</a> to show you how to execute a simple piece of code in the RPGWizard.

## Running a Program
Create a new program with the following content and save it:

![](images/programming_guide/00_hello_world/images/1.png)

<br/>

At the top of the editor look for the "Debug Program" button, and press it.

![](images/programming_guide/00_hello_world/images/2.png)

<br/>

You should get something similar to the following output depending on your game setup:

![](images/programming_guide/00_hello_world/images/3.png)

<br/>

Congratulations you've just run your first program in RPGWizard!

### For the Lazy People

```javascript
rpgcode.drawText(30, 30, "Hello world!");
rpgcode.renderNow();

rpgcode.endProgram();
```
