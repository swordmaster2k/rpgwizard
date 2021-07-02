export const state = {};

export function setup() {
   state.canvasId = "lighting.canvas";
   
   // Default values.
   state.change = 0.001;
   state.roomAlpha = 0.9;
   state.innerCircleAlpha = 0.9;
   state.outerCircleAlpha = 0.45;
   
   state.needRedraw = false;
   
   rpg.createCanvas(state.canvasId, rpg.getViewport().width, rpg.getViewport().height);
}

export function lighten() {
   state.needRedraw = false;
   if (state.roomAlpha - state.change >= 0) {
      state.roomAlpha -= state.change;
      state.needRedraw = true;
   }
   if (state.innerCircleAlpha - state.change >= 0) {
      state.innerCircleAlpha -= state.change;
      state.needRedraw = true;
   }
   if (state.outerCircleAlpha - (state.change / 2) >= 0) {
      state.outerCircleAlpha -= (state.change / 2);
      state.needRedraw = true;
   }
   
   return state.needRedraw;
}

export function darken() {
   state.needRedraw = false;
   if (state.roomAlpha + state.change <= 0.9) {
      state.roomAlpha += state.change;
      state.needRedraw = true;
   }
   if (state.innerCircleAlpha + state.change <= 0.9) {
      state.innerCircleAlpha += state.change;
      state.needRedraw = true;
   }
   if (state.outerCircleAlpha + (state.change / 2) <= 0.45) {
      state.outerCircleAlpha += (state.change / 2);
      state.needRedraw = true;
   }
   
   return state.needRedraw;
}

export function drawCutOutLight(ctx, x, y, radius) {
   if (state.needRedraw) {
      state.needRedraw = false;
   }
   rpg.clear(state.canvasId);
   
   ctx.globalCompositeOperation = "source-over";
   ctx.globalAlpha = state.roomAlpha;
   ctx.fillStyle = "black";
   
   ctx.beginPath();
   ctx.fillRect(0, 0, rpg.getViewport().width, rpg.getViewport().height);
   ctx.fill();
   
   ctx.globalCompositeOperation = "destination-out";
   ctx.beginPath();
   ctx.globalAlpha = state.innerCircleAlpha;
   ctx.arc(x, y, radius / 1.5, 0, Math.PI * 2);
   ctx.fill();
   
   ctx.beginPath();
   ctx.globalAlpha = state.outerCircleAlpha;
   ctx.arc(x, y, radius, 0, Math.PI * 2);
   ctx.fill();
   
   rpg.render(state.canvasId);
}
