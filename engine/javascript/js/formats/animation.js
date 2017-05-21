Animation.prototype.constructor = Animation;

function Animation(filename) {
    console.info("Loading Animation filename=[%s]", filename);
    
    // TODO: Make the changes here that chrome suggests.
    var req = new XMLHttpRequest();
    req.open("GET", filename, false);
    req.overrideMimeType("text/plain; charset=x-user-defined");
    req.send(null);

    var animation = JSON.parse(req.responseText);
    for (var property in animation) {
        this[property] = animation[property];
    }
}


