local rpg = require("scripts/libraries/rpgwizard/rpg")

local player = nil
local player_speed = 75

local script = nil

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
   player.trigger:setCollisionClass("Trigger")
   player.trigger:setMass(0)
   player.trigger:setPreSolve(function(collider_1, collider_2, contact)
      local object_1 = collider_1:getObject()
      local object_2 = collider_2:getObject()

      if object_1.layer ~= object_2.layer then
         contact:setEnabled(false)
      end
   end)
end

function love.update(dt)

   if script ~= nil then

      local exit = script.update(dt)
      if exit then
         script = nil
      end

   else
      rpg.update(dt)
      rpg.move_player(player, player_speed)

      if player.trigger:enter("Trigger") then
         local collision_data = player.trigger:getEnterCollisionData("Trigger")
         local object = collision_data.collider:getObject()
         for _, event in pairs(object.events) do
            script = require("scripts/" .. event.script:gsub(".lua", ""))
         end
      end
   end

end

function love.draw()

   rpg.draw()

end
