local rpg = { _version = "0.0.1" }

-- libraries
local anim8 = require("scripts/libraries/anim8")
local json = require("scripts/libraries/json")
local wf = require("scripts/libraries/windfield")
local vector = require("scripts/libraries/hump/vector")

-- state
local cache = {}
local current_map = nil
local world = nil

-- screen
local canvas = nil

--- ###############################################################################################
--- Asset Loading
--- ###############################################################################################

local function load_json(name)
    local contents, size = love.filesystem.read(name)

    -- TODO: error handling

    local json_data = json.decode(contents)

    -- TODO: error handling

    return json_data
end

function rpg.load_texture(name)
    local asset_name = "textures/" .. name

    -- Check the cache
    if cache[asset_name] ~= nil then
        return cache[asset_name]
    end

    local new_image = love.graphics.newImage(asset_name)

    -- Store in cache
    cache[asset_name] = new_image

    return new_image
end

function rpg.load_animation(name)
    local asset_name = "animations/" .. name

    local animation = nil

    -- Check the cache
    -- Slightly different here as we don't want to store Runtime data but
    -- we do want to load the original asset from the cache for speed
    if cache[asset_name] ~= nil then
        animation = cache[asset_name]
    else
        animation = load_json(asset_name)
        cache[asset_name] = animation
    end

    local image = rpg.load_texture(animation.spriteSheet.image)

    -- Runtime data, setup anim8
    local g = anim8.newGrid(animation.width, animation.height, image:getWidth(), image:getHeight())
    local row = (animation.spriteSheet.y / animation.height) + 1
    local col_start = (animation.spriteSheet.x / animation.width) + 1
    local col_end = col_start + (animation.spriteSheet.width / animation.width) - 1
    local range = tostring(col_start) .. '-' .. tostring(col_end)
    animation.animator = anim8.newAnimation(g(range, row), 1 / animation.frameRate)

    return animation
end

function rpg.load_sprite(name)
    local asset_name = "sprites/" .. name

    local sprite = nil

    -- Check the cache
    -- Slightly different here as we don't want to store Runtime data but
    -- we do want to load the original asset from the cache for speed
    if cache[asset_name] ~= nil then
        sprite = cache[asset_name]
    else
        sprite = load_json(asset_name)
        cache[asset_name] = sprite
    end

    -- Runtime data, setup anim8
    sprite.anim8 = {}
    for k, v in pairs(sprite.animations) do
        if v ~= nil and v ~= '' then
            sprite.anim8[k] = rpg.load_animation(v)
        end
    end

    return sprite
end

function rpg.load_tileset(name)
    local asset_name = "tilesets/" .. name

    -- Check the cache
    if cache[asset_name] ~= nil then
        return cache[asset_name]
    end

    local tileset = load_json(asset_name)
    local tileset_image = rpg.load_texture(tileset.image)

    -- Runtime data
    local tile_width = tileset.tileWidth
    local tile_height = tileset.tileHeight
    local image_width = tileset_image:getWidth()
    local image_height = tileset_image:getHeight()

    tileset.rows = math.floor(image_height / tile_height)
    tileset.columns = math.floor(image_width / tile_width)
    tileset.quads = {}

    -- Store the tiles in a 1D linear array
    for row = 0, tileset.rows - 1, 1 do
        for column = 0, tileset.columns - 1, 1 do
            local x = column * tile_width
            local y = row * tile_height
            local quad = love.graphics.newQuad(x, y, tile_width, tile_height, image_width, image_height)
            table.insert(tileset.quads, quad)
        end
    end

    -- Store in cache
    cache[asset_name] = tileset

    return tileset
end

function rpg.init_layer_collider(id, instance)
    -- TODO: move this to compile time
    local points = {}
    for i, point in pairs(instance.points) do
        table.insert(points, point.x)
        table.insert(points, point.y)
    end

    -- Runtime data, setup collider
    instance.collider = world:newPolygonCollider(points)
    instance.collider:setCollisionClass("Solid")
    instance.collider:setObject(instance)
    instance.collider:setType("static")
end

function rpg.init_layer_sprite(id, instance)
    -- Runtime data
    local sprite = rpg.load_sprite(instance.asset)
    instance.asset = sprite

    -- Assign active animation
    instance.active_animation = {}
    instance.active_animation = sprite.anim8["SOUTH"] -- TODO: Default to "idle"

    -- Runtime data, setup collider
    -- TODO: switch to circular collider in sprite format
    instance.collider = world:newCircleCollider(instance.startLocation.x, instance.startLocation.y, 10)
    instance.collider:setCollisionClass("Sprite")
    instance.collider:setFixedRotation(true)
    instance.collider:setObject(instance)
    instance.collider:setLinearDamping(2)
    instance.collider:setMass(math.huge)

    -- Runtime data, setup trigger
    -- TODO: switch to circular trigger in sprite format
    instance.trigger = world:newCircleCollider(instance.startLocation.x, instance.startLocation.y, 12)
    instance.trigger:setCollisionClass("Trigger")
    instance.trigger:setFixedRotation(true)
    instance.trigger:setObject(instance)
    instance.trigger:setLinearDamping(2)
    instance.trigger:setMass(math.huge)
end

function rpg.load_map(name)
    local asset_name = "maps/" .. name

    local map = nil

    -- Check the cache
    -- Slightly different here as we don't want to store Runtime data but
    -- we do want to load the original asset from the cache for speed
    if cache[asset_name] ~= nil then
        map = cache[asset_name]
    else
        map = load_json(asset_name)
        cache[asset_name] = map
    end

    -- Load tilesets
    for i, tileset_name in pairs(map.tilesets) do
        rpg.load_tileset(tileset_name)
    end

    -- TODO: Load music

    -- Iterate layers
    for i, layer in pairs(map.layers) do

        -- Iterate colliders
        for id, instance in pairs(layer.colliders) do
            rpg.init_layer_collider(id, instance)
            instance.layer = i
        end

        -- Iterate triggers
        -- TODO

        -- Iterate sprites
        for id, instance in pairs(layer.sprites) do
            rpg.init_layer_sprite(id, instance)
            instance.layer = i
        end

    end

    current_map = map

    return map
end

--- ###############################################################################################
--- Asset Drawing
--- ###############################################################################################

local function drawImage(image)
    -- TODO
end

local function drawSprite(sprite)
    if sprite.active_animation == nil then
        return
    end

    -- TODO: Optimize, many function calls
    local animation = sprite.active_animation
    local image = rpg.load_texture(animation.spriteSheet.image)
    local x = sprite.collider:getX() - math.floor(animation.width / 2)
    local y = sprite.collider:getY() - math.floor(animation.height / 2)
    animation.animator:draw(image, x, y)
end

local function drawTile(tile, row, column)
    -- TODO: Optimize, slow string split
    local tileset_index, tile_index = string.match(tile, "(%-?%d+):(%-?%d+)")
    tileset_index = tonumber(tileset_index)
    tile_index = tonumber(tile_index)
    --- TODO: end Optimize

    if tileset_index > -1 and tile_index > -1 then
        local tileset_name = current_map.tilesets[tileset_index + 1]
        local tileset = rpg.load_tileset(tileset_name)
        local tileset_image = rpg.load_texture(tileset.image)
        local quad = tileset.quads[tile_index + 1]

        if quad ~= nil then
            local xx = row * current_map.tileWidth
            local yy = column * current_map.tileHeight
            love.graphics.draw(tileset_image, quad, xx, yy)
        end
    end
end

local function drawTiles(layer)
    -- Draw the tiles
    for column = 0, current_map.height - 1, 1 do
        for row = 0, current_map.width - 1, 1 do
            local current_tile = column * current_map.width + row + 1
            local tile = layer.tiles[current_tile]
            drawTile(tile, row, column)
        end
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
        local sprite = layer.sprites[id]
        if sprite ~= nil then
            return sprite
        end
    end

    return nil

end

--- ###############################################################################################
--- Runtime
--- ###############################################################################################

function rpg.load()
    love.graphics.setDefaultFilter("nearest", "nearest")
    canvas = love.graphics.newCanvas(512, 288)

    world = wf.newWorld(0, 0, false)
    world:addCollisionClass("Solid")
    world:addCollisionClass("Player")
    world:addCollisionClass("Sprite")
    world:addCollisionClass("Trigger", { ignores = { "Solid", "Player", "Sprite" } })
end

function rpg.update(dt)
    if current_map == nil then
        return
    end

    world:update(dt)

    -- TODO: Optimize
    for i, layer in pairs(current_map.layers) do
        for j, sprite in pairs(layer.sprites) do
            if sprite.active_animation ~= nil then
                sprite.active_animation.animator:update(dt)
            end
        end
    end
end

function rpg.draw()
    if current_map == nil then
        return
    end

    love.graphics.setCanvas(canvas)

    -- TODO: Optimize
    for i, layer in pairs(current_map.layers) do

        drawTiles(layer)

        for j, sprite in pairs(layer.sprites) do
            drawSprite(sprite)
        end

        for j, image in pairs(layer.images) do
            drawImage(image)
        end

    end

    -- TODO: debug only
    world:draw()

    love.graphics.setCanvas()

    local scale = rpg.get_scale()
    love.graphics.draw(canvas, 0, 0, 0, scale, scale)

end

--- ###############################################################################################
--- Movement
--- ###############################################################################################

function rpg.move_player(player, player_speed)

    if player ~= nil then

        local delta = vector(0, 0)

        if love.keyboard.isDown("left") then
            delta.x = -1
            player.active_animation = player.asset.anim8["WEST"]
        elseif love.keyboard.isDown("right") then
            delta.x = 1
            player.active_animation = player.asset.anim8["EAST"]
        end
        if love.keyboard.isDown("up") then
            delta.y = -1
            player.active_animation = player.asset.anim8["NORTH"]
        elseif love.keyboard.isDown("down") then
            delta.y = 1
            player.active_animation = player.asset.anim8["SOUTH"]
        end

        delta:normalizeInplace()
        player.collider:setLinearVelocity(delta.x * player_speed, delta.y * player_speed)
        player.trigger:setPosition(player.collider:getPosition())
    end

end

return rpg
