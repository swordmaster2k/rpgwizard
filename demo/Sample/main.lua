local rpg = require("scripts/libraries/rpgwizard/rpg")

local player = nil
local player_speed = 75

function love.load()
   if arg[#arg] == "vsc_debug" then require("lldebugger").start() end

   rpg.load()

   -- map
   rpg.load_map("sample.map")

   -- player
   -- TODO move this into library
   player = rpg.get_sprite("player")
   player.collider:setCollisionClass("Player")
   player.collider:setMass(1)
   player.collider:setPreSolve(function(collider_1, collider_2, contact)
      local object_1 = collider_1:getObject()
      local object_2 = collider_2:getObject()

      if object_1.layer ~= object_2.layer then
         contact:setEnabled(false)
      end
   end)
end

function love.update(dt)

   rpg.move_player(player, player_speed)
   rpg.update(dt)

end

function love.draw()

   -- TODO: Scaling
   rpg.draw()

end
