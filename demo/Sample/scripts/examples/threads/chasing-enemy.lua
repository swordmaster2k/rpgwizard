local thread = { sprite = nil }

-- rpg
local rpg = require("libraries/rpgwizard/rpg")

-- libraries
local vector = require("libraries/hump/vector")

function thread.update(dt)

    local delta = vector(0, 1)
    local speed = 15
    delta:normalizeInplace()

    rpg.move_sprite(thread.sprite, delta.x * speed, delta.y * speed)

end

return thread
