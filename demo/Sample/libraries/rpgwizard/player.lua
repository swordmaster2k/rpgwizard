local player = { speed = 75 }

-- libraries
local vector = require("libraries/hump/vector")

function player.load(sprite_id, sprite)
    if sprite == nil then
        error("invalid state: cannot find player with sprite_id " .. sprite_id)
    end

    sprite.collider:setCollisionClass("Player")
    sprite.collider:setType("dynamic")
    sprite.collider:setMass(1)

    sprite.trigger:setCollisionClass("PlayerTrigger")
    sprite.collider:setType("dynamic")
    sprite.trigger:setMass(0)

    return sprite
end

local function move(active_player)

    if active_player ~= nil then

        local delta = vector(0, 0)

        if love.keyboard.isDown("left") then
            delta.x = -1
            active_player.active_animation = active_player.asset.anim8["west"]
        elseif love.keyboard.isDown("right") then
            delta.x = 1
            active_player.active_animation = active_player.asset.anim8["east"]
        end
        if love.keyboard.isDown("up") then
            delta.y = -1
            active_player.active_animation = active_player.asset.anim8["north"]
        elseif love.keyboard.isDown("down") then
            delta.y = 1
            active_player.active_animation = active_player.asset.anim8["south"]
        end

        delta:normalizeInplace()
        active_player.collider:setLinearVelocity(delta.x * player.speed, delta.y * player.speed)
        active_player.trigger:setPosition(active_player.collider:getPosition())
    end

end

local function enter_trigger(active_player, vm)
    local collision_data = active_player.trigger:getEnterCollisionData("Trigger")
    local object = collision_data.collider:getObject()

    if active_player.layer ~= object.layer then
        return vm
    end

    if object.event ~= nil then
        local event = object.event
        vm.source = object

        if event.type == "overlap" then
            vm.script = require("scripts/" .. event.script:gsub(".lua", ""))
        else
            vm.keypress_event.key = event.key
            vm.keypress_event.script = event.script
        end
    end

    return vm
end

local function exit_trigger(active_player, vm)
    local collision_data = active_player.trigger:getEnterCollisionData("Trigger")
    local object = collision_data.collider:getObject()

    if active_player.layer ~= object.layer then
        return vm
    end

    if object.event ~= nil then
        local event = object.event

        -- Remove registered key event
        if (vm.keypress_event.key ~= nil and vm.keypress_event.key == event.key) and
            (vm.keypress_event.script ~= nil and vm.keypress_event.script == event.script) then
            vm.keypress_event.key = nil
            vm.keypress_event.script = nil
        end
    end

    return vm
end

function player.update(dt, active_player, vm)
    move(active_player)

    if active_player.trigger:enter("Trigger") then
        vm = enter_trigger(active_player, vm)
    elseif active_player.trigger:exit("Trigger") then
        vm = exit_trigger(active_player, vm)
    end

    return vm
end

return player
