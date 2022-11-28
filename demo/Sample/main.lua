require("libraries/lovedebug")

_G.rpg = require("libraries/rpgwizard/rpg")

function love.load()

   rpg.load({
      debug = true,
      scale = 2,
      map = "start.map",
      player = "player"
   })

end

function love.update(dt)

   rpg.update(dt)

end

function love.draw()

   rpg.draw()

end

function love.keyreleased(key)

   rpg.keyreleased(key)

end
