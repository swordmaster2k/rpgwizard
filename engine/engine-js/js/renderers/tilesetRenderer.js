function TilesetRenderer(tileset) {
  this.tileSet = tileset;

  this.size = {
    x: this.tileSet.count * this.tileSet.tileWidth,
    y: this.tileSet.tileHeight
  };
}

TilesetRenderer.prototype.render = function (cnv) {
  var cnv = cnv || document.createElement("canvas");

  cnv.width = this.size.x;
  cnv.height = this.size.y;

  var context = cnv.getContext("2d");

  var offset = {
    x: 0,
    y: 0
  };

  // render each tile
  for (var i = 0; i < this.tileSet.count; i++) {
    this.renderTile(context, i, offset.x, offset.y);
    offset.x += this.tileSet.tileWidth;
  }

  return cnv;
};

TilesetRenderer.prototype.renderTile = function (context, tileIndex, offsetX, offsetY) {
  var tile = this.tileSet.getTile(tileIndex);

  if (tile) {
    context.putImageData(tile, offsetX, offsetY);
  }
};