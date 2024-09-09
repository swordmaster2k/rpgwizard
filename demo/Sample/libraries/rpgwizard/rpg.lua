local rpg = { _version = "0.0.1" }

-- modules
local map = require("libraries/rpgwizard/map")
local player = require("libraries/rpgwizard/player")
local sprite = require("libraries/rpgwizard/sprite")

-- libraries
local wf = require("libraries/windfield")
local camera = require("libraries/hump/camera")

-- startup
local game_config = nil

-- state
local world = nil
local current_map = nil

-- screen
local cam = nil
local scale = 1

-- player
local active_player = nil

-- events
local vm = {
    script = nil,
    script_name = nil,
    source = nil,
    keypress_event = {
        key = nil,
        script = nil
    }
}

--- ###############################################################################################
--- Runtime
--- ###############################################################################################

local function setup_world()
    rpg.log("setup_world", "setting up world")

    if world ~= nil then
        world:destroy()
        world = nil
    end

    world = wf.newWorld(0, 0, false)
    world:addCollisionClass("Solid")
    world:addCollisionClass("Player")
    world:addCollisionClass("Sprite")
    world:addCollisionClass("PlayerTrigger", { ignores = { "Solid", "Player", "Sprite" } })
    world:addCollisionClass("Trigger", { ignores = { "Trigger", "Solid", "Player", "Sprite" } })
end

local function setup_player(player_id)
    rpg.log("setup_player", string.format("player_id=[%s]", player_id))

    local player_sprite = rpg.get_sprite(player_id)
    active_player = player.load(player_id, player_sprite)
end

function rpg.load(config)
    game_config = config

    if config ~= nil then
        if config.debug then
            local has_lldebugger, lldebugger = pcall(require, "lldebugger")
            if has_lldebugger then
                lldebugger.start()
            end
        end

        if config.scale ~= nil then
            scale = config.scale
        end
    end

    love.graphics.setDefaultFilter("nearest", "nearest")

    cam = camera(0, 0, rpg.get_scale())
    cam.smoother = camera.smooth.damped(8)

    setup_world()

    -- Load initial map
    if config ~= nil then

        if config.map ~= nil then
            current_map = map.load(world, config.map)

            -- Load initial player
            if config.player ~= nil then
                setup_player(config.player)
            end
        end

    end

end

function rpg.update(dt)
    world:update(dt)

    if vm.script ~= nil then
        local ok, result_or_error = pcall(vm.script.update, dt)
        if ok then
            -- Do nothing
        else
            rpg.error("rpg.update", string.format("error running script=[%s], error=[%s]", vm.script_name, result_or_error))
        end

        vm.script = nil
        vm.source = nil
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
        map.draw(world, current_map)
    end
    cam:detach()
end

function rpg.keyreleased(key)
    if vm.script ~= nil then
        return
    end

    if key == vm.keypress_event.key then
        vm.script_name = "scripts/" .. vm.keypress_event.script:gsub(".lua", "")
        vm.script = require(vm.script_name)
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

    cam:lookAt(cam_x, cam_y)

    -- cam.x and cam.y keep track of where the camera is located
    -- the lookAt value may be moved if a screenshake is happening, so these
    -- values know where the camera should be, regardless of lookAt
    cam.x, cam.y = cam:position()

end

--- ###############################################################################################
--- Client API
--- ###############################################################################################

function rpg.log(function_name, message)
    local time = os.date("*t")
    print(string.format("%02d:%02d:%02d - INFO  - %s - %s", time.hour, time.min, time.sec, function_name, message))
end

function rpg.error(function_name, message)
    local time = os.date("*t")
    print(string.format("%02d:%02d:%02d - ERROR - %s - %s", time.hour, time.min, time.sec, function_name, message))
end

function rpg.get_scale()
    return scale
end

-- Sprite
function rpg.get_sprite(id)
    rpg.log("get_sprite", string.format("id=[%s]", id))

    if current_map == nil then
        error("invalid state: no map is loaded")
    end

    -- TODO: Optimize
    for i, layer in pairs(current_map.layers) do
        local map_sprite = layer.sprites[id]
        if map_sprite ~= nil then
            rpg.log("get_sprite", string.format("sprite found, id=[%s]", id))
            return map_sprite
        end
    end

    rpg.log("get_sprite", string.format("sprite not found, id=[%s]", id))

    return nil

end

function rpg.add_sprite(id, map_sprite)
    local layer_idx = map_sprite.startLocation.layer

    map.init_layer_sprite(id, world, map_sprite)
    map_sprite.layer = layer_idx

    current_map.layers[layer_idx].sprites[id] = map_sprite
end

function rpg.remove_sprite(id)
    local layer_idx = -1

    for i, layer in pairs(current_map.layers) do
        for sprite_id, instance in pairs(layer.sprites) do
            if id == sprite_id then
                sprite.destroy(instance)
                layer_idx = i
                break
            end
        end

        if layer_idx ~= -1 then
            break
        end
    end

    if layer_idx ~= -1 then
        current_map.layers[layer_idx].sprites[id] = nil
    end
end

function rpg.move_sprite(m_sprite, velocity_x, velocity_y)
    m_sprite.collider:setLinearVelocity(velocity_x, velocity_y)
    m_sprite.trigger:setPosition(m_sprite.collider:getPosition())
end

-- Map
function rpg.switch_map(new_map, x, y, layer)
    rpg.log("switch_map", string.format("start swtiching map, new_map=[%s], x=[%s], y=[%s], layer=[%s]", new_map, x, y, layer))

    setup_world()

    current_map = map.load(world, new_map)

    if game_config ~= nil and game_config.player ~= nil then
        setup_player(game_config.player)
        if active_player ~= nil then
            sprite.set_location(game_config.player, active_player, x * current_map.tileWidth, y * current_map.tileHeight, layer)
        end
    end

    rpg.log("switch_map", string.format("finished switching map, new_map=[%s], x=[%s], y=[%s], layer=[%s]", new_map, x, y, layer))
end

--- ###############################################################################################
--- end
--- ###############################################################################################

return rpg
