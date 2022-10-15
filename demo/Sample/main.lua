local rpg = require("scripts/libraries/rpgwizard/rpg")

local player = nil
local player_speed = 75

function love.load()
   if arg[#arg] == "vsc_debug" then require("lldebugger").start() end

   rpg.load()
   rpg.load_map("sample.map")
   player = rpg.get_sprite("player")
   player.collider:setMass(1)
end

function love.update(dt)

   rpg.move_player(player, player_speed)
   rpg.update(dt)

end

function love.draw()

   -- TODO: Scaling
   rpg.draw()

end
