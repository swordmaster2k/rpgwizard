local rpg = { _version = "0.0.1" }

-- modules
local map = require("scripts/libraries/rpgwizard/map")
local player = require("scripts/libraries/rpgwizard/player")

-- libraries
local wf = require("scripts/libraries/windfield")

-- state
local cache = {}
local world = nil
local current_map = nil

-- screen
local canvas = nil

-- player
local active_player = nil

-- events
local vm = {
    script = nil,
    source = nil,
    keypress_event = {
        key = nil,
        script = nil
    }
}

--- ###############################################################################################
--- Runtime
--- ###############################################################################################

function rpg.load(config)
    if config ~= nil then
        if config.debug then
            require("lldebugger").start()
        end
    end

    love.graphics.setDefaultFilter("nearest", "nearest")
    canvas = love.graphics.newCanvas(512, 288)

    world = wf.newWorld(0, 0, false)
    world:addCollisionClass("Solid")
    world:addCollisionClass("Player")
    world:addCollisionClass("Sprite")
    world:addCollisionClass("Trigger", { ignores = { "Solid", "Player", "Sprite" } })

    if config ~= nil then

        if config.map ~= nil then
            current_map = map.load(cache, world, config.map)
            if config.player ~= nil then
                local player_sprite = rpg.get_sprite(config.player)
                active_player = player.load(config.player, player_sprite)
            end
        end

    end

end

function rpg.update(dt)

    world:update(dt)

    if vm.script ~= nil then
        if vm.script.update(dt) then
            vm.script = nil
            vm.source = nil
        end
    elseif current_map ~= nil then
        map.update(dt, current_map)
        if active_player ~= nil then
            vm = player.update(dt, active_player, vm)
        end
    end

end

function rpg.draw()
    if current_map ~= nil then
        map.draw(cache, world, current_map, canvas, rpg.get_scale())
    end
end

function rpg.keyreleased(key)
    if vm.script ~= nil then
        return
    end

    if key == vm.keypress_event.key then
        vm.script = require("scripts/" .. vm.keypress_event.script:gsub(".lua", ""))
    end
end

--- ###############################################################################################
--- Client API
--- ###############################################################################################

function rpg.get_scale()
    return love.graphics.getWidth() / canvas:getWidth()
end

function rpg.get_sprite(id)
    if current_map == nil then
        error("invalid state: no map is loaded")
    end

    -- TODO: Optimize
    for i, layer in pairs(current_map.layers) do
        local map_sprite = layer.sprites[id]
        if map_sprite ~= nil then
            return map_sprite
        end
    end

    return nil

end

function rpg.move_sprite(sprite, velocity_x, velocity_y)
    sprite.collider:setLinearVelocity(velocity_x, velocity_y)
    sprite.trigger:setPosition(sprite.collider:getPosition())
end

--- ###############################################################################################
--- end
--- ###############################################################################################

return rpg
