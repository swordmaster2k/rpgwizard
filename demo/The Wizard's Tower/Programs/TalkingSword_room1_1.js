/* global rpgcode */

let swordActive = rpgcode.getGlobal("swordActive");
if (swordActive) {
  rpgcode.sendToBoard("Room2.board", 5.5, 17);
  rpgcode.endProgram();
} else {
   let config = {
      position: "BOTTOM",
      advancementKey: "E",
      nextMarkerImage: "next_marker.png",
      profileImage: "sword_profile_1_small.png",
      typingSound: "typing_loop.wav",
      text: `Hey where are you going. Come back here.`
   };
   await (dialog.show(config));
   rpgcode.endProgram();
}
