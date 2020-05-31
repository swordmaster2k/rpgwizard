// Assets need by the battle system
const assets = {
   "images": ["cursor.png"]
};

rpgcode.loadAssets(assets, function() {
   
   // Once assets are loaded go to the battle board
   rpgcode.sendToBoard("battle_system.board", 3, 15, 0);
   rpgcode.endProgram();
   
});
