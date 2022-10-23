local player = { speed = 75 }

-- libraries
local vector = require("scripts/libraries/hump/vector")

function player.load(sprite_id, sprite)
    if sprite == nil then
        error("invalid state: cannot find player with sprite_id " .. sprite_id)
    end

    sprite.collider:setCollisionClass("Player")
    sprite.collider:setMass(1)
    sprite.collider:setPreSolve(function(collider_1, collider_2, contact)
        local object_1 = collider_1:getObject()
        local object_2 = collider_2:getObject()

        if object_1.layer ~= object_2.layer then
            contact:setEnabled(false)
        end
    end)

    sprite.trigger:setCollisionClass("Trigger")
    sprite.trigger:setMass(0)
    sprite.trigger:setPreSolve(function(collider_1, collider_2, contact)
        local object_1 = collider_1:getObject()
        local object_2 = collider_2:getObject()

        if object_1.layer ~= object_2.layer then
            contact:setEnabled(false)
        end
    end)

    return sprite
end

local function move(active_player)

    if active_player ~= nil then

        local delta = vector(0, 0)

        if love.keyboard.isDown("left") then
            delta.x = -1
            active_player.active_animation = active_player.asset.anim8["WEST"]
        elseif love.keyboard.isDown("right") then
            delta.x = 1
            active_player.active_animation = active_player.asset.anim8["EAST"]
        end
        if love.keyboard.isDown("up") then
            delta.y = -1
            active_player.active_animation = active_player.asset.anim8["NORTH"]
        elseif love.keyboard.isDown("down") then
            delta.y = 1
            active_player.active_animation = active_player.asset.anim8["SOUTH"]
        end

        delta:normalizeInplace()
        active_player.collider:setLinearVelocity(delta.x * player.speed, delta.y * player.speed)
        active_player.trigger:setPosition(active_player.collider:getPosition())
    end

end

function player.update(dt, active_player, state)
    move(active_player)

    if active_player.trigger:enter("Trigger") then
        local collision_data = active_player.trigger:getEnterCollisionData("Trigger")
        local object = collision_data.collider:getObject()
        for _, event in pairs(object.events) do

            if event.type == "overlap" then
                state.script = require("scripts/" .. event.script:gsub(".lua", ""))
            else
                state.keypress_event.key = event.key
                state.keypress_event.script = event.script
            end

        end
    elseif active_player.trigger:exit("Trigger") then
        local collision_data = active_player.trigger:getEnterCollisionData("Trigger")
        local object = collision_data.collider:getObject()
        for _, event in pairs(object.events) do

            -- Remove registered key event
            if (state.keypress_event.key ~= nil and state.keypress_event.key == event.key) and
                (state.keypress_event.script ~= nil and state.keypress_event.script == event.script) then
                state.keypress_event.key = nil
                state.keypress_event.script = nil
            end
        end
    end

    return state
end

return player
