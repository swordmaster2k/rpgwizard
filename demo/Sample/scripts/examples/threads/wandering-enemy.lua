local thread = { sprite = nil }

-- rpg
local rpg = require("scripts/libraries/rpgwizard/rpg")

-- libraries
local vector = require("scripts/libraries/hump/vector")

-- directions, N, S, E, W
local directions = { vector(0, -1), vector(1, -1), vector(1, 0), vector(1, 1), vector(0, 1), vector(-1, 1), vector(-1, 0) }

function thread.load()
    thread.sprite.data.direction = directions[1]
    thread.sprite.data.time_wandering_range = { 1, 5 }
    thread.sprite.data.max_time_wandering = thread.sprite.data.time_wandering_range[1]
    thread.sprite.data.time_wandering = 0

    thread.sprite.collider:setPostSolve(function(collider_1, collider_2)
        if collider_2.collision_class == "Solid" then
            thread.sprite.data.time_wandering = thread.sprite.data.max_time_wandering
        end
    end)
end

function thread.should_switch_direction()
    return thread.sprite.data.time_wandering >= thread.sprite.data.max_time_wandering
end

function thread.switch_direction()
    local new_direction = directions[math.random(1, #directions)]
    repeat
        new_direction = directions[math.random(1, #directions)]
    until (new_direction ~= thread.sprite.data.direction)
    thread.sprite.data.direction = new_direction
    thread.sprite.data.max_time_wandering = math.random(thread.sprite.data.time_wandering_range[1],
        thread.sprite.data.time_wandering_range[2])
    thread.sprite.data.time_wandering = 0
end

function thread.update(dt)

    if thread.should_switch_direction() then
        thread.switch_direction()
    end

    local delta = thread.sprite.data.direction
    local speed = 30
    delta:normalizeInplace()

    rpg.move_sprite(thread.sprite, delta.x * speed, delta.y * speed)

    thread.sprite.data.time_wandering = thread.sprite.data.time_wandering + dt

end

return thread
