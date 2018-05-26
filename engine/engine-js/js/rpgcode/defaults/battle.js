/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/* global rpgcode, gui */

var battle = new Battle();

function Battle() {
    this.context = {
        MENU: "MENU",
        ITEM_SELECTION: "ITEM_SELECTION",
        ENEMY_SELECTION: "ENEMY_SELECTION",
        ENEMY_TURN: "ENEMY_TURN"
    };
}

//
// General Functions
//

Battle.prototype.show = function (config, callback) {
    if (!config.enemies || !config.characters || config.enemies.length < 1 || config.characters.length < 1) {
        rpgcode.endProgram();
    } else {
        this.config = config;
        this.callback = callback;
        config.enemies = config.enemies.slice(0, 4);
        config.characters = config.characters.slice(0, 4);
        this._loadAssets(config, function (config) {
            this._setup(config);
            this._createStage(config);
            this._placeEnemies(this._state.enemies);
            this._placeCharacters(this._state.characters);
            rpgcode.stopSound(rpgcode.getBoard().backgroundMusic);
            rpgcode.playSound("battle.stage", true, 1.0);
        }.bind(this));
    }
};

Battle.prototype._end = function () {
    clearInterval(this._state.drawInterval);
    clearInterval(this._state.flashInterval);
    this._state.characters.forEach(function (character) {
        rpgcode.destroySprite(character.id);
    });
    this._state.enemies.forEach(function (enemy) {
        rpgcode.destroySprite(enemy.id);
    });
    this._clearCanvases();
    rpgcode.getBoard().removeLayer(-1);
    rpgcode.stopSound("battle.stage");
    rpgcode.playSound(rpgcode.getBoard().backgroundMusic, true, 1.0);
    if (this.callback) {
        this.callback();
    } else {
        rpgcode.endProgram();
    }
};

Battle.prototype._setup = function (config) {
    this._scale = rpgcode.getScale();
    this.stage = {
        canvasId: "battle.stageCanvas",
        width: 640, height: 340,
        x: 0, y: 0,
        cursor: {
            canvasId: "battle.cursorCanvas",
            width: 25, height: 25,
            x: 0, y: 0
        }
    };
    this.window = {
        width: 640, height: 140,
        x: 100, y: 100,
        radius: 15,
        linePadding: 10,
        borderWidth: 5,
        area: {
            width: 490, height: 140,
            x: 0, y: 0,
            padding: {
                x: 15, y: 35
            },
            bar: {
                height: 8
            },
            selectedIndex: 1
        },
        menu: {
            width: 150, height: 140,
            x: 490, y: 0,
            padding: {
                x: 15, y: 35
            },
            selectedIndex: 1
        }
    };

    this.window.x = Math.floor((rpgcode.getViewport().width) / 2) - Math.floor(this.window.width / 2);
    this.window.y = Math.floor(rpgcode.getViewport().height - this.window.height);
    rpgcode.createCanvas(this.stage.width, this.stage.height, this.stage.canvasId);
    rpgcode.createCanvas(this.stage.cursor.width, this.stage.cursor.height, this.stage.cursor.canvasId);
    rpgcode.setCanvasPosition(0, 0, this.stage.canvasId);
    rpgcode.setCanvasPosition(0, 0, this.stage.cursor.canvasId);

    this.areaFrame = gui.createFrame({
        id: "Battle.areaFrameCanvas",
        width: 490,
        height: 140,
        x: 0,
        y: 340
    });
    this.areaFrame.setVisible(true);

    this.menuFrame = gui.createFrame({
        id: "Battle.menuFrameCanvas",
        width: 150,
        height: 140,
        x: rpgcode.getViewport().width - 150,
        y: 340
    });
    this.menuFrame.setVisible(true);

    this._state = {
        stageLayer: rpgcode.getBoard().layers.length,
        turnsTaken: 0,
        playerTurn: true,
        processingInput: false,
        currentContext: this.context.MENU,
        selectedCharacterIndex: 1,
        selectedEnemyIndex: 1,
        selectedItemIndex: 1,
        flashMenuSelection: true,
        drawInterval: setInterval(this._draw.bind(this), 50),
        flashInterval: setInterval(function () {
            this._state.flashMenuSelection = !this._state.flashMenuSelection;
        }.bind(this), 500),
        enemies: config.enemies,
        characters: config.characters,
        items: [],
        messages: []
    };
    rpgcode.registerKeyDown("ENTER", this._handleInput.bind(this), false);
    rpgcode.registerKeyDown("UP_ARROW", this._handleInput.bind(this), false);
    rpgcode.registerKeyDown("DOWN_ARROW", this._handleInput.bind(this), false);
    rpgcode.registerKeyDown("W", this._handleInput.bind(this), false);
    rpgcode.registerKeyDown("S", this._handleInput.bind(this), false);
};

Battle.prototype._loadAssets = function (config, callback) {
    var assets = {
        "images": [config.backgroundImage],
        "audio": {"battle.stage": config.battleMusic, "battle.item": config.itemSoundEffect}
    };
    rpgcode.loadAssets(assets, function () {
        this._loadEnemies(config, callback);
    }.bind(this));
};

Battle.prototype._loadEnemies = function (config, callback) {
    var enemies = [];
    var loaded = function (enemy, index) {
        enemies.push(enemy);
        if (config.enemies.length < 1) {
            config.enemies = enemies;
            this._loadCharacters(config, callback);
        } else {
            this._loadSprite(config.enemies.pop(), "enemy", index, loaded);
        }
    }.bind(this);
    this._loadSprite(config.enemies.pop(), "enemy", 1, loaded);
};

Battle.prototype._loadCharacters = function (config, callback) {
    var characters = [];
    var loaded = function (character, index) {
        characters.push(character);
        if (config.characters.length < 1) {
            config.characters = characters;
            callback(config);
        } else {
            this._loadSprite(config.characters.pop(), "character", index, loaded);
        }
    }.bind(this);
    this._loadSprite(config.characters.pop(), "character", 1, loaded);
};

Battle.prototype._loadSprite = function (file, type, index, callback) {
    var sprite = {
        "name": file,
        "id": type + "-" + index,
        "thread": "",
        "startingPosition": {"x": -100, "y": 100, "layer": rpgcode.getBoard().layers.length},
        "events": []
    };
    rpgcode.addSprite(sprite, function () {
        callback({"id": sprite.id, "data": rpgcode.getSprite(sprite.id).sprite[type]}, ++index);
    }.bind(this));
};

Battle.prototype._createStage = function (config) {
    var viewport = rpgcode.getViewport();
    var layer = {
        "tiles": [],
        "vectors": [],
        "images": [{"src": config.backgroundImage, "x": -Crafty.viewport._x - rpgwizard.craftyBoard.xShift, "y": -Crafty.viewport._y - rpgwizard.craftyBoard.yShift, "id": "battle.background"}],
        "name": "battle.stage"
    };
    rpgcode.getBoard().addLayer(layer);
};

Battle.prototype._getMenuItems = function () {
    return [
        {text: "Attack", execute: this._startEnemySelection.bind(this)},
//      {text: "Magic", execute: function() {}.bind(this)}, 
        {text: "Item", execute: this._startItemSelection.bind(this)},
        {text: "Flee", execute: this._flee.bind(this)}
    ];
};

Battle.prototype._getStatItems = function () {
    var statItems = [];
    var entities = this._state.currentContext === this.context.ENEMY_SELECTION ? this._state.enemies : this._state.characters;
    entities.forEach(function (entity) {
        statItems.push(this._getStatItem(entity.data));
    }.bind(this));
    return statItems;
};

Battle.prototype._getStatItem = function (entity) {
    var name = entity.name.padEnd(12);
    var hp = "HP " + entity.health.toString().padStart(4);
    var mp = "MP " + entity.magic.toString().padStart(4);
    var dp = "DP " + entity.defence.toString().padStart(4);
    return item = {
        text: name + hp + "  " + mp + "  " + dp,
        bars: [
            {position: name.length, value: entity.health, maxValue: entity.maxHealth, maxWidth: "1234567"},
            {position: name.length + hp.length + 2, value: entity.magic, maxValue: entity.maxMagic, maxWidth: "1234567"},
            {position: name.length + hp.length + 2 + mp.length + 2, value: entity.defence, maxValue: entity.maxDefence, maxWidth: "1234567"}
        ]
    };
};

Battle.prototype._getInventoryItems = function () {
    var inventoryItems = [];
    var inventory = rpgcode.getCharacter().inventory;
    Object.keys(inventory).forEach(function (key) {
        if (inventory[key].length > 0) {
            if (inventory[key][0].type.toLowerCase() === "battle") {
                inventoryItems.push(this._getInventoryItem(inventory[key][0], inventory[key].length, key));
            }
        }
    }.bind(this));
    this._state.items = inventoryItems;
    return inventoryItems;
};

Battle.prototype._getInventoryItem = function (inventoryItem, count, key) {
    return {text: (inventoryItem.name.padEnd(15) + " " + count.toString().padStart(2)).substring(0, 18), fileName: key, effects: inventoryItem.effects};
};

Battle.prototype._getCurrentSelection = function () {
    var selection = {activeAnimation: null, location: null};
    if (this._state.currentContext === this.context.ENEMY_SELECTION) {
        var selectedEnemy = this._state.enemies[this._state.selectedEnemyIndex - 1];
        if (!selectedEnemy) {
            return null;
        }
        var sprite = rpgcode.getSprite(selectedEnemy.id);
        if (!sprite) {
            return null;
        }
        selection.activeAnimation = sprite.sprite.enemy.spriteGraphics.active;
        selection.location = rpgcode.getSpriteLocation(selectedEnemy.id, false, true);
    } else {
        var selectedCharacter = this._state.characters[this._state.selectedCharacterIndex - 1];
        if (!selectedCharacter) {
            return null;
        }
        var sprite = rpgcode.getSprite(selectedCharacter.id);
        if (!sprite) {
            return null;
        }
        selection.activeAnimation = sprite.sprite.character.spriteGraphics.active;
        selection.location = rpgcode.getSpriteLocation(selectedCharacter.id, false, true);
    }
    return selection;
};

//
// Additional Setup Functions
//

Battle.prototype._placeEnemies = function (enemies) {
    var grid = {
        width: this.stage.width * 0.50,
        height: this.stage.height,
        center: {
            x: this.stage.width * 0.25,
            y: this.stage.height * 0.50
        }
    };
    if (enemies.length === 1) {
        var cells = [{x: 0, y: 0, center: {x: grid.width * 0.5, y: grid.height * 0.5}}];
    } else {
        var cells = [
            {x: 0, y: 0, center: {x: grid.width * 0.25, y: grid.height * 0.30}},
            {x: grid.width * 0.50, y: 0, center: {x: grid.width * 0.75, y: grid.height * 0.30}},
            {x: grid.width * 0, y: grid.height * 0.50, center: {x: grid.width * 0.25, y: grid.height * 0.70}},
            {x: grid.width * 0.50, y: grid.height * 0.50, center: {x: grid.width * 0.75, y: grid.height * 0.70}}
        ];
    }
    var xShift = -Crafty.viewport._x - rpgwizard.craftyBoard.xShift;
    var yShift = -Crafty.viewport._y - rpgwizard.craftyBoard.yShift
    for (var i = 0; i < enemies.length; i++) {
        rpgcode.setSpriteLocation(enemies[i].id, cells[i].center.x + xShift, cells[i].center.y + yShift, this._state.stageLayer, false);
        rpgcode.setSpriteStance(enemies[i].id, "EAST");
    }
};

Battle.prototype._placeCharacters = function (characters) {
    var grid = {
        width: this.stage.width,
        height: this.stage.height,
        center: {
            x: this.stage.width * 0.25,
            y: this.stage.height * 0.50
        }
    };
    if (characters.length === 1) {
        var cells = [{x: 0, y: 0, center: {x: grid.width * 0.75, y: grid.height * 0.5}}];
    } else {
        var cells = [
            {x: grid.width * 0.50, y: 0, center: {x: grid.width * 0.84, y: grid.height * 0.20}},
            {x: grid.width * 0.75, y: 0, center: {x: grid.width * 0.86, y: grid.height * 0.40}},
            {x: grid.width * 0.50, y: grid.height * 0.50, center: {x: grid.width * 0.88, y: grid.height * 0.60}},
            {x: grid.width * 0.75, y: grid.height * 0.50, center: {x: grid.width * 0.90, y: grid.height * 0.80}}
        ];
    }
    var xShift = -Crafty.viewport._x - rpgwizard.craftyBoard.xShift;
    var yShift = -Crafty.viewport._y - rpgwizard.craftyBoard.yShift
    for (var i = 0; i < characters.length; i++) {
        rpgcode.setSpriteLocation(characters[i].id, cells[i].center.x + xShift, cells[i].center.y + yShift, this._state.stageLayer, false);
        rpgcode.setSpriteStance(characters[i].id, "WEST");
    }
    rpgcode.moveSprite(this._state.characters[this._state.selectedCharacterIndex - 1].id, "WEST", 50);
};

//
// Battle Functions
//

Battle.prototype._checkEndConditions = function () {
    return this._state.characters.length < 1 || this._state.enemies.length < 1;
};

Battle.prototype._endTurn = function () {
    if (this._checkEndConditions()) {
        this._end();
    } else {
        if (this._state.playerTurn) {
            this._endCharacterTurn();
        } else {
            this._endEnemyTurn();
        }
        this._state.turnsTaken++;
        var totalTurns = this._state.playerTurn ? this._state.characters.length : this._state.enemies.length;
        if (this._state.turnsTaken >= totalTurns) {
            this._state.playerTurn = !this._state.playerTurn;
            this._state.turnsTaken = 0;
            if (this._state.playerTurn) {
                this._state.selectedCharacterIndex = this._state.selectedEnemyIndex = this.window.area.selectedIndex = 1;
                this._state.currentContext = this.context.MENU;
                this._startNextCharacterTurn();
            } else {
                this._state.selectedCharacterIndex = this._state.selectedEnemyIndex = this.window.area.selectedIndex = 1;
                this._state.currentContext = this.context.ENEMY_TURN;
                this._startNextEnemyTurn();
            }
        } else {
            if (this._state.playerTurn) {
                this._startNextCharacterTurn();
            } else {
                this._startNextEnemyTurn();
            }
        }
    }
};

Battle.prototype._startNextCharacterTurn = function () {
    rpgcode.moveSprite(this._state.characters[this._state.selectedCharacterIndex - 1].id, "WEST", 50);
};

Battle.prototype._endCharacterTurn = function () {
    rpgcode.moveSprite(this._state.characters[this._state.selectedCharacterIndex - 1].id, "EAST", 50);
    if (this._state.currentContext === this.context.ENEMY_SELECTION) {
        this._endEnemySelection();
    } else {
        this._endItemSelection(true);
    }
};

Battle.prototype._startNextEnemyTurn = function () {
    var enemy = this._state.enemies[this._state.selectedEnemyIndex - 1];
    this._state.selectedCharacterIndex = Math.round(Math.random() * ((this._state.characters.length) - 1) + 1);
    rpgcode.delay(1000, this._attackCharacter.bind(this));
};

Battle.prototype._endEnemyTurn = function () {
    this._state.selectedEnemyIndex++;
};

Battle.prototype._attackCharacter = function () {
    var enemy = this._state.enemies[this._state.selectedEnemyIndex - 1];
    var character = this._state.characters[this._state.selectedCharacterIndex - 1];
    var attackPower = this._determineAttackPower(enemy.data.attack);
    var location = rpgcode.getSpriteLocation(character.id, false, true);
    this._showToastMessage({text: attackPower, x: location.x, y: location.y});
    var playing = 2;
    var callback = function () {
        playing--;
        if (playing < 1) {
            character.data.health -= attackPower;
            if (character.data.health < 1) {
                this._removeCharacter(character, this._state.selectedCharacterIndex - 1);
            }
            this._endTurn();
        }
    };
    rpgcode.animateSprite(enemy.id, "ATTACK", callback.bind(this));
    rpgcode.animateSprite(character.id, "DEFEND", callback.bind(this));
};

Battle.prototype._removeCharacter = function (character, arrIndex) {
    this._state.characters.splice(arrIndex, 1);
    this._state.selectedCharacterIndex = this.window.area.selectedIndex = 1;
    rpgcode.animateSprite(character.id, "DIE", function () {
        rpgcode.destroySprite(character.id);
    }.bind(this));
};

Battle.prototype._attackEnemy = function () {
    var enemy = this._state.enemies[this._state.selectedEnemyIndex - 1];
    var character = this._state.characters[this._state.selectedCharacterIndex - 1];
    var attackPower = this._determineAttackPower(character.data.attack);
    var location = rpgcode.getSpriteLocation(enemy.id, false, true);
    this._showToastMessage({text: attackPower, x: location.x, y: location.y});
    var playing = 2;
    var callback = function () {
        playing--;
        if (playing < 1) {
            enemy.data.health -= attackPower;
            if (enemy.data.health < 1) {
                this._removeEnemy(enemy, this._state.selectedEnemyIndex - 1);
            }
            this._endTurn();
        }
    };
    rpgcode.animateSprite(enemy.id, "DEFEND", callback.bind(this));
    rpgcode.animateSprite(character.id, "ATTACK", callback.bind(this));
};

Battle.prototype._removeEnemy = function (enemy, arrIndex) {
    this._state.enemies.splice(arrIndex, 1);
    this._state.selectedEnemyIndex = this.window.area.selectedIndex = 1;
    rpgcode.animateSprite(enemy.id, "DIE", function () {
        rpgcode.destroySprite(enemy.id);
    }.bind(this));
};

Battle.prototype._flee = function () {
    this._end();
};

//
// Item Functions
//

Battle.prototype._useItem = function () {
    var selectedItem = this._state.items[this._state.selectedItemIndex - 1];
    if (selectedItem) {
        var character = this._state.characters[this._state.selectedCharacterIndex - 1].data;
        character.health += selectedItem.effects.health;
        if (character.health > character.maxHealth) {
            character.health = character.maxHealth;
        }
        character.attack += selectedItem.effects.attack;
        if (character.attack > character.maxAttack) {
            character.attack = character.maxAttack;
        }
        character.defence += selectedItem.effects.defence;
        if (character.defence > character.maxDefence) {
            character.defence = character.maxDefence;
        }
        character.magic += selectedItem.effects.magic;
        if (character.magic > character.maxMagic) {
            character.magic = character.maxMagic;
        }
        rpgcode.takeItem(selectedItem.fileName, "");
        rpgcode.playSound("battle.item", false, 1.0);
        this._endTurn();
    } else {
        this._endItemSelection(false);
    }
};

//
// AI Functions
//

Battle.prototype._determineAttackPower = function (attack) {
    return Math.round(Math.random() * ((attack * 1.1) - (attack * 0.6)) + (attack * 0.6));
};

//
// Input Functions
//

Battle.prototype._handleInput = function (e) {
    if (!this._state.playerTurn || this._state.processingInput) {
        return;
    }
    this._state.processingInput = true;
    switch (e.key) {
        case 13:
            this._handleEnter();
            break;
        case 38: // UP_ARROW
        case 87: // W
            this._handleUpArrow();
            break;

        case 40: // DOWN_ARROW
        case 83: // S
            this._handleDownArrow();
            break;
        default:
            return;
    }
};

Battle.prototype._handleEnter = function () {
    if (this._state.currentContext === this.context.MENU) {
        switch (this.window.menu.selectedIndex) {
            case 1:
            case 2:
            case 3:
            case 4:
                this._getMenuItems()[this.window.menu.selectedIndex - 1].execute();
            default:
                return;
        }
    } else if (this._state.currentContext === this.context.ENEMY_SELECTION) {
        this._attackEnemy();
    } else if (this._state.currentContext === this.context.ITEM_SELECTION) {
        this._useItem();
    }
};

Battle.prototype._handleUpArrow = function () {
    if (this._state.currentContext === this.context.MENU) {
        this.window.menu.selectedIndex = this.window.menu.selectedIndex > 1 ? this.window.menu.selectedIndex - 1 : this.window.menu.selectedIndex;
    } else if (this._state.currentContext === this.context.ENEMY_SELECTION) {
        this.window.area.selectedIndex = this._state.selectedEnemyIndex = this._state.selectedEnemyIndex > 1 ? this._state.selectedEnemyIndex - 1 : this._state.selectedEnemyIndex;
    } else if (this._state.currentContext === this.context.ITEM_SELECTION) {
        this.window.area.selectedIndex = this._state.selectedItemIndex = this._state.selectedItemIndex > 1 ? this._state.selectedItemIndex - 1 : this._state.selectedItemIndex;
    }
    this._state.processingInput = false;
};

Battle.prototype._handleDownArrow = function () {
    if (this._state.currentContext === this.context.MENU) {
        this.window.menu.selectedIndex = this.window.menu.selectedIndex < this._getMenuItems().length ? this.window.menu.selectedIndex + 1 : this.window.menu.selectedIndex;
    } else if (this._state.currentContext === this.context.ENEMY_SELECTION) {
        this.window.area.selectedIndex = this._state.selectedEnemyIndex = this._state.selectedEnemyIndex < this._state.enemies.length ? this._state.selectedEnemyIndex + 1 : this._state.selectedEnemyIndex;
    } else if (this._state.currentContext === this.context.ITEM_SELECTION) {
        this.window.area.selectedIndex = this._state.selectedItemIndex = this._state.selectedItemIndex < this._state.items.length + 1 ? this._state.selectedItemIndex + 1 : this._state.selectedItemIndex;
    }
    this._state.processingInput = false;
};

Battle.prototype._startEnemySelection = function () {
    this._state.currentContext = this.context.ENEMY_SELECTION;
    this.window.area.selectedIndex = this._selectedEnemyIndex = 1;
    this._state.processingInput = false;
};

Battle.prototype._endEnemySelection = function () {
    this._state.currentContext = this.context.MENU;
    this.window.area.selectedIndex = ++this._state.selectedCharacterIndex;
    this._state.processingInput = false;
};

Battle.prototype._startItemSelection = function () {
    this._state.currentContext = this.context.ITEM_SELECTION;
    this.window.area.selectedIndex = this._state.selectedItemIndex = 1;
    this._state.processingInput = false;
};

Battle.prototype._endItemSelection = function (advance) {
    this._state.currentContext = this.context.MENU;
    this.window.area.selectedIndex = advance ? ++this._state.selectedCharacterIndex : this._state.selectedCharacterIndex;
    this._state.processingInput = false;
};


//
// Drawing Functions
//

Battle.prototype._clearCanvases = function () {
    rpgcode.clearCanvas(this.stage.canvasId);
    rpgcode.clearCanvas(this.stage.cursor.canvasId);
    rpgcode.clearCanvas(this.window.canvasId);
    rpgcode.clearCanvas(this.areaFrame.id);
    rpgcode.clearCanvas(this.menuFrame.id);
};

Battle.prototype._draw = function () {
    this._clearCanvases();
    this.areaFrame.draw();
    this.menuFrame.draw();
    this._drawMenu(this._getMenuItems());
    if (this._state.currentContext === this.context.ITEM_SELECTION) {
        this._drawInventory(this._getInventoryItems());
    } else {
        this._drawStats(this._getStatItems());
    }
    this._drawSelection();
    this._drawMessages();
    rpgcode.renderNow(this.areaFrame.id);
    rpgcode.renderNow(this.menuFrame.id);
    rpgcode.renderNow(this.stage.canvasId);
};

Battle.prototype._drawStats = function (statItems) {
    this._drawStatSelection();
    for (var i = 0; i < statItems.length; i++) {
        this._drawStatItem(statItems[i], i);
    }
    rpgcode.drawOntoCanvas(this.areaFrame.id, this.window.area.x, this.window.area.y, this.window.area.width, this.window.area.height, this.window.canvasId);
};

Battle.prototype._drawStatItem = function (item, index) {
    rpgcode.font = gui.getFont();
    var x = this.window.area.padding.x;
    var y = this.window.area.padding.y + ((gui.getFontSize() + this.window.linePadding) * index);
    item.bars.forEach(function (bar) {
        this._drawStatBar(bar, x, y);
    }.bind(this));
    gui.prepareTextColor();
    rpgcode.drawText(x, y, item.text, this.areaFrame.id);
};

Battle.prototype._drawStatBar = function (bar, x, y) {
    var charWidth = rpgcode.measureText("0").width;
    var maxWidth = rpgcode.measureText(bar.maxWidth).width;
    var width = maxWidth * (bar.value / bar.maxValue);
    var height = this.window.area.bar.height;
    var barX = x + (bar.position * charWidth);
    var barY = y - (height / 2);
    gui.prepareStatBarColor();
    rpgcode.fillRect(barX, barY, width, height, this.areaFrame.id);
};

Battle.prototype._drawStatSelection = function () {
    gui.prepareSelectionColor();
    var x = this.window.area.padding.x - (this.window.area.padding.x / 2);
    var y = this.window.area.padding.y + ((gui.getFontSize() + this.window.linePadding) * (this.window.area.selectedIndex - 1)) - gui.getFontSize() - (this.window.linePadding / 4);
    var width = this.window.area.width - this.window.area.padding.x;
    var height = gui.getFontSize() + (this.window.linePadding);
    rpgcode.fillRect(x, y, width, height, this.areaFrame.id);
};

Battle.prototype._drawInventory = function (inventoryItems) {
    this._drawInventorySelection();
    var i = 0;
    for (; i < inventoryItems.length; i++) {
        this._drawInventoryItem(inventoryItems[i], i);
    }
    this._drawInventoryItem({text: "<- Back"}, i);
    rpgcode.drawOntoCanvas(this.areaFrame.id, this.window.area.x, this.window.area.y, this.window.area.width, this.window.area.height, this.window.canvasId);
};

Battle.prototype._drawInventoryItem = function (item, index) {
    rpgcode.font = gui.getFont();
    var x = index < 4 ? this.window.area.padding.x : this.window.area.padding.x + (this.window.area.width / 2);
    var y = this.window.area.padding.y + ((gui.getFontSize() + this.window.linePadding) * (index < 4 ? index : index - 4));
    gui.prepareTextColor();
    rpgcode.drawText(x, y, item.text, this.areaFrame.id);
};

Battle.prototype._drawInventorySelection = function () {
    gui.prepareSelectionColor();
    var index = this.window.area.selectedIndex - 1;
    var x = index < 4 ? this.window.area.padding.x : this.window.area.padding.x + (this.window.area.width / 2);
    var y = this.window.area.padding.y + ((gui.getFontSize() + this.window.linePadding) * (index < 4 ? index : index - 4)) - gui.getFontSize() - (this.window.linePadding / 4);
    var width = (this.window.area.width / 2) - (this.window.area.padding.x * 2);
    var height = gui.getFontSize() + (this.window.linePadding);
    rpgcode.fillRect(x, y, width, height, this.areaFrame.id);
};

Battle.prototype._drawMenu = function (menuItems) {
    if (this._state.flashMenuSelection && this._state.currentContext !== this.context.ENEMY_TURN) {
        this._drawMenuSelection();
    }
    for (var i = 0; i < menuItems.length; i++) {
        this._drawMenuItem(menuItems[i], i);
    }
    rpgcode.drawOntoCanvas(this.menuFrame.id, this.window.menu.x, this.window.menu.y, this.window.menu.width, this.window.menu.height, this.window.canvasId);
};

Battle.prototype._drawMenuItem = function (item, index) {
    rpgcode.font = gui.getFont();
    gui.prepareTextColor();
    var x = this.window.menu.padding.x;
    var y = this.window.menu.padding.y + ((gui.getFontSize() + this.window.linePadding) * index);
    rpgcode.drawText(x, y, item.text, this.menuFrame.id);
};

Battle.prototype._drawMenuSelection = function () {
    gui.prepareSelectionColor();
    var x = this.window.menu.padding.x - (this.window.menu.padding.x / 2);
    var y = this.window.menu.padding.y + ((gui.getFontSize() + this.window.linePadding) * (this.window.menu.selectedIndex - 1)) - gui.getFontSize() - (this.window.linePadding / 4);
    var width = this.window.menu.width - this.window.menu.padding.x;
    var height = gui.getFontSize() + (this.window.linePadding);
    rpgcode.fillRect(x, y, width, height, this.menuFrame.id);
};

Battle.prototype._drawSelection = function () {
    var selection = this._getCurrentSelection();
    if (!selection) {
        return;
    }
    gui.prepareSelectionColor();
    rpgcode.fillRect(0, 0, this.stage.cursor.width, this.stage.cursor.height, this.stage.cursor.canvasId);
    this.stage.cursor.width = selection.activeAnimation.width;
    this.stage.cursor.height = selection.activeAnimation.height;
    this.stage.cursor.x = selection.location.x - (this.stage.cursor.width / 2);
    this.stage.cursor.y = selection.location.y - (this.stage.cursor.height / 2);
    rpgcode.drawOntoCanvas(this.stage.cursor.canvasId, this.stage.cursor.x, this.stage.cursor.y, this.stage.cursor.width, this.stage.cursor.height, this.stage.canvasId);
};

Battle.prototype._drawMessages = function () {
    gui.prepareTextColor();
    this._state.messages.forEach(function (message) {
        rpgcode.drawText(message.x, message.y, message.text, this.stage.canvasId);
    }.bind(this));
};

Battle.prototype._showToastMessage = function (message, location) {
    rpgcode.font = gui.getFont();
    message.x -= rpgcode.measureText(message.text).width / 2;
    this._state.messages.push(message);
    var callback = function (_this, message, startY) {
        message.y--;
        if (startY - message.y < 25) {
            rpgcode.delay(10, function () {
                callback(_this, message, startY);
            });
        } else {
            _this._state.messages.shift();
        }
    };
    rpgcode.delay(10, function () {
        callback(this, message, message.y);
    }.bind(this));
};