import * as ai from "./ai.js";
import * as combat from "./combat.js";
import * as controller from "./controller.js";
import * as hud from "./hud.js";
import * as items from "./items.js";
import * as util from "./util.js";

export async function setup() {
   rpg.attachController("player", controller.build());

   rpg.registerKeyDown("SPACE", async function() {
      await this.slashSword();
   }.bind(this), true);

   hud.setup();
}

export async function dropItem(spriteFile, location) {
   await items.drop(spriteFile, location);
}

export async function pickupItem(sprite) {
   await items.pickup(sprite);
}

export async function slashSword() {
   const hits = util.findClosetObjects(rpg.getSpriteLocation("player"), rpg.getSpriteDirection("player"), 25);
   const drop = await combat.slashSword(hits);
   if (drop) {
      await items.drop(drop.item, drop.location);
   }
}

export async function bumpPlayer(sprite) {
   await combat.bumpPlayer(sprite);
}

export async function wander(sprite, distance, time) {
   const direction = util.randomDirection(sprite, distance);
   await ai.wander(sprite, distance, direction, time);
}

export async function chase(sprite, targetId) {
   await ai.chase(sprite, targetId);
}
