local rpg = require("rpg")

local player = nil
local player_speed = 75

function love.load()
   if arg[#arg] == "vsc_debug" then require("lldebugger").start() end

   rpg.load_map("sample.map")
   player = rpg.get_sprite("player")
end

function love.update(dt)

   if player ~= nil then

      if love.keyboard.isDown("left") then

         player.active_animation = player.asset.anim8["WEST"]
         player.x = player.x - player_speed * dt

      elseif love.keyboard.isDown("right") then

         player.active_animation = player.asset.anim8["EAST"]
         player.x = player.x + player_speed * dt

      elseif love.keyboard.isDown("up") then

         player.active_animation = player.asset.anim8["NORTH"]
         player.y = player.y - player_speed * dt

      elseif love.keyboard.isDown("down") then

         player.active_animation = player.asset.anim8["SOUTH"]
         player.y = player.y + player_speed * dt

      end

   end

   rpg.update(dt)

end

function love.draw()

   -- TODO: Scaling
   rpg.draw()

end
