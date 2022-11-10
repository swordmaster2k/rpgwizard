local rpg = { _version = "0.0.1" }

-- modules
local map = require("scripts/libraries/rpgwizard/map")
local player = require("scripts/libraries/rpgwizard/player")

-- libraries
local wf = require("scripts/libraries/windfield")
local camera = require("scripts/libraries/hump/camera")

-- state
local cache = {}
local world = nil
local current_map = nil

-- screen
local cam = nil

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

    cam = camera(0, 0, rpg.get_scale())
    cam.smoother = camera.smooth.damped(8)

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
            rpg.update_camera()
        end
    end

end

function rpg.draw()
    cam:attach()
    if current_map ~= nil then
        map.draw(cache, world, current_map)
    end
    cam:detach()
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
--- Camera
--- ###############################################################################################

function rpg.update_camera()

    local cam_x, cam_y = active_player.collider:getPosition()

    -- This section prevents the camera from viewing outside the background
    -- First, get width/height of the game window, divided by the game scale
    local w = love.graphics.getWidth()
    local h = love.graphics.getHeight()
    local sw = w / rpg.get_scale()
    local sh = h / rpg.get_scale()

    -- Get width/height of background
    local map_w = current_map.width * current_map.tileWidth
    local map_h = current_map.height * current_map.tileHeight

    -- Left border
    if cam_x < sw / 2 then
        cam_x = sw / 2
    end

    -- -- Right border
    if cam_y < sh / 2 then
        cam_y = sh / 2
    end

    -- -- Right border
    if cam_x > (map_w - sw / 2) then
        cam_x = (map_w - sw / 2)
    end

    -- -- Bottom border
    if cam_y > (map_h - sh / 2) then
        cam_y = (map_h - sh / 2)
    end

    cam:lockPosition(cam_x, cam_y)

    -- cam.x and cam.y keep track of where the camera is located
    -- the lookAt value may be moved if a screenshake is happening, so these
    -- values know where the camera should be, regardless of lookAt
    cam.x, cam.y = cam:position()

end

--- ###############################################################################################
--- Client API
--- ###############################################################################################

-- TODO: replace with love2d scale
function rpg.get_scale()
    return love.graphics.getWidth() / 512
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
