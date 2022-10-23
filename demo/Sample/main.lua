local rpg = require("scripts/libraries/rpgwizard/rpg")

function love.load()

   rpg.load({ debug = true, map = "sample.map", player = "player" })

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
