## Saving a JSON File
In RPGWizard you can create and store [JSON](https://www.w3schools.com/whatis/whatis_json.asp) data in a games directory at runtime, this makes it possible to save custom state beyond the scope of a global variable, which are lost when the engine is shutdown.

To save JSON data you need to supply the following:

* **path:** The path to the file that you want to create in the games directory, if any of the folders in the path don't exist then they will be created.
* **data:** Blob of JSON data that you want to store in the file at the **path**.
* **success callback:** Function to call when the save was a success.
* **failure callback:** Function to call when the save was a failure.

> IMPORTANT: If the file already exists it will be overwritten by the engine without any feedback.

```javascript
rpgcode.saveJSON(
 {
     path: "example/custom.json", // Subdirectory and file to save.
     data: {"Test": "Hello world!"} // JSON data to store.
 },
 function(response) {
    // Success callback.
    console.log(response);
    rpgcode.endProgram();
 },
 function(response) {
    // Failure callback.
    console.log(response);
    rpgcode.endProgram();
 }
);
```

## Loading a JSON File
In RPGWizard you can read files back as [JSON](https://www.w3schools.com/whatis/whatis_json.asp) data from your games directory at runtime, this makes it possible to read custom saved state or even game assets such as boards.

To load JSON data you need to supply the following:

* **path:** The path to the file that you want to load from the games directory, this can be any file, and is not restricted to custom files.
* **success callback:** Function to call when the load was a success.
* **failure callback:** Function to call when the load was a failure.

```javascript
rpgcode.loadJSON(
     "Boards/start.board",
     function(response) {
         // Success callback.
         console.log(response); // response contains raw text from the file
     },
     function(response) {
         // Failure callback.
         console.log(response);
     }
 );
```
