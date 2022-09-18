local rpg = require("rpg")

local player = nil
local player_speed = 75

function love.load()
   require("lldebugger").start()

   --rpg.load_map("sample.map")
   player = rpg.getSprite("player")
end

function love.update(dt)

   if player ~= nil then

      if love.keyboard.isDown("left") then
         player.x = player.x - player_speed * dt
      elseif love.keyboard.isDown("right") then
         player.x = player.x + player_speed * dt
      elseif love.keyboard.isDown("up") then
         player.y = player.y - player_speed * dt
      elseif love.keyboard.isDown("down") then
         player.y = player.y + player_speed * dt
      end

   end

end

function love.draw()

   -- TODO: Scaling
   rpg.drawMap()

end
