var TypeWriter = new typeWriter();

function typeWriter() {   
   // Config
   this.maxLines = 2;
   this.characterDelay = 50;
   this.markerBlinkDelay = 500;
   this.font = "20px Arial";
   this.color = {
     "r": 255,
     "g": 255,
     "b": 255,
     "a": 1.0 
   };
   this.backgroundColor = {
     "r": 35,
     "g": 35,
     "b": 35,
     "a": 1.0 
   };
   this.advancementKey = "E";
   this.soundEffect = "";
   this.nextMarker = "";

   // Canvases
   this.windowCanvas = "TypeWriter.windowCanvas";
   this.nextMarkerCanvas = "TypeWriter.nextMarkerCanvas";

   // State
   this.blink = false;
   this.nextMarkerVisible = false;
   this.defaultX = 0;
   this.defaultY = 0;
   this.cursorX = this.defaultX;
   this.cursorY = this.defaultY;
   this.currentLines = 1;
}

typeWriter.prototype.setup = function (width, height, padding, linePadding, font, soundEffect, nextMarker) {
   var scale = rpgcode.getScale();

   this.maxWidth = width * scale;
   this.maxHeight = height * scale;
   this.padding = padding * scale;
   this.linePadding = linePadding * scale;
   this.effectiveWidth = this.maxWidth + (this.padding * 2);
   this.effectiveHeight = this.maxHeight + (this.padding * 2);
   this.x = Math.floor(rpgcode.getViewport().width / 2) - Math.floor(this.effectiveWidth / 2);
   this.y = Math.floor(rpgcode.getViewport().height - this.effectiveHeight);
   this.font = font;
   this.soundEffect = soundEffect;
   this.nextMarker = nextMarker;

   rpgcode.createCanvas(this.effectiveWidth, this.effectiveHeight, this.windowCanvas);
   rpgcode.setCanvasPosition(this.x, this.y, this.windowCanvas);

   // Only create the next marker's canvas if one is set.
   if (this.nextMarker) {
      var scale = rpgcode.getScale();
      var image = rpgcode.getImage(this.nextMarker);
      var width = image.width * scale;
      var height = image.height * scale;
      var x = this.x + (this.effectiveWidth - (width + width / 2));
      var y = this.y + (this.effectiveHeight - (height + height / 2));

      rpgcode.createCanvas(width, height, this.nextMarkerCanvas);
      rpgcode.setCanvasPosition(x, y, this.nextMarkerCanvas);
   }
};

typeWriter.prototype._reset = function(lineHeight) {
   this.cursorX = this.defaultX + this.padding;
   this.cursorY = this.defaultY + this.padding + lineHeight;
   this.currentLines = 1;
   
   this._drawBackground(this.defaultX, this.defaultY, this.maxWidth + (this.padding * 2), this.maxHeight + (this.padding * 2));
};

typeWriter.prototype._sortLines = function(text) {
   var words = text.split(" ");
   var lines = [];
   var line = words[0];

   for (var i = 1; i < words.length; i++) {
      var newLine = line + " " + words[i];
      if (rpgcode.measureText(newLine).width < this.maxWidth) {
         line = newLine;
      } else {
         lines.push(line);
         line = words[i];
      }
   }
   lines.push(line);
   
   return lines;
}

typeWriter.prototype._printCharacters = function(characters, callback) {
   var character = characters.shift();
   rpgcode.drawText(TypeWriter.cursorX, TypeWriter.cursorY, character, this.windowCanvas);
   rpgcode.renderNow(this.windowCanvas);
   TypeWriter.cursorX += rpgcode.measureText(character).width;

   if (characters.length) {
      rpgcode.delay(TypeWriter.characterDelay, function() {
         TypeWriter._printCharacters(characters, callback);   
      });
   } else {
      callback();
   }
};

typeWriter.prototype._printLines = function(lines, callback) {
   rpgcode.setColor(this.color.r, this.color.g, this.color.b, this.color.a);

   var line = lines.shift();
   this._printCharacters(line.split(""), function() {
      if (lines.length) {
         TypeWriter.currentLines++;
		 if (TypeWriter.currentLines > TypeWriter.maxLines) {
			 TypeWriter._stopTypingSound();
          TypeWriter.blink = true;
          TypeWriter._blinkNextMarker();
			 rpgcode.registerKeyDown(TypeWriter.advancementKey, function() {
             TypeWriter.blink = false;
             TypeWriter._clearNextMarker();
				 rpgcode.unregisterKeyDown(TypeWriter.advancementKey);
				 TypeWriter._reset(rpgcode.measureText(lines[0]).height);
				 TypeWriter._printLines(lines, callback);
				 TypeWriter._playTypingSound();
			 }, false);
		 } else {
            TypeWriter.cursorX = TypeWriter.defaultX + TypeWriter.padding;
            TypeWriter.cursorY += rpgcode.measureText(line).height + TypeWriter.linePadding;
            TypeWriter._printLines(lines, callback);
		 }
      } else {
         TypeWriter._stopTypingSound();
         rpgcode.registerKeyDown(TypeWriter.advancementKey, function() {
            rpgcode.clearCanvas(TypeWriter.nextMarkerCanvas);
            rpgcode.clearCanvas(TypeWriter.windowCanvas);
            callback();
        }, false);
      }
   });
};

typeWriter.prototype._blinkNextMarker = function() {
   if (this.nextMarker && this.blink) {
      if (!this.nextMarkerVisible) {
         this._drawNextMarker();
      } else {
         this._clearNextMarker();
      }
      rpgcode.delay(this.markerBlinkDelay, this._blinkNextMarker.bind(this));
   }
};

typeWriter.prototype._clearNextMarker = function() {
   if (this.nextMarker) {
      rpgcode.clearCanvas(this.nextMarkerCanvas);
      this.nextMarkerVisible = false;
   }
};

typeWriter.prototype._drawNextMarker = function() {
   if (this.nextMarker) {
      var scale = rpgcode.getScale();
      var image = rpgcode.getImage(this.nextMarker);
      var width = image.width * scale;
      var height = image.height * scale;
      rpgcode.setImage(this.nextMarker, 0, 0, width, height, this.nextMarkerCanvas);
      rpgcode.renderNow(this.nextMarkerCanvas);
      this.nextMarkerVisible = true;
   }
};

typeWriter.prototype._drawBackground = function(x, y, width, height) {
   rpgcode.setColor(this.backgroundColor.r, this.backgroundColor.g, this.backgroundColor.b, this.backgroundColor.a);
   rpgcode.clearCanvas(this.windowCanvas);
   rpgcode.fillRect(x, y, width, height, this.windowCanvas);
   rpgcode.renderNow(this.windowCanvas);
};

typeWriter.prototype._stopTypingSound = function() {
   if (this.soundEffect) {
      rpgcode.stopSound(this.soundEffect);
   }
};

typeWriter.prototype._playTypingSound = function() {
   if (this.soundEffect) {
      rpgcode.playSound(this.soundEffect, true);
   }
};

typeWriter.prototype.showDialog = function(text, callback) {
   rpgcode.font = this.font;
   
   var lines = this._sortLines(text);
   if (lines.length > 0) {
      this._reset(rpgcode.measureText(lines[0]).height);
      this._printLines(lines, callback);
      this._playTypingSound();
   } else {
	   callback();
   }
}
