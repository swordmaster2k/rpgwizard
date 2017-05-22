function Project(filename) {
    console.info("Loading Project filename=[%s]", filename);
    
    // TODO: Make the changes here that chrome suggests.
    var req = new XMLHttpRequest();
    req.open("GET", filename, false);
    req.overrideMimeType("text/plain; charset=x-user-defined");
    req.send(null);

    var project = JSON.parse(req.responseText);
    for (var property in project) {
        this[property] = project[property];
    }
}
